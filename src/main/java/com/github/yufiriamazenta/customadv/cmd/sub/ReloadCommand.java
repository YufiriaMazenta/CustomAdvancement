package com.github.yufiriamazenta.customadv.cmd.sub;

import com.github.yufiriamazenta.customadv.CustomAdvancement;
import com.github.yufiriamazenta.customadv.loader.AdvancementLoader;
import crypticlib.command.api.ISubcmdExecutor;
import crypticlib.command.impl.SubcmdExecutor;
import crypticlib.util.MsgUtil;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReloadCommand extends SubcmdExecutor {

    public static final ReloadCommand INSTANCE = new ReloadCommand();

    private final Map<String, ISubcmdExecutor> subCommandMap = new ConcurrentHashMap<>();

    public ReloadCommand() {
        super("reload", "custom_advancement.command.reload");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        CustomAdvancement.getInstance().reloadConfig();
        CustomAdvancement.getInstance().reloadNamespace();
        CustomAdvancement.getInstance().getLangFile().reloadConfig();
        AdvancementLoader.INSTANCE.reloadAdvancements();
        MsgUtil.sendMsg(
                sender,
                CustomAdvancement.getInstance().getLangFile().config().getString("command.reload_success", "command.reload_success"),
                Map.of("%prefix%", CustomAdvancement.getInstance().getPrefix()));
        return true;
    }

}
