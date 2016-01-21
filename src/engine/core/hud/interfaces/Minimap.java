package engine.core.hud.interfaces;

import engine.DisplayManager;
import engine.core.Camera;
import engine.core.hud.containers.Window;
import engine.core.model.RawModel;
import engine.render.buffers.WaterFrameBuffers;
import engine.render.texture.DefaultTextures;
import org.lwjgl.util.vector.Vector2f;

/**
 * Created by Mitch on 12/13/2015.
 */
public class Minimap extends Window {

    private WaterFrameBuffers fbos;

    public Minimap(DefaultTextures textures, Vector2f position, Vector2f scale, WaterFrameBuffers fbos) {
        super(textures, position, new Vector2f(scale.x, scale.y * DisplayManager.ASPECT_RATIO), "", false);
        this.fbos = fbos;
    }

    @Override
    public void onKeyTyped() {

    }
}
