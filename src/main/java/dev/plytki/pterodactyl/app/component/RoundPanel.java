package dev.plytki.pterodactyl.app.component;

import lombok.Getter;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/**
 * A custom JPanel with rounded corners. Allows specifying different
 * corner radii for each corner.
 */
@Getter
public class RoundPanel extends JPanel {

    private int roundTopLeft = 0;
    private int roundTopRight = 0;
    private int roundBottomLeft = 0;
    private int roundBottomRight = 0;

    /**
     * Default constructor. Initializes with opaque set to false.
     */
    public RoundPanel() {
        setOpaque(false);
    }

    /**
     * Constructor with specified corner radii.
     *
     * @param roundTopLeft     Radius for the top left corner.
     * @param roundTopRight    Radius for the top right corner.
     * @param roundBottomLeft  Radius for the bottom left corner.
     * @param roundBottomRight Radius for the bottom right corner.
     */
    public RoundPanel(int roundTopLeft, int roundTopRight, int roundBottomLeft, int roundBottomRight) {
        setOpaque(false);
        this.roundTopLeft = roundTopLeft;
        this.roundTopRight = roundTopRight;
        this.roundBottomLeft = roundBottomLeft;
        this.roundBottomRight = roundBottomRight;
    }

    /**
     * Sets the radius for the top left corner and repaints the panel.
     *
     * @param roundTopLeft The new radius for the top left corner.
     */
    public void setRoundTopLeft(int roundTopLeft) {
        this.roundTopLeft = roundTopLeft;
        repaint();
    }

    /**
     * Sets the radius for the top right corner and repaints the panel.
     *
     * @param roundTopRight The new radius for the top right corner.
     */
    public void setRoundTopRight(int roundTopRight) {
        this.roundTopRight = roundTopRight;
        repaint();
    }

    /**
     * Sets the radius for the bottom left corner and repaints the panel.
     *
     * @param roundBottomLeft The new radius for the bottom left corner.
     */
    public void setRoundBottomLeft(int roundBottomLeft) {
        this.roundBottomLeft = roundBottomLeft;
        repaint();
    }

    /**
     * Sets the radius for the bottom right corner and repaints the panel.
     *
     * @param roundBottomRight The new radius for the bottom right corner.
     */
    public void setRoundBottomRight(int roundBottomRight) {
        this.roundBottomRight = roundBottomRight;
        repaint();
    }

    /**
     * Paints the component with rounded corners.
     *
     * @param graphics The Graphics object to protect.
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        Area area = new Area(createRoundTopLeft());
        if (roundTopRight > 0) {
            area.intersect(new Area(createRoundTopRight()));
        }
        if (roundBottomLeft > 0) {
            area.intersect(new Area(createRoundBottomLeft()));
        }
        if (roundBottomRight > 0) {
            area.intersect(new Area(createRoundBottomRight()));
        }
        g2.fill(area);
        g2.dispose();
        super.paintComponent(graphics);
    }

    /**
     * Creates a shape with rounded top left corner.
     *
     * @return The Shape with rounded top left corner.
     */
    private Shape createRoundTopLeft() {
        int width = getWidth();
        int height = getHeight();
        int roundX = Math.min(width, roundTopLeft);
        int roundY = Math.min(height, roundTopLeft);
        Area area = new Area(new RoundRectangle2D.Double(0, 0, width, height, roundX, roundY));
        area.add(new Area(new Rectangle2D.Double((double) roundX / 2, 0, width - (double) roundX / 2, height)));
        area.add(new Area(new Rectangle2D.Double(0, (double) roundY / 2, width, height - (double) roundY / 2)));
        return area;
    }

    /**
     * Creates a shape with rounded top right corner.
     *
     * @return The Shape with rounded top right corner.
     */
    private Shape createRoundTopRight() {
        int width = getWidth();
        int height = getHeight();
        int roundX = Math.min(width, roundTopRight);
        int roundY = Math.min(height, roundTopRight);
        Area area = new Area(new RoundRectangle2D.Double(0, 0, width, height, roundX, roundY));
        area.add(new Area(new Rectangle2D.Double(0, 0, width - (double) roundX / 2, height)));
        area.add(new Area(new Rectangle2D.Double(0, (double) roundY / 2, width, height - (double) roundY / 2)));
        return area;
    }

    /**
     * Creates a shape with rounded bottom left corner.
     *
     * @return The Shape with rounded bottom left corner.
     */
    private Shape createRoundBottomLeft() {
        int width = getWidth();
        int height = getHeight();
        int roundX = Math.min(width, roundBottomLeft);
        int roundY = Math.min(height, roundBottomLeft);
        Area area = new Area(new RoundRectangle2D.Double(0, 0, width, height, roundX, roundY));
        area.add(new Area(new Rectangle2D.Double((double) roundX / 2, 0, width - (double) roundX / 2, height)));
        area.add(new Area(new Rectangle2D.Double(0, 0, width, height - (double) roundY / 2)));
        return area;
    }

    /**
     * Creates a shape with rounded bottom right corner.
     *
     * @return The Shape with rounded bottom right corner.
     */
    private Shape createRoundBottomRight() {
        int width = getWidth();
        int height = getHeight();
        int roundX = Math.min(width, roundBottomRight);
        int roundY = Math.min(height, roundBottomRight);
        Area area = new Area(new RoundRectangle2D.Double(0, 0, width, height, roundX, roundY));
        area.add(new Area(new Rectangle2D.Double(0, 0, width - (double) roundX / 2, height)));
        area.add(new Area(new Rectangle2D.Double(0, 0, width, height - (double) roundY / 2)));
        return area;
    }
}
