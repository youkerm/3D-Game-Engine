package engine.render.shader;

import engine.core.Camera;
import engine.core.tools.Maths;
import org.lwjgl.util.vector.Matrix4f;

/**
 * Created by Mitch on 9/1/2015.
 */
public class BoundingShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/engine/render/shader/opengl/boundingVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/engine/render/shader/opengl/boundingFragmentShader.txt";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;

    public BoundingShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");

    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(location_projectionMatrix, projection);
    }
}
