package engine.render;

import engine.core.loaders.GLLoader;
import engine.core.model.RawModel;
import engine.core.tools.Maths;
import engine.core.tools.Settings;
import engine.render.shader.HudShader;
import engine.render.texture.HudTexture;
import engine.render.tools.Graphics;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;

import java.util.List;

public class HudRenderer {

	private static final float FADE_CONSTANT = .05f;
	
	private final RawModel quad;
	private HudShader shader;

	private float alpha = 0;

	public HudRenderer(GLLoader openGLLoader) {
							//(TL),  (BL),  (TR),  (BR)
		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
		quad = openGLLoader.loadToVAO(positions, 2);
		shader = new HudShader();
	}
	
	public void render(List<HudTexture> fpsHuds, List<HudTexture> guiHuds) {
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);

		Graphics.enable2D();
		shader.loadAlpha(1);
		for (HudTexture hud: fpsHuds) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, hud.getTexture());

			Graphics.enable2DFilters();

			Matrix4f matrix = Maths.createTransformationMatrix(hud.getPosition(), hud.getRotation(), hud.getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}


		updateAlpha();

		if (!Settings.isFPSMode() || alpha != 0) { //Render GUIS
			shader.loadAlpha(alpha);
			for (HudTexture hud : guiHuds) {
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, hud.getTexture());

				Graphics.enable2DFilters();

				Matrix4f matrix = Maths.createTransformationMatrix(hud.getPosition(), hud.getRotation(), hud.getScale());
				shader.loadTransformation(matrix);
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
			}
		}
		Graphics.enable3D();
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	public void cleanUp() {
		shader.cleanUp();
	}

	private void updateAlpha() {
		if (!Settings.isFPSMode()) { //Render GUIS
			if (alpha < 1.00) {
				alpha += FADE_CONSTANT;
				alpha = alpha % 1.05f;
			} else {
				alpha = 1.0f;
			}
		} else {
			if (alpha > 0) {
				alpha -= FADE_CONSTANT;
			} else {
				alpha = 0;
			}
		}
	}

}
