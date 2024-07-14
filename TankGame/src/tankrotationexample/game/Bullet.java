package tankrotationexample.game;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet {

    private float x;
    private float y;
    private float angle;
    private float speed = 3;
    private BufferedImage img;
    private Tank firingTank;

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

    public void draw(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.img, rotation, null);
    }

    public boolean collidesWith(Tank tank) {
        Rectangle bulletRect = new Rectangle((int) x, (int) y, img.getWidth(), img.getHeight());
        Rectangle tankRect = new Rectangle((int) tank.getX(), (int) tank.getY(), (int) tank.getWidth(), (int) tank.getHeight());
        return bulletRect.intersects(tankRect);
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
}
