package com.colisiones;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Main game/simulation panel — renders the scene and drives the game loop.
 */
public class GamePanel extends JPanel implements KeyListener {

    // ── Scene ─────────────────────────────────────────────────────────────────
    public static final int W = 860, H = 480;

    private final List<Obstacle> obstacles = new ArrayList<>();
    private GameObject player;
    private PhysicsEngine physics;

    // Keys
    private boolean leftDown, rightDown;

    // State
    private boolean collisionHappening = false;
    private int     collisionFlash     = 0;

    // Sprites (may be null if images not found)
    private BufferedImage barbieImg, carroImg;

    // ── Constructor ───────────────────────────────────────────────────────────

    public GamePanel() {
        setPreferredSize(new Dimension(W, H));
        setBackground(new Color(18, 20, 30));
        setFocusable(true);
        addKeyListener(this);

        loadImages();
        buildScene();

        physics = new PhysicsEngine(W, H);

        // Game loop ~60 fps
        Timer timer = new Timer(16, e -> tick());
        timer.start();
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public void setHitbox(HitboxType t) {
        if (player != null) player.hitboxType = t;
    }

    public void setCharacter(CharacterType t) {
        double oldCX = (player != null) ? player.cx() : W / 2.0;
        double oldY  = (player != null) ? player.y    : H - t.height - 10;

        BufferedImage img = (t == CharacterType.BARBIE) ? barbieImg : carroImg;
        HitboxType hb = (player != null) ? player.hitboxType : HitboxType.AABB;

        player = new GameObject(t, img);
        player.hitboxType = hb;
        player.x = oldCX - t.width / 2.0;
        player.y = oldY;
    }

    // ── Scene setup ───────────────────────────────────────────────────────────

    private void buildScene() {
        // Ground is handled by PhysicsEngine bounds
        // Platform 1 – mid left
        obstacles.add(new Obstacle(80,  300, 160, 18, new Color(80, 140, 200), "Plataforma A"));
        // Platform 2 – mid right (with a gap)
        obstacles.add(new Obstacle(380, 230, 140, 18, new Color(80, 200, 140), "Plataforma B"));
        // Tall narrow wall – forces hitbox comparison
        obstacles.add(new Obstacle(600, 200, 22, 280, new Color(200, 120, 80), "Pared estrecha"));
        // Step block
        obstacles.add(new Obstacle(260, 370, 80,  50, new Color(180, 80, 160), "Escalón"));
        // Ceiling shelf
        obstacles.add(new Obstacle(150, 110, 200, 16, new Color(220, 180, 60), "Techo"));

        // Default character
        setCharacter(CharacterType.BARBIE);
        player.x = 40;
        player.y = H - player.height - 20;
    }

    // ── Images ────────────────────────────────────────────────────────────────

    private void loadImages() {
        barbieImg = loadRes("barbie.png");
        carroImg  = loadRes("carro.png");
    }

    private BufferedImage loadRes(String name) {
        try {
            var url = getClass().getResource(name);
            if (url == null) url = getClass().getResource("/" + name);
            if (url != null) return ImageIO.read(url);
        } catch (IOException ignored) {}
        return null;
    }

    // ── Game loop ─────────────────────────────────────────────────────────────

    private void tick() {
        if (player == null) return;

        // Input → velocity
        if (leftDown)  player.vx -= GameObject.MOVE_SPEED * 0.4;
        if (rightDown) player.vx += GameObject.MOVE_SPEED * 0.4;

        physics.step(player, obstacles);

        // Collision feedback
        collisionHappening = false;
        for (Obstacle o : obstacles) {
            if (CollisionEngine.collides(player, o)) {
                collisionHappening = true;
                break;
            }
        }
        if (collisionHappening) collisionFlash = Math.min(collisionFlash + 1, 8);
        else                    collisionFlash = Math.max(collisionFlash - 1, 0);

        repaint();
    }

    // ── Rendering ────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawBackground(g);
        drawObstacles(g);
        if (player != null) {
            drawPlayer(g);
            drawHitbox(g);
        }
        drawCollisionFlash(g);
        drawHUD(g);
    }

    private void drawBackground(Graphics2D g) {
        // Subtle grid
        g.setColor(new Color(255, 255, 255, 12));
        g.setStroke(new BasicStroke(1));
        for (int x = 0; x < W; x += 40) g.drawLine(x, 0, x, H);
        for (int y = 0; y < H; y += 40) g.drawLine(0, y, W, y);

        // Floor line
        g.setColor(new Color(255, 255, 255, 30));
        g.setStroke(new BasicStroke(2));
        g.drawLine(0, H - 1, W, H - 1);
    }

    private void drawObstacles(Graphics2D g) {
        for (Obstacle o : obstacles) {
            // Check if any hitbox overlaps this obstacle
            boolean hit = player != null && CollisionEngine.collides(player, o);

            // Fill
            Color base = o.color;
            Color fill = hit ? base.brighter().brighter() : base.darker();
            g.setColor(fill);
            g.fillRoundRect(o.x, o.y, o.width, o.height, 6, 6);

            // Border
            g.setColor(hit ? Color.WHITE : base);
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(o.x, o.y, o.width, o.height, 6, 6);

            // Label
            g.setFont(new Font("SansSerif", Font.BOLD, 10));
            g.setColor(new Color(255, 255, 255, 180));
            g.drawString(o.label, o.x + 4, o.y - 4);
        }
    }

    private void drawPlayer(Graphics2D g) {
        int px = (int) player.x, py = (int) player.y;
        int pw = player.width,   ph = player.height;

        if (player.hitboxType == HitboxType.OBB) {
            // Draw sprite rotated around center
            Graphics2D g2 = (Graphics2D) g.create();
            g2.rotate(Math.toRadians(player.angle), player.cx(), player.cy());
            drawSprite(g2, px, py, pw, ph);
            g2.dispose();
        } else {
            drawSprite(g, px, py, pw, ph);
        }
    }

    private void drawSprite(Graphics2D g, int px, int py, int pw, int ph) {
        BufferedImage img = player.sprite;
        if (img != null) {
            g.drawImage(img, px, py, pw, ph, null);
        } else {
            // Fallback: colored rect
            g.setColor(new Color(255, 200, 120));
            g.fillRoundRect(px, py, pw, ph, 10, 10);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 11));
            g.drawString(player.type.label, px + 2, py + ph / 2);
        }
    }

    private void drawHitbox(Graphics2D g) {
        Color hitColor  = new Color(255, 80,  80,  200);
        Color safeColor = new Color(80,  255, 160, 200);
        Color col = collisionHappening ? hitColor : safeColor;

        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1, new float[]{6, 3}, 0));

        switch (player.hitboxType) {
            case BOUNDING_SPHERE -> drawSphere(g, col);
            case AABB            -> drawAABB(g, col);
            case OBB             -> drawOBB(g, col);
            case CAPSULE         -> drawCapsule(g, col);
        }
    }

    private void drawSphere(Graphics2D g, Color col) {
        int r  = (int) player.sphereRadius();
        int cx = (int) (player.cx() - r);
        int cy = (int) (player.cy() - r);
        g.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 40));
        g.fillOval(cx, cy, r * 2, r * 2);
        g.setColor(col);
        g.drawOval(cx, cy, r * 2, r * 2);
    }

    private void drawAABB(Graphics2D g, Color col) {
        int px = (int) player.x, py = (int) player.y;
        g.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 40));
        g.fillRect(px, py, player.width, player.height);
        g.setColor(col);
        g.drawRect(px, py, player.width, player.height);
    }

    private void drawOBB(Graphics2D g, Color col) {
        Point2D[] pts = player.obbCorners();
        int[] xs = new int[4], ys = new int[4];
        for (int i = 0; i < 4; i++) {
            xs[i] = (int) pts[i].getX();
            ys[i] = (int) pts[i].getY();
        }
        g.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 40));
        g.fillPolygon(xs, ys, 4);
        g.setColor(col);
        g.drawPolygon(xs, ys, 4);
    }

    private void drawCapsule(Graphics2D g, Color col) {
        double r   = player.capsuleRadius();
        Point2D tp = player.capsuleTop();
        Point2D bp = player.capsuleBot();

        Path2D path = new Path2D.Double();
        // Left side
        path.moveTo(tp.getX() - r, tp.getY());
        path.lineTo(bp.getX() - r, bp.getY());
        // Bottom arc
        path.append(new Arc2D.Double(bp.getX() - r, bp.getY() - r, r * 2, r * 2,
                180, 180, Arc2D.OPEN), true);
        // Right side
        path.lineTo(tp.getX() + r, tp.getY());
        // Top arc
        path.append(new Arc2D.Double(tp.getX() - r, tp.getY() - r, r * 2, r * 2,
                0, 180, Arc2D.OPEN), true);
        path.closePath();

        g.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 40));
        g.fill(path);
        g.setColor(col);
        g.draw(path);
    }

    private void drawCollisionFlash(Graphics2D g) {
        if (collisionFlash > 0) {
            float alpha = (collisionFlash / 8f) * 0.25f;
            g.setColor(new Color(1f, 0.2f, 0.1f, alpha));
            g.fillRect(0, 0, W, H);
        }
    }

    private void drawHUD(Graphics2D g) {
        // Status pill
        String status = collisionHappening ? "⚠ COLISIÓN DETECTADA" : "✓ Sin colisión";
        Color  bgCol  = collisionHappening ? new Color(200, 50, 30, 200)
                                            : new Color(30, 160, 90, 200);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(status);
        int tx = W / 2 - tw / 2;
        g.setColor(bgCol);
        g.fillRoundRect(tx - 10, 10, tw + 20, 26, 14, 14);
        g.setColor(Color.WHITE);
        g.drawString(status, tx, 28);

        // Hitbox description
        if (player != null) {
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.setColor(new Color(255, 255, 255, 160));
            g.drawString(player.hitboxType.description, 8, H - 8);
        }

        // Controls hint
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.setColor(new Color(255, 255, 255, 100));
        g.drawString("← → para mover  |  ESPACIO para saltar", W - 230, H - 8);
    }

    // ── Input ─────────────────────────────────────────────────────────────────

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT,  KeyEvent.VK_A -> leftDown  = true;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> rightDown = true;
            case KeyEvent.VK_SPACE, KeyEvent.VK_W, KeyEvent.VK_UP -> {
                if (player != null && player.onGround)
                    player.vy = GameObject.JUMP_FORCE;
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT,  KeyEvent.VK_A -> leftDown  = false;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> rightDown = false;
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
}
