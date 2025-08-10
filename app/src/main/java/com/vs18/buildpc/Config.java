package com.vs18.buildpc;

import java.util.List;

public class Config {
    int configId;
    String configName;
    String createdAt;
    List<Component> components;

    public Config(int configId, String configName, String createdAt, List<Component> components) {
        this.configId = configId;
        this.configName = configName;
        this.createdAt = createdAt;
        this.components = components;
    }

    public int getConfigId() { return configId; }
    public String getConfigName() { return configName; }
    public String getCreatedAt() { return createdAt; }
    public List<Component> getComponents() { return components; }
}