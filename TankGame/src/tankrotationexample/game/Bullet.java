package tankrotationexample.game;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet {

    private float x;
    private float y;
    private final float angle;
    private final float speed = 4;
    private final BufferedImage img;
    private final Tank firingTank;

    public Bullet(float x, float y, float angle, BufferedImage img, Tank firingTank) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.img = img;
        this.firingTank = firingTank;
    }

    public void updatePosition() {
        x += Math.round(speed * Math.cos(Math.toRadians(angle)));
        y += Math.round(speed * Math.sin(Math.toRadians(angle)));
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Tank getFiringTank() {
        return firingTank;
    }

    public void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), img.getWidth() / 2.0, img.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(img, rotation, null);
    }
}
