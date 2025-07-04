package com.ruinscraft.panilla.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class PanillaCommand implements CommandExecutor {

    private final PanillaPlugin panillaPlugin;

    public PanillaCommand(PanillaPlugin panillaPlugin) {
        this.panillaPlugin = panillaPlugin;
    }

    private boolean showInfo(CommandSender sender) {
        String version = panillaPlugin.getDescription().getVersion();
        List<String> authors = panillaPlugin.getDescription().getAuthors();
        sender.sendMessage(ChatColor.GOLD + "Running PanillaX version " + version + " by " + String.join(", ", authors));
        return true;
    }

    private boolean showDebug(CommandSender sender) {
        List<String> debugInfo = new ArrayList<>();
        debugInfo.add(ChatColor.BOLD + "=== PanillaX Debug ===");
        debugInfo.add("Bukkit version: " + Bukkit.getVersion());
        debugInfo.add("PanillaX version: " + panillaPlugin.getDescription().getVersion());
        debugInfo.add("Java version: " + System.getProperty("java.version"));
        List<String> pluginNames = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            pluginNames.add(plugin.getName());
        }
        debugInfo.add("Plugins: [" + String.join(", ", pluginNames) + "]");
        for (String line : debugInfo) {
            sender.sendMessage(line);
        }
        return true;
    }

    private final String noPerm = ChatColor.RED + "You don't have permission for this command.";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            if (sender.hasPermission("panilla.command")) {
                return showInfo(sender);
            } else {
                sender.sendMessage(noPerm);
            }
        } else if (args[0].equalsIgnoreCase("debug")) {
            if (sender.hasPermission("panilla.command.debug")) {
                return showDebug(sender);
            } else {
                sender.sendMessage(noPerm);
            }
        }

        return true;
    }

}
