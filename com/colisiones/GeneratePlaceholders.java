package com.colisiones;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utility to generate placeholder images if barbie.png / carro.png are missing.
 * Run once to create them in the resources folder.
 */
public class GeneratePlaceholders {

    public static void main(String[] args) throws Exception {
        String base = "src/com/colisiones/";

        // Barbie – tall & slim (magenta silhouette)
        BufferedImage barbie = new BufferedImage(40, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = barbie.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Body
        g.setColor(new Color(230, 80, 160));
        g.fillRoundRect(10, 30, 20, 55, 8, 8);
        // Head
        g.setColor(new Color(255, 200, 150));
        g.fillOval(10, 4, 20, 22);
        // Hair
        g.setColor(new Color(255, 220, 80));
        g.fillRoundRect(8, 2, 24, 14, 10, 10);
        // Legs
        g.setColor(new Color(200, 60, 130));
        g.fillRoundRect(10, 82, 8, 18, 4, 4);
        g.fillRoundRect(22, 82, 8, 18, 4, 4);
        // Arms
        g.fillRoundRect(3,  32, 7, 30, 4, 4);
        g.fillRoundRect(30, 32, 7, 30, 4, 4);
        g.dispose();
        ImageIO.write(barbie, "PNG", new File(base + "barbie.png"));
        System.out.println("Created barbie.png");

        // Carro – wide & short (blue car silhouette)
        BufferedImage carro = new BufferedImage(90, 55, BufferedImage.TYPE_INT_ARGB);
        g = carro.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Body lower
        g.setColor(new Color(60, 120, 220));
        g.fillRoundRect(2, 28, 86, 22, 10, 10);
        // Cabin
        g.setColor(new Color(80, 150, 250));
        g.fillRoundRect(18, 10, 54, 22, 10, 10);
        // Windshields
        g.setColor(new Color(180, 230, 255, 200));
        g.fillRoundRect(22, 12, 22, 16, 6, 6);
        g.fillRoundRect(46, 12, 22, 16, 6, 6);
        // Wheels
        g.setColor(new Color(30, 30, 30));
        g.fillOval(8,  36, 20, 20);
        g.fillOval(62, 36, 20, 20);
        g.setColor(new Color(160, 160, 160));
        g.fillOval(12, 40, 12, 12);
        g.fillOval(66, 40, 12, 12);
        // Headlights
        g.setColor(new Color(255, 240, 120));
        g.fillOval(78, 31, 8, 8);
        g.setColor(new Color(255, 60, 60));
        g.fillOval(4,  31, 8, 8);
        g.dispose();
        ImageIO.write(carro, "PNG", new File(base + "carro.png"));
        System.out.println("Created carro.png");
    }
}
