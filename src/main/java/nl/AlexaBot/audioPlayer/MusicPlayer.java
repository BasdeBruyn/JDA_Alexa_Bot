package nl.AlexaBot.audioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import nl.AlexaBot.Strings;
import nl.AlexaBot.alexa.MessageController;

import java.util.HashMap;
import java.util.Map;

public class MusicPlayer {
    private static MusicPlayer mInstance;

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    private MusicPlayer() {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public static MusicPlayer getInstance() {
        if (mInstance == null) {
            mInstance = new MusicPlayer();
        }
        return mInstance;
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager, guild);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public void loadAndPlay(AlexaCommand alexaCommand) {
        GuildMusicManager musicManager = getGuildAudioPlayer(alexaCommand.getGuild());

        playerManager.loadItemOrdered(musicManager, alexaCommand.getTrackUrl(),
                new LoadResultHandler(alexaCommand, this, musicManager));
    }

    void play(AlexaCommand alexaCommand, GuildMusicManager musicManager, AudioTrack track) {
        connectToVoiceChannel(alexaCommand);

        track.setUserData(alexaCommand);

        musicManager.scheduler.queue(track);
    }

    public void skipTrack(AlexaCommand alexaCommand) {
        GuildMusicManager musicManager = getGuildAudioPlayer(alexaCommand.getGuild());

        if ( musicManager.player.getPlayingTrack() == null ){
            MessageController.sendMessage(alexaCommand, Strings.NOT_PLAYING);
        } else if (musicManager.scheduler.nextTrack()) {
            MessageController.sendMessage(alexaCommand, Strings.SKIPPED_TRACK);
        } else {
            MessageController.sendMessage(alexaCommand, Strings.QUEUE_FINISHED);
            leaveChannel(alexaCommand);
        }
    }

    private static void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            VoiceChannel voiceChannel = audioManager.getGuild().getVoiceChannels().get(0);
            audioManager.openAudioConnection(voiceChannel);
        }
    }

    private static void connectToVoiceChannel(AlexaCommand alexaCommand) {
        AudioManager audioManager = alexaCommand.getGuild().getAudioManager();
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            audioManager.openAudioConnection(alexaCommand.getVoiceChannel());
        }
    }

    public void stopPlaying(AlexaCommand alexaCommand){
        GuildMusicManager musicManager = getGuildAudioPlayer(alexaCommand.getGuild());
        
        if ( musicManager.player.getPlayingTrack() == null ) {
            MessageController.sendMessage(alexaCommand, Strings.NOT_PLAYING);
        } else {
            musicManager.scheduler.queue.clear();
            musicManager.player.stopTrack();
            musicManager.player.setPaused(false);

            leaveChannel(alexaCommand);

            MessageController.sendMessage(alexaCommand, Strings.STOPPED_PLAYING);
        }
    }

    public boolean leaveChannel(AlexaCommand alexaCommand){
        Guild guild = alexaCommand.getGuild();
        if ( guild.getAudioManager().getConnectedChannel() == null ) {
            MessageController.sendMessage(alexaCommand, Strings.NOT_IN_CHANNEL);
            return false;
        }
        else {
            guild.getAudioManager().closeAudioConnection();
            return true;
        }
    }

    public void searchVideo(AlexaCommand alexaCommand){
        alexaCommand.setTrackUrl(Strings.YT_SEARCH_SELECTOR + alexaCommand.getTrackUrl());
        loadAndPlay(alexaCommand);
    }

    public boolean isPlaying(Guild guild){
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);

        return !(musicManager.player.getPlayingTrack() == null);
    }
}
