import java.awt.Graphics;
import java.awt.Image;

public class Hero implements Movement {
    private int x;
    private int y;
    private int width;
    private int height;
    private int speed;
    private Image heroImage;

    public Hero(int x, int y, int width, int height, int speed, Image heroImage) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.heroImage = heroImage;
    }

    @Override
    public void doMovement() {
    }

    public void Draw(Graphics g) {
        g.drawImage(heroImage, x, y, width, height, null);
    }
    
    public boolean isCatch(Task task) {
        return (task.getX() < this.x + this.width &&
                task.getX() + task.getWidth() > this.x &&
                task.getY() + task.getHeight() > this.y &&
                task.getY() < this.y + this.height);
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getSpeed() { return speed; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}
