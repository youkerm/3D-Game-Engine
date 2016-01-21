package engine.core.loaders;

import engine.core.Terrain;
import engine.core.entity.Entity;
import engine.core.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitch on 9/14/2015.
 */
public class GameLoader extends TerrainLoader {

    private List<Entity> entities = new ArrayList<Entity>();
    private List<Entity> normalEntities = new ArrayList<Entity>();
    private List<Entity> added_entities = new ArrayList<Entity>();
    private List<Terrain> terrains = new ArrayList<Terrain>();

    private EntityLoader entityLoader;
    private Player player;

    public GameLoader(GLLoader loader, Player player) {
        super(loader, player);
        this.entityLoader = new EntityLoader(loader);
        this.player = player;
        start();
    }

    private void start() {
        super.loadCurrentChunk(player);
        super.updateTerrainChunks();
        terrains = super.getTerrains();
        entities = entityLoader.loadObjects(super.getCurrentChunk(player));
        entities.add(player);
    }

    public void update() {
        if (super.chunksChanged()) {
            super.updateTerrainChunks();
            terrains = super.getTerrains();
            entities = entityLoader.loadObjects(super.getCurrentChunk(player));
        }
        for (Entity entity: added_entities) {
            if (!entities.contains(entity)) {
                entities.add(entity);
            }
        }
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }

    public void addEntity(Entity entity) {
        try {
            Entity copy = entity.clone();
            added_entities.add(copy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addNormalEntity(Entity entity) {
        try {
            Entity copy = entity.clone();
            normalEntities.add(copy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Entity> getNormalEntities() {
        return normalEntities;
    }

}
