package com.vs18.buildpc;

import java.io.*;

public class ComponentItem implements Serializable{

    private int componentId;

    public ComponentItem(int componentId) {
        this.componentId = componentId;
    }

    public int getComponentId() {
        return componentId;
    }

    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

}
