package nl.AlexaBot.audioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.TextChannel;
import nl.AlexaBot.Strings;

public class LoadResultHandler implements AudioLoadResultHandler {
    private TextChannel textChannel;

    private MusicPlayer musicPlayer;

    private GuildMusicManager musicManager;

    private String trackUrl;

    public LoadResultHandler(TextChannel textChannel, MusicPlayer musicPlayer, GuildMusicManager musicManager, String trackUrl) {
        this.textChannel = textChannel;
        this.musicPlayer = musicPlayer;
        this.musicManager = musicManager;
        this.trackUrl = trackUrl;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        textChannel.sendMessage(
                Strings.TRACK_LOADED + track.getInfo().title).queue();

        musicPlayer.play(textChannel.getGuild(), musicManager, track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        AudioTrack audioTrack = playlist.getTracks().get(0);

        textChannel.sendMessage(Strings.TRACK_LOADED + audioTrack.getInfo().title).queue();
        if (trackUrl.startsWith(Strings.YT_SEARCH_SELECTOR))
            textChannel.sendMessage(audioTrack.getInfo().uri).queue();

        musicPlayer.play(textChannel.getGuild(), musicManager, audioTrack);
    }

    @Override
    public void noMatches() {
        textChannel.sendMessage(Strings.NO_MATCHES + trackUrl).queue();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        textChannel.sendMessage(Strings.LOAD_FAILED + exception.getMessage()).queue();
    }
}
