/**
 * 
 */
package engine.core.hud.component;

import engine.input.IOManager;
import engine.input.InputEvent;
import engine.render.texture.DefaultTextures;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;


public class TextField extends Button {

	public static final int BACK_SPACE_ASCII = 8;

	private static final String VALID_INPUT = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890~!@#$%^&*()_+{}|[];':\",./<>?";

	public TextField(DefaultTextures textures, Vector2f position, Vector2f scale, Vector2f textPosition, String stringText) {
		super(textures, position, scale, stringText);
		super.setTextPosition(textPosition);
	}

	public TextField(Vector2f position, Vector2f scale, Vector2f textPosition, String stringText) {
		super(1, position, scale, stringText);
		super.setTextPosition(textPosition);
	}

	@Override
	public void onKeyPress() {
		if (hasFocus()) {
			char pressed = IOManager.getCharacterPressed();
			String temp = text.getTextString();
			if (pressed != BACK_SPACE_ASCII) {
				if (Character.isLetterOrDigit(pressed) || Character.isSpaceChar(pressed) || VALID_INPUT.contains(Character.toString(pressed))) {
					temp = temp + IOManager.getCharacterPressed();
				}
			}
			text.setTextString(temp);
		}
	}

	@Override
	public void onKeyRelease() {

	}

	@Override
	public void onKeyTyped() {
		if (Keyboard.isKeyDown(Keyboard.KEY_BACK)) {
			String temp = text.getTextString();
			int length = temp.length();
			if (length > 0) {
				temp = temp.substring(0, length - 1);
			}
			text.setTextString(temp);
		}
	}

	@Override
	public void onMouseMove() {
		super.onMouseMove();
		System.out.println("Focus: " + hasFocus());
	}

	@Override
	public int priority() {
		return InputEvent.HIGHEST_PRIORITY;
	}
}
