import java.awt.Graphics;
import java.awt.Image;

public class Hero implements Movement {

    private int x, y;
    private int width, height;
    private int speed;

    private Image heroRight;
    private Image heroLeft;
    private Image heroIdle;
    private Image currentImage;
    private final GamePage gamePage;
    public Hero(int x, int y, int width, int height, int speed,
                Image heroRight, Image heroLeft, Image heroIdle,
                GamePage gamePage) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.heroRight = heroRight;
        this.heroLeft  = heroLeft;
        this.heroIdle  = heroIdle;
        this.currentImage = (heroIdle != null) ? heroIdle : heroRight;
        this.gamePage = gamePage;
    }

    @Override
    public void doMovement() {
        if (gamePage == null) return;

        int newX = x;

        boolean movingLeft  = gamePage.isLeftPressed();
        boolean movingRight = gamePage.isRightPressed();

        if (movingLeft && !movingRight) {
            newX -= speed;
            currentImage = (heroLeft != null) ? heroLeft : currentImage;
        } 
        else if (movingRight && !movingLeft) {
            newX += speed;
            currentImage = (heroRight != null) ? heroRight : currentImage;
        } 
        else {
            if (heroIdle != null) currentImage = heroIdle;
        }

        int panelW = gamePage.getWidth() > 0 ? gamePage.getWidth() : 900;
        if (newX < 0) newX = 0;
        if (newX > panelW - width) newX = panelW - width;

        x = newX;
    }

    public void Draw(Graphics g) {
        if (currentImage != null) {
            g.drawImage(currentImage, x, y, width, height, null);
        } else {
            g.setColor(java.awt.Color.BLUE);
            g.fillRect(x, y, width, height);
        }
    }

    public boolean isCatch(Task task) {
        return task.getX() < x + width &&
               task.getX() + task.getWidth() > x &&
               task.getY() < y + height &&
               task.getY() + task.getHeight() > y;
    }

    public int getX()      { return x; }
    public int getY()      { return y; }
    public int getWidth()  { return width; }
    public int getHeight() { return height; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}
