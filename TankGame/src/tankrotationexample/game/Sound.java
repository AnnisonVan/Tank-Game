package tankrotationexample.game;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.util.spi.ResourceBundleControlProvider;

public class Sound {
    private Clip clip;
    private ResourceBundleControlProvider sound;

    public Sound(Clip c){
        this.clip = c;
    }

    public void play(){
        if(clip.isRunning()){
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.start();
    }

    public void stop(){
        this.clip.stop();
    }

    public void loopContinuously(){
        this.clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void setVolume(float v) {
        FloatControl volume = (FloatControl) this.clip.getControl(FloatControl.Type.MASTER_GAIN);
        volume.setValue(20.0f * (float)Math.log10(v));
    }
}
