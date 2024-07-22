package tankrotationexample.game;

import java.awt.image.BufferedImage;

public class BreakableWall extends Wall {

    private static int health = 1;

    public BreakableWall(float x, float y, BufferedImage img) {
        super(x, y, img, true);
    }

    @Override
    public void takeDamage() {
        if (health > 0) {
            health -= 1; // Example damage value
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
