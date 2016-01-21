package engine.render.shader;

import engine.DisplayManager;
import engine.core.Camera;
import engine.core.tools.Maths;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;


public class SkyboxShader extends ShaderProgram {
 
    private static final String VERTEX_FILE = "src/engine/render/shader/opengl/skyboxVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/engine/render/shader/opengl/skyboxFragmentShader.txt";
    
    private static final float ROTATION_SPEED = 3f;
    
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_fogColor1;
    private int location_fogColor2;
    private int location_cubeMap;
    private int location_cubeMap2;
    private int location_blendFactor;
    
    private float rotation = 0;
     
    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
     
    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }
 
    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = Maths.createViewMatrix(camera);
        matrix.m30 = 0;
        matrix.m31 = 0;
        matrix.m32 = 0;
        rotation += ROTATION_SPEED * DisplayManager.getDelta();
        Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0), matrix, matrix);
        super.loadMatrix(location_viewMatrix, matrix);
    }
    
    public void loadFogColor(Vector3f fog1, Vector3f fog2) {
    	super.loadVector(location_fogColor1, fog1);
    	super.loadVector(location_fogColor2, fog2);
    }
    
    public void connectTextureUnits() {
    	super.loadInt(location_cubeMap, 0);
    	super.loadInt(location_cubeMap2, 1);
    }
    
    public void loadBlendFactor(float factor) {
    	super.loadFloat(location_blendFactor, factor);
    }
     
    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_fogColor1 = super.getUniformLocation("fogColor1");
        location_fogColor2 = super.getUniformLocation("fogColor2");
        location_cubeMap = super.getUniformLocation("cubeMap");
        location_cubeMap2 = super.getUniformLocation("cubeMap2");
        location_blendFactor = super.getUniformLocation("blendFactor");
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
 
}