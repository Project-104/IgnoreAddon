package org.vined.ignore;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.mixininterface.IChatHud;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.Module;

import com.mojang.logging.LogUtils;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.reflections.Reflections;
import org.slf4j.Logger;

import java.io.*;
import java.util.Set;

public class IgnoreAddon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category IGNORE = new Category("IgnoreAddon");

    public static File FOLDER = null;
    public static File IGNORE_FILE = null;

    @Override
    public void onInitialize() {
        FOLDER = new File(MeteorClient.FOLDER, "ignore-addon");

        registerModules();
        registerCommands();

        FOLDER.getParentFile().mkdirs();
        FOLDER.mkdir();

        IGNORE_FILE = new File(FOLDER, "ignore_list.txt");
        try {
            if (!IGNORE_FILE.exists()) {
                IGNORE_FILE.createNewFile();
            }
        } catch (IOException e) {
            IgnoreAddon.LOG.error(e.getMessage());
        }
    }

    // this may be bad code, but I forget to add Modules.get().add() every time
    // https://github.com/ck-clarity/internal/blob/607ba5d8e25df3517a705795088e24b2d4753def/src/main/java/clarity/gay/modules/impl/ModuleManager.java#L25C1-L37C10

    private void registerModules() {
        Reflections reflections = new Reflections(getPackage() + ".modules");
        Set<Class<? extends Module>> moduleClasses = reflections.getSubTypesOf(Module.class);
        for (Class<? extends Module> clazz : moduleClasses) {
            try {
                Module module = clazz.getDeclaredConstructor().newInstance();
                Modules.get().add(module);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void registerCommands() {
        Reflections reflections = new Reflections(getPackage() + ".commands");
        Set<Class<? extends Command>> commandClasses = reflections.getSubTypesOf(Command.class);
        for (Class<? extends Command> clazz : commandClasses) {
            try {
                Command command = clazz.getDeclaredConstructor().newInstance();
                Commands.add(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendMsg(String message) {
        ((IChatHud) MeteorClient.mc.inGameHud.getChatHud()).meteor$add(Text.of(Formatting.DARK_GRAY + "[" + Formatting.YELLOW + "IgnoreAddon" + Formatting.DARK_GRAY + "] " + Formatting.WHITE).copy().append(message), 0);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(IGNORE);
        LOG.info("Loaded IgnoreAddon");
    }

    @Override
    public String getPackage() {
        return "org.vined.ignore";
    }
}
