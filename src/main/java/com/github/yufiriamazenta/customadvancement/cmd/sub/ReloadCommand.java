package com.github.yufiriamazenta.customadvancement.cmd.sub;

import com.github.yufiriamazenta.customadvancement.loader.AdvancementLoader;
import com.github.yufiriamazenta.customadvancement.CustomAdvancement;
import crypticlib.command.ISubCommand;
import crypticlib.util.MsgUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ReloadCommand implements ISubCommand {

    INSTANCE;

    private final Map<String, ISubCommand> subCommandMap = new ConcurrentHashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        CustomAdvancement.getInstance().reloadConfig();
        CustomAdvancement.getInstance().getLangFile().reloadConfig();
        AdvancementLoader.INSTANCE.reloadAdvancements();
        MsgUtil.sendLang(
                sender,
                CustomAdvancement.getInstance().getLangFile().getConfig(),
                "command.reload_success",
                Map.of("%prefix%", CustomAdvancement.getInstance().getPrefix()));
        return true;
    }

    @Override
    public String getSubCommandName() {
        return "reload";
    }

    @Override
    public String getPerm() {
        return "custom_advancement.command.reload";
    }

    @Override
    public void setPerm(String perm) {}

    @Override
    public @NotNull Map<String, ISubCommand> getSubCommands() {
        return subCommandMap;
    }

}
