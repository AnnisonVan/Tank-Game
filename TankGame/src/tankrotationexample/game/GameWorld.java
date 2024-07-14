package tankrotationexample.game;


import tankrotationexample.GameConstants;
import tankrotationexample.Launcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Objects;
import java.util.Arrays;


/**
 * @author anthony-pc
 */
public class GameWorld extends JPanel implements Runnable {

    private BufferedImage world;
    private BufferedImage backgroundImage;
    private BufferedImage bulletImage;
    private Tank t1;
    private Tank t2;
    private final Launcher lf;
    private long tick = 0;

    /**
     *
     */
    public GameWorld(Launcher lf) {
        this.lf = lf;
    }

    @Override
    public void run() {
        try {
            while (true) {
                this.tick++;
                this.t1.update(); // update tank 1
                this.t1.updateBullets(Arrays.asList(t1, t2)); // Update bullets for tank 1
                this.t2.update(); //update tank 2
                this.t2.updateBullets(Arrays.asList(t1, t2)); // Update bullets for tank 2
                this.repaint();   // redraw game
                /*
                 * Sleep for 1000/144 ms (~6.9ms). This is done to have our 
                 * loop run at a fixed rate per/sec. 
                */
                Thread.sleep(1000 / 144);
            }
        } catch (InterruptedException ignored) {
            System.out.println(ignored);
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
    }

    /**
     * Load all resources for Tank Wars Game. Set all Game Objects to their
     * initial state as well.
     */
    public void InitializeGame() {
        this.world = new BufferedImage(GameConstants.GAME_SCREEN_WIDTH,
                GameConstants.GAME_SCREEN_HEIGHT,
                BufferedImage.TYPE_INT_RGB);
        // Load background image
        try {
            backgroundImage = ImageIO.read(
                    Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("background/Background.bmp"))
            );
        } catch (IOException ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        BufferedImage t1img = null;
        BufferedImage t2img = null;
        try {
            /*
             * note class loaders read files from the out folder (build folder in Netbeans) and not the
             * current working directory. When running a jar, class loaders will read from within the jar.
             */
            t1img = ImageIO.read(
                    Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("tank/tank1.png"),
                            "Could not find tank1.png")
            );
            t2img = ImageIO.read(
                    Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("tank/tank2.png"),
                            "Could not find tank1.png")
            );
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        // Load bullet image
        try {
            bulletImage = ImageIO.read(
                    Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("bullet/Shell.gif"),
                            "Could not find Shell.gif")
            );
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        t1 = new Tank(300, 300, 0, 0, (short) 0, t1img, bulletImage);
        t2 = new Tank(500, 500, 0, 0, (short) 0, t2img, bulletImage);
        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);
        this.lf.getJf().addKeyListener(tc1);
        this.lf.getJf().addKeyListener(tc2);
    }

    public BufferedImage getBulletImage(){
        return bulletImage;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Graphics2D buffer = world.createGraphics();

        // Draw background image
        if(backgroundImage != null) {
            buffer.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), null);
        }

        this.t1.drawImage(buffer);
        this.t2.drawImage(buffer);

        //draw bullets
        for (Bullet bullet : t1.getBullets()) {
            bullet.draw(buffer);
        }
        for (Bullet bullet : t2.getBullets()) {
            bullet.draw(buffer);
        }
        g2.drawImage(world, 0, 0, null);
    }
}
