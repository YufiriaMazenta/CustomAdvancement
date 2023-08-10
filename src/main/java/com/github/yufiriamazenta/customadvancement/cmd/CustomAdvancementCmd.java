package com.github.yufiriamazenta.customadvancement.cmd;

import com.github.yufiriamazenta.customadvancement.CustomAdvancement;
import crypticlib.command.IPluginCommand;
import crypticlib.command.ISubCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum CustomAdvancementCmd implements IPluginCommand {

    INSTANCE;
    private final Map<String, ISubCommand> subCommandMap;

    CustomAdvancementCmd() {
        subCommandMap = new ConcurrentHashMap<>();
    }

    @Override
    public Plugin getPlugin() {
        return CustomAdvancement.getInstance();
    }

    @Override
    public @NotNull Map<String, ISubCommand> getSubCommands() {
        return subCommandMap;
    }
}
