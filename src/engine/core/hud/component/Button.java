package engine.core.hud.component;

import engine.DisplayManager;
import engine.core.hud.containers.Container;
import engine.input.IOManager;
import engine.input.InputEvent;
import engine.render.font.FontType;
import engine.render.manager.TextManager;
import engine.render.texture.DefaultTextures;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Button extends Container {

	/* Constants */
	private final static float CORNER_SCALE_FACTOR = .03f;
	private final static float BAR_SCALE_FACTOR = 3.45f;

	protected Text text;

	public Button(DefaultTextures textures, Vector2f position, Vector2f scale) {
		super(textures.getButtonBackgroundHoverOffTexture(), textures.getButtonBackgroundHoverOnTexture(), position, scale);
		super.addComponents(textures.getButtonBarHoverOffTexture(), textures.getButtonBarHoverOnTexture(),
				textures.getButtonCornerHoverOffTexture(), textures.getButtonCornerHoverOnTexture(), CORNER_SCALE_FACTOR, BAR_SCALE_FACTOR);
		IOManager.add(this);
	}

	public Button(DefaultTextures textures, Vector2f position, Vector2f scale, String stringText) {
		super(textures.getButtonBackgroundHoverOffTexture(), textures.getButtonBackgroundHoverOnTexture(), position, scale);
		super.addComponents(textures.getButtonBarHoverOffTexture(), textures.getButtonBarHoverOnTexture(),
				textures.getButtonCornerHoverOffTexture(), textures.getButtonCornerHoverOnTexture(), CORNER_SCALE_FACTOR, BAR_SCALE_FACTOR);

		text = new Text(stringText, 1f, TextManager.getFont("candara"), new Vector2f(0,0), scale.x, false);
		add(text);
		IOManager.add(this);
	}

	public Button(int hoverOFF, Vector2f position, Vector2f scale, String stringText) {
		super(hoverOFF, position, scale);
		text = new Text(stringText, 1f, TextManager.getFont("candara"), new Vector2f(0,0), scale.x, false);
		add(text);
		IOManager.add(this);
	}

	public Button(int hoverOFF, int hoverOn, Vector2f position, Vector2f scale) {
		super(hoverOFF, hoverOn, position, scale);
		IOManager.add(this);
	}

	public String getText() {
		return text.getTextString();
	}

	public void setText(String stringText) {
		text.setTextString(stringText);
	}

	public Vector3f getFontColor() {
		return text.getColor();
	}

	public void setFontColor(Vector3f color) {
		text.setColor(color.x, color.y, color.z);
	}

	public FontType getFont() {
		return text.getFont();
	}

	public void setFont(FontType font) {
		text.setFont(font);
	}

	public void setTextPosition(Vector2f position) {
//		remove(text);
		text.setOriginalPosition(position);
//		add(text);
	}

	@Override
	public void onClick() {
		System.out.println("Mouse Clicked!");
	}

	@Override
	public void onRelease() {
		System.out.println("Mouse Released!");
	}

	@Override
	public void onMouseScroll() {

	}

	@Override
	public void onKeyPress() {

	}

	@Override
	public void onKeyRelease() {

	}

	@Override
	protected void onHover() {
		enableHover(true);
		for (Component c: children) {
			c.enableHover(true);
		}
	}

	@Override
	protected void offHover() {
		enableHover(false);
		for (Component c: children) {
			c.enableHover(false);
		}
	}

	@Override
	public void onKeyTyped() {

	}

	@Override
	public int priority() {
		return InputEvent.MIDDLE_PRIORITY;
	}

}