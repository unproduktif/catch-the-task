import java.awt.*;

public class Theme {

    public static final Color PIXEL_BG_PANEL     = Color.decode("#050814");
    public static final Color PIXEL_BORDER_BLACK = Color.decode("#000000");
    public static final Color PIXEL_SHADOW_DARK  = Color.decode("#101010");

    public static final Color NEON_CYAN   = new Color(0, 255, 255);
    public static final Color NEON_PURPLE = new Color(180, 0, 255);
    public static final Color NEON_PINK   = new Color(255, 40, 150);
    public static final Color NEON_YELLOW = new Color(255, 220, 120);

    public static final Color PIXEL_BUTTON_ORANGE = new Color(255, 136, 0);
    public static final Color PIXEL_BUTTON_HOVER  = new Color(255, 180, 80);

    public static final Color PIXEL_SCORE_BG   = new Color(10, 15, 30, 220);
    public static final Color PIXEL_SCORE_TEXT = NEON_CYAN;

    public static final Color PIXEL_GO_BG    = new Color(5, 5, 15, 225);
    public static final Color PIXEL_GO_ERROR = NEON_PINK;

    public static Font PIXEL(int size) {
        return PixelFont.load(size);
    }
}
