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
import java.util.Arrays;
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

    /**
     * Constructs the GameWorld.
     * @param lf The launcher instance.
     */
    public GameWorld(Launcher lf) {
        this.lf = lf;
    }

    @Override
    public void run() {
        try {
            while (true) {
                this.tick++;

                // Update tanks and their bullets
                if (t1 != null) {
                    t1.update();
                }
                if (t2 != null) {
                    t2.update();
                }


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
        this.t1.setPosition(t1.getX(), t2.getY(), 0);

        this.t2.setHealth(100);
        this.t2.setLives(3);
        this.t2.setPosition(t2.getX(), t2.getY(), 180);
    }

    public void resetTankPosition(){
        this.t1.setPosition(t1.getX(),t1.getY(),0);
        this.t2.setPosition(t2.getX(),t2.getY(),180);
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
                        ResourceManager.class.getClassLoader().getResourceAsStream("map/map.csv")
                )
        );

        // Initialize CSV map
        int row = 0;
        try(BufferedReader mapReader = new BufferedReader(isr)){
            while(mapReader.ready()){
                String line = mapReader.readLine();
                String[] objs = line.split(",");
                for (int col = 0; col < objs.length; col++) {
                    String gameItem = objs[col];
                    if (gameItem.equals("9") || gameItem.equals("2")) {
                        this.gObjs.add(new Wall(col * 32, row * 32, ResourceManager.getSprite("unbreakableWall")));
                    } else if (gameItem.equals("3")) {
                        this.gObjs.add(new BreakableWall(col * 32, row * 32, ResourceManager.getSprite("breakableWall")));
                    } else if (gameItem.equals("4")) {
                        this.gObjs.add(new BreakableWall(col * 32, row * 32, ResourceManager.getSprite("health")));
                    } else if (gameItem.equals("6")) {
                        this.gObjs.add(new BreakableWall(col * 32, row * 32, ResourceManager.getSprite("damage")));
                    } else if (gameItem.equals("7")) {
                        this.gObjs.add(new Speed(col * 32, row * 32, ResourceManager.getSprite("speed")));
                    } else if (gameItem.equals("11")) {
                        this.t1 = new Tank(col * 32, row * 32, 0, 0, 0, "t1", "bullet", 3, "RED", this);
                        this.gObjs.add(t1);
                    } else if (gameItem.equals("22")) {
                        this.t2 = new Tank(col * 32, row * 32, 0, 0, 180, "t2", "bullet", 3, "BLUE", this);
                        this.gObjs.add(t2);
                    }
                }
                row++;
            }
        } catch (IOException e){
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
        BufferedImage floor = ResourceManager.getSprite("background");
        for(int i = 0; i < GameConstants.GAME_WORLD_WIDTH; i+=320){
            for(int j = 0; j < GameConstants.GAME_WORLD_HEIGHT; j+=240){
                buffer.drawImage(floor, i, j, null);
            }
        }
    }

    static double scaleFactor = .15;

    private void displayMiniMap(Graphics2D g2) {
        BufferedImage mm = this.world.getSubimage(0,0,GameConstants.GAME_WORLD_WIDTH, GameConstants.GAME_WORLD_HEIGHT);
        double mmx = GameConstants.GAME_SCREEN_WIDTH/2f - (GameConstants.GAME_WORLD_WIDTH*scaleFactor)/2;
        double mmy = GameConstants.GAME_SCREEN_HEIGHT - (GameConstants.GAME_WORLD_HEIGHT*scaleFactor)-39;
        AffineTransform scaler = AffineTransform.getTranslateInstance(mmx, mmy);
        scaler.scale(scaleFactor,scaleFactor);

        g2.drawImage(mm, scaler, null);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Graphics2D buffer = world.createGraphics();

        // Draw background image
        this.renderFloor(buffer);

        for(Object o : gObjs){
            if(o instanceof Wall w){
                w.drawImage(buffer);
            }else if(o instanceof Health h){
                h.drawImage(buffer);
            }else if(o instanceof BreakableWall bw){
                bw.drawImage(buffer);
            }else if(o instanceof Speed speed){
                speed.drawImage(buffer);
            }else if(o instanceof Tank tank){
                tank.drawImage(buffer);
            }
        }

        this.displayMiniMap(g2);

    }
}
