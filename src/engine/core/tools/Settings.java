package engine.core.tools;

import engine.core.Light;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Mitch on 9/24/2015.
 */
public class Settings {

    private static boolean FPS_MODE = true;
    private static boolean FULLSCREEN = true;
    private static Light sun_light = new Light(new Vector3f(10000, 10000, -10000), new Vector3f(1.3f, 1.3f, 1.3f), new Vector3f(1, 0 ,0));

    public static Light getSun() {
        return sun_light;
    }

    public static void setFPSEnable(boolean enable) {
        if (enable) {
            Mouse.setGrabbed(true);
        } else {
            Mouse.setGrabbed(false);
        }
        FPS_MODE = enable;
    }

    public static boolean isFPSMode() {
        return FPS_MODE;
    }

    public void setFullscreenEnable(boolean enable) {
        FULLSCREEN = enable;
    }

    public static boolean isFullscreen() {
        return FULLSCREEN;
    }

}
