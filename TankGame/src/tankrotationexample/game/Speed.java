package tankrotationexample.game;

import java.awt.*;
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

    public void setX(int i){
        this.x = x;
    }

    public void setY(int i){
        this.y = y;
    }

    public float getX(){
        return this.x = x;
    }

    public float getY(){
        return this.y = y;
    }

    public Rectangle2D getHitBox() {
        return new Rectangle((int) x, (int) y, img.getWidth(), img.getHeight());
    }

    public void drawImage(Graphics g) {
        g.drawImage(img, (int) x, (int) y, null);
    }
}
