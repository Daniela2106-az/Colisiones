package com.colisiones;

public enum CharacterType {
    BARBIE("Barbie", "barbie.png", 40, 100),   // slim & tall
    CARRO("Carro",   "carro.png",  90, 55);     // wide & short

    public final String label;
    public final String image;
    public final int width;
    public final int height;

    CharacterType(String label, String image, int width, int height) {
        this.label  = label;
        this.image  = image;
        this.width  = width;
        this.height = height;
    }
}
