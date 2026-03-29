package com.bizflow.knowledge.api;

import jakarta.validation.constraints.NotBlank;

public class KnowledgeSearchRequest {
    @NotBlank
    private String query;
    private int limit = 5;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
