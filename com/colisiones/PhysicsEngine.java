package com.colisiones;

import java.util.List;

/**
 * Applies gravity, resolves collisions, and enforces scene bounds.
 */
public class PhysicsEngine {

    private final int sceneW, sceneH;

    public PhysicsEngine(int sceneW, int sceneH) {
        this.sceneW = sceneW;
        this.sceneH = sceneH;
    }

    public void step(GameObject g, List<Obstacle> obstacles) {
        g.update();
        resolveCollisions(g, obstacles);
        clampToScene(g);
    }

    private void resolveCollisions(GameObject g, List<Obstacle> obstacles) {
        g.onGround = false;
        for (Obstacle o : obstacles) {
            if (!CollisionEngine.collides(g, o)) continue;

            // Simple push-out: find smallest overlap axis using AABB approach
            // (works well enough for rectangular obstacles in all hitbox modes)
            double overlapLeft   =  (g.x + g.width)  - o.x;
            double overlapRight  =  (o.x + o.width)  - g.x;
            double overlapTop    =  (g.y + g.height) - o.y;
            double overlapBottom =  (o.y + o.height) - g.y;

            double minOverlapX = Math.min(overlapLeft,  overlapRight);
            double minOverlapY = Math.min(overlapTop,   overlapBottom);

            if (minOverlapX < minOverlapY) {
                // Horizontal resolution
                if (overlapLeft < overlapRight) g.x -= overlapLeft;
                else                            g.x += overlapRight;
                g.vx = 0;
            } else {
                // Vertical resolution
                if (overlapTop < overlapBottom) {
                    g.y -= overlapTop;
                    g.vy = 0;
                    g.onGround = true;
                } else {
                    g.y += overlapBottom;
                    g.vy = 0;
                }
            }
        }
    }

    private void clampToScene(GameObject g) {
        int floor = sceneH - g.height;
        if (g.y >= floor) {
            g.y = floor;
            g.vy = 0;
            g.onGround = true;
        }
        if (g.y < 0)         { g.y = 0;           g.vy = 0; }
        if (g.x < 0)         { g.x = 0;           g.vx = 0; }
        if (g.x + g.width > sceneW) { g.x = sceneW - g.width; g.vx = 0; }
    }
}
