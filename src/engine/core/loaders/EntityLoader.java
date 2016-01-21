package engine.core.loaders;

import engine.core.Terrain;
import engine.core.entity.Entity;
import engine.core.tools.OBJLoader;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitch on 5/17/2015.
 */
public class EntityLoader {

    private GLLoader loader;

    public EntityLoader(GLLoader loader) {
        this.loader = loader;
    }

    public List<Entity> loadObjects(Terrain terrain) {
        List<Entity> entities = new ArrayList<>();

        FileReader isrSpawn = null;
        File spawn = new File("res/terrain/" + terrain.getGridX() + "-" + terrain.getGridZ() + "/spawn.txt");
        try {
            isrSpawn = new FileReader(spawn);
        } catch (FileNotFoundException e) {
            System.err.println("File not found in res folder!");
            System.exit(-1);
        }
        BufferedReader readerSpawn = new BufferedReader(isrSpawn);
        String lineSpawn;
        try {
            while (true) {
                lineSpawn = readerSpawn.readLine();
                if (!lineSpawn.startsWith("#")) {
                    if(!lineSpawn.isEmpty()) {
                        String[] temp = lineSpawn.split("\t");
                        int id = Integer.parseInt(temp[0]);
                        float x = Float.parseFloat(temp[1]);
                        float y = Float.parseFloat(temp[2]);
                        float z = Float.parseFloat(temp[3]);
                        float rotX = Float.parseFloat(temp[4]);
                        float rotY = Float.parseFloat(temp[5]);
                        float rotZ = Float.parseFloat(temp[6]);
                        float scale = Float.parseFloat(temp[7]);

                        if(y == 0) {
                            y = terrain.getHeightOfTerrain(x+terrain.getX(), z+terrain.getZ());
                        }
                        Vector3f position = new Vector3f((x+terrain.getX()),y,(z+terrain.getZ()));
                        Vector3f rotation = new Vector3f(rotX,rotY,rotZ);

                        Entity entity = OBJLoader.loadEntity(id, loader);
                        entity.getPositionSystem().setPosition(position);
                        entity.getPositionSystem().setRotation(rotation);
                        entity.getPositionSystem().setScale(scale);
                        entities.add(entity);
                    }
                }
                if (lineSpawn.startsWith("#END")) {
                    break;
                }
            }
            readerSpawn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return entities;
    }

}
