package tankrotationexample.game;

import tankrotationexample.GameConstants;
import tankrotationexample.Launcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * GameWorld class for handling game state and rendering.
 */
public class GameWorld extends JPanel implements Runnable {

    private BufferedImage world;
    private Tank t1;
    private Tank t2;
    private final Launcher lf;
    private long tick = 0;
    ArrayList gObjs = new ArrayList();
    private final List<Bullet> bullets = new ArrayList<>();

    /**
     * Constructs the GameWorld.
     *
     * @param lf The launcher instance.
     */
    public GameWorld(Launcher lf) {
        this.lf = lf;
    }

    @Override
    public void run() {
        Sound bg = ResourceManager.getSound("bg");
        bg.stop();
        try {
            while (true) {
                this.tick++;
                // Update tanks and their bullets
                t1.update();
                t2.update();

                // Update bullets
                updateBullets();

                // Check for power-up pickups
                checkPowerUpCollisions();

                // Check if either tank has lost all lives
                if (t1.getLives() <= 0 || t2.getLives() <= 0) {
                    resetGame();
                    this.lf.setFrame("end");
                    return;
                }

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

        this.t1.setHealth(100);
        this.t1.setLives(3);
        this.t1.setPosition(t1.getSpawnX(), t2.getSpawnY(), 0);

        this.t2.setHealth(100);
        this.t2.setLives(3);
        this.t2.setPosition(t2.getSpawnX(), t2.getSpawnY(), 180);
    }

    /**
     * Load all resources for the game. Set all game objects to their
     * initial state as well.
     */
    public void InitializeGame() {
        this.world = new BufferedImage(GameConstants.GAME_WORLD_WIDTH,
                GameConstants.GAME_WORLD_HEIGHT,
                BufferedImage.TYPE_INT_RGB);

        // Initialize the ResourceManager
        try {
            ResourceManager.init();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error initializing ResourceManager: " + e.getMessage());
            return; // Exit method if initialization fails
        }

        InputStreamReader isr = new InputStreamReader(
                Objects.requireNonNull(
                        ResourceManager.class.getClassLoader().getResourceAsStream("map/map(Sheet1).csv")
                )
        );

        // Initialize CSV map
        int row = 0;
        try (BufferedReader mapReader = new BufferedReader(isr)) {
            while (mapReader.ready()) {
                String line = mapReader.readLine();
                String[] objs = line.split(",");
                for (int col = 0; col < objs.length; col++) {
                    String gameItem = objs[col];
                    if (gameItem.equals("9") || gameItem.equals("2")) {
                        this.gObjs.add(new Wall(col * 32, row * 32, ResourceManager.getSprite("unbreakableWall")));
                    } else if (gameItem.equals("3")) {
                        this.gObjs.add(new BreakableWall(col * 32, row * 32, ResourceManager.getSprite("breakableWall")));
                    } else if (gameItem.equals("4")) {
                        this.gObjs.add(new Health(col * 32, row * 32, ResourceManager.getSprite("health")));
                    } else if (gameItem.equals("6")) {
                        this.gObjs.add(new Damage(col * 32, row * 32, ResourceManager.getSprite("damage")));
                    } else if (gameItem.equals("7")) {
                        this.gObjs.add(new Speed(col * 32, row * 32, ResourceManager.getSprite("speed")));
                    } else if (gameItem.equals("11")) {
                        this.t1 = new Tank(col * 32, row * 32, 0, 0, 0, "t1", "bullet", 3, "RED", this);
                        t1.setSpawnX(col*32);
                        t1.setSpawnY(row*32);
                        this.gObjs.add(t1);
                    } else if (gameItem.equals("22")) {
                        this.t2 = new Tank(col * 32, row * 32, 0, 0, 180, "t2", "bullet", 3, "BLUE", this);
                        t2.setSpawnX(col*32);
                        t2.setSpawnY(row*32);
                        this.gObjs.add(t2);
                    }
                }
                row++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Initialize tanks with resources from ResourceManager
        BufferedImage t1img = ResourceManager.getSprite("t1");
        BufferedImage t2img = ResourceManager.getSprite("t2");

        if (t1img == null || t2img == null) {
            System.out.println("One or more tank images could not be loaded.");
            return; // Exit method if any tank image is not found
        }

        // Initialize tank controls
        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);

        // Add key listeners to the frame
        this.lf.getJf().addKeyListener(tc1);
        this.lf.getJf().addKeyListener(tc2);
    }

    private void renderFloor(Graphics buffer) {
        for (int i = 0; i < GameConstants.GAME_WORLD_WIDTH; i += 1280) {
            for (int j = 0; j < GameConstants.GAME_WORLD_HEIGHT; j += 1280) {
                buffer.drawImage(ResourceManager.getSprite("background"), i, j, null);
            }
        }
    }

    private void checkPowerUpCollisions() {
        List<Object> objectsToRemove = new ArrayList<>();

        for (Object o : gObjs) {
            if (o instanceof Health health) {
                if (t1.getHitBox().intersects(health.getHitBox())) {
                    t1.increaseHealth(20); // Increase health by 20 or any desired amount
                    objectsToRemove.add(health);
                } else if (t2.getHitBox().intersects(health.getHitBox())) {
                    t2.increaseHealth(20);
                    objectsToRemove.add(health);
                }
            } else if (o instanceof Damage damage) {
                if (t1.getHitBox().intersects(damage.getHitBox())) {
                    t1.takeDamage(10); // Increase damage by 10 or any desired amount
                    objectsToRemove.add(damage);
                } else if (t2.getHitBox().intersects(damage.getHitBox())) {
                    t2.takeDamage(10);
                    objectsToRemove.add(damage);
                }
            } else if (o instanceof Speed speed) {
                if (t1.getHitBox().intersects(speed.getHitBox())) {
                    t1.increaseSpeed(1.5f); // Increase speed by a factor of 1.5 or any desired value
                    objectsToRemove.add(speed);
                } else if (t2.getHitBox().intersects(speed.getHitBox())) {
                    t2.increaseSpeed(1.5f);
                    objectsToRemove.add(speed);
                }
            }
        }

        // Remove the collected objects after iteration
        gObjs.removeAll(objectsToRemove);
    }

    private void displaySplitScreen(Graphics2D onScreenPanel) {
        int minimapOriginalHeight = GameConstants.GAME_WORLD_HEIGHT;
        double scaleFactor = 0.10; // As defined earlier
        int minimapHeight = (int) (minimapOriginalHeight * scaleFactor);
        int padding = 10; // Additional padding above the minimap

        int availableHeightForScreens = GameConstants.GAME_SCREEN_HEIGHT - minimapHeight - padding;

        BufferedImage lh = this.world.getSubimage((int) this.t1.getScreenX(), (int) this.t1.getScreenY(), GameConstants.GAME_SCREEN_WIDTH / 2, availableHeightForScreens - 25);
        onScreenPanel.drawImage(lh, 0, 0, null);

        BufferedImage rh = this.world.getSubimage((int) this.t2.getScreenX(), (int) this.t2.getScreenY(), GameConstants.GAME_SCREEN_WIDTH / 2, availableHeightForScreens - 25);
        onScreenPanel.drawImage(rh, GameConstants.GAME_SCREEN_WIDTH / 2, 0, null);
    }

    static double scaleFactor = .10;

    private void displayMiniMap(Graphics2D g2) {
        BufferedImage mm = this.world.getSubimage(0, 0, GameConstants.GAME_WORLD_WIDTH, GameConstants.GAME_WORLD_HEIGHT);
        double mmx = GameConstants.GAME_SCREEN_WIDTH / 2f - (GameConstants.GAME_WORLD_WIDTH * scaleFactor) / 2;
        double mmy = GameConstants.GAME_SCREEN_HEIGHT - (GameConstants.GAME_WORLD_HEIGHT * scaleFactor) - 35;

        // Calculate the width and height of the mini-map after scaling
        int mmWidth = (int) (GameConstants.GAME_WORLD_WIDTH * scaleFactor);
        int mmHeight = (int) (GameConstants.GAME_WORLD_HEIGHT * scaleFactor);

        // Draw black background
        g2.setColor(Color.BLACK);
        g2.fillRect((int) mmx, (int) mmy, mmWidth, mmHeight);

        AffineTransform scaler = AffineTransform.getTranslateInstance(mmx - 139, mmy);
        scaler.scale(.2, scaleFactor);

        g2.drawImage(mm, scaler, null);
    }

    // Method to add a bullet to the list
    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    private void updateBullets() {
        List<Bullet> bulletsToRemove = new ArrayList<>();
        Sound explosion = ResourceManager.getSound("explosion");

        for (Iterator<Bullet> iterator = bullets.iterator(); iterator.hasNext(); ) {
            Bullet bullet = iterator.next();
            bullet.updatePosition();

            boolean bulletHitSomething = false;

            // Check if the bullet collides with its own tank
            Tank firingTank = bullet.getFiringTank();
            if (firingTank != null && bullet.getHitBox().intersects(firingTank.getHitBox())) {
                continue;
            }

            // Check for collisions with other tanks
            for (Object o : gObjs) {
                if (o instanceof Tank tank && tank != firingTank && bullet.getHitBox().intersects(tank.getHitBox())) {
                    bulletsToRemove.add(bullet);
                    bulletHitSomething = true;
                    bullet.applyDamageToTank(tank); // Apply damage to the tank
                    explosion.play();
                    break;
                }
            }

            // Check for collisions with walls and breakable walls
            for (Object o : gObjs) {
                if (o instanceof Wall wall && bullet.getHitBox().intersects(wall.getHitBox())) {
                    bulletsToRemove.add(bullet);
                    bulletHitSomething = true;
                    explosion.play();
                    break;
                } else if (o instanceof BreakableWall breakableWall && bullet.getHitBox().intersects(breakableWall.getHitBox())) {
                    bulletsToRemove.add(bullet);
                    bulletHitSomething = true;
                    gObjs.remove(breakableWall);
                    explosion.play();
                    break;
                }
            }

            // Remove bullets that go out of bounds
            if (bullet.getX() < 0 || bullet.getX() >= GameConstants.GAME_WORLD_WIDTH ||
                    bullet.getY() < 0 || bullet.getY() >= GameConstants.GAME_WORLD_HEIGHT) {
                bulletsToRemove.add(bullet);
                bulletHitSomething = true;
            }

            // If the bullet hit something, mark it for removal
            if (bulletHitSomething) {
                bulletsToRemove.add(bullet);
            }
        }

        bullets.removeAll(bulletsToRemove);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Graphics2D buffer = world.createGraphics();

        // Draw black background
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw background image
        this.renderFloor(buffer);

        // Create a copy of gObjs to avoid ConcurrentModificationException
        List<Object> gObjsCopy;
        synchronized (gObjs) {
            gObjsCopy = new ArrayList<>(gObjs);
        }

        // Iterate over the copy to draw objects
        for (Object o : gObjsCopy) {
            if (o instanceof Tank tank) {
                tank.drawImage(buffer);
            } else if (o instanceof Health h) {
                h.drawImage(buffer);
            } else if (o instanceof BreakableWall bw) {
                bw.drawImage(buffer);
            } else if (o instanceof Speed speed) {
                speed.drawImage(buffer);
            } else if (o instanceof Wall w) {
                w.drawImage(buffer);
            } else if (o instanceof Damage d){
                d.drawImage(buffer);
            }
        }

        // Draw bullets
        synchronized (bullets) {
            for (Bullet bullet : bullets) {
                bullet.drawImage(buffer);
            }
        }

        // Display split screen views
        this.displaySplitScreen(g2);

        // Draw black line between split screens
        int lineWidth = 5; // Adjust width of the line as needed
        int lineX = GameConstants.GAME_SCREEN_WIDTH / 2 - lineWidth / 2;
        g2.setColor(Color.BLACK);
        g2.fillRect(lineX, 0, lineWidth, GameConstants.GAME_SCREEN_HEIGHT);

        // Display minimap
        this.displayMiniMap(g2);
    }


}
