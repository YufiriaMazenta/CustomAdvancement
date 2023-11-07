package com.github.yufiriamazenta.customadv.cmd;

import com.github.yufiriamazenta.customadv.CustomAdvancement;
import com.github.yufiriamazenta.customadv.cmd.sub.GrantCommand;
import com.github.yufiriamazenta.customadv.cmd.sub.ReloadCommand;
import com.github.yufiriamazenta.customadv.cmd.sub.RevokeCommand;
import crypticlib.annotations.BukkitCommand;
import crypticlib.command.IPluginCmdExecutor;
import crypticlib.command.ISubCmdExecutor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@BukkitCommand(name = "customadvancement", alias = {"ca", "cadv"}, permission = "custom_advancement.command")
public class CustomAdvancementCmd implements IPluginCmdExecutor {

    private final Map<String, ISubCmdExecutor> subCommandMap;

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
    public @NotNull Map<String, ISubCmdExecutor> subCommands() {
        return subCommandMap;
    }

}
