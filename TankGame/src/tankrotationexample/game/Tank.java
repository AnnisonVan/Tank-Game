package tankrotationexample.game;

import tankrotationexample.GameConstants;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Tank {

    private GameWorld gameWorld;

    private float screenX, screenY;
    private float x;
    private float y;
    private float vx;
    private float vy;
    private float angle;
    private float width;
    private float height;
    private int health;
    private int lives;
    private final String type;

    private final float R = 5;
    private final float ROTATIONSPEED = 3.0f;

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
        this.screenX = x;
        this.screenY = y;
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

    public float getScreenX(){
        return screenX;
    }

    public float getScreenY(){
        return screenY;
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
        centerScreen();
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

    private void centerScreen(){
        this.screenX = this.x - GameConstants.GAME_SCREEN_WIDTH/4f;
        this.screenY = this.y - GameConstants.GAME_SCREEN_HEIGHT/2f;

        if(this.screenX < 0) screenX = 0;
        if(this.screenY < 0) screenY = 0;

        if(this.screenX > GameConstants.GAME_WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH/2f){
            this.screenX = GameConstants.GAME_WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH/2f;
        }
        if(this.screenY > GameConstants.GAME_WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT){
            this.screenY = GameConstants.GAME_WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT+192;
        }
    }

    private void checkBorder() {
        if (x < 30) {
            x = 30;
        }
        if (y < 40) {
            y = 40;
        }
        if (x >= GameConstants.GAME_WORLD_WIDTH - 88) {
            x = GameConstants.GAME_WORLD_WIDTH - 88;
        }

        if (y >= GameConstants.GAME_WORLD_HEIGHT - 120) {
            y = GameConstants.GAME_WORLD_HEIGHT - 120;
        }
    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", angle=" + angle;
    }

    public float getY() {
        return this.y;
    }

    public float getX() {
        return this.x;
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

    public void shoot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= FIRE_DELAY) {
            if (this.bulletImage == null) {
                System.out.println("Bullet image is null!");
                return; // Avoid creating a bullet without an image
            }

            // Create a new Bullet at the end of the barrel
            Bullet bullet = new Bullet(x, y, this.angle, this.bulletImage, this);
            gameWorld.addBullet(bullet); // Add bullet to the game world
            lastShotTime = currentTime; // Update last shot time
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, (int) width, (int) height);
    }

    // Collision detection with a BreakableWall
    public boolean collidesWith(BreakableWall wall) {
        return this.getBounds().intersects(wall.getBounds());
    }

    // Collision detection with a NonBreakableWall
    public boolean collidesWith(Wall wall) {
        return this.getBounds().intersects(wall.getBounds());
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

}
