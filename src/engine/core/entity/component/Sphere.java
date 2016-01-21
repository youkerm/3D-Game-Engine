package engine.core.entity.component;

import engine.core.loaders.GLLoader;
import engine.core.model.RawModel;
import engine.core.model.Vertex;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Sphere extends Bounding implements Component {

	private final int SPACE = 10; //the number of division in the sphere

	private RawModel model;
	private GLLoader loader;

	public Sphere(Vector3f max, Vector3f min, PositionSystem positionSystem, GLLoader loader) {
		super(max, min, positionSystem);
		this.loader = loader;
        model = loader.loadToVAO(createVertices(), 3);
	}

	public Sphere(float[] vertices, PositionSystem positionSystem, GLLoader loader) {
		super(vertices, positionSystem);
		this.loader = loader;
        model = loader.loadToVAO(createVertices(), 3);
    }

	public float getRadius() {
        Vector3f max = getMaxVertices();
        Vector3f min = getMinVertices();
        Vector3f temp = Vector3f.sub(max, min, null);

        float radius = Math.abs(temp.x)/2;
        if (radius < Math.abs(temp.y)/2) {
            radius = Math.abs(temp.y)/2;
        }
        if (radius < Math.abs(temp.z)/2) {
            radius = Math.abs(temp.z)/2;
        }
		return radius * positionSystem.getScale();
	}

	public Vector3f getCenter() {
		Vector3f max = getMaxCoords();
		Vector3f min = getMinCoords();
		return new Vector3f((max.x + min.x)/2, (max.y + min.y)/2, (max.z + min.z)/2);
	}

	public RawModel getModel() {
		return model;
	}

	private float[] createVertices() {
		List<Vertex> vertices = new ArrayList<Vertex>();
		
		double a;
		double b;

        float x = 0;
        float y = 0;
        float z = 0;

        float radius = getRadius()/positionSystem.getScale();

		for (b = 0; b <= 180; b += SPACE) {
			for (a = 0; a <= 360; a += SPACE) {
				Vector3f position = new Vector3f();
				
				position.x = (float) (radius * Math.sin((a) / 180 * Math.PI) * Math.sin((b) / 180 * Math.PI) - x);
				position.y = (float) (radius * Math.cos((a) / 180 * Math.PI) * Math.sin((b) / 180 * Math.PI) + y);
				position.z = (float) (radius * Math.cos((b) / 180 * Math.PI) - z);
				vertices.add(new Vertex(0, position));

				position.x = (float) (radius * Math.sin((a) / 180 * Math.PI) * Math.sin((b + SPACE) / 180 * Math.PI) - x);
				position.y = (float) (radius * Math.cos((a) / 180 * Math.PI) * Math.sin((b + SPACE) / 180 * Math.PI) + y);
				position.z = (float) (radius * Math.cos((b + SPACE) / 180 * Math.PI) - z);
				vertices.add(new Vertex(0, position));

				position.x = (float) (radius * Math.sin((a + SPACE) / 180 * Math.PI) * Math.sin((b) / 180 * Math.PI) - x);
				position.y = (float) (radius * Math.cos((a + SPACE) / 180 * Math.PI) * Math.sin((b) / 180 * Math.PI) + y);
				position.z = (float) (radius * Math.cos((b) / 180 * Math.PI) - z);
				vertices.add(new Vertex(0, position));
				
				position.x = (float) (radius * Math.sin((a + SPACE) / 180 * Math.PI) * Math.sin((b + SPACE) / 180 * Math.PI) - x);
				position.y = (float) (radius * Math.cos((a + SPACE) / 180 * Math.PI) * Math.sin((b + SPACE) /180 * Math.PI) + y);
				position.z = (float) (radius * Math.cos((b + SPACE) / 180 * Math.PI) - z);
				vertices.add(new Vertex(0, position));
			}
		}
		for (a = 0; a <= 360; a += SPACE) {
			for (b = 0; b <= 180; b += SPACE) {
				Vector3f position = new Vector3f();

				position.x = (float) (radius * Math.sin((a) / 180 * Math.PI) * Math.sin((b) / 180 * Math.PI) - x);
				position.y = (float) (radius * Math.cos((a) / 180 * Math.PI) * Math.sin((b) / 180 * Math.PI) + y);
				position.z = (float) (radius * Math.cos((b) / 180 * Math.PI) - z);
				vertices.add(new Vertex(0, position));

				position.x = (float) (radius * Math.sin((a) / 180 * Math.PI) * Math.sin((b + SPACE) / 180 * Math.PI) - x);
				position.y = (float) (radius * Math.cos((a) / 180 * Math.PI) * Math.sin((b + SPACE) / 180 * Math.PI) + y);
				position.z = (float) (radius * Math.cos((b + SPACE) / 180 * Math.PI) - z);
				vertices.add(new Vertex(0, position));

				position.x = (float) (radius * Math.sin((a + SPACE) / 180 * Math.PI) * Math.sin((b) / 180 * Math.PI) - x);
				position.y = (float) (radius * Math.cos((a + SPACE) / 180 * Math.PI) * Math.sin((b) / 180 * Math.PI) + y);
				position.z = (float) (radius * Math.cos((b) / 180 * Math.PI) - z);
				vertices.add(new Vertex(0, position));

				position.x = (float) (radius * Math.sin((a + SPACE) / 180 * Math.PI) * Math.sin((b + SPACE) / 180 * Math.PI) - x);
				position.y = (float) (radius * Math.cos((a + SPACE) / 180 * Math.PI) * Math.sin((b + SPACE) /180 * Math.PI) + y);
				position.z = (float) (radius * Math.cos((b + SPACE) / 180 * Math.PI) - z);
				vertices.add(new Vertex(0, position));
			}
		}
        float[] floats = new float[vertices.size()*3];
        int i = 0, counter = 0;
        while(counter < vertices.size()) {
            floats[i] = vertices.get(counter).getPosition().x;
            i++;
            floats[i] = vertices.get(counter).getPosition().y;
            i++;
            floats[i] = vertices.get(counter).getPosition().z;
            i++;
            counter++;
        }
		return floats;
	}

	@Override
	public Component clone() {
		return new Sphere(super.getMaxVertices(), super.getMinVertices(), (PositionSystem) super.getPositionSystem().clone(), loader);
	}

	@Override
	public ID getID() {
		return ID.SHPERE;
	}

}
