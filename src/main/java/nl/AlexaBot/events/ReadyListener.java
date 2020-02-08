package nl.AlexaBot.events;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class ReadyListener implements EventListener {

    private static final String API_IS_READY = "API is ready!";

    public void onEvent(@NotNull Event event) {
        if (event instanceof ReadyEvent)
            System.out.println(API_IS_READY);
    }
}
