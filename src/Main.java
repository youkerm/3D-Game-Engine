import engine.GameLoop;

public class Main {

	private static int FPS_CAP = 1000;

	/* The width and height for a window. */
	private static int WIDTH = 1280;
	private static int HEIGHT = 720;

	/* The height and width for fullscreen mode. */
//	private static int WIDTH = 1920;
//	private static int HEIGHT = 1080;

	public static void main(String[] args) {
		System.out.println("Starting game.");
		GameLoop gameLoop = new GameLoop(WIDTH, HEIGHT, FPS_CAP);
		
		gameLoop.createWindow("The Forgotten Era");
		gameLoop.start();
		gameLoop.run();
	}

}
