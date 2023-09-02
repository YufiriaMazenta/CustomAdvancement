package com.github.yufiriamazenta.customadvancement.criteria;

import com.github.yufiriamazenta.customadvancement.criteria.impl.ChatCriteria;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public enum CriteriaManager {

    INSTANCE;
    private final Map<String, Function<Map<String, Object>, ICriteria<?>>> criteriaConstructorMap;
    private final Map<String, List<List<ICriteria<?>>>> advancementRequirementsMap;

    CriteriaManager() {
        criteriaConstructorMap = new HashMap<>();
        advancementRequirementsMap = new HashMap<>();
        loadDefCriteria();
    }

    public boolean loadAdvancementCriteria(String advancementKey, Map<String, Object> criteria, List<Object> requirementsList) {
        //TODO
        return false;
    }

    private void loadDefCriteria() {
        criteriaConstructorMap.put("chat", ChatCriteria::new);
    }

    public Map<String, Function<Map<String, Object>, ICriteria<?>>> getCriteriaConstructorMap() {
        return criteriaConstructorMap;
    }
}
