package com.colisiones;

import java.awt.geom.*;

/**
 * Contains all collision-detection algorithms used in the demo.
 */
public class CollisionEngine {

    // ── Bounding Sphere ──────────────────────────────────────────────────────

    public static boolean sphereVsRect(GameObject g, Obstacle o) {
        double r  = g.sphereRadius();
        double cx = g.cx(), cy = g.cy();
        // Closest point on rect to sphere center
        double nearX = clamp(cx, o.x, o.x + o.width);
        double nearY = clamp(cy, o.y, o.y + o.height);
        double dx = cx - nearX, dy = cy - nearY;
        return dx*dx + dy*dy <= r*r;
    }

    // ── AABB ─────────────────────────────────────────────────────────────────

    public static boolean aabbVsRect(GameObject g, Obstacle o) {
        return g.x < o.x + o.width  &&
               g.x + g.width > o.x  &&
               g.y < o.y + o.height &&
               g.y + g.height > o.y;
    }

    // ── OBB ──────────────────────────────────────────────────────────────────

    public static boolean obbVsRect(GameObject g, Obstacle o) {
        // SAT: test OBB axes + AABB axes
        Point2D[] corners = g.obbCorners();
        double rad = Math.toRadians(g.angle);
        double cos = Math.cos(rad), sin = Math.sin(rad);

        // Axes to test: OBB local X, OBB local Y, world X, world Y
        double[][] axes = {
            { cos,  sin },   // OBB local X
            {-sin,  cos },   // OBB local Y
            { 1,    0   },   // World X (AABB normal)
            { 0,    1   }    // World Y (AABB normal)
        };

        // AABB corners
        Point2D[] oCorners = {
            new Point2D.Double(o.x,           o.y),
            new Point2D.Double(o.x + o.width, o.y),
            new Point2D.Double(o.x + o.width, o.y + o.height),
            new Point2D.Double(o.x,           o.y + o.height)
        };

        for (double[] axis : axes) {
            double[] pA = project(corners, axis);
            double[] pB = project(oCorners, axis);
            if (pA[1] < pB[0] || pB[1] < pA[0]) return false; // separating axis found
        }
        return true;
    }

    // ── Capsule ───────────────────────────────────────────────────────────────

    public static boolean capsuleVsRect(GameObject g, Obstacle o) {
        // Distance from capsule segment to closest point on rect
        Point2D top = g.capsuleTop();
        Point2D bot = g.capsuleBot();
        double r    = g.capsuleRadius();

        // Closest point on rect to the capsule segment
        double dist2 = segmentToRectDist2(top, bot, o);
        return dist2 <= r * r;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static double[] project(Point2D[] pts, double[] axis) {
        double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
        for (Point2D p : pts) {
            double d = p.getX()*axis[0] + p.getY()*axis[1];
            if (d < min) min = d;
            if (d > max) max = d;
        }
        return new double[]{ min, max };
    }

    /**
     * Squared distance from line segment (p1→p2) to the closest point inside
     * or on the boundary of rectangle o.
     */
    private static double segmentToRectDist2(Point2D p1, Point2D p2, Obstacle o) {
        // Sample the segment and find the minimum squared distance to the rect
        double minD2 = Double.MAX_VALUE;
        int STEPS = 12;
        for (int i = 0; i <= STEPS; i++) {
            double t  = (double) i / STEPS;
            double sx = p1.getX() + t * (p2.getX() - p1.getX());
            double sy = p1.getY() + t * (p2.getY() - p1.getY());
            double nx = clamp(sx, o.x, o.x + o.width);
            double ny = clamp(sy, o.y, o.y + o.height);
            double dx = sx - nx, dy = sy - ny;
            double d2 = dx*dx + dy*dy;
            if (d2 < minD2) minD2 = d2;
        }
        return minD2;
    }

    // ── Dispatch ─────────────────────────────────────────────────────────────

    public static boolean collides(GameObject g, Obstacle o) {
        return switch (g.hitboxType) {
            case BOUNDING_SPHERE -> sphereVsRect(g, o);
            case AABB            -> aabbVsRect(g, o);
            case OBB             -> obbVsRect(g, o);
            case CAPSULE         -> capsuleVsRect(g, o);
        };
    }
}
