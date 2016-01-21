package engine.render.texture;

import engine.core.loaders.GLLoader;

public class DefaultTextures {

	/* Textures used for containers */
	private int WINDOW_CORNER = -1;
	private int WINDOW_BAR = -1;
	private int WINDOW_BACKGROUND = -1;
	private int WINDOW_CLOSE = -1;

	/* Textures used for buttons with hover off */
	private int BUTTON_BACKGROUND_HOVER_OFF = -1;
	private int BUTTON_CORNER_HOVER_OFF = -1;
	private int BUTTON_BAR_HOVER_OFF = -1;

	/* Textures used for buttons with hover on */
	private int BUTTON_BACKGROUND_HOVER_ON = -1;
	private int BUTTON_CORNER_HOVER_ON = -1;
	private int BUTTON_BAR_HOVER_ON = -1;


	/* Textures used for text boxes */
	private int TEXTFIELD;
	private int HOVER_TEXTFIELD;
	
	
	/**
	 * Default constructor
	 * @param openGLLoader
	 */
	public DefaultTextures(GLLoader openGLLoader) {
		/* load window components */
		WINDOW_BACKGROUND = openGLLoader.loadHUDTexture("window/background");
		WINDOW_CORNER = openGLLoader.loadHUDTexture("window/corner");
		WINDOW_BAR = openGLLoader.loadHUDTexture("window/bar");
		WINDOW_CLOSE = openGLLoader.loadHUDTexture("window/close");

		/* load button components */
		BUTTON_BACKGROUND_HOVER_OFF = openGLLoader.loadHUDTexture("button/background_off");
		BUTTON_CORNER_HOVER_OFF = openGLLoader.loadHUDTexture("button/corner_off");
		BUTTON_BAR_HOVER_OFF = openGLLoader.loadHUDTexture("button/bar_off");

		BUTTON_BACKGROUND_HOVER_ON = openGLLoader.loadHUDTexture("button/background_on");
		BUTTON_CORNER_HOVER_ON = openGLLoader.loadHUDTexture("button/corner_on");
		BUTTON_BAR_HOVER_ON = openGLLoader.loadHUDTexture("button/bar_on");
	}
	
	/**
	 * Allows for dynamic textures
	 * @param corner
	 * @param bar
	 * @param background
	 */
	public DefaultTextures(int corner, int bar, int background) {
		WINDOW_CORNER = corner;
		WINDOW_BAR = bar;
		WINDOW_BACKGROUND = background;
	}


	/**
	 * Allows for dynamic textures
	 * @param corner
	 * @param bar
	 * @param background
	 */
	public DefaultTextures(int corner, int bar, int background, int hoverON, int hoverOFF) {
		this(corner, bar, background);
		BUTTON_BACKGROUND_HOVER_ON = hoverON;
		BUTTON_BACKGROUND_HOVER_OFF = hoverOFF;
	}

	/* Mutator methods to window textures */
	public void setWindowTexture(int background, int corner, int bar) {
		WINDOW_BACKGROUND = background;
		WINDOW_CORNER = corner;
		WINDOW_BAR = bar;
	}

	public void setWindowTexture(int background, int corner, int bar, int close) {
		setWindowTexture(background, corner, bar);
		WINDOW_CLOSE = close;
	}

	/* Accessors methods to window textures */
	public int getWindowBackgroundTexture() { return WINDOW_BACKGROUND; }
	public int getWindowCornerTexture() { return WINDOW_CORNER; }
	public int getWindowBarTexture() { return WINDOW_BAR; }
	public int getWindowCloseTexture() { return WINDOW_CLOSE; }




	/* Mutator methods to button textures */
	public void setButtonTexture(int backgroundOFF, int cornerOFF, int barOFF) {
		BUTTON_BACKGROUND_HOVER_OFF = backgroundOFF;
		BUTTON_CORNER_HOVER_OFF = cornerOFF;
		BUTTON_BAR_HOVER_OFF = barOFF;
	}

	public void setButtonTexture(int backgroundOFF, int backgroundON, int cornerOFF, int cornerON, int barOFF, int barON) {
		setButtonTexture(backgroundOFF, cornerOFF, barOFF);
		BUTTON_BACKGROUND_HOVER_ON = backgroundON;
		BUTTON_CORNER_HOVER_ON = cornerON;
		BUTTON_BAR_HOVER_ON = barON;
	}

	/* Accessors methods to button textures */
	public int getButtonBackgroundHoverOffTexture() { return BUTTON_BACKGROUND_HOVER_OFF; }
	public int getButtonBackgroundHoverOnTexture() { return BUTTON_BACKGROUND_HOVER_ON; }

	public int getButtonCornerHoverOffTexture() { return BUTTON_CORNER_HOVER_OFF; }
	public int getButtonCornerHoverOnTexture() { return BUTTON_CORNER_HOVER_ON; }

	public int getButtonBarHoverOffTexture() { return BUTTON_BAR_HOVER_OFF; }
	public int getButtonBarHoverOnTexture() { return BUTTON_BAR_HOVER_ON; }

}
