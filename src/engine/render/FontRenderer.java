package engine.render;

import engine.core.tools.Settings;
import engine.render.font.FontType;
import engine.core.hud.component.Text;
import engine.render.shader.FontShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.Map;

public class FontRenderer {

	private static final float FADE_CONSTANT = .05f;

	private FontShader shader;

	public FontRenderer() {
		shader = new FontShader();
	}

	private float alpha = 0;

	public void render(Map<FontType, List<Text>> fps_texts, Map<FontType, List<Text>> gui_texts) {
		prepare();
		shader.loadAlpha(1);
		for (FontType font: fps_texts.keySet()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
			for (Text text : fps_texts.get(font)) {
				renderText(text);
			}
		}

		updateAlpha();

		if (!Settings.isFPSMode() || alpha != 0) { //Render GUIS
			shader.loadAlpha(alpha);
			for (FontType font : gui_texts.keySet()) {
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
				for (Text text : gui_texts.get(font)) {
					renderText(text);
				}
			}
		}

		endRendering();
	}

	public void cleanUp(){
		shader.cleanUp();
	}
	
	private void prepare(){
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		shader.start();
	}
	
	private void renderText(Text text){
		GL30.glBindVertexArray(text.getMesh());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		shader.loadColor(text.getColor());
		shader.loadTranslation(text.getPosition());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
	
	private void endRendering(){
		shader.stop();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
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
