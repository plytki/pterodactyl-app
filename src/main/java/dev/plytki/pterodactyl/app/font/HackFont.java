package dev.plytki.pterodactyl.app.font;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class HackFont {

    public static final Font REGULAR;
    public static final Font BOLD;
    public static final Font ITALIC;
    public static final Font BOLD_ITALIC;

    static {
        try {
            REGULAR = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/Hack-Regular.ttf"));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        try {
            BOLD = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/Hack-Bold.ttf"));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        try {
            ITALIC = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/Hack-Italic.ttf"));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        try {
            BOLD_ITALIC = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/Hack-BoldItalic.ttf"));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
