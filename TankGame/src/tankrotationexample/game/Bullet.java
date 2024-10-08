package tankrotationexample.game;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet {
    private float x;
    private float y;
    private final float angle;
    private final float speed = 12;
    private final BufferedImage img;
    private final Tank firingTank;
    private final Rectangle hitBox;
    private int damage;

    public Bullet(float x, float y, float angle, BufferedImage img, Tank firingTank) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.img = img;
        this.firingTank = firingTank;
        this.hitBox = new Rectangle((int)x, (int)y, img.getWidth(), img.getHeight());
        this.damage = 30;
    }

    public void updatePosition() {
        x += Math.round(speed * Math.cos(Math.toRadians(angle)));
        y += Math.round(speed * Math.sin(Math.toRadians(angle)));
        hitBox.setLocation((int)x, (int)y); // Update hitbox position
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

    public Rectangle getHitBox() {
        return hitBox;
    }

    public void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), img.getWidth() / 2.0, img.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(img, rotation, null);
    }

    // Method to apply damage
    public void applyDamageToTank(Tank tank) {
            tank.takeDamage(this.damage);
    }
}
