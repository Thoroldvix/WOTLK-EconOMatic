package com.thoroldvix.pricepal.server;

public enum ServerType {
    PVE,
    PVP,
    RP,
    PVP_RP;
    @Override
    public String toString() {
        String typeName = this.name().replaceAll("_", " ");
        return typeName.replaceAll("V", "v");
    }
}
