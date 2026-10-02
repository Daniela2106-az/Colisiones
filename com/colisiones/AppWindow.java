package com.colisiones;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main application window with the game panel and all controls.
 */
public class AppWindow extends JFrame {

    private final GamePanel gamePanel;

    public AppWindow() {
        super("Detección de Colisiones en Videojuegos — Demo Educativo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        gamePanel = new GamePanel();

        // ── Root layout ───────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(new Color(14, 16, 26));

        root.add(buildHeader(),    BorderLayout.NORTH);
        root.add(gamePanel,        BorderLayout.CENTER);
        root.add(buildControls(),  BorderLayout.EAST);
        root.add(buildInfoBar(),   BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(20, 22, 36));
        p.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("Técnicas de Detección de Colisiones");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(230, 230, 255));

        JLabel sub = new JLabel("Selecciona hitbox y personaje · Usa ←→ y ESPACIO para mover");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(new Color(150, 155, 180));

        p.add(title, BorderLayout.WEST);
        p.add(sub,   BorderLayout.EAST);

        p.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 55, 80)),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        return p;
    }

    // ── Right panel: controls ─────────────────────────────────────────────────

    private JPanel buildControls() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(20, 22, 36));
        p.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(50, 55, 80)),
                BorderFactory.createEmptyBorder(16, 14, 16, 14)));
        p.setPreferredSize(new Dimension(210, 0));

        p.add(sectionLabel("TIPO DE HITBOX"));
        p.add(Box.createVerticalStrut(8));

        ButtonGroup bg = new ButtonGroup();
        for (HitboxType ht : HitboxType.values()) {
            JRadioButton rb = styledRadio(ht.label, ht == HitboxType.AABB);
            rb.addActionListener(e -> {
                gamePanel.setHitbox(ht);
                gamePanel.requestFocusInWindow();
                updateInfoLabel(ht);
            });
            bg.add(rb);
            p.add(rb);
            p.add(Box.createVerticalStrut(4));
        }

        p.add(Box.createVerticalStrut(20));
        p.add(sectionLabel("PERSONAJE"));
        p.add(Box.createVerticalStrut(8));

        ButtonGroup bg2 = new ButtonGroup();
        for (CharacterType ct : CharacterType.values()) {
            JRadioButton rb = styledRadio(ct.label + "  (" + ct.width + "×" + ct.height + "px)",
                    ct == CharacterType.BARBIE);
            rb.addActionListener(e -> {
                gamePanel.setCharacter(ct);
                gamePanel.requestFocusInWindow();
            });
            bg2.add(rb);
            p.add(rb);
            p.add(Box.createVerticalStrut(4));
        }

        p.add(Box.createVerticalStrut(20));
        p.add(sectionLabel("LEYENDA"));
        p.add(Box.createVerticalStrut(8));
        p.add(legendItem(new Color(80, 255, 160),  "Sin colisión"));
        p.add(Box.createVerticalStrut(4));
        p.add(legendItem(new Color(255, 80, 80),   "Colisión activa"));
        p.add(Box.createVerticalStrut(4));
        p.add(legendItem(new Color(80, 140, 200),  "Plataformas / Pared"));

        p.add(Box.createVerticalGlue());
        return p;
    }

    // ── Bottom info bar ───────────────────────────────────────────────────────

    private JLabel infoLabel;

    private JPanel buildInfoBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(14, 16, 26));
        p.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(50, 55, 80)),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));

        infoLabel = new JLabel(HitboxType.AABB.description);
        infoLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        infoLabel.setForeground(new Color(160, 170, 210));
        p.add(infoLabel, BorderLayout.WEST);

        JLabel credit = new JLabel("Presentación Educativa · Detección de Colisiones");
        credit.setFont(new Font("SansSerif", Font.PLAIN, 11));
        credit.setForeground(new Color(90, 95, 120));
        p.add(credit, BorderLayout.EAST);
        return p;
    }

    private void updateInfoLabel(HitboxType ht) {
        if (infoLabel != null) infoLabel.setText(ht.description);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 10));
        l.setForeground(new Color(120, 130, 180));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JRadioButton styledRadio(String text, boolean selected) {
        JRadioButton rb = new JRadioButton(text, selected);
        rb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rb.setForeground(new Color(210, 215, 240));
        rb.setBackground(new Color(20, 22, 36));
        rb.setFocusPainted(false);
        rb.setAlignmentX(Component.LEFT_ALIGNMENT);
        return rb;
    }

    private JPanel legendItem(Color dot, String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setBackground(new Color(20, 22, 36));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel circle = new JLabel("●");
        circle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        circle.setForeground(dot);

        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(new Color(200, 205, 230));

        row.add(circle);
        row.add(label);
        return row;
    }
}
