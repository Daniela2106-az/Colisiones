package com.colisiones;

public enum HitboxType {
    BOUNDING_SPHERE("Bounding Sphere", "Esfera envolvente — baja complejidad, rápida pero imprecisa"),
    AABB("AABB", "Axis-Aligned Bounding Box — caja sin rotación, eficiente"),
    OBB("OBB", "Oriented Bounding Box — caja que rota con el objeto"),
    CAPSULE("Capsule", "Cápsula — cilindro con semiesferas, ideal para personajes");

    public final String label;
    public final String description;

    HitboxType(String label, String description) {
        this.label = label;
        this.description = description;
    }
}
