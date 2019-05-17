package nl.AlexaBot.events;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import nl.AlexaBot.Strings;

public class ReadyListener implements EventListener {
    public void onEvent(Event event) {
        if (event instanceof ReadyEvent)
            System.out.println(Strings.API_READY);
    }
}
