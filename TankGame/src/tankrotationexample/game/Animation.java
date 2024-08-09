package tankrotationexample.game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Animation {
    private float x, y;
    private final List<BufferedImage> frames;
    private final int delay = 30;
    private long timeSinceLastFrameUpdate = 0;
    private int currentFrame = 0;
    private boolean running = true;
    private final List<Animation> anims = new ArrayList<>();

    public Animation(float x, float y, List<BufferedImage> frames) {
        this.x = x - frames.get(0).getWidth() / 2f;
        this.y = y - frames.get(0).getHeight() / 2f;
        this.frames = frames;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();

        if (this.timeSinceLastFrameUpdate + delay < currentTime) {
            this.currentFrame++;
            if (this.currentFrame >= this.frames.size()) {
                this.running = false;
            }
            this.timeSinceLastFrameUpdate = currentTime;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void render(Graphics g) {
        if (this.running) {
            Graphics2D g2d = (Graphics2D) g;
            BufferedImage currentImg = frames.get(currentFrame);

            // Draw the current frame at the specified location
            g2d.drawImage(currentImg, (int) x, (int) y, null);
        }
    }



    public void addAnimation(Animation animation) {
        anims.add(animation);  // Ensure this adds to the anims list
    }

}
