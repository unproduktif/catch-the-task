import java.awt.Image;
import java.awt.Graphics;

public abstract class Task implements Movement {

    // ==================== ATTRIBUTES =====================
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int speed;
    protected int poin;
    protected Image imageTask;

    public Task(int x, int y, int width, int height, int speed, int poin, Image imageTask) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.poin = poin;
        this.imageTask = imageTask;
    }
    public abstract void DoCatch(); 

    public void Move() {    
    }

    public void Draw(Graphics g) {
        g.drawImage(imageTask, x, y, width, height, null);
    }

    @Override
    public void doMovement() {
    }
    
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }

    public int getPoin() { return poin; }
    public void setPoin(int poin) { this.poin = poin; }
}
