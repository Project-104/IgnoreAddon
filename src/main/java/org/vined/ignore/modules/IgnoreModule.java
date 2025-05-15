package org.vined.ignore.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import org.vined.ignore.IgnoreAddon;
import org.vined.ignore.utils.IgnoreUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IgnoreModule extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> joinMessages = sgGeneral.add(new BoolSetting.Builder()
        .name("join-messages")
        .description("Block join messages by ignored users.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> leaveMessages = sgGeneral.add(new BoolSetting.Builder()
        .name("leave-messages")
        .description("Block leave messages by ignored users.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> whisperMessages = sgGeneral.add(new BoolSetting.Builder()
        .name("whisper-messages")
        .description("Block whisper messages by ignored users.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> deathMessages = sgGeneral.add(new BoolSetting.Builder()
        .name("death-messages")
        .description("Block death messages by ignored users.")
        .defaultValue(false)
        .build()
    );

    public IgnoreModule() {
        super(IgnoreAddon.IGNORE, "ignore-module", "Stops showing messages from people in the ignore list. Add and remove people with the ignore command.");
    }

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event) {
        String message = event.getMessage().getString();
        String name = IgnoreUtils.getName(message);

        if (name == null || !IgnoreUtils.isIgnored(name)) return;

        if (message.contains("»")) {
            event.cancel();
            return;
        }

        if (joinMessages.get() && message.matches(name + " joined\\.")) {
            event.cancel();
            return;
        }

        if (leaveMessages.get() && message.matches(name + " quit\\.")) {
            event.cancel();
            return;
        }

        if (whisperMessages.get()) {
            String fixed = message.length() > 2 ? message.substring(2) : "";
            if (fixed.startsWith(name + " whispers:")) {
                event.cancel();
                return;
            }
        }

        if (deathMessages.get() && (message.startsWith(name + " died.") || message.startsWith(name + " was utterly destroyed by"))) {
            event.cancel();
        }
    }
}
