package engine.render.shader;

import engine.core.Light;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.util.List;

public class NormalShader extends ShaderProgram {

    private static final int MAX_LIGHTS = 4;

    private static final String VERTEX_FILE = "src/engine/render/shader/opengl/normalVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/engine/render/shader/opengl/normalFragmentShader.txt";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lightPositionEyeSpace[];
    private int location_lightColour[];
    private int location_attenuation[];
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_skyColor1;
    private int location_skyColor2;
    private int location_blendFactor;
    private int location_numberOfRows;
    private int location_offset;
    private int location_plane;
    private int location_modelTexture;
    private int location_normalMap;

    public NormalShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
        super.bindAttribute(2, "normal");
        super.bindAttribute(3, "tangent");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_skyColor1 = super.getUniformLocation("skyColor1");
        location_skyColor2 = super.getUniformLocation("skyColor2");
        location_blendFactor = super.getUniformLocation("blendFactor");
        location_numberOfRows = super.getUniformLocation("numberOfRows");
        location_offset = super.getUniformLocation("offset");
        location_plane = super.getUniformLocation("plane");
        location_modelTexture = super.getUniformLocation("modelTexture");
        location_normalMap = super.getUniformLocation("normalMap");

        location_lightPositionEyeSpace = new int[MAX_LIGHTS];
        location_lightColour = new int[MAX_LIGHTS];
        location_attenuation = new int[MAX_LIGHTS];
        for(int i=0;i<MAX_LIGHTS;i++){
            location_lightPositionEyeSpace[i] = super.getUniformLocation("lightPositionEyeSpace[" + i + "]");
            location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
            location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }
    }

    public void connectTextureUnits(){
        super.loadInt(location_modelTexture, 0);
        super.loadInt(location_normalMap, 1);
    }

    public void loadClipPlane(Vector4f plane){
        super.loadVector(location_plane, plane);
    }

    public void loadNumberOfRows(int numberOfRows){
        super.loadFloat(location_numberOfRows, numberOfRows);
    }

    public void loadOffset(float x, float y){
        super.load2DVector(location_offset, new Vector2f(x, y));
    }

    public void loadSkyColor(Vector3f sky1, Vector3f sky2) {
        super.loadVector(location_skyColor1, sky1);
        super.loadVector(location_skyColor2, sky2);
    }

    public void loadBlendFactor(float blendFactor) {
        super.loadFloat(location_blendFactor, blendFactor);
    }

    public void loadShineVariables(float damper, float reflectivity){
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadLights(List<Light> lights, Matrix4f viewMatrix){
        for(int i=0;i<MAX_LIGHTS;i++){
            if(i<lights.size()){
                super.loadVector(location_lightPositionEyeSpace[i], getEyeSpacePosition(lights.get(i), viewMatrix));
                super.loadVector(location_lightColour[i], lights.get(i).getColour());
                super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
            }else{
                super.loadVector(location_lightPositionEyeSpace[i], new Vector3f(0, 0, 0));
                super.loadVector(location_lightColour[i], new Vector3f(0, 0, 0));
                super.loadVector(location_attenuation[i], new Vector3f(1, 0, 0));
            }
        }
    }

    public void loadViewMatrix(Matrix4f viewMatrix){
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projection){
        super.loadMatrix(location_projectionMatrix, projection);
    }

    private Vector3f getEyeSpacePosition(Light light, Matrix4f viewMatrix){
        Vector3f position = light.getPosition();
        Vector4f eyeSpacePos = new Vector4f(position.x,position.y, position.z, 1f);
        Matrix4f.transform(viewMatrix, eyeSpacePos, eyeSpacePos);
        return new Vector3f(eyeSpacePos);
    }
}