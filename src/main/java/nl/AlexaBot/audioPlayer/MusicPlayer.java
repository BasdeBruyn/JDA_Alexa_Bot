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
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public void loadAndPlay(final TextChannel textChannel, final String trackUrl) {
        GuildMusicManager musicManager = getGuildAudioPlayer(textChannel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl,
                new LoadResultHandler(textChannel, this, musicManager,trackUrl));
    }

    void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
        connectToFirstVoiceChannel(guild.getAudioManager());

        musicManager.scheduler.queue(track);
    }

    public void skipTrack(TextChannel textChannel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(textChannel.getGuild());

        if ( musicManager.player.getPlayingTrack() == null ){
            textChannel.sendMessage(Strings.NOT_PLAYING).queue();
        } else if (musicManager.scheduler.nextTrack()) {
            textChannel.sendMessage(Strings.SKIPPED_TRACK).queue();
        } else {
            textChannel.sendMessage(Strings.QUEUE_FINISHED).queue();
            leaveChannel(textChannel);
        }
    }

    private static void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            VoiceChannel voiceChannel = audioManager.getGuild().getVoiceChannels().get(0);
            audioManager.openAudioConnection(voiceChannel);
        }
    }

    public void stopPlaying(TextChannel textChannel){
        GuildMusicManager musicManager = getGuildAudioPlayer(textChannel.getGuild());
        
        if ( musicManager.player.getPlayingTrack() == null ) {
            textChannel.sendMessage(Strings.NOT_PLAYING).queue();
        } else {
            musicManager.scheduler.queue.clear();
            musicManager.player.stopTrack();
            musicManager.player.setPaused(false);

            leaveChannel(textChannel);

            textChannel.sendMessage(Strings.STOPPED_PLAYING).queue();
        }
    }

    public boolean leaveChannel(TextChannel textChannel){
        Guild guild = textChannel.getGuild();
        if ( guild.getAudioManager().getConnectedChannel() == null ) {
            textChannel.sendMessage(Strings.NOT_IN_CHANNEL).queue();
            return false;
        }
        else {
            guild.getAudioManager().closeAudioConnection();
            return true;
        }
    }

    public void searchVideo(TextChannel textChannel, String query){
        loadAndPlay(textChannel, Strings.YT_SEARCH_SELECTOR + query);
    }

    public boolean isPlaying(TextChannel textChannel){
        GuildMusicManager musicManager = getGuildAudioPlayer(textChannel.getGuild());

        return !(musicManager.player.getPlayingTrack() == null);
    }
}
