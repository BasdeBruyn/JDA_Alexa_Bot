package nl.AlexaBot.events;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import nl.AlexaBot.Main;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadyListener implements EventListener {

    private static final String API_IS_READY = "API is ready!";

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public void onEvent(@NotNull Event event) {
        if (event instanceof ReadyEvent)
            logger.info(API_IS_READY);
    }
}
