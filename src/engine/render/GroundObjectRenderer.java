package engine.render;

import engine.core.entity.Entity;
import engine.core.entity.component.ModelSystem;
import engine.core.entity.component.PositionSystem;
import engine.core.model.RawModel;
import engine.core.model.StaticModel;
import engine.core.tools.Maths;
import engine.render.shader.GroundObjectShader;
import engine.render.texture.ModelTexture;
import engine.render.tools.Frustum;
import engine.render.tools.Graphics;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import java.util.List;
import java.util.Map;

/**
 * Created by Mitch on 9/29/2015.
 */
public class GroundObjectRenderer {

    private GroundObjectShader shader;
    private Frustum frustum;

    public GroundObjectRenderer(GroundObjectShader shader, Matrix4f projectionMatrix, Frustum frustum) {
        this.shader = shader;
        this.frustum = frustum;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Map<ModelSystem, List<Entity>> entities) {
        for(ModelSystem model: entities.keySet()) {
            List<Entity> batch = entities.get(model);
            prepareStaticModel(model);

            for(Entity entity:batch) {
                float distance = frustum.distance(entity.getSphere());
                if (distance < 700 && distance > 0) {
                    prepareInstance(entity);
                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getLevelOfDetail(StaticModel.MAX_LEVEL_OF_DETAIL).getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                }
            }
            unbindStaticModel();
        }
    }

    private void prepareStaticModel(ModelSystem model) {
        RawModel rawModel = model.getLevelOfDetail(StaticModel.MAX_LEVEL_OF_DETAIL);
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        ModelTexture texture = model.getTexture();
        shader.loadNumberOfRows(texture.getNumberOfRows());
        if(texture.isTransparent()) {
            Graphics.disableCulling();
        }
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
        Graphics.enableAnisotropic();
        Graphics.enableMipmapping();
    }

    private void unbindStaticModel() {
        Graphics.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Entity entity) {
        PositionSystem positionSystem = entity.getPositionSystem();
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(positionSystem.getPosition(), positionSystem.getRotation(), positionSystem.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(entity.getModelSystem().getTextureXOffset(), entity.getModelSystem().getTextureYOffset());
    }

}
