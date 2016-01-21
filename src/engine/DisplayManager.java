package engine;

import engine.core.tools.Settings;
import engine.core.tools.Time;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.DisplayMode;

import java.awt.*;

public class DisplayManager {
	
	public static float ASPECT_RATIO;
	public static int WIDTH, HEIGHT;
	private static int FPS_CAP;
    private static String TITLE;
	
	private static long lastFrameTime;
	private static float delta;

	private static long lastFPSTime = 0;
	private static int fps = 0;

	private static long variableYieldTime;
	private static long lastTime;

	public static void createWindowDisplay(int width, int height, int fps, String title) {
		WIDTH = width;
		HEIGHT = height;
        TITLE = title;
		FPS_CAP = fps;
		ASPECT_RATIO = (float) width / (float) height;

        ContextAttribs attribs = new ContextAttribs(3,3)
				.withForwardCompatible(true)
				.withProfileCore(true);

        try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat(), attribs);
			Display.setTitle(title);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		 GL11.glViewport(0, 0, width, height);
        lastFPSTime = Time.getCurrentTime();
	}

	public static void createFullscreenDisplay(int width, int height, int fps) {
		WIDTH = width;
		HEIGHT = height;
		FPS_CAP = fps;
		ASPECT_RATIO = (float) width / (float) height;

		ContextAttribs attribs = new ContextAttribs(3,3)
				.withForwardCompatible(true)
				.withProfileCore(true);
		try {
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			for (DisplayMode mode: modes) {
				System.out.println(mode);
			}

			System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
			Display.setDisplayMode(new DisplayMode(1920, 1080));
			Display.create(new PixelFormat(), attribs);
			Display.setFullscreen(true);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createPanelDisplay(Canvas canvas, int width, int height, int fps) {
		WIDTH = width;
		HEIGHT = height;
		FPS_CAP = fps;
		ASPECT_RATIO = (float) width / (float) height;

		ContextAttribs attribs = new ContextAttribs(3, 2)
				.withForwardCompatible(true)
				.withProfileCore(true);

        try {
            Display.setDisplayMode(new DisplayMode(width, height));
			Display.setParent(canvas);
            Display.create(new PixelFormat(), attribs);
			Display.setResizable(false);
			GL11.glViewport(0, 0, width, height);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
        lastFPSTime = Time.getCurrentTime();
	}
	
	public static void updateDisplay() {
        Display.sync(FPS_CAP);
		Display.update();

		long currentFrameTime = Time.getCurrentTime();
		delta = (currentFrameTime - lastFrameTime)/1000f;
		lastFrameTime = currentFrameTime;
	}

	/**
	 * An accurate sync method that adapts automatically
	 * to the system it runs on to provide reliable results.
	 *
	 * @param fps The desired frame rate, in fps per second
	 * @author kappa (On the LWJGL Forums)
	 */
	private static void sync(int fps) {
		if (fps <= 0) return;

		long sleepTime = 1000000000 / fps; // nanoseconds to sleep this frame
		// yieldTime + remainder micro & nano seconds if smaller than sleepTime
		long yieldTime = Math.min(sleepTime, variableYieldTime + sleepTime % (1000*1000));
		long overSleep = 0; // time the sync goes over by

		try {
			while (true) {
				long t = System.nanoTime() - lastTime;

				if (t < sleepTime - yieldTime) {
					Thread.sleep(1);
				} else if (t < sleepTime) {
					// burn the last few CPU cycles to ensure accuracy
					Thread.yield();
				} else {
					overSleep = t - sleepTime;
					break; // exit while loop
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lastTime = System.nanoTime() - Math.min(overSleep, sleepTime);

			// auto tune the time sync should yield
			if (overSleep > variableYieldTime) {
				// increase by 200 microseconds (1/5 a ms)
				variableYieldTime = Math.min(variableYieldTime + 200*1000, sleepTime);
			} else if (overSleep < variableYieldTime - 200*1000) {
				// decrease by 2 microseconds
				variableYieldTime = Math.max(variableYieldTime - 2*1000, 0);
			}
		}
	}
	
	public static float getDelta() {
		return delta;
	}
	
	public static void updateFPS() {
		if (Time.getCurrentTime() - lastFPSTime > 1000) {
			String text = String.format("%.1f", (fps * 1000.0 / (Time.getCurrentTime() - lastFPSTime)));
			Display.setTitle(TITLE + " - " + text);
			lastFPSTime += 1000;
			fps = 0;
		}
        fps++;
	}


    public static void closeDisplay() {
		Display.destroy();
		System.exit(0);
	}

}
