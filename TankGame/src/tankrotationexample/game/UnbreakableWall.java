package tankrotationexample.game;

import java.awt.image.BufferedImage;

public class UnbreakableWall extends Wall {

    public UnbreakableWall(float x, float y, BufferedImage img) {
        super(x, y, img, false);
    }

    @Override
    public void takeDamage() {
        // Unbreakable walls don't take damage
    }

    @Override
    public boolean isDestroyed() {
        return false; // Unbreakable walls never get destroyed
    }
}
