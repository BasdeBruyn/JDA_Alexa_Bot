package nl.AlexaBot.audioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;

public class GuildMusicManager {
    /**
     * Audio player for the guild.
     */
    private final AudioPlayer player;
    /**
     * Track scheduler for the player.
     */
    private final TrackScheduler scheduler;

    /**
     * Creates a player and a track scheduler.
     *
     * @param manager Audio player manager to use for creating the player.
     */
    public GuildMusicManager(AudioPlayerManager manager, Guild guild) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player, guild);
        player.addListener(scheduler);
    }

    /**
     * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }

    public void queueTrack(AudioTrack track) {
        scheduler.queue(track);
    }

    public boolean nextTrack() {
        return scheduler.nextTrack();
    }

    public void stopPlaying() {
        scheduler.clearQueue();
        player.stopTrack();
        player.setPaused(false);
    }

    public boolean isPlaying() {
        return player.getPlayingTrack() != null;
    }
}
