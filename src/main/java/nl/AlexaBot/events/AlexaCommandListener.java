package nl.AlexaBot.events;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import nl.AlexaBot.audioPlayer.MusicPlayer;
import javax.annotation.Nonnull;

import java.net.URL;

public class AlexaCommandListener extends ListenerAdapter {
    private static final String ALEXA_PREFIX = "alexa";
    private static final String MEMBER_NOT_IN_VOICE_CHANNEL = "You haven't joined a voice-channel jet.";
    private static final String DISCONNECTED = "Disconnected.";
    private static final String NO_PLAY_ARGUMENT_GIVEN = "Please enter a search phrase or link.";
    private static final String PLAY_COMMAND = "play";
    private static final String SKIP_COMMAND = "skip";
    private static final String STOP_COMMAND = "stop";
    private static final String DISCONNECT_COMMAND = "disconnect";

    private final MusicPlayer musicPlayer;

    public AlexaCommandListener() {
        musicPlayer = MusicPlayer.getInstance();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent messageEvent) {
        Member member = messageEvent.getMember();
        String messageContent = messageEvent.getMessage().getContentDisplay();
        String[] messageComponents = extractMessageComponents(messageContent);
        TextChannel textChannel = messageEvent.getTextChannel();

        if (!shouldEventBeHandled(member, messageComponents, textChannel)) return;

        VoiceChannel voiceChannel = messageEvent.getMember().getVoiceState().getChannel();
        handleCommand(messageComponents, voiceChannel, textChannel);
    }

    private boolean shouldEventBeHandled(Member member, String[] messageComponents, TextChannel textChannel) {
        User user = member.getUser();
        String prefix = extractPrefix(messageComponents);
        String command = extractCommand(messageComponents);

        if (user.isBot()) return false;
        if (prefix == null) return false;
        if (!isAlexaPrefix(prefix)) return false;
        if (command == null) return false;
        if (!isMemberInVoiceChannel(member)) {
            textChannel.sendMessage(MEMBER_NOT_IN_VOICE_CHANNEL).queue();
            return false;
        }

        return true;
    }

    private void handleCommand(String[] messageComponents, VoiceChannel voiceChannel, TextChannel textChannel) {
        String command = extractCommand(messageComponents);
        String argument = extractArgument(messageComponents);

        switch (command) {
            case PLAY_COMMAND:
                play(argument, textChannel, voiceChannel); break;
            case SKIP_COMMAND:
                skip(textChannel); break;
            case STOP_COMMAND:
                stop(textChannel); break;
            case DISCONNECT_COMMAND:
                disconnect(textChannel); break;
        }
    }

    private String[] extractMessageComponents(String messageContent) {
        return messageContent.split(" ", 3);
    }

    private String extractPrefix(String[] messageComponents) {
        return extractMessageComponent(messageComponents, 0);
    }

    private String extractCommand(String[] messageComponents) {
        return extractMessageComponent(messageComponents, 1);
    }

    private String extractArgument(String[] messageComponents) {
        return extractMessageComponent(messageComponents, 2);
    }

    private String extractMessageComponent(String[] messageComponents, int position) {
        if (messageComponents.length >= position + 1)
            return messageComponents[position];
        else
            return null;
    }

    private void play(String argument, TextChannel textChannel, VoiceChannel voiceChannel) {
        if (argument == null) {
            textChannel.sendMessage(NO_PLAY_ARGUMENT_GIVEN).queue();
            return;
        }

        if (isUrl(argument))
            musicPlayer.loadAndPlay(argument, textChannel, voiceChannel);
        else
            musicPlayer.searchVideo(argument, textChannel, voiceChannel);
    }

    private void skip(TextChannel textChannel) {
        musicPlayer.skipTrack(textChannel);
    }

    private void stop(TextChannel textChannel) {
        musicPlayer.stopPlaying(textChannel);
    }

    private void disconnect(TextChannel textChannel) {
        if (musicPlayer.isPlaying(textChannel.getGuild())) {
            musicPlayer.stopPlaying(textChannel);
        } else {
            if (musicPlayer.leaveChannel(textChannel))
                textChannel.sendMessage(DISCONNECTED).queue();
        }
    }

    private static boolean isUrl(String url) {
        try {
            new URL(url);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isAlexaPrefix(String prefix) {
        return prefix.equals(ALEXA_PREFIX);
    }

    private boolean isMemberInVoiceChannel(Member member) {
        return member.getVoiceState().inVoiceChannel();
    }
}
