package engine.core.cycles;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Mitch on 9/22/2015.
 */
public class Component {

    private int texture;
    private Vector3f fogColor;
    private Vector3f sunPosition;
    private float startTime;
    private float duration;

    public Component(int texture, Vector3f fogColor, float startTime, float duration) {
        this.fogColor = fogColor;
        this.startTime = startTime;
        this.duration = duration;
        this.texture = texture;
    }

    public Vector3f getFogColor() {
        return fogColor;
    }

    public int getTexture() {
        return texture;
    }

    public float getStartTime() {
        return startTime;
    }

    public float getDuration() {
        return duration;
    }

}
