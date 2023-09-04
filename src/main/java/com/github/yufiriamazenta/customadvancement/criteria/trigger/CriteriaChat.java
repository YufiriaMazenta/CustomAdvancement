package com.github.yufiriamazenta.customadvancement.criteria.trigger;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.regex.Pattern;

public class CriteriaChat implements ICriteria<AsyncPlayerChatEvent> {

    private final Pattern pattern;
    private final String advancementKey;
    private boolean done;

    public CriteriaChat(String advancementKey, Map<String, Object> paramMap) {
        this.advancementKey = advancementKey;
        done = false;
        if (paramMap.containsKey("pattern")) {
            pattern = Pattern.compile((String) paramMap.get("pattern"));
        } else {
            pattern = null;
        }
    }

    @Override
    public boolean check(AsyncPlayerChatEvent event) {
        if (isDone())
            return true;
        //TODO 从持久化数据中获取是否已经达成
        boolean bool;
        if (pattern == null) {
            bool = true;
        } else {
            bool = pattern.matcher(event.getMessage()).find();
        }
        if (bool)
            setDone(true);
        return done;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String getAdvancementKey() {
        return advancementKey;
    }

}
