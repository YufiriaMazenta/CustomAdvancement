package com.github.yufiriamazenta.customadvancement.criteria.trigger;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.regex.Pattern;

public class CriteriaChat implements ICriteria<AsyncPlayerChatEvent> {

    private final Pattern pattern;

    public CriteriaChat(Map<String, Object> paramMap) {
        if (paramMap.containsKey("pattern")) {
            pattern = Pattern.compile((String) paramMap.get("pattern"));
        } else {
            pattern = null;
        }
    }

    @Override
    public boolean check(AsyncPlayerChatEvent event) {
        if (pattern == null) {
            return true;
        }
        return pattern.matcher(event.getMessage()).find();
    }

}
