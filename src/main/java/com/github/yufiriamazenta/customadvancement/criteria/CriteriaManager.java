package com.github.yufiriamazenta.customadvancement.criteria;

import com.github.yufiriamazenta.customadvancement.criteria.trigger.CriteriaChat;
import com.github.yufiriamazenta.customadvancement.criteria.trigger.ICriteria;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum CriteriaManager {

    INSTANCE;
    private final Map<String, Function<Map<String, Object>, ICriteria<?>>> criteriaProviderMap;
    private final Map<String, Map<String, ICriteria<?>>> advancementCriteriaMap;

    CriteriaManager() {
        criteriaProviderMap = new HashMap<>();
        advancementCriteriaMap = new HashMap<>();
        loadDefCriteriaTrigger();
    }

    public void loadAdvancementCriteria(String advancementKey, Map<String, Object> criteria) {
        for (String criteriaName : criteria.keySet()) {
            Map<String, ICriteria<?>> advancementCriteria = new HashMap<>();
            if (criteria.get(criteriaName) instanceof Map) {
                Map<String, Object> criteriaMap = (Map<String, Object>) criteria.get(criteriaName);
                String triggerName = (String) criteriaMap.get("trigger");
                Function<Map<String, Object>, ICriteria<?>> criteriaProvider = getCriteriaProvider(triggerName);
                ICriteria<?> criteriaObj = criteriaProvider != null? criteriaProvider.apply((Map<String, Object>) criteriaMap.get("conditions")) : null;
                if (criteriaObj != null) {
                    advancementCriteria.put(criteriaName, criteriaObj);
                }
            }
            advancementCriteriaMap.put(advancementKey, advancementCriteria);
        }
    }

    private void loadDefCriteriaTrigger() {
        regCriteriaProvider("chat", CriteriaChat::new);
    }

    public Function<Map<String, Object>, ICriteria<?>> getCriteriaProvider(String criteriaName) {
        return criteriaProviderMap.get(criteriaName);
    }

    public boolean regCriteriaProvider(String criteriaName, Function<Map<String, Object>, ICriteria<?>> criteriaProvider, boolean force) {
        if (criteriaProviderMap.containsKey(criteriaName)) {
            if (!force)
                return false;
        }
        criteriaProviderMap.put(criteriaName, criteriaProvider);
        return true;
    }

    public boolean regCriteriaProvider(String criteriaName, Function<Map<String, Object>, ICriteria<?>> triggerProvider) {
        return regCriteriaProvider(criteriaName, triggerProvider, false);
    }

    public Map<String, Map<String, ICriteria<?>>> getAdvancementCriteriaMap() {
        return advancementCriteriaMap;
    }

}
