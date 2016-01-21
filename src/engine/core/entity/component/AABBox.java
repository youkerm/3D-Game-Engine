package engine.core.entity.component;


import engine.core.loaders.GLLoader;
import engine.core.model.RawModel;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;

public class AABBox extends Bounding implements Component {

	private RawModel box;
    private GLLoader loader;

    public AABBox(Vector3f max, Vector3f min, PositionSystem positionSystem, GLLoader loader) {
        super(max, min, positionSystem);
        this.loader = loader;
        this.box = loader.loadToVAO(createBoxVertices(), 3);
    }

	public AABBox(float[] vertices, PositionSystem positionSystem, GLLoader loader) {
		super(vertices, positionSystem);
        this.loader = loader;
		this.box = loader.loadToVAO(createBoxVertices(), 3);
	}

    private float[] createBoxVertices() {
        Vector3f min = getMinVertices();
        Vector3f max = getMaxVertices();
        return new float[] {
                min.x, max.y, min.z,
                min.x, min.y, min.z,
                max.x, min.y, min.z,
                max.x, min.y, min.z,
                max.x, max.y, min.z,
                min.x, max.y, min.z,

                min.x, min.y, max.z,
                min.x, min.y, min.z,
                min.x, max.y, min.z,
                min.x, max.y, min.z,
                min.x, max.y, max.z,
                min.x, min.y, max.z,

                max.x, min.y, min.z,
                max.x, min.y, max.z,
                max.x, max.y, max.z,
                max.x, max.y, max.z,
                max.x, max.y, min.z,
                max.x, min.y, min.z,

                min.x, min.y, max.z,
                min.x, max.y, max.z,
                max.x, max.y, max.z,
                max.x, max.y, max.z,
                max.x, min.y, max.z,
                min.x, min.y, max.z,

                min.x, max.y, min.z,
                max.x, max.y, min.z,
                max.x, max.y, max.z,
                max.x, max.y, max.z,
                min.x, max.y, max.z,
                min.x, max.y, min.z,

                min.x, min.y, min.z,
                min.x, min.y, max.z,
                max.x, min.y, min.z,
                max.x, min.y, min.z,
                min.x, min.y, max.z,
                max.x, min.y, max.z
        };
    }

	public boolean getIntersection(Vector3f ray, Vector3f direction) {
		Vector3f inverse = new Vector3f(1/direction.x, 1/direction.y, 1/direction.z);

		Vector3f min = super.getMinCoords();
		Vector3f max = super.getMaxCoords();

        float tx1 = (min.x - ray.x) * inverse.x;
        float tx2 = (max.x - ray.x) * inverse.x;

        float tmin = Math.min(tx1, tx2);
        float tmax = Math.max(tx1, tx2);

        float ty1 = (min.y - ray.y)*inverse.y;
        float ty2 = (max.y - ray.y)*inverse.y;

        tmin = Math.max(tmin, Math.min(ty1, ty2));
        tmax = Math.min(tmax, Math.max(ty1, ty2));

        float tz1 = (min.z - ray.z) * inverse.z;
        float tz2 = (max.z - ray.z) * inverse.z;
        tmin = Math.max(tmin, Math.min(tz1, tz2));
        tmax = Math.min(tmax, Math.max(tz1, tz2));

        return tmax >= tmin;
	}

    public ArrayList<Vector3f> getBounds() {
        ArrayList<Vector3f> points = new ArrayList<Vector3f>();
        Vector3f max = super.getMaxCoords();
        Vector3f min = super.getMinCoords();

        points.add(new Vector3f(min.x, max.y, max.z));
        points.add(new Vector3f(max.x, max.y, max.z));
        points.add(new Vector3f(max.x, max.y, min.z));
        points.add(new Vector3f(min.x, max.y, min.z));

        points.add(new Vector3f(min.x, min.y, max.z));
        points.add(new Vector3f(max.x, min.y, max.z));
        points.add(new Vector3f(max.x, min.y, min.z));
        points.add(new Vector3f(min.x, min.y, min.z));
        return points;
    }

	public RawModel getModel() {
		return box;
	}

    @Override
    public Component clone() {
        return new AABBox(super.getMaxVertices(), super.getMinVertices(), (PositionSystem) super.getPositionSystem().clone(), loader);
    }

    @Override
	public ID getID() {
		return ID.AABB_BOX;
	}
}
