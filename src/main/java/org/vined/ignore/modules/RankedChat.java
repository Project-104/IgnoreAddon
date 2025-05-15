package org.vined.ignore.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import org.vined.ignore.IgnoreAddon;
import org.vined.ignore.utils.IgnoreUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RankedChat extends Module {
    private final Pattern pattern = Pattern.compile("^\\[(\\w+(?: \\w+)*)] (\\w+) »");
    private final Pattern whisperPattern = Pattern.compile("^\\w+ whispers: ");
    private final Pattern joinPattern = Pattern.compile("^\\w+ joined\\.");
    private final Pattern leavePattern = Pattern.compile("^\\w+ quit\\.");
    private final Pattern deathPattern = Pattern.compile("^\\w+ (died\\.|was .*)");

    public RankedChat() {
        super(IgnoreAddon.IGNORE, "ranked-chat", "Only shows messages sent by ranked players and you.");
    }

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event) {
        String message = event.getMessage().getString();

        if (whisperPattern.matcher(message).find()
            || joinPattern.matcher(message).find()
            || leavePattern.matcher(message).find()
            || deathPattern.matcher(message).find()
            || message.startsWith("You whisper")) {
            return;
        }

        if (!message.contains("»")) return;

        if (Objects.equals(IgnoreUtils.getName(message), mc.getSession().getUsername())) return;

        Matcher matcher = pattern.matcher(message);
        if (!matcher.find()) {
            event.cancel();
        }
    }
}
