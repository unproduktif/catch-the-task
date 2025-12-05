import java.awt.*;

public abstract class Task implements Movement{

    protected int x, y, w, h;
    protected int speed;
    protected int poin;
    protected Image img;

    private int shakeOffsetX = 0;
    private int shakeOffsetY = 0;

    public Task(int x, int y, int w, int h, int speed, int poin, Image img) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.speed = speed;
        this.poin = poin;
        this.img = img;
    }

    public void doMovement() {
        update();
    }

    public void update() {
        movement();
        pixelEffect();
    }

    protected abstract void movement();
    protected void pixelEffect() {}

    public void onCatch() {}
    public void onMiss() {}

    protected void shake(int power) {
        shakeOffsetX = (int)(Math.random() * power - power/2);
        shakeOffsetY = (int)(Math.random() * power - power/2);
    }

    public void Draw(Graphics2D g2) {
        int drawX = x + shakeOffsetX;
        int drawY = y + shakeOffsetY;

        if (img != null) {
            g2.drawImage(img, drawX, drawY, w, h, null);
        } else {
            g2.setColor(Color.RED);
            g2.fillRect(drawX, drawY, w, h);
        }

        shakeOffsetX = 0;
        shakeOffsetY = 0;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth()  { return w; }
    public int getHeight() { return h; }
    public int getPoin() { return poin; }
}
