package tankrotationexample.game;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Speed {
    float x,y;
    BufferedImage img;

    public Speed(float x, float y, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.img = img;
    }

    public Rectangle2D getHitBox() {
        return new Rectangle((int) x, (int) y, img.getWidth(), img.getHeight());
    }

    public void drawImage(Graphics g) {
        g.drawImage(img, (int) x, (int) y, null);
    }
}
