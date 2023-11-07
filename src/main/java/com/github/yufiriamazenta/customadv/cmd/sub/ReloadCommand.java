package com.github.yufiriamazenta.customadv.cmd.sub;

import com.github.yufiriamazenta.customadv.CustomAdvancement;
import com.github.yufiriamazenta.customadv.loader.AdvancementLoader;
import crypticlib.command.ISubCmdExecutor;
import crypticlib.util.MsgUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ReloadCommand implements ISubCmdExecutor {

    INSTANCE;

    private final Map<String, ISubCmdExecutor> subCommandMap = new ConcurrentHashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        CustomAdvancement.getInstance().reloadConfig();
        CustomAdvancement.getInstance().reloadNamespace();
        CustomAdvancement.getInstance().getLangFile().reloadConfig();
        AdvancementLoader.INSTANCE.reloadAdvancements();
        MsgUtil.sendLang(
                sender,
                CustomAdvancement.getInstance().getLangFile().config(),
                "command.reload_success",
                Map.of("%prefix%", CustomAdvancement.getInstance().getPrefix()));
        return true;
    }

    @Override
    public String subCommandName() {
        return "reload";
    }

    @Override
    public String permission() {
        return "custom_advancement.command.reload";
    }

    @Override
    public @NotNull Map<String, ISubCmdExecutor> subCommands() {
        return subCommandMap;
    }

}
