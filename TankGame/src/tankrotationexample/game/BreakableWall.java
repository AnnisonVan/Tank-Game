package tankrotationexample.game;

import java.awt.image.BufferedImage;

public class BreakableWall extends Wall {

    private int health;

    public BreakableWall(float x, float y, BufferedImage img, int initialHealth) {
        super(x, y, img, true);
        this.health = initialHealth;
    }

    @Override
    public void takeDamage() {
        if (health > 0) {
            health -= 10; // Example damage value
            if (health <= 0) {
                destroyed = true;
            }
        }
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
