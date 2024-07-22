package tankrotationexample.game;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ResourceManager {
    private final static Map<String, BufferedImage> sprites = new HashMap<>();
    private final static Map<String, Clip> sounds = new HashMap<>();
    private final static Map<String, List<BufferedImage>> animations = new HashMap<>();

    public static void init() throws IOException {
        initSprites();
        initSounds();
        initAnimations();
    }

    private static void initSprites() throws IOException {
        sprites.put("t1", loadSprite("tank/tank1.png"));
        sprites.put("t2", loadSprite("tank/tank2.png"));
        sprites.put("bullet", loadSprite("bullet/Rocket1.gif"));
        sprites.put("breakableWall", loadSprite("wall/wall2.png"));
        sprites.put("unbreakableWall", loadSprite("wall/wall1.png"));
        sprites.put("background", loadSprite("background/Background.bmp"));
    }

    private static BufferedImage loadSprite(String path) throws IOException {
        return ImageIO.read(
                Objects.requireNonNull(
                        ResourceManager.class.getClassLoader().getResource(path),
                        "Resource %s was not found".formatted(path)
                )
        );
    }

    private static void initSounds() throws IOException {
        // Initialize sounds
    }

    private static void initAnimations() throws IOException {
        // Initialize animations
    }

    public static BufferedImage getSprite(String name) {
        return sprites.get(name);
    }

    public static Clip getSound(String name) {
        return sounds.get(name);
    }

    public static List<BufferedImage> getAnimation(String name) {
        return animations.get(name);
    }
}
