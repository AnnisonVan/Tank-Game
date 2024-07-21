package tankrotationexample.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Wall {

    protected float x;
    protected float y;
    protected BufferedImage img;
    protected boolean destroyed;
    protected boolean breakable;

    public Wall(float x, float y, BufferedImage img, boolean breakable) {
        this.x = x;
        this.y = y;
        this.img = img;
        this.breakable = breakable;
        this.destroyed = false;
    }

    public abstract void takeDamage();

    public abstract boolean isDestroyed();

    public boolean isBreakable() {
        return breakable;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, img.getWidth(), img.getHeight());
    }

    public void draw(Graphics g) {
        g.drawImage(img, (int) x, (int) y, null);
    }
}
