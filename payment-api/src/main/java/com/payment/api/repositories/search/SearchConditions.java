package com.payment.api.repositories.search;

import java.util.HashMap;
import java.util.Map;

public class SearchConditions {
    private Map<String, Object> conditions;

    public SearchConditions() {
        this.conditions = new HashMap<>();
    }

    public SearchConditions(Map<String, Object> searchArgs) {
        this.conditions = searchArgs;
    }

    public Map<String, ?> getSearchArgs() {
        return conditions;
    }

    public SearchConditions put(String column, Object value) {
        conditions.put(column, value);
        return this;
    }

}