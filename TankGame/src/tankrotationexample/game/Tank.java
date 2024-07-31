package tankrotationexample.game;

import tankrotationexample.GameConstants;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Tank {

    private GameWorld gameWorld;

    private float x;
    private float y;
    private float vx;
    private float vy;
    private float angle;
    private int health;
    private int lives;
    private final String type;

    private final float R = 2;
    private final float ROTATIONSPEED = 2.0f;

    private BufferedImage img;
    private BufferedImage bulletImage;
    private BufferedImage lifeImage;

    private boolean UpPressed;
    private boolean DownPressed;
    private boolean RightPressed;
    private boolean LeftPressed;
    private final List<Bullet> bullets = new ArrayList<>();

    private long lastShotTime;
    private final long FIRE_DELAY = 300; // delay in milliseconds

    // Update constructor to use ResourceManager
    Tank(float x, float y, float vx, float vy, float angle, String imageName, String bulletImageName, int lives, String type, GameWorld gameWorld) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.angle = angle;
        this.img = ResourceManager.getSprite(imageName); // Load tank image
        this.bulletImage = ResourceManager.getSprite(bulletImageName); // Load bullet image
        this.lifeImage = ResourceManager.getSprite("life");
        this.health = 100;
        this.lives = lives;
        this.type = type;
        this.gameWorld = gameWorld;
    }

    void setX(float x) {
        this.x = x;
    }

    void setY(float y) {
        this.y = y;
    }

    void toggleUpPressed() {
        this.UpPressed = true;
    }

    void toggleDownPressed() {
        this.DownPressed = true;
    }

    void toggleRightPressed() {
        this.RightPressed = true;
    }

    void toggleLeftPressed() {
        this.LeftPressed = true;
    }

    void unToggleUpPressed() {
        this.UpPressed = false;
    }

    void unToggleDownPressed() {
        this.DownPressed = false;
    }

    void unToggleRightPressed() {
        this.RightPressed = false;
    }

    void unToggleLeftPressed() {
        this.LeftPressed = false;
    }

    void update() {
        if (this.UpPressed) {
            this.moveForwards();
        }

        if (this.DownPressed) {
            this.moveBackwards();
        }

        if (this.LeftPressed) {
            this.rotateLeft();
        }

        if (this.RightPressed) {
            this.rotateRight();
        }
    }

    private int getHealth() {
        return this.health;
    }

    void setHealth(int health) {
        this.health = health;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.lives--;
            if(this.lives > 0){
                gameWorld.resetTankPosition();
                resetTank();
            }
        }
    }

    private void resetPosition(){
        if(gameWorld != null){
            gameWorld.resetTankPosition();
        }
    }

    private void resetTank() {
        this.health = 100;
    }

    private void rotateLeft() {
        this.angle -= this.ROTATIONSPEED;
    }

    private void rotateRight() {
        this.angle += this.ROTATIONSPEED;
    }

    private void moveBackwards() {
        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x -= vx;
        y -= vy;
        checkBorder();
    }

    private void moveForwards() {
        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
        checkBorder();
    }

    private void checkBorder() {
        if (x < 30) {
            x = 30;
        }
        if (x >= GameConstants.GAME_WORLD_WIDTH - 88) {
            x = GameConstants.GAME_WORLD_WIDTH - 88;
        }
        if (y < 40) {
            y = 40;
        }
        if (y >= GameConstants.GAME_WORLD_HEIGHT - 80) {
            y = GameConstants.GAME_WORLD_HEIGHT - 80;
        }
    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", angle=" + angle;
    }

    void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;
        if(this.img == null){
            System.out.println("Tank image is null!");
            return;
        }
        g2d.drawImage(this.img, rotation, null);

        // Draw health bar
        g2d.setColor(Color.RED);
        g2d.fillRect((int) x, (int) y - 10, 40, 5);

        // Actual health bar
        g2d.setColor(Color.GREEN); // Health color
        g2d.fillRect((int) x, (int) y - 10, (int) (40 * (health / 100.0)), 5); // Actual health bar

        // Draw lives
        if (this.lifeImage != null) {
            for (int i = 0; i < lives; i++) {
                g2d.drawImage(this.lifeImage, (int) x + i * (this.lifeImage.getWidth() + 2), (int) y - 30, null);
            }
        }

    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, (int) getWidth(), (int) getHeight());
    }

    public void shoot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= FIRE_DELAY) {
            if (this.bulletImage == null) {
                System.out.println("Bullet image is null!");
                return; // Avoid creating a bullet without an image
            }

            // Create a new Bullet at the end of the barrel
            Bullet bullet = new Bullet(x, y, this.angle, "bullet", this);
            bullets.add(bullet);
            lastShotTime = currentTime; // Update last shot time
        }
    }


    public void updateBullets(List<Tank> tanks, List<Wall> walls) {
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.updatePosition();

            // Check for collision with tanks
            for (Tank tank : tanks) {
                if (tank != bullet.getFiringTank() && bullet.collidesWith(tank)) {
                    tank.takeDamage(20);
                    bullets.remove(i);
                    i--;
                    break;
                }
            }

            // Check if bullet goes offscreen
            if (bullet.getX() < 0 || bullet.getX() > GameConstants.GAME_SCREEN_WIDTH ||
                    bullet.getY() < 0 || bullet.getY() > GameConstants.GAME_SCREEN_HEIGHT) {
                bullets.remove(i);
                i--;
            }
        }
    }

    public float getHeight() {
        return img.getHeight();
    }

    public float getWidth() {
        return img.getWidth();
    }

    public float getY() {
        return this.y;
    }

    public float getX() {
        return this.x;
    }

    public List<Bullet> getBullets() {
        return this.bullets;
    }

    public void setLives(int numLives){
        this.lives = numLives;
    }

    public int getLives(){
        return this.lives;
    }

    public void setPosition(float x, float y, float angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

}
