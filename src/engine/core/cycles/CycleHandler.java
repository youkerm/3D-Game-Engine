package engine.core.cycles;

import engine.DisplayManager;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitch on 9/22/2015.
 */
public class CycleHandler {

    private final int ACCELERATED_CLOCK = 500;
    private final int MAX_TIME = 24000;

    public static final float FOG_DENSTIY = 0.0035f;

    private List<Component> components = new ArrayList<Component>();
    private Component currentComponent = null;
    private Component nextComponent = null;

    private static float time = 0;  //The current time of the cycles.
    private float blendFactor = 0;  //How much to blend the two fogs or textures together.
    private int texture1, texture2; //The texture of the current state of the cycle.
    private Vector3f fog1, fog2;    //The fog colors of the current state of the cycle.

    public CycleHandler(Component... components) {
        for (Component component : components) {
            this.components.add(component);
        }
    }

    public void update() {
        time += DisplayManager.getDelta() * ACCELERATED_CLOCK; //Accelerating the clock.
        time %= MAX_TIME; //Making sure the clock doesn't go over 24000

        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (time >= c.getStartTime() && time <= c.getStartTime() + c.getDuration()) {
                currentComponent = c;
                //Getting the next component that follows currentComponent.
                if (components.size() > 1) {
                    if (i < components.size() - 1) {
                        nextComponent = components.get(i + 1);
                    } else {
                        nextComponent = components.get(0);
                    }
                } else {
                    nextComponent = currentComponent;
                }
            }
        }

        if (currentComponent != null && components.size() >= 1) {
            float startTime = currentComponent.getStartTime();
            float duration = currentComponent.getDuration();

            //Calculating the blend factor, fog, and the texture color.
            blendFactor = (time - startTime)/(duration);
            fog2 = currentComponent.getFogColor();
            fog1 = nextComponent.getFogColor();
            texture2 = currentComponent.getTexture();
            texture1 = nextComponent.getTexture();
        }
    }

    public float getBlendFactor() {
        return blendFactor;
    }

    public int getFirstTexture() {
        return texture1;
    }

    public int getSecondTexture() {
        return texture2;
    }

    public Vector3f getFirstFog() {
        return fog1;
    }

    public Vector3f getSecondFog() {
        return fog2;
    }

    public float getCurrentTime() {
        return time;
    }

    public Vector3f getBackgroundColor() {
        float r = blendFactor * fog2.x + ((blendFactor-1) * -1) * fog1.x;
        float g = blendFactor * fog2.y + ((blendFactor-1) * -1) * fog1.y;
        float b = blendFactor * fog2.z + ((blendFactor-1) * -1) * fog1.z;
        return new Vector3f(r, g, b);
    }

    public static void setTime(float t) {
        time = t;
    }

}
