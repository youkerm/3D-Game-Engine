package engine.render.texture;

import org.lwjgl.util.vector.Vector3f;

public class TerrainTexturePack {
	
	private TerrainTexture backgroundTexture;
	private TerrainTexture rTexture;
	private TerrainTexture gTexture;
	private TerrainTexture bTexture;
	
	private Vector3f backgroundColor = new Vector3f(0,0,0);
	private Vector3f rColor = new Vector3f(0,0,0);
	private Vector3f bColor = new Vector3f(0,0,0);
	private Vector3f gColor = new Vector3f(0,0,0);
	
	public TerrainTexturePack(TerrainTexture backgroundTexture,
			TerrainTexture rTexture, TerrainTexture gTexture,
			TerrainTexture bTexture) {
		super();
		this.backgroundTexture = backgroundTexture;
		this.rTexture = rTexture;
		this.gTexture = gTexture;
		this.bTexture = bTexture;
	}

	public TerrainTexture getBackgroundTexture() {
		return backgroundTexture;
	}

	public TerrainTexture getrTexture() {
		return rTexture;
	}

	public TerrainTexture getgTexture() {
		return gTexture;
	}

	public TerrainTexture getbTexture() {
		return bTexture;
	}

	public Vector3f getBackgroundColor() {
		return backgroundColor;
	}
	
	public Vector3f getRColor() {
		return rColor;
	}
	
	public Vector3f getBColor() {
		return bColor;
	}
	
	public Vector3f getGColor() {
		return gColor;
	}
}
