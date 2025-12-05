import java.awt.Image;

public class Good extends Task {

    public Good(int x, int y, int w, int h, int speed, int poin, Image img) {
        super(x, y, w, h, speed, poin, img);
    }

    @Override
    public void update() {
        y += speed;
    }
}
