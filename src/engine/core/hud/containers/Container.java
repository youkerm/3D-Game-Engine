package engine.core.hud.containers;

import engine.DisplayManager;
import engine.core.hud.component.Component;
import engine.core.hud.component.Sprite;
import engine.render.manager.HUDManager;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Container extends Sprite {

	private List<Component> sprites = new ArrayList<Component>();

	public Container(int backgroundTextureOff, Vector2f position, Vector2f scale) {
		super(backgroundTextureOff, position, scale);
		updatePosition(position, scale);
	}

	public Container(int backgroundTextureOff, int backgroundTextureOn, Vector2f position, Vector2f scale) {
		super(backgroundTextureOff, backgroundTextureOn, position, scale);
	}

	protected void updatePosition(Vector2f position, Vector2f scale) {
		Vector2f finalPosition = new Vector2f();
		finalPosition.x = position.x  - (position.x * scale.x);
		finalPosition.y = position.y  - (position.y * scale.y);
		super.setPosition(finalPosition);
	}

	public void add(Container container) {
		super.add(container);
		HUDManager.addComponent(container);
	}

	protected void addComponents(int barTextureHoverOff, int barTextureHoverOn, int cornerTextureHoverOff, int cornerTextureHoverOn, float cornerScaleFactor, float barScaleFactor) {
		/* Calculates the width and height*/
		float cornerWidth = cornerScaleFactor / super.getScale().x;
		float cornerHeight = cornerScaleFactor / super.getScale().y * DisplayManager.ASPECT_RATIO;

		float barWidth = cornerWidth / barScaleFactor;
		float barHeight = cornerHeight / barScaleFactor;

		Sprite TB = new Sprite(barTextureHoverOff, barTextureHoverOn, new Vector2f(0, 1), new Vector2f(1f-(cornerWidth * 2), barHeight));
		Sprite BB = new Sprite(barTextureHoverOff, barTextureHoverOn, new Vector2f(0, -1), new Vector2f(1f-(cornerWidth * 2), barHeight));
		BB.setRotation(180f);

		Sprite LB = new Sprite(barTextureHoverOff, barTextureHoverOn, new Vector2f(-1, 0), new Vector2f(barWidth, 1f-(cornerHeight * 2)));
		LB.setRotation(90f);
		Sprite RB = new Sprite(barTextureHoverOff, barTextureHoverOn, new Vector2f(1, 0), new Vector2f(barWidth, 1f-(cornerHeight * 2)));
		RB.setRotation(270f);

		sprites.add(TB);
		sprites.add(BB);
		sprites.add(LB);
		sprites.add(RB);

		Sprite BL = new Sprite(cornerTextureHoverOff, cornerTextureHoverOn, new Vector2f(-1, -1), new Vector2f(cornerWidth, cornerHeight));

		Sprite TR = new Sprite(cornerTextureHoverOff, cornerTextureHoverOn, new Vector2f(1, 1), new Vector2f(cornerWidth, cornerHeight));
		TR.setRotation(180f);

		Sprite TL = new Sprite(cornerTextureHoverOff, cornerTextureHoverOn, new Vector2f(-1, 1), new Vector2f(cornerWidth, cornerHeight));
		TL.setRotation(270f);

		Sprite BR = new Sprite(cornerTextureHoverOff, cornerTextureHoverOn, new Vector2f(1, -1), new Vector2f(cornerWidth, cornerHeight));
		BR.setRotation(90f);

		sprites.add(TL);
		sprites.add(TR);
		sprites.add(BL);
		sprites.add(BR);

		for (Component c: sprites) {
			c.displaceInFPS(isDisplayInFPS());
			super.add(c);
		}
	}

	protected void addComponents(int barTextureHoverOff, int cornerTextureHoverOff, float cornerScaleFactor, float barScaleFactor) {
		/* Calculates the width and height*/
		float cornerWidth = cornerScaleFactor / this.getScale().x;
		float cornerHeight = cornerScaleFactor / this.getScale().y * DisplayManager.ASPECT_RATIO;

		float barWidth = cornerWidth / barScaleFactor;
		float barHeight = cornerHeight / barScaleFactor;

		Sprite TB = new Sprite(barTextureHoverOff, new Vector2f(0, 1), new Vector2f(1f-(cornerWidth * 2), barHeight));
		Sprite BB = new Sprite(barTextureHoverOff, new Vector2f(0, -1), new Vector2f(1f-(cornerWidth * 2), barHeight));
		BB.setRotation(180f);

		Sprite LB = new Sprite(barTextureHoverOff, new Vector2f(-1, 0), new Vector2f(barWidth, 1f-(cornerHeight * 2)));
		LB.setRotation(90f);
		Sprite RB = new Sprite(barTextureHoverOff, new Vector2f(1, 0), new Vector2f(barWidth, 1f-(cornerHeight * 2)));
		RB.setRotation(270f);

		sprites.add(TB);
		sprites.add(BB);
		sprites.add(LB);
		sprites.add(RB);

		Sprite BL = new Sprite(cornerTextureHoverOff, new Vector2f(-1, -1), new Vector2f(cornerWidth, cornerHeight));

		Sprite TR = new Sprite(cornerTextureHoverOff, new Vector2f(1, 1), new Vector2f(cornerWidth, cornerHeight));
		TR.setRotation(180f);

		Sprite TL = new Sprite(cornerTextureHoverOff, new Vector2f(-1, 1), new Vector2f(cornerWidth, cornerHeight));
		TL.setRotation(270f);

		Sprite BR = new Sprite(cornerTextureHoverOff, new Vector2f(1, -1), new Vector2f(cornerWidth, cornerHeight));
		BR.setRotation(90f);

		sprites.add(TL);
		sprites.add(TR);
		sprites.add(BL);
		sprites.add(BR);

		for (Component c: sprites) {
			c.displaceInFPS(isDisplayInFPS());
			super.add(c);
		}
	}

	@Override
	public void onKeyTyped() {

	}
}
