package com.vs18.buildpc;

import java.util.List;

public class CompatibilityRule {
    int ruleId;
    boolean is_compatible;
    List<Component> components;

    public CompatibilityRule(int ruleId, boolean is_compatible, List<Component> components) {
        this.ruleId = ruleId;
        this.is_compatible = is_compatible;
        this.components = components;
    }

    public int getRuleId() {
        return ruleId;
    }

    public boolean isIs_compatible() {
        return is_compatible;
    }

    public List<Component> getComponents() {
        return components;
    }
}
