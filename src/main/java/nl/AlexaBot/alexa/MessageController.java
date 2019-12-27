package nl.AlexaBot.alexa;

import nl.AlexaBot.audioPlayer.AlexaCommand;

public class MessageController {
    public static void sendMessage(AlexaCommand alexaCommand, String message) {
        alexaCommand.getTextChannel().sendMessage(message).queue();
    }
}
