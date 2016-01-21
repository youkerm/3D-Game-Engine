package engine.render.texture;

public class ModelTexture {
    
    private int textureID;
	private int normalMap;
    
    private float shineDamper = 1;
    private float reflectivity = 0;
    
    private boolean isTransparent = false;
    private boolean useFakeLighting = false;
    
    private int numberOfRows = 1;

	public ModelTexture(int texture){
        this.textureID = texture;
    }
     
    public int getID(){
        return textureID;
    }

	public int getNormalMap() {
		return normalMap;
	}

	public void setNormalMap(int normalMap) {
		this.normalMap = normalMap;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public boolean isTransparent() {
		return isTransparent;
	}

	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}
 
    public void setTransparent(boolean isTransparent) {
		this.isTransparent = isTransparent;
	}
    
    public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}
    
    public int getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}
}