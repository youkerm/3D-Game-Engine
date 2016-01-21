package engine.render;

import engine.DisplayManager;
import engine.core.Camera;
import engine.core.Light;
import engine.core.Terrain;
import engine.core.loaders.GLLoader;
import engine.core.model.RawModel;
import engine.core.tools.Maths;
import engine.render.buffers.WaterFrameBuffers;
import engine.render.shader.WaterShader;
import engine.render.texture.WaterTile;
import engine.render.tools.Graphics;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;


public class WaterRenderer {

    private static final String DUDV_MAP = "waterDUDV";
    private static final String NORMAL_MAP = "normalMap";
    private static final float WAVE_SPEED = 0.05f;

    private RawModel quad;
    private WaterShader shader;
    private WaterFrameBuffers fbos;

    private int dudvTexture;
    private int normalMap;

    private float moveFactor = 0;

    public WaterRenderer(GLLoader loader, WaterShader shader, Matrix4f projectionMatrix, WaterFrameBuffers fbos) {
        this.shader = shader;
        this.fbos = fbos;
        dudvTexture = loader.loadTerrainTexture(DUDV_MAP);
        normalMap = loader.loadTerrainTexture(NORMAL_MAP);
        shader.start();
        shader.connectTextureUnits();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
        setUpVAO(loader);
    }
 
    public void render(List<Terrain> terrains, Camera camera, Light sun) {
        prepareRender(camera, sun);
        for (Terrain terrain: terrains) {
            for (WaterTile tile : terrain.getWaterTiles()) {
                Matrix4f modelMatrix = Maths.createTransformationMatrix(new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), new Vector3f(0, 0, 0), WaterTile.TILE_SIZE);
                shader.loadModelMatrix(modelMatrix);
                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
            }
        }
        unbind();
    }
     
    private void prepareRender(Camera camera, Light sun){
        shader.start();
        shader.loadViewMatrix(camera);
        moveFactor += WAVE_SPEED * DisplayManager.getDelta();
        moveFactor %= 1;
        shader.loadMoveFactor(moveFactor);
        shader.loadLights(sun);
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getReflectionTexture());
        Graphics.enableAnisotropic();

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionTexture());
        Graphics.enableAnisotropic();

        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, dudvTexture);
        Graphics.enableAnisotropic();

        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalMap);

        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionDepthTexture());
        Graphics.enableAnisotropic();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_BLEND_SRC, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }
     
    private void unbind(){
        GL11.glDisable(GL11.GL_BLEND);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }
 
    private void setUpVAO(GLLoader openGLLoader) {
        // Just x and z vectex positions here, y is set to 0 in v.shader
        float[] vertices = { -1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1 };
        quad = openGLLoader.loadToVAO(vertices, 2);
    }
 
}