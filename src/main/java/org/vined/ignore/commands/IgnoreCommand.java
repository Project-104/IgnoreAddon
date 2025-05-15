package org.vined.ignore.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.PlayerListEntryArgumentType;
import meteordevelopment.meteorclient.mixininterface.IChatHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.vined.ignore.IgnoreAddon;
import org.vined.ignore.utils.IgnoreUtils;

import java.util.List;

public class IgnoreCommand extends Command {
    public IgnoreCommand() {
        super("ignore", "Ignore people on 6b6t");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("add").then(argument("player", PlayerListEntryArgumentType.create()).executes(ctx -> {
            PlayerListEntry entry = ctx.getArgument("player", PlayerListEntry.class);
            String username = entry.getProfile().getName();

            IgnoreUtils.IgnoreResult result = IgnoreUtils.ignore(username);

            switch (result) {
                case SUCCESS -> IgnoreAddon.sendMsg("Ignored " + username + ".");
                case ALREADY_IGNORED -> IgnoreAddon.sendMsg(username + " is already ignored.");
                case ERROR -> IgnoreAddon.sendMsg(Formatting.RED + "Failed to ignore " + Formatting.WHITE + username + Formatting.RED + ".");
            }

            return SINGLE_SUCCESS;
        })));

        builder.then(literal("remove").then(argument("player", StringArgumentType.word())
                .suggests(IGNORED_USERS)
                .executes(ctx -> {
                    String name = StringArgumentType.getString(ctx, "player");
                    if (IgnoreUtils.unignore(name)) {
                        IgnoreAddon.sendMsg("Removed " + name + " from the ignore list");
                    } else {
                        IgnoreAddon.sendMsg(Formatting.RED + "Failed to remove " + Formatting.WHITE + name + Formatting.RED + " from the ignore list");
                    }
                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("view").executes(ctx -> {
            List<String> ignoreList = IgnoreUtils.getIgnored();
            if (ignoreList.size() > 100) {
                IgnoreAddon.sendMsg(Formatting.RED + "The ignore list has more than 100 usernames.");
                IgnoreAddon.sendMsg("You can find and view the ignore list at this path: " + Formatting.GREEN + IgnoreAddon.IGNORE_FILE.toString());
            } else {
                if (ignoreList.isEmpty()) {
                    IgnoreAddon.sendMsg("The ignore list is empty.");
                } else {
                    for (int i = 0; i < ignoreList.size(); i++) {
                        String username = ignoreList.get(i);
                        IgnoreAddon.sendMsg((i + 1) + ". " + username);
                    }
                }
            }
            return SINGLE_SUCCESS;
        }));
    }

    private static final SuggestionProvider<CommandSource> IGNORED_USERS = (context, builder) -> {
        for (String name : IgnoreUtils.getIgnored()) {
            builder.suggest(name);
        }
        return builder.buildFuture();
    };
}
