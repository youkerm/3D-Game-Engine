package engine.core.entity.component;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Mitch on 6/16/2015.
 */
public class Bounding {

    private Vector3f max, min;

    protected PositionSystem positionSystem;

    public Bounding(float[] vertices, PositionSystem positionSystem) {
        calculateBounds(vertices);
        this.positionSystem = positionSystem;
    }

    public Bounding(Vector3f max, Vector3f min, PositionSystem positionSystem) {
        this.max = max;
        this.min = min;
        this.positionSystem = positionSystem;
    }

    private void calculateBounds(float[] vertices) {
        if (max == null) {
            float maxX = 0, minX = 0,
                  maxY = 0, minY = 0,
                  maxZ = 0, minZ = 0;

            int i = 0;
            while (i < vertices.length) {
                //X position
                if (vertices[i] > maxX) {
                    maxX = vertices[i];
                } else if (vertices[i] < minX) {
                    minX = vertices[i];
                }
                i++; //Y position
                if (vertices[i] > maxY) {
                    maxY = vertices[i];
                } else if (vertices[i] < minY) {
                    minY = vertices[i];
                }
                i++;//Z position
                if (vertices[i] > maxZ) {
                    maxZ = vertices[i];
                } else if (vertices[i] < minZ) {
                    minZ = vertices[i];
                }
                i++;//Reset to x value
            }

            max = new Vector3f(maxX,maxY,maxZ);
            min = new Vector3f(minX,minY,minZ);
        }
    }

    protected Vector3f getMaxVertices() {
        return max;
    }

    protected Vector3f getMinVertices() {
        return min;
    }

    protected Vector3f getMinCoords() {
        Vector3f temp = new Vector3f(min.x, min.y, min.z);
        temp.scale(positionSystem.getScale());
        temp.translate(positionSystem.getPosition().getX(), positionSystem.getPosition().getY(), positionSystem.getPosition().getZ());
        return temp;
    }

    protected Vector3f getMaxCoords() {
        Vector3f temp = new Vector3f(max.x, max.y, max.z);
        temp.scale(positionSystem.getScale());
        temp.translate(positionSystem.getPosition().getX(), positionSystem.getPosition().getY(), positionSystem.getPosition().getZ());
        return temp;
    }

    public PositionSystem getPositionSystem() {
        return positionSystem;
    }

}
