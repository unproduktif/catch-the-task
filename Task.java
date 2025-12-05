import java.awt.*;

public abstract class Task {

    protected int x, y, w, h;
    protected int speed;
    protected int poin;
    protected Image img;

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

    public abstract void update();

    public void Draw(Graphics2D g2) {
        if (img != null) {
            g2.drawImage(img, x, y, w, h, null);
        } else {
            g2.setColor(Color.RED);
            g2.fillRect(x, y, w, h);
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth()  { return w; }
    public int getHeight() { return h; }

    public int getPoin() { return poin; }
}
