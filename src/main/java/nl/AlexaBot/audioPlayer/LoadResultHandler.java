package nl.AlexaBot.audioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class LoadResultHandler implements AudioLoadResultHandler {
    private static final String ADDING_TO_QUEUE = "Adding to queue ";
    private static final String NOTHING_FOUND_BY = "Nothing found by ";
    private static final String COULD_NOT_PLAY = "Could not play: ";

    private String trackUrl;
    private TextChannel textChannel;
    private VoiceChannel voiceChannel;
    private MusicPlayer musicPlayer;

    public LoadResultHandler(String trackUrl, TextChannel textChannel, VoiceChannel voiceChannel) {
        this.trackUrl = trackUrl;
        this.textChannel = textChannel;
        this.voiceChannel = voiceChannel;
        this.musicPlayer = MusicPlayer.getInstance();
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        textChannel.sendMessage(ADDING_TO_QUEUE + track.getInfo().title).queue();
        track.setUserData(textChannel);

        musicPlayer.play(voiceChannel, track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        AudioTrack audioTrack = playlist.getTracks().get(0);

        textChannel.sendMessage(ADDING_TO_QUEUE + audioTrack.getInfo().title).queue();
        if (trackUrl.startsWith(MusicPlayer.YTSEARCH_PREFIX))
            textChannel.sendMessage(audioTrack.getInfo().uri).queue();

        musicPlayer.play(voiceChannel, audioTrack);
    }

    @Override
    public void noMatches() {
        textChannel.sendMessage(NOTHING_FOUND_BY + trackUrl).queue();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        textChannel.sendMessage(COULD_NOT_PLAY + exception.getMessage()).queue();
    }
}
