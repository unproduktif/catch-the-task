import java.awt.Image;

public class Good extends Task {

    private int jiggle = 0;
    private int jiggleTick = 0;

    public Good(int x, int y, int w, int h, int speed, int poin, Image img) {
        super(x, y, w, h, speed, poin, img);
    }

    @Override
    protected void movement() {
        y += speed;
    }

    @Override
    protected void pixelEffect() {
        jiggleTick++;
        if (jiggleTick % 8 == 0) {
            jiggle = (jiggle == 0) ? 1 : 0;
        }
        x += jiggle;
    }

    @Override
    public void onCatch() {
        shake(2);
    }
}
