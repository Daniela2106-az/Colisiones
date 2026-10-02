package com.colisiones;

import java.awt.*;

/**
 * A static rectangular obstacle in the scene.
 */
public class Obstacle {
    public int x, y, width, height;
    public Color color;
    public String label;

    public Obstacle(int x, int y, int w, int h, Color color, String label) {
        this.x = x; this.y = y;
        this.width = w; this.height = h;
        this.color = color;
        this.label = label;
    }

    public Rectangle bounds() {
        return new Rectangle(x, y, width, height);
    }
}
