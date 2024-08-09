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
    private int health;
    private int lives;
    private int damage;
    private final String type;
    private float spawnX;
    private float spawnY;

    private float R = 2;
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
    private final long FIRE_DELAY = 1200; // delay in milliseconds

    Tank(float x, float y, float vx, float vy, float angle, String imageName, String bulletImageName, int lives, String type, GameWorld gameWorld) {
        this.x = x;
        this.y = y;
        this.screenX = x;
        this.screenY = y;
        this.vx = vx;
        this.vy = vy;
        this.angle = angle;
        this.img = ResourceManager.getSprite(imageName);
        this.bulletImage = ResourceManager.getSprite(bulletImageName);
        this.lifeImage = ResourceManager.getSprite("life");
        this.health = 100;
        this.lives = 3;
        this.type = type;
        this.gameWorld = gameWorld;
        this.damage = 30;
        this.spawnX = 0;
        this.spawnY = 0;
    }

    public float getSpawnX(){
        return this.spawnX;
    }

    public float getSpawnY(){
        return this.spawnY;
    }

    public void setSpawnX(float x) {
        this.spawnX = x;
    }

    public void setSpawnY(float y) {
        this.spawnY = y;
    }

    void setX(float x) {
        this.x = x;
    }

    void setY(float y) {
        this.y = y;
    }

    public float getScreenX() {
        return screenX;
    }

    public float getScreenY() {
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
        float newX = x - Math.round(R * Math.cos(Math.toRadians(angle)));
        float newY = y - Math.round(R * Math.sin(Math.toRadians(angle)));

        if (!checkCollision(newX, newY)) {
            x = newX;
            y = newY;
        }
        checkBorder();
    }

    private void moveForwards() {
        float newX = x + Math.round(R * Math.cos(Math.toRadians(angle)));
        float newY = y + Math.round(R * Math.sin(Math.toRadians(angle)));

        if (!checkCollision(newX, newY)) {
            x = newX;
            y = newY;
        }
        checkBorder();
    }

    private boolean checkCollision(float newX, float newY) {
        Rectangle tankBounds = new Rectangle((int) newX, (int) newY, img.getWidth(), img.getHeight());

        for (Object obj : gameWorld.gObjs) {
            if (obj instanceof Wall wall) {
                if (tankBounds.intersects(wall.getHitBox())) {
                    return true;
                }
            }else if(obj instanceof BreakableWall bWall){
                if (tankBounds.intersects(bWall.getHitBox())) {
                    return true;
                }
            }
        }
        return false;
    }


    private void centerScreen() {
        this.screenX = this.x - GameConstants.GAME_SCREEN_WIDTH / 4f;
        this.screenY = this.y - GameConstants.GAME_SCREEN_HEIGHT / 2f;

        if (this.screenX < 0) screenX = 0;
        if (this.screenY < 0) screenY = 0;

        if (this.screenX > GameConstants.GAME_WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 2f) {
            this.screenX = GameConstants.GAME_WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 2f;
        }
        if (this.screenY > GameConstants.GAME_WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT) {
            this.screenY = GameConstants.GAME_WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT + 192;
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

    public void setLives(int numLives) {
        this.lives = numLives;
    }

    public int getLives() {
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
                return;
            }

            // Calculate the position to start the bullet
            float bulletX = x + (float) Math.cos(Math.toRadians(angle)) * img.getWidth() / 2;
            float bulletY = y + (float) Math.sin(Math.toRadians(angle)) * img.getHeight() / 2;

            // Add bullet to the game world
            Bullet bullet = new Bullet(bulletX, bulletY, this.angle, this.bulletImage, this);
            gameWorld.addBullet(bullet);
            lastShotTime = currentTime; // Update last shot time
            ResourceManager.getSound("shooting").play();

            // Add puff smoke animation at the location where the bullet is fired
            gameWorld.anims.add(new Animation(bulletX, bulletY, ResourceManager.getAnim("puffsmoke")));

        }
    }

    public Rectangle getHitBox() {
        return new Rectangle((int) x, (int) y, img.getWidth(), img.getHeight());
    }

    public void increaseHealth(int i) {
        if(health >= 90){
            this.health = 100;
        }else{
            this.health += i;
        }
    }

    public void increaseSpeed(float speed){
        this.R *= speed;
    }

    public void increaseDamageBoost(int damage) {
        this.damage += damage;
    }

    public void takeDamage(int damage) {
        Sound explosion = ResourceManager.getSound("lifeLost");
        this.health -= this.damage;
        if (this.health <= 0) {
            this.lives--;
            explosion.play();
            this.health = 100;
            gameWorld.resetPosition();
        }else if(this.lives == 0){
            gameWorld.resetGame();
        }
    }

    void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;
        if (this.img == null) {
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
