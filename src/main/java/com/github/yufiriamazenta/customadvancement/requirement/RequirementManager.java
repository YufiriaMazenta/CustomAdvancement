package com.github.yufiriamazenta.customadvancement.requirement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum RequirementManager {

    INSTANCE;

    private final Map<String, List<List<String>>> advancementRequirementMap;

    RequirementManager() {
        advancementRequirementMap = new ConcurrentHashMap<>();
    }

    public void loadAdvancementRequirements(String advancementKey,  Map<String, Object> criteria) {
        List<String> requirements = new ArrayList<>(criteria.keySet());
        List<List<String>> requirementList = new ArrayList<>();
        requirementList.add(requirements);
        advancementRequirementMap.put(advancementKey, requirementList);
    }

    public void loadAdvancementRequirements(String advancementKey, List<List<String>> requirements) {
        advancementRequirementMap.put(advancementKey, requirements);
    }

    public Map<String, List<List<String>>> getAdvancementRequirementMap() {
        return advancementRequirementMap;
    }

    public boolean checkAdvancement() {

    }

}
