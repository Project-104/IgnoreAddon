package org.vined.ignore.utils;

import org.vined.ignore.IgnoreAddon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IgnoreUtils {
    public static IgnoreResult ignore(String username) {
        try {
            if (isIgnored(username)) return IgnoreResult.ALREADY_IGNORED;
            Files.write(
                IgnoreAddon.IGNORE_FILE.toPath(),
                Collections.singletonList(username + System.lineSeparator()),
                StandardOpenOption.APPEND,
                StandardOpenOption.CREATE
            );
            return IgnoreResult.SUCCESS;
        } catch (IOException e) {
            return IgnoreResult.ERROR;
        }
    }

    public static boolean unignore(String username) {
        try {
            List<String> usernames = Files.readAllLines(IgnoreAddon.IGNORE_FILE.toPath());
            boolean unignored = usernames.removeIf(line -> line.equals(username));
            if (unignored) Files.write(IgnoreAddon.IGNORE_FILE.toPath(), usernames);
            return unignored;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isIgnored(String username) {
        List<String> ignoreList = getIgnored();
        if (ignoreList == null) return false;
        return ignoreList.contains(username);
    }

    public static List<String> getIgnored() {
        try {
            return Files.readAllLines(IgnoreAddon.IGNORE_FILE.toPath()).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
        } catch (IOException e) {
            IgnoreAddon.LOG.error(e.getMessage());
        }

        return List.of();
    }


    public static String getName(String message) {
        if (message.startsWith("§d")) { // whispers start with §d
            message = message.length() > 2 ? message.substring(2) : "";
        }

        Pattern chatPattern = Pattern.compile("(?:\\[\\w+(?: \\w+)*] )?(\\w+) »");
        Matcher matcher = chatPattern.matcher(message);
        if (matcher.find()) return matcher.group(1);

        Pattern namePattern = Pattern.compile("^(\\w+)(?: joined\\.| quit\\.| whispers:| .+)");
        matcher = namePattern.matcher(message);
        if (matcher.find()) return matcher.group(1);

        return null;
    }

    public enum IgnoreResult {
        SUCCESS,
        ALREADY_IGNORED,
        ERROR
    }
}
