package nl.AlexaBot.alexa;

import nl.AlexaBot.audioPlayer.MusicPlayer;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.net.URL;

import static nl.AlexaBot.alexa.Alexa_Strings.*;

public class AlexaController extends ListenerAdapter {

    private final MusicPlayer musicPlayer;

    public AlexaController() {
        musicPlayer = MusicPlayer.getInstance();
    }



    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (isBot(event))
            return;

        Message message = event.getMessage();
        String messageContent = message.getContentDisplay();
        String[] command = messageContent.split(" ", 3);
        TextChannel textChannel = event.getTextChannel();

        if (isThisIsSoSad(messageContent)) {
            playDespacito(textChannel);
        }

        if ( !isAlexaPrefix(command[0]) )
            return;
        
        if (!isMemberInVoiceChannel(event.getMember())){
            textChannel.sendMessage(notInVoiceChannelMessage).queue();
        } else
            switch (command[1]) {
                case playCommand:
                    play(textChannel, command);
                    break;
                case skipCommand:
                    skip(textChannel);
                    break;
                case stopCommand:
                    stop(textChannel);
                    break;
                case searchCommand:
                    search(textChannel, command);
                    break;
                case disconnectCommand:
                    disconnect(textChannel);
                    break;
            }
    }



    private void play(TextChannel textChannel, String[] command){
        if (command.length < 3) {
            textChannel.sendMessage("Please enter a search phrase or link.").queue();
            return;
        }

        URL url = getURL(command[2]);
        if (url != null)
            musicPlayer.loadAndPlay(textChannel, command[2]);
        else
            musicPlayer.searchVideo(textChannel, command[2]);
    }

    private void skip(TextChannel textChannel) {
        musicPlayer.skipTrack(textChannel);
    }

    private void stop(TextChannel textChannel){
        musicPlayer.stopPlaying(textChannel);
    }

    private void search(TextChannel textChannel, String[] command){
        if (command.length < 3)
            textChannel.sendMessage("Please enter a search phrase.").queue();
        else
            musicPlayer.searchVideo(textChannel, command[2]);
    }

    private void disconnect(TextChannel textChannel){
        if (musicPlayer.isPlaying(textChannel)) {
            musicPlayer.stopPlaying(textChannel);
        } else {
            if (musicPlayer.leaveChannel(textChannel))
                textChannel.sendMessage("Disconnected.").queue();
        }
    }

    private void playDespacito(TextChannel textChannel){
        musicPlayer.searchVideo(textChannel, despacitoTitle);
    }



    private static URL getURL(String url) {
        try {
            return new URL(url);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isBot(MessageReceivedEvent event){
        return event.getMember().getUser().isBot();
    }

    private boolean isAlexaPrefix(String prefix){
        return prefix.equals(alexaPrefix);
    }

    private boolean isMemberInVoiceChannel(Member member){
        return member.getVoiceState().inVoiceChannel();
    }

    private boolean isThisIsSoSad(String message){
        return message.equalsIgnoreCase(thisIsSoSad)
                || message.equalsIgnoreCase(thisIsSoSad + "!");
    }
}
