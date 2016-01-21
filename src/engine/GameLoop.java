package engine;

import engine.core.*;
import engine.core.entity.Entity;
import engine.core.entity.Player;
import engine.core.entity.component.AABBox;
import engine.core.entity.component.ModelSystem;
import engine.core.entity.component.PositionSystem;
import engine.core.entity.component.Sphere;
import engine.core.hud.component.Button;
import engine.core.hud.component.Text;
import engine.core.hud.component.TextField;
import engine.core.hud.containers.Window;
import engine.core.hud.interfaces.Chatbox;
import engine.core.hud.interfaces.Minimap;
import engine.core.loaders.GLLoader;
import engine.core.loaders.GameLoader;
import engine.core.model.ModelData;
import engine.core.model.RawModel;
import engine.core.particle.ParticleEmitter;
import engine.core.tools.MousePicker;
import engine.core.tools.OBJLoader;
import engine.input.IOManager;
import engine.render.MasterRender;
import engine.render.manager.TextManager;
import engine.render.manager.HUDManager;
import engine.render.manager.ParticleManager;
import engine.render.texture.DefaultTextures;
import engine.render.texture.ModelTexture;
import engine.render.texture.ParticleTexture;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.awt.Canvas;
import java.util.ArrayList;
import java.util.List;

public class GameLoop {
	
	private GLLoader loader;
	private MasterRender renderer;

	private boolean isRunning;
	
	private int HEIGHT;
	private int WIDTH;
	private int FPS_CAP;

	public GameLoop(int width, int height, int fps) {
		this.isRunning = false;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.FPS_CAP = fps;
	}
	
	public void createWindow(String title) {
		initializeWindowDisplay(title);
	}
	
	public void createCanvas(Canvas canvas) {
		canvas.setSize(WIDTH, HEIGHT);
		canvas.setIgnoreRepaint(true);
		initializePanelDisplay(canvas);
	}

	public void createFullscreenDisplay() {
		DisplayManager.createFullscreenDisplay(WIDTH, HEIGHT, FPS_CAP);
	}
	
	private void initializeWindowDisplay(String title) {
		DisplayManager.createWindowDisplay(WIDTH, HEIGHT, FPS_CAP, title);
	}
	
	private void initializePanelDisplay(Canvas canvas) {
		DisplayManager.createPanelDisplay(canvas, WIDTH, HEIGHT, FPS_CAP);
	}

	public void start() {
		isRunning = true;
		
		loader = new GLLoader();
		renderer = new MasterRender(loader);
	}
	
	public void stop() {
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
	
	private boolean isRunning() {
		return isRunning;
	}
	
	public void run() {
		ModelData player_data = OBJLoader.loadOBJ("person");
		RawModel player_rawModel = loader.loadToVAO(player_data.getVertices(), player_data.getTextureCoords(), player_data.getNormals(), player_data.getIndices());
		ModelSystem model = new ModelSystem(player_rawModel, new ModelTexture(loader.loadTexture("person")));
		PositionSystem position = new PositionSystem(new Vector3f(0,0,0), new Vector3f(0,0,0), 1f);
		AABBox box = new AABBox(player_data.getVertices(), position, loader);
		Sphere sphere = new Sphere(player_data.getVertices(), position, loader);
		Player player = new Player(model, position, box, sphere);

		GameLoader game = new GameLoader(loader, player);

		RawModel crate_raw = OBJLoader.loadNormalMapOBJ("bridge", loader);
		ModelSystem crate_model = new ModelSystem(crate_raw, new ModelTexture(loader.loadTexture("bridge")));
		PositionSystem barrel_position = new PositionSystem(new Vector3f(20,10,20), new Vector3f(0,0,0), 1f);

		crate_model.getTexture().setNormalMap(loader.loadTexture("bridgeNormal"));
		crate_model.getTexture().setShineDamper(10);
		crate_model.getTexture().setReflectivity(.2f);

		Entity barrel = new Entity(crate_model, barrel_position);
		game.addNormalEntity(barrel);

		IOManager input = new IOManager();
		Camera camera = new Camera(player);
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), game.getCurrentChunk(player));

		List<Light> lights = new ArrayList<Light>();

		DefaultTextures textures = new DefaultTextures(loader);

//		Window test = new Window(textures, new Vector2f(0f, 0f), new Vector2f(.5f, .5f), "null", true);
//		Window test2 = new Window(textures, new Vector2f(1f, -1f), new Vector2f(.25f, .25f), "null", true);
//		TextField button = new TextField(textures, new Vector2f(0f,0f), new Vector2f(.075f, .075f), "m8!");
//		test.add(button);

		Chatbox chat = new Chatbox(textures, new Vector2f(-1, -1), new Vector2f(.40f, .25f));
		chat.displaceInFPS(true);
		HUDManager.addComponent(chat);

//		TextField test1 = new TextField(textures, new Vector2f(.5f, .5f), new Vector2f(.5f, .1f), 0f, "Test: ");
//		TextField test2 = new TextField(textures, new Vector2f(.5f, -.5f), new Vector2f(.5f, .1f), 1f, "Test: ");
//		TextField test3 = new TextField(textures, new Vector2f(-.5f, .5f), new Vector2f(.25f, .4f), 0f, "Test: ");
//		TextField test4 = new TextField(textures, new Vector2f(-.5f, -.5f), new Vector2f(.1f, .2f), 1f, "Test: ");
//		HUDManager.addComponent(test1);
//		HUDManager.addComponent(test2);
//		HUDManager.addComponent(test3);
//		HUDManager.addComponent(test4);

		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4, true);

		ParticleEmitter system = new ParticleEmitter(particleTexture, 40, 5, -0f, 10, 1f);
		system.setDirection(new Vector3f(0, 20, 0), 2f);
		system.setLifeError(0.1f);
		system.setSpeedError(0.1f);
		system.setScaleError(0.9f);
		system.randomizeRotation();

		while (isRunning()) {
			System.out.println("X: " + Mouse.getX()  + ", Y:" + Mouse.getY());
			//Game Logic
			camera.move();
			input.update();
			game.update();
 			picker.update();

			if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
				system.generateParticles(player.getPositionSystem().getPosition());
			}
			ParticleManager.update(camera);

			player.move(game.getCurrentChunk(player));


			//Rendering logic
			DisplayManager.updateFPS();
			renderer.renderBuildMode(game.getTerrains(), game.getEntities(), game.getNormalEntities(), lights, HUDManager.getFPSHUDs(), HUDManager.getGUIHUDs(), camera, new Vector4f(0, 1, 0, 10000), null);
			TextManager.render();
			DisplayManager.updateDisplay();

			//Quiting
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				isRunning = false;
			}
		}
	}

}
