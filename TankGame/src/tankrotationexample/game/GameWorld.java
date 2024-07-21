package tankrotationexample.game;

import tankrotationexample.GameConstants;
import tankrotationexample.Launcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GameWorld class for handling game state and rendering.
 */
public class GameWorld extends JPanel implements Runnable {

    private BufferedImage world;
    private BufferedImage backgroundImage;
    private BufferedImage bulletImage;
    private Tank t1;
    private Tank t2;
    private int lives;
    private final Launcher lf;
    private List<Wall> walls; // Added walls list for collision detection
    private long tick = 0;

    /**
     * Constructs the GameWorld.
     * @param lf The launcher instance.
     */
    public GameWorld(Launcher lf) {
        this.lf = lf;
        this.walls = new ArrayList<>(); // Initialize walls list
    }

    @Override
    public void run() {
        try {
            while (true) {
                this.tick++;

                // Update tanks and their bullets
                t1.update();
                t1.updateBullets(Arrays.asList(t1, t2), walls);
                t2.update();
                t2.updateBullets(Arrays.asList(t1, t2), walls);

                // Repaint the game world
                this.repaint();

                // Sleep for ~6.9ms to achieve approximately 144 frames per second
                Thread.sleep(1000 / 144);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupt status
            System.out.println("Game loop interrupted: " + e.getMessage());
        }
    }

    /**
     * Reset game to its initial state.
     */
    public void resetGame() {
        this.tick = 0;
        this.t1.setX(300);
        this.t1.setY(300);
        this.t2.setX(500);
        this.t2.setY(500);
        this.t1.setLives(3); // Reset lives for tank 1
        this.t2.setLives(3); // Reset lives for tank 2
    }

    /**
     * Load all resources for the game. Set all game objects to their
     * initial state as well.
     */
    public void InitializeGame() {
        this.world = new BufferedImage(GameConstants.GAME_SCREEN_WIDTH,
                GameConstants.GAME_SCREEN_HEIGHT,
                BufferedImage.TYPE_INT_RGB);

        // Initialize the ResourceManager
        try {
            ResourceManager.init();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error initializing ResourceManager: " + e.getMessage());
            return; // Exit method if initialization fails
        }

        // Load background image
        backgroundImage = ResourceManager.getSprite("background");
        if (backgroundImage == null) {
            System.out.println("Could not find background image.");
            return; // Exit method if background image is not found
        }

        // Load bullet image
        bulletImage = ResourceManager.getSprite("bullet");
        if (bulletImage == null) {
            System.out.println("Could not find bullet image.");
            return; // Exit method if bullet image is not found
        }

        // Initialize tanks with resources from ResourceManager
        BufferedImage t1img = ResourceManager.getSprite("t1");
        BufferedImage t2img = ResourceManager.getSprite("t2");

        if (t1img == null || t2img == null) {
            System.out.println("One or more tank images could not be loaded.");
            return; // Exit method if any tank image is not found
        }

        // Create tanks
        t1 = new Tank(300, 300, 0, 0, 0, "t1", "bullet",3);
        t2 = new Tank(500, 500, 0, 0, 0, "t2", "bullet",3);

        // Initialize tank controls
        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);

        // Add key listeners to the frame
        this.lf.getJf().addKeyListener(tc1);
        this.lf.getJf().addKeyListener(tc2);
    }

    public BufferedImage getBulletImage() {
        return bulletImage;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Graphics2D buffer = world.createGraphics();

        // Draw background image
        if (backgroundImage != null) {
            buffer.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), null);
        }

        // Draw tanks
        t1.drawImage(buffer);
        t2.drawImage(buffer);

        // Draw bullets
        for (Bullet bullet : t1.getBullets()) {
            bullet.draw(buffer);
        }
        for (Bullet bullet : t2.getBullets()) {
            bullet.draw(buffer);
        }

        // Draw lives
        g2.setColor(Color.WHITE);
        g2.drawString("Tank 1 Lives: " + t1.getLives(), 10, 20);
        g2.drawString("Tank 2 Lives: " + t2.getLives(), getWidth() - 150, 20);

        g2.drawImage(world, 0, 0, null);
    }

}
