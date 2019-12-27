package nl.AlexaBot.audioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import nl.AlexaBot.Strings;
import nl.AlexaBot.alexa.MessageController;

public class LoadResultHandler implements AudioLoadResultHandler {
    private AlexaCommand alexaCommand;
    private MusicPlayer musicPlayer;
    private GuildMusicManager musicManager;

    public LoadResultHandler(AlexaCommand alexaCommand, MusicPlayer musicPlayer, GuildMusicManager musicManager) {
        this.alexaCommand = alexaCommand;
        this.musicPlayer = musicPlayer;
        this.musicManager = musicManager;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        MessageController.sendMessage(
                alexaCommand,
                Strings.TRACK_LOADED + track.getInfo().title);

        musicPlayer.play(alexaCommand, musicManager, track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        AudioTrack audioTrack = playlist.getTracks().get(0);

        MessageController.sendMessage(
                alexaCommand,
                Strings.TRACK_LOADED + audioTrack.getInfo().title);
        if (alexaCommand.getTrackUrl().startsWith(Strings.YT_SEARCH_SELECTOR))
            MessageController.sendMessage(alexaCommand, audioTrack.getInfo().uri);

        musicPlayer.play(alexaCommand, musicManager, audioTrack);
    }

    @Override
    public void noMatches() {
        MessageController.sendMessage(
                alexaCommand,
                Strings.NO_MATCHES + alexaCommand.getTrackUrl());
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        MessageController.sendMessage(
                alexaCommand,
                Strings.LOAD_FAILED + exception.getMessage());
    }
}
