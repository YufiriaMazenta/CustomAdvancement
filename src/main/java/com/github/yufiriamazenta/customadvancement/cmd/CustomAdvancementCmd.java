package com.github.yufiriamazenta.customadvancement.cmd;

import com.github.yufiriamazenta.customadvancement.CustomAdvancement;
import com.github.yufiriamazenta.customadvancement.cmd.sub.GrantCommand;
import com.github.yufiriamazenta.customadvancement.cmd.sub.ReloadCommand;
import com.github.yufiriamazenta.customadvancement.cmd.sub.RevokeCommand;
import crypticlib.annotations.BukkitCommand;
import crypticlib.command.IPluginCommand;
import crypticlib.command.ISubCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@BukkitCommand(command = "customadvancement")
public class CustomAdvancementCmd implements IPluginCommand {

    private final Map<String, ISubCommand> subCommandMap;

    public CustomAdvancementCmd() {
        subCommandMap = new ConcurrentHashMap<>();
        regSubCommand(ReloadCommand.INSTANCE);
        regSubCommand(GrantCommand.INSTANCE);
        regSubCommand(RevokeCommand.INSTANCE);
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
