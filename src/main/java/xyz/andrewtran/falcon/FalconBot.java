package xyz.andrewtran.falcon;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FalconBot extends ListenerAdapter {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm");;
    private static FalconBot instance;
    private JDA jda;

    public static void main(String[] args) {
        if (instance != null) {
            throw new IllegalStateException();
        }
        if (args.length < 1) {
            System.err.println("Please provide token in arguments");
            return;
        }
        instance = new FalconBot(args[0]);
    }

    public FalconBot(String token) {
        try {
            jda = JDABuilder
                .createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(this)
                .build();
            jda.getPresence().setActivity(Activity.playing("Type \"check talon\""));
        } catch (LoginException e) {
            e.printStackTrace();
            return;
        }

        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate(new TimerTask() {
            private final long THREE_HOURS_MS = 1000L * 60L * 60L * 3L;
            private long lastSentNotification = 1;
            private final PrivateChannel andrewChannel = jda
                    .retrieveUserById(127170421987475456L)
                    .complete()
                    .openPrivateChannel()
                    .complete();

            public void run() {
                if ((System.currentTimeMillis() - lastSentNotification) <= THREE_HOURS_MS) {
                    return;
                }
                try {
                    boolean ctre = FalconChecker.checkCTRE();
                    boolean vex = FalconChecker.checkVEX();
                    System.out.println("CHECK AT " + DATE_FORMAT.format(new Date()));
                    System.out.println("CTRE: " + ctre);
                    System.out.println("VEX : " + vex);
                    System.out.println("--------------------");
                    boolean either = ctre || vex;
                    if (either) {
                        lastSentNotification = System.currentTimeMillis();
                        check(andrewChannel); // My PMs
                        check(jda.getTextChannelById("349005378039709706")); // My spam channel
                        check(jda.getTextChannelById("770846258037915688")); // Robotics falcon-checker checker
                    }
                } catch (Exception e) {
                    System.err.println("Error in periodic timer task:");
                    e.printStackTrace();
                }

            }
        }, 0, 1000L * 60L * 5L /* 5 minutes */);
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        handle(event.getMessage(), event.getChannel());
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        handle(event.getMessage(), event.getChannel());
    }

    public void handle(Message msg, MessageChannel channel) {
        if (msg.getContentRaw().startsWith("check talon")) {
            check(channel);
        }
    }

    public void check(MessageChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        boolean ctre, vex;
        try {
            ctre = FalconChecker.checkCTRE();
            vex = FalconChecker.checkVEX();
        } catch (Exception e) {
            channel.sendMessage("**Error:** " + e.getMessage()).queue();
            e.printStackTrace();
            return;
        }
        builder.addField("CTRE", ctre ? "In stock!!!" : "Out of stock", true);
        builder.addField("VEX", vex ? "In stock!!!" : "Out of stock", true);
        boolean eitherInStock = ctre || vex;
        builder.setColor(eitherInStock ? Color.GREEN : Color.RED);
        builder.setTitle("Falcon 500 Stock Checker");
        builder.setDescription("Checked at " + DATE_FORMAT.format(new Date()));
        channel.sendMessage(builder.build()).queue();
    }
}
