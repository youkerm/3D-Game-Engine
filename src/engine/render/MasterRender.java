package engine.render;

import engine.core.Camera;
import engine.core.Light;
import engine.core.Terrain;
import engine.core.cycles.Component;
import engine.core.cycles.CycleHandler;
import engine.core.cycles.Cycles;
import engine.core.entity.Entity;
import engine.core.entity.component.AABBox;
import engine.core.entity.component.ModelSystem;
import engine.core.entity.component.Sphere;
import engine.core.loaders.GLLoader;
import engine.render.buffers.MinimapFrameBuffer;
import engine.render.buffers.WaterFrameBuffers;
import engine.render.manager.ParticleManager;
import engine.render.manager.TextManager;
import engine.render.shader.*;
import engine.render.texture.HudTexture;
import engine.render.texture.WaterTile;
import engine.render.tools.Frustum;
import engine.render.tools.Graphics;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRender {

	public static final float FOV = 70;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000f;

	private Map<ModelSystem, List<Entity>> entities = new HashMap<ModelSystem, List<Entity>>();
	private Map<ModelSystem, List<Entity>> normalEntities = new HashMap<ModelSystem, List<Entity>>();
	private List<AABBox> boundedAabb = new ArrayList<AABBox>();
	private List<Sphere> boundedSphere = new ArrayList<Sphere>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private List<WaterTile> waterTiles = new ArrayList<WaterTile>();
	private List<Light> lights = new ArrayList<Light>();

	private WaterFrameBuffers waterFrameBuffer;
	private MinimapFrameBuffer minimapFrameBuffer;
	private Frustum frustum;

	private Matrix4f projectionMatrix;

	private StaticShader staticShader = new StaticShader();
	private GroundObjectShader groundObjectShader = new GroundObjectShader();
	private BoundingShader boundingShader = new BoundingShader();
	private TerrainShader terrainShader = new TerrainShader();
	private WaterShader waterShader = new WaterShader();

	private EntityRenderer staticRenderer;
	private NormalRenderer normalRenderer;
	private GroundObjectRenderer groundObjectRenderer;
	private BoundingRender boundingRender;
	private TerrainRenderer terrainRender;
	private SkyboxRenderer skyboxRenderer;
    private WaterRenderer waterRenderer;
    private HudRenderer hudRenderer;
	private CycleHandler cycles;
	
	public MasterRender(GLLoader loader) {
		Graphics.enableCulling();
		createProjectionMatrix();
		waterFrameBuffer = new WaterFrameBuffers();
		minimapFrameBuffer = new MinimapFrameBuffer();
		frustum = new Frustum(projectionMatrix);
		staticRenderer = new EntityRenderer(staticShader, projectionMatrix, frustum);
		normalRenderer = new NormalRenderer(projectionMatrix);
		groundObjectRenderer = new GroundObjectRenderer(groundObjectShader, projectionMatrix, frustum);
		boundingRender = new BoundingRender(boundingShader, projectionMatrix, frustum);
		terrainRender = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
        waterRenderer = new WaterRenderer(loader, waterShader, projectionMatrix, waterFrameBuffer);
        hudRenderer = new HudRenderer(loader);
		cycles = new CycleHandler(new Component(loader.loadCubeMap(Cycles.DAY.TEXTURE_FILES), Cycles.DAY.FOG_COLOR, 0, 12000), new Component(loader.loadCubeMap(Cycles.NIGHT.TEXTURE_FILES), Cycles.NIGHT.FOG_COLOR, 12000, 12000));
		TextManager.init(loader);
		ParticleManager.init(loader, projectionMatrix);
	}

	public void renderBuildMode(List<Terrain> terrainList, List<Entity> entitiesList, List<Entity> normalList, List<Light> lightList, List<HudTexture> fpsHudList, List<HudTexture> guiHudList, Camera camera, Vector4f clipPlane, Entity buildEntity) {
		processScene(terrainList, new ArrayList<Entity>(), new ArrayList<Entity>(), new ArrayList<Light>());
		prepare(camera);
		minimapFrameBuffer.bindReflectionFrameBuffer();
		float pitch = camera.getPitch();
		float zoom = camera.getZoom();

		camera.setPitch(90);
		camera.setZoom(200);
		renderScene(camera, clipPlane);

		camera.setPitch(pitch);
		camera.setZoom(zoom);
		waterFrameBuffer.unbindCurrentFrameBuffer();

		processScene(terrainList, entitiesList, normalList, lightList);
		prepare(camera);
		processEntity(buildEntity);
		if (waterTiles.get(0) != null) {
 			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			//Render reflecting texture
			waterFrameBuffer.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - waterTiles.get(0).getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();

			processScene(terrainList, entitiesList, normalList, lightList);
			prepare(camera);
			processEntity(buildEntity);
			renderScene(camera, new Vector4f(0, 1, 0, -waterTiles.get(0).getHeight() - .5f));

			camera.getPosition().y += distance;
			camera.invertPitch();
			waterFrameBuffer.unbindCurrentFrameBuffer();

			//Render refraction texture
			waterFrameBuffer.bindRefractionFrameBuffer();

			processScene(terrainList, entitiesList, normalList, lightList);
			prepare(camera);
			processEntity(buildEntity);
			renderScene(camera, new Vector4f(0, -1, 0, waterTiles.get(0).getHeight()));

			waterFrameBuffer.unbindCurrentFrameBuffer();
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
		}

		processScene(terrainList, entitiesList, normalList, lightList);
		prepare(camera);
		processEntity(buildEntity);
		renderScene(camera, clipPlane);
		waterRenderer.render(terrainList, camera, engine.core.tools.Settings.getSun());
		ParticleManager.render(camera);
		hudRenderer.render(fpsHudList, guiHudList);
		TextManager.render();
	}

	public void render(List<Terrain> terrainList, List<Entity> entitiesList, List<Entity> normalList, List<Light> lightList, List<HudTexture> fpsHudList, List<HudTexture> guiHudList, Camera camera, Vector4f clipPlane) {
		renderBuildMode(terrainList, entitiesList, normalList, lightList, fpsHudList, guiHudList, camera, clipPlane, null);
	}

	private void renderScene(Camera camera, Vector4f clipPlane) {
		staticShader.start();
		staticShader.loadClipPlane(clipPlane);
		staticShader.loadFogDensity(CycleHandler.FOG_DENSTIY);
		staticShader.loadSkyColor(cycles.getFirstFog(), cycles.getSecondFog());
		staticShader.loadBlendFactor(cycles.getBlendFactor());
		staticShader.loadLights(lights);
		staticShader.loadViewMatrix(camera);
		staticRenderer.render(entities);
		staticShader.stop();

		normalRenderer.render(normalEntities, clipPlane, lights, camera, cycles.getFirstFog(), cycles.getSecondFog(), cycles.getBlendFactor());

        groundObjectShader.start();
        groundObjectShader.loadClipPlane(clipPlane);
        groundObjectShader.loadFogDensity(CycleHandler.FOG_DENSTIY);
        groundObjectShader.loadSkyColor(cycles.getFirstFog(), cycles.getSecondFog());
        groundObjectShader.loadBlendFactor(cycles.getBlendFactor());
        groundObjectShader.loadLights(lights);
        groundObjectRenderer.render(entities);//NEED TO FIX
        groundObjectShader.stop();


		boundingShader.start();
		boundingShader.loadViewMatrix(camera);
		boundingRender.render(boundedAabb, boundedSphere);
		boundingShader.stop();

		terrainShader.start();
		terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadFogDensity(CycleHandler.FOG_DENSTIY);
		terrainShader.loadSkyColor(cycles.getFirstFog(), cycles.getSecondFog());
		terrainShader.loadBlendFactor(cycles.getBlendFactor());
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRender.render(terrains);
		terrainShader.stop();

		if (clipPlane.w == 10000) {
			skyboxRenderer.render(camera, cycles.getFirstTexture(), cycles.getSecondTexture(), cycles.getFirstFog(), cycles.getSecondFog(), cycles.getBlendFactor());
		}
		terrains.clear();
		waterTiles.clear();
		entities.clear();
		normalEntities.clear();
		boundedAabb.clear();
		boundedSphere.clear();
		lights.clear();
	}

	private void processScene(List<Terrain> terrainList, List<Entity> entityList, List<Entity> normalList, List<Light> lightList) {
		for (Terrain t : terrainList) {
			processTerrain(t);
		}
		for (Entity e: entityList) {
			processEntity(e);
		}
		for (Entity e: normalList) {
			processNormalEntity(e);
		}
		processSunLight(engine.core.tools.Settings.getSun());
		for (Light l: lightList) {
			processLight(l);
		}
	}

	private void processTerrain(Terrain terrain) {
        if (terrain != null) {
            terrains.add(terrain);
			for (WaterTile tile : terrain.getWaterTiles()) {
				waterTiles.add(tile);
			}
        }
	}
	
	private void processEntity(Entity entity) {
        if (entity != null) {
            if (entity.getModelSystem() != null) {
                ModelSystem entityModel = entity.getModelSystem();
                List<Entity> batch = entities.get(entityModel);
                if (batch != null) {
                    batch.add(entity);
                } else {
                    List<Entity> newBatch = new ArrayList<Entity>();
                    newBatch.add(entity);
                    entities.put(entityModel, newBatch);
                }
				if (entity.getAABBox() != null) {
					boundedAabb.add(entity.getAABBox());
				}
				if (entity.getSphere() != null){
					boundedSphere.add(entity.getSphere());
				}
            }
        }
	}

	private void processNormalEntity(Entity entity) {
		if (entity != null) {
			if (entity.getModelSystem() != null) {
				ModelSystem entityModel = entity.getModelSystem();
				List<Entity> batch = normalEntities.get(entityModel);
				if (batch != null) {
					batch.add(entity);
				} else {
					List<Entity> newBatch = new ArrayList<Entity>();
					newBatch.add(entity);
					normalEntities.put(entityModel, newBatch);
				}
			}
		}
	}

	private void processSunLight(Light light) {
		if (light != null) {
			lights.add(0, light);
        }
	}

	private void processLight(Light light) {
        if (light != null) {
            lights.add(light);
        }
	}
	
	private void prepare(Camera camera){
		frustum.update(camera);
		cycles.update();
    	GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(cycles.getBackgroundColor().x, cycles.getBackgroundColor().y, cycles.getBackgroundColor().z, 1);
    }

	public void cleanUp() {
		waterFrameBuffer.cleanUp();
		staticShader.cleanUp();
		normalRenderer.cleanUp();
		terrainShader.cleanUp();
		waterShader.cleanUp();
		hudRenderer.cleanUp();
		ParticleManager.cleanUp();
		TextManager.cleanUp();
	}
	
	private void createProjectionMatrix() {
    	float aspect = Display.getWidth() / Display.getHeight();
    	float y_scale = (float) ((1F / Math.tan(Math.toRadians(FOV / 2F))) * aspect);
    	float x_scale = y_scale / aspect;
    	float frustrum_length = FAR_PLANE - NEAR_PLANE;

    	projectionMatrix = new Matrix4f();
    	projectionMatrix.m00 = x_scale;
    	projectionMatrix.m11 = y_scale;
    	projectionMatrix.m22 = -((FAR_PLANE - NEAR_PLANE) / frustrum_length);
    	projectionMatrix.m23 = -1;
    	projectionMatrix.m32 = -((2 * FAR_PLANE * NEAR_PLANE) / frustrum_length);
    	projectionMatrix.m33 = 0;
    }

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public WaterFrameBuffers getFBOs() {
		return waterFrameBuffer;
	}
	
}
