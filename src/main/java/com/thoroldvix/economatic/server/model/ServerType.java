package com.thoroldvix.economatic.server.model;

public enum ServerType {
    PVE,
    PVP,
    RP,
    PVP_RP;
    @Override
    public String toString() {
        String typeName = this.name().replace("_", " ");
        return typeName.replace("V", "v");
    }
}
