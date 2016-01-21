package engine.core.loaders;

import engine.core.Terrain;
import engine.core.entity.Player;
import engine.render.texture.TerrainTexture;
import engine.render.texture.TerrainTexturePack;
import engine.render.texture.WaterTile;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mitch on 5/17/2015.
 */
public class TerrainLoader extends EntityLoader {

    private final int LOAD_CHUNK_MIN = 300;
    private final int LOAD_CHUNK_MAX = 500;

    private GLLoader loader;
    private Player player;

    private HashMap<String, TerrainTexture> terrainTextures = new HashMap<String, TerrainTexture>();
    private List<Terrain> terrains = new ArrayList<Terrain>();

    Vector2f[] chunks = new Vector2f[8];

    public TerrainLoader(GLLoader loader, Player player) {
        super(loader);
        this.loader = loader;
        this.player = player;
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }

    public boolean chunksChanged() {
        Vector2f[] temp = getClosestChunks();

        for (int i = 0; i < chunks.length; i++) {
            if (chunks[i] != temp[i]) {
                return true;
            }
        }
        return false;
    }

    public void updateTerrainChunks() {
        Vector2f[] chunks = getClosestChunks();

        if (chunks != null) {
            for (int i = 0; i < chunks.length; i++) {
                if (chunks[i] != null) {
                    if (!containChunk((int) chunks[i].x, (int) chunks[i].y)) {
                        Terrain temp = loadChunk((int) chunks[i].x, (int) chunks[i].y);
                        if(temp != null) {
                           terrains.add(temp);
                        }
                    }
                }
            }
        }
        //Removes unused chunks
        for (int i = 0; i < terrains.size(); i++) {
            if (terrains.get(i) != null) {
                if (!containsChunk(chunks, new Vector2f(terrains.get(i).getGridX(), terrains.get(i).getGridZ()), player)) {
                    deleteTerrain(terrains.get(i));
                    terrains.remove(i);
                }
            }
        }
    }

    public void loadCurrentChunk(Player player) {
        int gridX = (int) (player.getPositionSystem().getPosition().x / 800);
        int gridZ = (int) (player.getPositionSystem().getPosition().z / 800);
        Terrain t = loadChunk(gridX, gridZ);
        terrains.add(t);
    }

    public Terrain getCurrentChunk(Player player) {
        Terrain terrain = null;

        int gridX = (int) (player.getPositionSystem().getPosition().x / 800);
        int gridZ = (int) (player.getPositionSystem().getPosition().z / 800);
        for (Terrain t : terrains) {
            if (t != null) {
                if (t.getGridX() == gridX && t.getGridZ() == gridZ) {
                    terrain = t;
                }
            }
        }
        return terrain;
    }

    private void deleteTerrain(Terrain terrain) {
        List<Integer> Textures = new ArrayList<>();

        Textures.add(terrain.getBlendMap().getTextureID());
        Textures.add(terrain.getTexturePack().getBackgroundTexture().getTextureID());
        Textures.add(terrain.getTexturePack().getrTexture().getTextureID());
        Textures.add(terrain.getTexturePack().getgTexture().getTextureID());
        Textures.add(terrain.getTexturePack().getbTexture().getTextureID());

        loader.vaos.remove((Integer) terrain.getModel().getVaoID());
        GL30.glDeleteVertexArrays(terrain.getModel().getVaoID());

        for(int i = 0; i < terrain.getModel().getVbosID().size(); i++) {
            int v = terrain.getModel().getVbosID().get(i);
            loader.vbos.remove((Integer) v);
            GL15.glDeleteBuffers(v);
        }
    }

    private Terrain loadChunk(int chunkX, int chunkZ) {
        Terrain terrain = null;
        if (chunkExists(chunkX, chunkZ)) {
            FileReader isr = null;
            File terrainData = new File("res/terrain/" + chunkX + "-" + chunkZ + "/" + chunkX + "-" + chunkZ + ".txt");
            try {
                isr = new FileReader(terrainData);
            } catch (FileNotFoundException e) {
                System.err.println("File not found in res folder!");
                System.exit(0);
            }
            BufferedReader reader = new BufferedReader(isr);
            String line;
            boolean finished = false;

            String background = null, redTexture = null, greenTexture = null, blueTexture = null;
            List<WaterTile> waterTiles = new ArrayList<WaterTile>();

            try {
                while (!finished) {
                    line = reader.readLine();
                    if(!line.isEmpty()) {
                        if (line.startsWith("#background")) {
                            line = reader.readLine();
                            String[] temp = line.split("\t");
                            background = temp[0];
                            redTexture = temp[1];
                            greenTexture = temp[2];
                            blueTexture = temp[3];
                        } else if (line.startsWith("#water")) {
                            line = reader.readLine();
                            while(!line.startsWith("#END")) {
                                if (!line.isEmpty()) {
                                    String[] temp = line.split(",");
                                    waterTiles.add(new WaterTile(Float.parseFloat(temp[0]), Float.parseFloat(temp[1]), Float.parseFloat(temp[2])));
                                }
                                line = reader.readLine();
                            }
                        }
                    }
                    if (line.startsWith("#END")) {
                        finished = true;
                        reader.close();
                    }
                    if (background != null && redTexture != null && greenTexture != null && blueTexture != null) {
                        TerrainTexture backgroundTexture = getTexture(background);
                        TerrainTexture rTexture = getTexture(redTexture);
                        TerrainTexture gTexture = getTexture(greenTexture);
                        TerrainTexture bTexture = getTexture(blueTexture);

                        TerrainTexture blendMap = getTexture(chunkX + "-" + chunkZ + "/blendMap");

                        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
                        terrain = new Terrain(chunkX, chunkZ, loader, texturePack, blendMap, "heightmap", waterTiles);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading terrain chunk.");
            }
        } else {
//            System.err.println("Couldn't find terrain chunk " + chunkX + ", " + chunkZ + ".");
        }
        return terrain;
    }

    public TerrainTexture getTexture(String name) {
        TerrainTexture texture;
        if (terrainTextures.get(name) != null) {
            texture = terrainTextures.get(name);
        } else {
            texture = new TerrainTexture(loader.loadTerrainTexture(name));
            terrainTextures.put(name, texture);
        }
        return texture;
    }

    public boolean containChunk(int gridX, int gridZ) {
        boolean found = false;

        for (Terrain t: terrains) {
            if (t != null) {
                if (t.getGridX() == gridX && t.getGridZ() == gridZ) {
                    found = true;
                }
            }
        }
        return found;
    }

    public boolean containsChunk(Vector2f[] chunks, Vector2f terrain, Player player) {
        boolean found = false;

        if (chunks != null) {
            for (int i = 0; i < chunks.length; i++) {
                if (chunks[i] != null) {
                    if (terrain.x == chunks[i].x && terrain.y == chunks[i].y) {
                        found = true;
                    }
                }
            }
        }

        int gridX = (int) (player.getPositionSystem().getPosition().x / 800);
        int gridZ = (int) (player.getPositionSystem().getPosition().z / 800);
        if (gridX == terrain.x && gridZ == terrain.y) {
            found = true;
        }
        return found;
    }

    private Vector2f[] getClosestChunks() {
        Vector2f[] chunks = new Vector2f[3];
        int counter = 0;
        if (player != null) {
            int gridX = (int) (player.getPositionSystem().getPosition().x / 800);
            int gridZ = (int) (player.getPositionSystem().getPosition().z / 800);

            int x = (int) (player.getPositionSystem().getPosition().x % 800);
            int z = (int) (player.getPositionSystem().getPosition().z % 800);

            //Loads left and right relevant to current terrain
            if (x > LOAD_CHUNK_MAX) {
                int GX = gridX-1;
                chunks[counter] = new Vector2f(GX, gridZ);
                counter++;
            } else if (x < LOAD_CHUNK_MIN) {
                int GX = gridX+1;
                chunks[counter] = new Vector2f(GX, gridZ);
                counter++;
            }

            //Loads top and bottom relevant to current terrain
            if (z > LOAD_CHUNK_MAX) {
                int GZ = gridZ+1;
                chunks[counter] = new Vector2f(gridX, GZ);
                counter++;
            } else if (z < LOAD_CHUNK_MIN) {
                int GZ = gridZ-1;
                chunks[counter] = new Vector2f(gridX, GZ);
                counter++;
            }

            //Loads top and bottom of right corner relevant to current terrain
            if (z > LOAD_CHUNK_MAX && x < LOAD_CHUNK_MIN) {
                int GZ = gridZ+1;
                int GX = gridZ+1;
                chunks[counter] = new Vector2f(GX, GZ);
                counter++;
            } else if (z < LOAD_CHUNK_MIN && x < LOAD_CHUNK_MIN) {
                int GZ = gridZ-1;
                int GX = gridZ+1;
                chunks[counter] = new Vector2f(GX, GZ);
                counter++;
            }

            //Loads top and bottom of left corner relevant to current terrain
            if (z > LOAD_CHUNK_MAX && x > LOAD_CHUNK_MAX) {
                int GZ = gridZ+1;
                int GX = gridZ-1;
                chunks[counter] = new Vector2f(GX, GZ);
                counter++;
            } else if (z < LOAD_CHUNK_MIN && x > LOAD_CHUNK_MAX) {
                int GZ = gridZ-1;
                int GX = gridZ-1;
                chunks[counter] = new Vector2f(GX, GZ);
                counter++;
            }
        }
        if (counter == 0) {
            return null; //No closest terrain!
        } else {
            this.chunks = chunks;
            return chunks;
        }
    }

    private boolean chunkExists(int chunkX, int chunkZ) {
        File path = new File("res/terrain/" + chunkX + "-" + chunkZ + "/");
        return path.isDirectory();
    }

}
