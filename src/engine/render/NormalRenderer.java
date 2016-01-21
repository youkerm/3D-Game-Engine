package engine.render;

/**
 * Created by Mitch on 10/17/2015.
 */
import java.util.List;
import java.util.Map;

import engine.core.Camera;
import engine.core.Light;
import engine.core.entity.Entity;
import engine.core.entity.component.ModelSystem;
import engine.core.model.RawModel;
import engine.core.model.StaticModel;
import engine.core.tools.Maths;
import engine.render.shader.NormalShader;
import engine.render.texture.ModelTexture;
import engine.render.tools.Graphics;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class NormalRenderer {

    private NormalShader shader;

    public NormalRenderer(Matrix4f projectionMatrix) {
        this.shader = new NormalShader();
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextureUnits();
        shader.stop();
    }

    public void render(Map<ModelSystem, List<Entity>> entities, Vector4f clipPlane, List<Light> lights, Camera camera, Vector3f skyColor1, Vector3f skyColor2, float blendFactor) {
        shader.start();
        prepare(clipPlane, lights, camera, skyColor1, skyColor2, blendFactor);
        for (ModelSystem model : entities.keySet()) {
            prepareTexturedModel(model);
            List<Entity> batch = entities.get(model);
            for (Entity entity : batch) {
                prepareInstance(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getModel(StaticModel.MAX_LEVEL_OF_DETAIL).getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel();
        }
        shader.stop();
    }

    public void cleanUp(){
        shader.cleanUp();
    }

    private void prepareTexturedModel(ModelSystem model) {
        RawModel rawModel = model.getLevelOfDetail(StaticModel.MAX_LEVEL_OF_DETAIL);
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
        ModelTexture texture = model.getTexture();
        shader.loadNumberOfRows(texture.getNumberOfRows());
        if (texture.isTransparent()) {
            Graphics.disableCulling();
        }
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getNormalMap());
    }

    private void unbindTexturedModel() {
        Graphics.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Entity entity) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPositionSystem().getPosition(), entity.getPositionSystem().getRotation(), entity.getPositionSystem().getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(entity.getModelSystem().getTextureXOffset(), entity.getModelSystem().getTextureYOffset());
    }

    private void prepare(Vector4f clipPlane, List<Light> lights, Camera camera, Vector3f skyColor1, Vector3f skyColor2, float blendFactor) {
        shader.loadClipPlane(clipPlane);
        shader.loadSkyColor(skyColor1, skyColor2);
        shader.loadBlendFactor(blendFactor);
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);

        shader.loadLights(lights, viewMatrix);
        shader.loadViewMatrix(viewMatrix);
    }

}
