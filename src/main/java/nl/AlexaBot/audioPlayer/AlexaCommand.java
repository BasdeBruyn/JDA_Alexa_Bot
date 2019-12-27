package nl.AlexaBot.audioPlayer;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class AlexaCommand {
    private MessageReceivedEvent event;
    private String messageContent;
    private String[] command;
    private Guild guild;
    private TextChannel textChannel;
    private VoiceChannel voiceChannel;
    private String trackUrl;

    public AlexaCommand(MessageReceivedEvent event) {
        this.event = event;
        messageContent = event.getMessage().getContentDisplay();
        command = messageContent.split(" ", 3);
        guild = event.getGuild();
        textChannel = event.getTextChannel();
        voiceChannel = event.getMember().getVoiceState().getChannel();
    }

    public AlexaCommand(
            MessageReceivedEvent event,
            String messageContent,
            String[] command,
            Guild guild,
            TextChannel textChannel,
            VoiceChannel voiceChannel,
            String trackUrl
    ) {
        this.event = event;
        this.messageContent = messageContent;
        this.command = command;
        this.guild = guild;
        this.textChannel = textChannel;
        this.voiceChannel = voiceChannel;
        this.trackUrl = trackUrl;
    }

    public MessageReceivedEvent getEvent() { return event; }
    public void setEvent(MessageReceivedEvent event) { this.event = event; }
    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }
    public String[] getCommand() { return command; }
    public void setCommand(String[] command) { this.command = command; }
    public Guild getGuild() { return guild; }
    public void setGuild(Guild guild) { this.guild = guild; }
    public TextChannel getTextChannel() { return textChannel; }
    public void setTextChannel(TextChannel textChannel) { this.textChannel = textChannel; }
    public VoiceChannel getVoiceChannel() { return voiceChannel; }
    public void setVoiceChannel(VoiceChannel voiceChannel) { this.voiceChannel = voiceChannel; }
    public String getTrackUrl() { return trackUrl; }
    public void setTrackUrl(String trackUrl) { this.trackUrl = trackUrl; }
}
