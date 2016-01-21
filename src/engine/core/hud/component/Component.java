package engine.core.hud.component;

import engine.DisplayManager;
import engine.core.tools.Settings;
import engine.input.InputEvent;
import engine.render.texture.HudTexture;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Component extends HudTexture implements InputEvent {

	protected List<Component> children = new ArrayList<Component>();
	protected List<Text> textChildren = new ArrayList<Text>();

	private Vector2f orginalPosition;

	private boolean focus;

	public Component(int hoverOFF, Vector2f position, Vector2f scale) {
		super(hoverOFF, position, scale);
		orginalPosition = new Vector2f(position.x, position.y);
	}

	public Component(int hoverOFF, int hoverON, Vector2f position, Vector2f scale) {
		super(hoverOFF, hoverON, position, scale);
		orginalPosition = new Vector2f(position.x, position.y);
	}

	protected void enableHover(boolean enable) {
		setHover(enable);
	}

	public void add(Component component) {
		Vector2f scale = resize(component.getScale());
		reposition(component.getPosition(), scale);

		children.add(component);
	}

	private Vector2f reposition(Vector2f position, Vector2f updatedScale) {
		float newXPos = (float) ((getBounds().getWidth()/2.0f) * position.x) - (position.x * updatedScale.x * DisplayManager.WIDTH/2.0f);
		float newYPos = (float) (((getBounds().getHeight() + 1)/2.0f) * position.y) - (position.y * updatedScale.y * DisplayManager.HEIGHT/2.0f);

		float centerX = ((DisplayManager.WIDTH/2.0f) + ((DisplayManager.WIDTH/2.0f) * getPosition().x));
		float centerY = ((DisplayManager.HEIGHT/2.0f) + ((DisplayManager.HEIGHT/2.0f) * getPosition().y));

		Vector2f normalized = getNormalisedCoordinates((centerX + newXPos), (centerY + newYPos));

		position.x = normalized.x;
		position.y = normalized.y;

		return position;
	}

	private Vector2f reposition2(Vector2f position, Vector2f updatedScale) {
		float newXPos = (float) ((getBounds().getWidth()/2.0f) * position.x) + (updatedScale.x * DisplayManager.WIDTH/2.0f);
		float newYPos = (float) (((getBounds().getHeight() + 1)/2.0) * position.y) + (updatedScale.y * DisplayManager.HEIGHT/2.0f);

		float centerX = ((DisplayManager.WIDTH/2.0f) + ((DisplayManager.WIDTH/2.0f) * getPosition().x));
		float centerY = ((DisplayManager.HEIGHT/2.0f) +((DisplayManager.HEIGHT/2.0f) * getPosition().y));

		Vector2f normalized = getNormalisedCoordinates((centerX + newXPos), (centerY + newYPos));

		position.x = normalized.x;
		position.y = normalized.y;

		return position;
	}

	private Vector2f resize(Vector2f scale) {
		scale.x = scale.x * getScale().x;
		scale.y = scale.y * getScale().y;
		return scale;
	}

	private Vector2f getNormalisedCoordinates(float posX, float posY) {
		float x = (2.0f * posX) / Display.getWidth() - 1f;
		float y = (2.0f * posY) / Display.getHeight() - 1f;
		return new Vector2f(x, y);
	}


	public void add(Text text) {
		//Position in font rendering coordinate system
		float xPos = (text.getOriginalPosition().x) * DisplayManager.WIDTH;
		float yPos = (1 - text.getOriginalPosition().y) * DisplayManager.HEIGHT;

		float destX = xPos - (DisplayManager.WIDTH/2 * text.getMaxLineSize());
		float destY = yPos;

		//Position in normalized device coordinate system
		Vector2f normalized = getNormalisedCoordinates(destX, destY);
		normalized = reposition2(normalized, new Vector2f(text.getMaxLineSize() * getScale().x, text.getLineHeight()));

		//Position back into font rendering coordinate system.
		normalized.x = (normalized.x+1.0f)/2.0f;
		normalized.y = 1 - ((normalized.y+1.0f)/2.0f);

		text.setPosition(normalized);

		textChildren.add(text);
	}

	public void remove(Component component) {
		children.remove(component);
	}

	public List<Component> getChildren() {
		return children;
	}

    public void remove(Text text) {
        textChildren.remove(text);
    }

    public List<Text> getTextChildren() {
        return textChildren;
    }

	public Vector2f getOrginalPosition() {
		return orginalPosition;
	}
	
	public Rectangle getBounds() {
		Vector2f position = getPosition();
		Vector2f scale = getScale();

		float centerX = ((DisplayManager.WIDTH/2) + ((DisplayManager.WIDTH/2) * position.x));
		float centerY = ((DisplayManager.HEIGHT/2) + ((DisplayManager.HEIGHT/2) * position.y));

		int width = (int) (DisplayManager.WIDTH*scale.x);
		int height = (int) (DisplayManager.HEIGHT*scale.y);

		int x = (int) (centerX - (width/2));
		int y = (int) (centerY - (height/2));

		return new Rectangle(x, y, width, height);
	}

	public abstract void onClick();
	public abstract void onRelease();
	public abstract void onMouseScroll();
	public abstract void onKeyPress();
	public abstract void onKeyRelease();

	protected void onHover() {
		enableHover(true);
	}

	protected void offHover() {
		enableHover(false);
	}


	@Override
	public void onKeyPressed() {
		if (!Settings.isFPSMode()) {
			onKeyPress();
		}
	}

	@Override
	public void onKeyReleased() {
		if (!Settings.isFPSMode()) {
			onKeyRelease();
		}
	}

	@Override
	public void onKeyTyped() {

	}

	@Override
	public void onMouseClick() {
		if (!Settings.isFPSMode()) {
			if (getBounds().contains(Mouse.getX(), Mouse.getY())) {
				onClick();
			}
		}
	}

	@Override
	public void onMouseMove() {
		if (!Settings.isFPSMode()) {
			if (getBounds().contains(Mouse.getX(), Mouse.getY())) {
				onHover();
			} else {
				offHover();
			}
		}
	}

	@Override
	public void onMouseReleased() {
		if (!Settings.isFPSMode()) {
			if (getBounds().contains(Mouse.getX(), Mouse.getY())) {
				onRelease();
				focus = true;
			} else {
				focus = false;
			}
		}
	}

	@Override
	public void onMouseWheel() {
		if (!Settings.isFPSMode()) {
			onMouseScroll();
		}
	}

	public boolean hasFocus() {
		return focus;
	}

}
