package tankrotationexample.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Wall {

    protected float x;
    protected float y;
    protected BufferedImage img;

    public Wall(float x, float y, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.img = img;
    }

    public Rectangle getHitBox() {
        return new Rectangle((int) x, (int) y, img.getWidth(), img.getHeight());
    }

    public void drawImage(Graphics g) {
        g.drawImage(img, (int) x, (int) y, null);
    }
}
