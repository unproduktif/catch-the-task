import java.awt.Image;

public class Bad extends Task {
    private boolean Boom;

    public Bad(int x, int y, int width, int height, int speed, int poin, Image img, boolean boom) {
        super(x, y, width, height, speed, poin, img);
        this.Boom = boom;
    }

    public void DoCatchBad() {
    }

    @Override
    public void DoCatch() {
    }

    public boolean getBoom() {
        return Boom;
    }
}
