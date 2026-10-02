package com.colisiones;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * Represents the player-controlled character with position, velocity, and hitbox.
 */
public class GameObject {

    // World position (top-left corner)
    public double x, y;
    public double vx, vy; // velocity
    public boolean onGround;

    // Logical dimensions
    public int width, height;

    // Rotation angle (degrees) – used by OBB
    public double angle = 0;

    public BufferedImage sprite;
    public CharacterType type;
    public HitboxType hitboxType = HitboxType.AABB;

    // Gravity & physics constants
    public static final double GRAVITY    = 0.55;
    public static final double JUMP_FORCE = -13.0;
    public static final double MOVE_SPEED = 4.5;
    public static final double FRICTION   = 0.78;

    public GameObject(CharacterType type, BufferedImage sprite) {
        this.type   = type;
        this.sprite = sprite;
        this.width  = type.width;
        this.height = type.height;
    }

    /** Center X */
    public double cx() { return x + width / 2.0; }
    /** Center Y */
    public double cy() { return y + height / 2.0; }

    // ── Hitbox helpers ───────────────────────────────────────────────────────

    /** Radius for Bounding Sphere */
    public double sphereRadius() {
        return Math.max(width, height) / 2.0;
    }

    /** AABB rectangle */
    public Rectangle2D aabb() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    /**
     * Capsule: axis along the vertical center, radius = width/2,
     * segment from top-center to bottom-center minus the radius cap.
     */
    public double capsuleRadius() { return width / 2.0; }
    public Point2D capsuleTop()   { return new Point2D.Double(cx(), y + capsuleRadius()); }
    public Point2D capsuleBot()   { return new Point2D.Double(cx(), y + height - capsuleRadius()); }

    /** OBB corners in world space (rotated around center) */
    public Point2D[] obbCorners() {
        double hw = width  / 2.0;
        double hh = height / 2.0;
        double rad = Math.toRadians(angle);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        double[][] local = { {-hw,-hh},{hw,-hh},{hw,hh},{-hw,hh} };
        Point2D[] pts = new Point2D[4];
        for (int i = 0; i < 4; i++) {
            double lx = local[i][0], ly = local[i][1];
            pts[i] = new Point2D.Double(cx() + lx*cos - ly*sin,
                                        cy() + lx*sin + ly*cos);
        }
        return pts;
    }

    public void update() {
        vy += GRAVITY;
        x  += vx;
        y  += vy;
        vx *= FRICTION;

        // Slow rotation for OBB when moving
        if (hitboxType == HitboxType.OBB) {
            angle += vx * 0.8;
        } else {
            angle = 0;
        }
    }
}
