package com.github.yufiriamazenta.customadv;

import com.github.yufiriamazenta.customadv.adv.AbstractAdvancementWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AdvancementsCache {

    private final static Map<String, AbstractAdvancementWrapper> advancementWrapperMap;

    static {
        advancementWrapperMap = new ConcurrentHashMap<>();
    }

    public static Map<String, AbstractAdvancementWrapper> getAdvancementWrapperMap() {
        return advancementWrapperMap;
    }

    public static List<String> getAdvancements() {
        return new ArrayList<>(advancementWrapperMap.keySet());
    }

}
