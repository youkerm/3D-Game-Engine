package engine.render;

import engine.core.entity.component.AABBox;
import engine.core.entity.component.PositionSystem;
import engine.core.entity.component.Sphere;
import engine.core.model.RawModel;
import engine.core.tools.Maths;
import engine.core.tools.Settings;
import engine.render.shader.BoundingShader;
import engine.render.tools.Frustum;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import java.util.List;

/**
 * Created by Mitch on 9/1/2015.
 */
public class BoundingRender {

    private BoundingShader shader;
    private Frustum frustum;

    public BoundingRender(BoundingShader shader, Matrix4f projectionMatrix, Frustum frustum) {
        this.shader = shader;
        this.frustum = frustum;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(List<AABBox> boxes, List<Sphere> spheres) {
//            for (AABBox box : boxes) {
//                if (frustum.contains(box)) {
//                    prepareInstance(box);
//                    prepareRawModel(box.getModel());
//                    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36);
//                    unbindRawModel();
//                }
//            }
////        if (Settings.isSphereEnable()) {
//            for (Sphere sphere : spheres) {
//            if (frustum.contains(sphere)) {
//                prepareInstance(sphere);
//                prepareRawModel(sphere.getModel());
//                GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, sphere.getModel().getVertexCount());
//                unbindRawModel();
//            }
//            }
////        }
//        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

    }

    private void prepareRawModel(RawModel model) {
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
    }

    private void unbindRawModel() {
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(AABBox box) {
        PositionSystem positionSystem = box.getPositionSystem();
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(positionSystem.getPosition(), positionSystem.getRotation(), positionSystem.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
    }

    private void prepareInstance(Sphere sphere) {
        PositionSystem positionSystem = sphere.getPositionSystem();
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(sphere.getCenter(), positionSystem.getRotation(), positionSystem.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
    }

}
