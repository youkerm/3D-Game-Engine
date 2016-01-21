package engine.render.shader;

import engine.core.Camera;
import engine.core.Light;
import engine.core.tools.Maths;
import org.lwjgl.util.vector.Matrix4f;

public class WaterShader extends ShaderProgram {
 
    private final static String VERTEX_FILE = "src/engine/render/shader/opengl/waterVertex.txt";
    private final static String FRAGMENT_FILE = "src/engine/render/shader/opengl/waterFragment.txt";
 
    private int location_modelMatrix;
    private int location_viewMatrix;
    private int location_projectionMatrix;

    private int location_reflectionTexture;
    private int location_refractionTexture;
    private int location_dudvMap;
    private int location_normalMap;
    private int location_depthMap;
    private int location_moveFactor;
    private int location_cameraPosition;
    private int location_lightColor;
    private int location_lightPosition;
 
    public WaterShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
 
    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
    }
 
    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_modelMatrix = super.getUniformLocation("modelMatrix");

        location_reflectionTexture = super.getUniformLocation("reflectionTexture");
        location_refractionTexture = super.getUniformLocation("refractionTexture");
        location_dudvMap = super.getUniformLocation("dudvMap");
        location_moveFactor = super.getUniformLocation("moveFactor");
        location_cameraPosition = super.getUniformLocation("cameraPosition");
        location_normalMap = super.getUniformLocation("normalMap");
        location_lightColor = super.getUniformLocation("lightColor");
        location_lightPosition = super.getUniformLocation("lightPosition");
        location_depthMap = super.getUniformLocation("depthMap");
    }

    public void connectTextureUnits() {
        super.loadInt(location_reflectionTexture, 0);
        super.loadInt(location_refractionTexture, 1);
        super.loadInt(location_dudvMap, 2);
        super.loadInt(location_normalMap, 3);
        super.loadInt(location_depthMap, 4);
    }

    public void loadLights(Light sun) {
        super.loadVector(location_lightColor, sun.getColour());
        super.loadVector(location_lightPosition, sun.getPosition());
    }

    public void loadMoveFactor(float factor) {
        super.loadFloat(location_moveFactor, factor);
    }
 
    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(location_projectionMatrix, projection);
    }
     
    public void loadViewMatrix(Camera camera){
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        loadMatrix(location_viewMatrix, viewMatrix);
        super.loadVector(location_cameraPosition, camera.getPosition());
    }
 
    public void loadModelMatrix(Matrix4f modelMatrix){
        super.loadMatrix(location_modelMatrix, modelMatrix);
    }
 
}