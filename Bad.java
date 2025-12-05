import java.awt.Image;
import java.util.Random;

public class Bad extends Task {

    private int zigzag = 0;
    private int tick = 0;
    private static final Random rand = new Random();

    public Bad(int x, int y, int w, int h, int speed, int poin, Image img) {
        super(x, y, w, h, speed, poin, img);
    }

    @Override
    protected void movement() {
        y += speed;

        tick++;

        if (tick % 4 == 0)
            zigzag = (rand.nextBoolean() ? -1 : 1) * (1 + rand.nextInt(3));  

        x += zigzag;

        if (tick % 5 == 0) {
            x += rand.nextInt(3) - 1;
            y += rand.nextInt(2);
        }

        if (x < 0) x = 0;
        if (x + w > 900) x = 900 - w;
    }


    @Override
    public void onCatch() {
        super.shake(6);  

        for (int i = 0; i < 3; i++) {
            x += rand.nextInt(7) - 3; 
            y += rand.nextInt(5) - 2;
        }
    }


    @Override
    public void onMiss() {
    }
}
