package engine.render.shader;
import org.lwjgl.util.vector.Matrix4f;

public class HudShader extends ShaderProgram {
     
    private static final String VERTEX_FILE = "src/engine/render/shader/opengl/hudVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/engine/render/shader/opengl/hudFragmentShader.txt";
     
    private int location_transformationMatrix;
    private int location_alpha;
 
    public HudShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
     
    public void loadTransformation(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadAlpha(float alpha) {
        super.loadFloat(location_alpha, alpha);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_alpha = super.getUniformLocation("alpha");
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
     
}