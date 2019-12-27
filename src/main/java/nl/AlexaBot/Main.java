package nl.AlexaBot;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import nl.AlexaBot.alexa.AlexaController;
import nl.AlexaBot.events.ReadyListener;

import javax.security.auth.login.LoginException;

public class Main
{
    public static void main(String[] args)
            throws InterruptedException
    {
        if (args.length == 0 || args[0].length() == 0)
            throw new RuntimeException(Strings.NO_BOT_TOKEN);
        String token = args[0];

        try {
            JDA jda = new JDABuilder(token)
                    .addEventListener(new ReadyListener(), new AlexaController())
                    .build();
            jda.awaitReady();
        } catch (LoginException e) {
            e.printStackTrace();
            System.out.println("token = " + token);
        }
    }
}
