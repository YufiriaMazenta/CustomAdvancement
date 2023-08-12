package com.github.yufiriamazenta.customadvancement.cmd.sub;

import com.github.yufiriamazenta.customadvancement.AdvancementManager;
import com.github.yufiriamazenta.customadvancement.CustomAdvancement;
import crypticlib.command.ISubCommand;
import crypticlib.platform.IPlatform;
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
        if (CustomAdvancement.getInstance().getPlatform().getPlatform().equals(IPlatform.Platform.FOLIA)) {
            MsgUtil.info("&cUnable to reload advancements on the folia server, you need to restart the server to reload advancements");
        } else {
            AdvancementManager.reloadAdvancements();
        }
        MsgUtil.info(CustomAdvancement.getInstance().getConfig().getString("command.reload_success", "command.reload_success"));
        return true;
    }

    @Override
    public String getSubCommandName() {
        return "reload";
    }

    @Override
    public String getPerm() {
        return "customadvancement.command.reload";
    }

    @Override
    public void setPerm(String perm) {}

    @Override
    public @NotNull Map<String, ISubCommand> getSubCommands() {
        return subCommandMap;
    }

}
