package nl.AlexaBot.audioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.HashMap;
import java.util.Map;

public class MusicPlayer {
    public static final String YTSEARCH_PREFIX = "ytsearch:";

    private static final String CURRENTLY_NOT_PLAYING = "Currently not playing.";
    private static final String SKIPPED_TO_NEXT_TRACK = "Skipped to next track.";
    private static final String QUEUE_FINISHED = "Queue finished.";
    private static final String BOT_NOT_IN_VOICE_CHANNEL = "I haven't joined a voice-channel jet.";
    private static final String STOPPED_PLAYING = "Stopped playing.";

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

    public void loadAndPlay(String trackUrl, TextChannel textChannel, VoiceChannel voiceChannel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(textChannel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl,
                new LoadResultHandler(trackUrl, textChannel, voiceChannel));
    }

    public void play(VoiceChannel voiceChannel, AudioTrack track) {
        Guild guild = voiceChannel.getGuild();
        connectToVoiceChannel(voiceChannel, guild.getAudioManager());

        getGuildAudioPlayer(guild).queueTrack(track);
    }

    public void skipTrack(TextChannel textChannel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(textChannel.getGuild());

        if (musicManager.isPlaying()) {
            textChannel.sendMessage(CURRENTLY_NOT_PLAYING).queue();
        } else if (musicManager.nextTrack()) {
            textChannel.sendMessage(SKIPPED_TO_NEXT_TRACK).queue();
        } else {
            textChannel.sendMessage(QUEUE_FINISHED).queue();
            leaveChannel(textChannel);
        }
    }

    public void searchVideo(String searchString, TextChannel textChannel, VoiceChannel voiceChannel) {
        searchString = (YTSEARCH_PREFIX + searchString);
        loadAndPlay(searchString, textChannel, voiceChannel);
    }

    public void stopPlaying(TextChannel textChannel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(textChannel.getGuild());

        if (!musicManager.isPlaying()) {
            textChannel.sendMessage(CURRENTLY_NOT_PLAYING).queue();
        } else {
            musicManager.stopPlaying();
            leaveChannel(textChannel);
            textChannel.sendMessage(STOPPED_PLAYING).queue();
        }
    }

    private static void connectToVoiceChannel(VoiceChannel voiceChannel, AudioManager audioManager) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            audioManager.openAudioConnection(voiceChannel);
        }
    }

    public boolean leaveChannel(TextChannel textChannel) {
        Guild guild = textChannel.getGuild();
        if (guild.getAudioManager().getConnectedChannel() == null) {
            textChannel.sendMessage(BOT_NOT_IN_VOICE_CHANNEL).queue();
            return false;
        } else {
            guild.getAudioManager().closeAudioConnection();
            return true;
        }
    }

    public boolean isPlaying(Guild guild) {
        return getGuildAudioPlayer(guild).isPlaying();
    }
}
