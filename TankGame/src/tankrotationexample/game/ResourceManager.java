package tankrotationexample.game;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class ResourceManager {
    private final static Map<String, BufferedImage> sprites = new HashMap<>();
    private final static Map<String, Sound> sounds = new HashMap<>();
    private final static Map<String, List<BufferedImage>> animations = new HashMap<>();
    private final static Map<String, Integer> animInfo = new HashMap<>(){{
        put("bullethit", 24);
        put("bulletshoot", 23);
        put("powerpick",31);
        put("rocketflame",16);
        put("rockethit",32);
        put("puffsmoke", 31);
    }};

    public static void init() throws IOException {
        try {
            initSprites();
            loadSounds();
            loadAnimations();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadAnimations() {
        String baseFormat = "animations/%s/%s_%04d.png";
        ResourceManager.animInfo.forEach((animationName, frameCount) -> {
            List<BufferedImage> frames = new ArrayList<>(frameCount);
            try {
                for (int i = 0; i < frameCount; i++) {
                    String spritePath = String.format(baseFormat, animationName, animationName, i);
                    frames.add(loadSprite(spritePath));
                }
                ResourceManager.animations.put(animationName, frames);
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        });
    }

    private static void initSprites() throws IOException {
        sprites.put("t1", loadSprite("tank/tank1.png"));
        sprites.put("t2", loadSprite("tank/tank2.png"));
        sprites.put("bullet", loadSprite("bullet/Rocket1.gif"));
        sprites.put("breakableWall", loadSprite("wall/wall2.png"));
        sprites.put("unbreakableWall", loadSprite("wall/wall1.png"));
        sprites.put("background", loadSprite("background/TankGamefield.jpg"));
        sprites.put("life", loadSprite("tank/life.png"));
        sprites.put("map", loadSprite("map/map(Sheet1).csv"));
        sprites.put("speed", loadSprite("powerup/speedBoost.png"));
        sprites.put("health", loadSprite("powerup/health.png"));
        sprites.put("damage", loadSprite("powerup/damage.png"));
    }

    private static BufferedImage loadSprite(String path) throws IOException {
        return ImageIO.read(
                Objects.requireNonNull(
                        ResourceManager.class.getClassLoader().getResource(path),
                        "Resource %s was not found".formatted(path)
                )
        );
    }

    private static Sound loadSound(String path) throws UnsupportedAudioFileException,IOException, LineUnavailableException {
        AudioInputStream ais = AudioSystem.getAudioInputStream(
                Objects.requireNonNull(
                        ResourceManager.class.getClassLoader().getResource(path),
                        "Sound Resource %s not found".formatted(path)
                )
        );
        Clip c = AudioSystem.getClip();
        c.open(ais);
        Sound s = new Sound(c);
        s.setVolume(.2f);
        return s;
    }

    public static void loadSounds() {
        try {
            ResourceManager.sounds.put("bg", loadSound("music/Music.mid"));
            ResourceManager.sounds.put("bullet_collide", loadSound("sounds/bullet.wav"));
            ResourceManager.sounds.put("pickup", loadSound("sounds/pickup.wav"));
            ResourceManager.sounds.put("shooting", loadSound("sounds/shotfiring.wav"));
            ResourceManager.sounds.put("explosion", loadSound("sounds/explosion.wav"));
            ResourceManager.sounds.put("lifeLost", loadSound("Explosion_large.wav"));
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }


    public static BufferedImage getSprite(String name) {
        if(!ResourceManager.sprites.containsKey(name)) {
            throw new IllegalArgumentException("Sprite %s does not exist in map.".formatted(name));
        }
        return ResourceManager.sprites.get(name);
    }

    public static Sound getSound(String key) {
        if(!ResourceManager.sounds.containsKey(key)) {
            throw new IllegalArgumentException("Sprite %s does not exist in map.".formatted(key));
        }
        return ResourceManager.sounds.get(key);
    }

    public static List<BufferedImage> getAnim(String key) {
        if(!ResourceManager.animations.containsKey(key)) {
            throw new IllegalArgumentException("Sprite %s does not exist in map.".formatted(key));
        }
        return ResourceManager.animations.get(key);
    }

    public static List<BufferedImage> getAnimation(String name) {
        return animations.get(name);
    }
}
