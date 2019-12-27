package nl.AlexaBot.alexa;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import nl.AlexaBot.audioPlayer.AlexaCommand;
import nl.AlexaBot.audioPlayer.MusicPlayer;

import java.net.URL;

import static nl.AlexaBot.alexa.Alexa_Strings.*;

public class AlexaController extends ListenerAdapter {

    private final MusicPlayer musicPlayer;

    public AlexaController() {
        musicPlayer = MusicPlayer.getInstance();
    }



    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        AlexaCommand alexaCommand = new AlexaCommand(event);

        if (isBot(alexaCommand))
            return;

        if (!isThisIsSoSad(alexaCommand) && !isAlexaPrefix(alexaCommand))
            return;

        if (!isMemberInVoiceChannel(alexaCommand)) {
            MessageController.sendMessage(alexaCommand, notInVoiceChannelMessage);
            return;
        }

        if (isThisIsSoSad(alexaCommand)) {
            playDespacito(alexaCommand);
        }

        switch (alexaCommand.getCommand()[1]) {
            case playCommand:
                play(alexaCommand);
                break;
            case skipCommand:
                skip(alexaCommand);
                break;
            case stopCommand:
                stop(alexaCommand);
                break;
            case searchCommand:
                search(alexaCommand);
                break;
            case disconnectCommand:
                disconnect(alexaCommand);
                break;
        }
    }



    private void play(AlexaCommand alexaCommand) {
        if (alexaCommand.getCommand().length < 3) {
            MessageController.sendMessage(alexaCommand, "Please enter a search phrase or link.");
            return;
        }

        alexaCommand.setTrackUrl(alexaCommand.getCommand()[2]);

        URL url = getURL(alexaCommand.getTrackUrl());
        if (url != null)
            musicPlayer.loadAndPlay(alexaCommand);
        else
            musicPlayer.searchVideo(alexaCommand);
    }

    private void skip(AlexaCommand alexaCommand) {
        musicPlayer.skipTrack(alexaCommand);
    }

    private void stop(AlexaCommand alexaCommand){
        musicPlayer.stopPlaying(alexaCommand);
    }

    private void search(AlexaCommand alexaCommand) {
        if (alexaCommand.getCommand().length < 3)
            MessageController.sendMessage(alexaCommand, "Please enter a search phrase.");
        else {
            alexaCommand.setTrackUrl(alexaCommand.getCommand()[2]);
            musicPlayer.searchVideo(alexaCommand);
        }
    }

    private void playDespacito(AlexaCommand alexaCommand) {
        alexaCommand.setTrackUrl(despacitoTitle);
        musicPlayer.searchVideo(alexaCommand);
    }

    private void disconnect(AlexaCommand alexaCommand) {
        if (musicPlayer.isPlaying(alexaCommand.getGuild())) {
            musicPlayer.stopPlaying(alexaCommand);
        } else {
            if (musicPlayer.leaveChannel(alexaCommand))
                MessageController.sendMessage(alexaCommand, "Disconnected.");
        }
    }



    private static URL getURL(String url) {
        try {
            return new URL(url);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isBot(AlexaCommand alexaCommand) {
        return alexaCommand
                .getEvent()
                .getMember()
                .getUser()
                .isBot();
    }

    private boolean isAlexaPrefix(AlexaCommand alexaCommand) {
        return alexaCommand
                .getCommand()[0]
                .equals(alexaPrefix);
    }

    private boolean isMemberInVoiceChannel(AlexaCommand alexaCommand) {
        return alexaCommand
                .getEvent()
                .getMember()
                .getVoiceState()
                .inVoiceChannel();
    }

    private boolean isThisIsSoSad(AlexaCommand alexaCommand) {
        return alexaCommand
                .getMessageContent()
                .startsWith(thisIsSoSad.toLowerCase());
    }
}
