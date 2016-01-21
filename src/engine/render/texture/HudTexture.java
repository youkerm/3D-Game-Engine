package engine.render.texture;

import org.lwjgl.util.vector.Vector2f;

public class HudTexture {

	private int textureHoverOFF;
	private int textureHoverON;

	private Vector2f position;
	private Vector2f scale;

	private float rotation = 0f;

	private boolean hover = false;
	private boolean displayInFPS = false; //If the component should be render on FPS mode

	public HudTexture(int textureHoverOFF, Vector2f position, Vector2f scale, float rotation) {
		this.textureHoverOFF = textureHoverOFF;
		this.position = new Vector2f(position.x, position.y);
		this.scale = new Vector2f(scale.x, scale.y);
		this.rotation = rotation;
	}

	public HudTexture(int textureHoverOFF, int textureHoverON, Vector2f position, Vector2f scale, float rotation) {
		this(textureHoverOFF, position, scale, rotation);
		this.textureHoverON = textureHoverON;
	}
	
	public HudTexture(int textureHoverOFF, Vector2f position, float scale, float rotation) {
		this(textureHoverOFF, position, new Vector2f(scale, scale), rotation);
	}

	public HudTexture(int textureHoverOFF, int textureHoverON, Vector2f position, float scale, float rotation) {
		this(textureHoverOFF, position, new Vector2f(scale, scale), rotation);
		this.textureHoverON = textureHoverON;
	}
	
	public HudTexture(int textureHoverOFF, Vector2f position, Vector2f scale) {
		this(textureHoverOFF, position, scale, 0f);
	}

	public HudTexture(int textureHoverOFF, int textureHoverON, Vector2f position, Vector2f scale) {
		this(textureHoverOFF, position, scale, 0f);
		this.textureHoverON = textureHoverON;
	}
	
	public HudTexture(int textureHoverOFF, Vector2f position, float scale) {
		this(textureHoverOFF, position, new Vector2f(scale, scale));
	}

	public HudTexture(int textureHoverOFF, int textureHoverON, Vector2f position, float scale) {
		this(textureHoverOFF, position, new Vector2f(scale, scale));
		this.textureHoverON = textureHoverON;
	}

	public int getTexture() {
		if (hover) {
			return textureHoverON;
		}
		return textureHoverOFF;
	}

	public void setTexture(int textureHoverOFF) {
		this.textureHoverOFF = textureHoverOFF;
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

	public Vector2f getScale() {
		return scale;
	}

	public void setScale(Vector2f scale) {
		this.scale = scale;
	}
	
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public float getRotation() {
		return rotation;
	}

	public void setHover(boolean hover) {
		this.hover = hover;
	}

	public boolean getHover() {
		return hover;
	}

	public void displaceInFPS(boolean enable) {
		this.displayInFPS = enable;
	}

	public boolean isDisplayInFPS() {
		return displayInFPS;
	}

}
