import java.awt.Font;
import java.io.File;

public class PixelFont {

    public static Font load(int size) {
        try {
            File fontFile = new File("assets/font/pixel.ttf");
            Font base = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            return base.deriveFont((float) size);
        } catch (Exception e) {
            System.out.println("PixelFont error: " + e.getMessage());
            return new Font("Monospaced", Font.BOLD, size);
        }
    }
}
