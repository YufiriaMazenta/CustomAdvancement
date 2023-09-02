package com.github.yufiriamazenta.customadvancement.criteria.impl;

import com.github.yufiriamazenta.customadvancement.criteria.ICriteria;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.regex.Pattern;

public class ChatCriteria implements ICriteria<AsyncPlayerChatEvent> {

    private final Pattern pattern;

    public ChatCriteria(Map<String, Object> paramMap) {
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
