package tankrotationexample.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Damage {
    float x,y;
    BufferedImage img;

    public Damage(float x, float y, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.img = img;
    }

    public void drawImage(Graphics g) {
        g.drawImage(img, (int) x, (int) y, null);
    }

}
