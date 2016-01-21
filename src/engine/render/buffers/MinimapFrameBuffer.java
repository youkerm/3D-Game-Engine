package engine.render.buffers;

import engine.core.hud.interfaces.Minimap;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * Created by Mitch on 12/16/2015.
 */
public class MinimapFrameBuffer extends FrameBuffer {

    private static final int REFLECTION_WIDTH = 640;
    private static final int REFLECTION_HEIGHT = 360;

    private int reflectionFrameBuffer;
    private int reflectionTexture;
    private int reflectionDepthBuffer;

    public MinimapFrameBuffer() {//call when loading the game
        initialiseReflectionFrameBuffer();
    }

    private void initialiseReflectionFrameBuffer() {
        reflectionFrameBuffer = createFrameBuffer();
        reflectionTexture = createTextureAttachment(REFLECTION_WIDTH,REFLECTION_HEIGHT);
        reflectionDepthBuffer = createDepthBufferAttachment(REFLECTION_WIDTH,REFLECTION_HEIGHT);
        unbindCurrentFrameBuffer();
    }

    public void bindReflectionFrameBuffer() {//call before rendering to this FBO
        bindFrameBuffer(reflectionFrameBuffer,REFLECTION_WIDTH,REFLECTION_HEIGHT);
    }

    public int getReflectionTexture() {//get the resulting texture
        return reflectionTexture;
    }

    @Override
    public void cleanUp() {
        GL30.glDeleteFramebuffers(reflectionFrameBuffer);
        GL11.glDeleteTextures(reflectionTexture);
        GL30.glDeleteRenderbuffers(reflectionDepthBuffer);
    }
}
