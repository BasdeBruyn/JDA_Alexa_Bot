package nl.AlexaBot;

import nl.AlexaBot.alexa.AlexaController;
import nl.AlexaBot.events.ReadyListener;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;

public class Main
{
    public static void main(String[] args)
            throws LoginException, InterruptedException
    {
        if (args.length == 0 || args[0].length() == 0)
            throw new RuntimeException(Strings.NO_BOT_TOKEN);
        String token = args[0];

        JDA jda = new JDABuilder(token)
                .addEventListener(new ReadyListener(), new AlexaController())
                .build();

        jda.awaitReady();
    }
}
