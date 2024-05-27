package dev.plytki.pterodactyl.app.util;

import javax.swing.*;
import java.awt.*;

public class ScrollUtils {

    public static void fixScrolling(JScrollPane scrollPane) {
        JLabel systemLabel = new JLabel();
        FontMetrics metrics = systemLabel.getFontMetrics(systemLabel.getFont());
        int lineHeight = metrics.getHeight();
        int charWidth = metrics.getMaxAdvance();

        JScrollBar systemVBar = new JScrollBar(JScrollBar.VERTICAL);
        JScrollBar systemHBar = new JScrollBar(JScrollBar.HORIZONTAL);
        int verticalIncrement = systemVBar.getUnitIncrement();
        int horizontalIncrement = systemHBar.getUnitIncrement();

        scrollPane.getVerticalScrollBar().setUnitIncrement(lineHeight * verticalIncrement);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(charWidth * horizontalIncrement);
    }

}
