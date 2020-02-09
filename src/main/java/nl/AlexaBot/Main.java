package nl.AlexaBot;

import net.dv8tion.jda.core.JDABuilder;
import nl.AlexaBot.events.AlexaCommandListener;
import nl.AlexaBot.events.ReadyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class Main {
    private static final String NO_BOT_TOKEN_GIVEN = "No bot token given";

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0)
            throw new RuntimeException(NO_BOT_TOKEN_GIVEN);

        String token = args[0];
        logger.info("token provided: " + token);

        try {
            new JDABuilder(token)
                    .addEventListener(new ReadyListener(), new AlexaCommandListener())
                    .build()
                    .awaitReady();
        } catch (LoginException exception) {
            logger.error(exception.getMessage());
        }
    }
}
