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

        // default posisi diam
        this.currentImage = (heroIdle != null) ? heroIdle : heroRight;

        this.gamePage = gamePage;
    }

    @Override
    public void doMovement() {
        if (gamePage == null) return;

        int newX = x;
        int newY = y;

        boolean movingLeft  = gamePage.isLeftPressed();
        boolean movingRight = gamePage.isRightPressed();
        boolean movingUp    = gamePage.isUpPressed();
        boolean movingDown  = gamePage.isDownPressed();

        // Gerak horizontal
        if (movingLeft && !movingRight) {
            newX -= speed;
            currentImage = (heroLeft != null) ? heroLeft : currentImage;
        } else if (movingRight && !movingLeft) {
            newX += speed;
            currentImage = (heroRight != null) ? heroRight : currentImage;
        } else {
            // kalau tidak gerak kiri/kanan → idle
            if (heroIdle != null) currentImage = heroIdle;
        }

        // Batasi X
        int panelW = gamePage.getWidth() > 0 ? gamePage.getWidth() : 900;
        if (newX < 0) newX = 0;
        if (newX > panelW - width) newX = panelW - width;

        // Gerak vertikal
        int panelH = gamePage.getHeight() > 0 ? gamePage.getHeight() : 600;

        if (movingUp && !movingDown) {
            newY -= speed;
        } else if (movingDown && !movingUp) {
            newY += speed;
        }

        // Biar tetap di area bawah
        int minY = panelH - 180;       // batas atas area gerak
        int maxY = panelH - height - 20; // batas bawah
        if (newY < minY) newY = minY;
        if (newY > maxY) newY = maxY;

        x = newX;
        y = newY;
    }

    public void Draw(Graphics g) {
        if (currentImage != null) {
            g.drawImage(currentImage, x, y, width, height, null);
        } else {
            // fallback kalau gambar null
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

    // Getter & Setter dipakai GamePage
    public int getX()      { return x; }
    public int getY()      { return y; }
    public int getWidth()  { return width; }
    public int getHeight() { return height; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}
