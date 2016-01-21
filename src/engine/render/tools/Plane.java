package engine.render.tools;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**
 * Created by Mitch on 1/7/2016.
 */
public class Plane {
    private Vector3f normal = new Vector3f();
    private float d;

    public Plane() {}

    public Plane(Vector3f v1, Vector3f v2, Vector3f v3) {
        calculateNormal(v1, v2, v3);
    }

    public void update(Vector3f v1, Vector3f v2, Vector3f v3) {
        calculateNormal(v1, v2, v3);
    }

    private void calculateNormal(Vector3f v1, Vector3f v2, Vector3f v3) {
        Vector3f aux1, aux2;

        aux1 = Vector3f.sub(v1, v2, null);
        aux2 = Vector3f.sub(v3, v2, null);

        normal = Vector3f.cross(aux2, aux1, null);

        normal.normalise(null);
        d = -(Vector3f.dot(normal, v2));
    }

    public void update(float a, float b, float c, float d) {
        // set the normal vector
        normal.set(a,b,c);
        //compute the lenght of the vector
        float l = normal.length();
        // normalize the vector
        normal.set(a/l,b/l,c/l);
        // and divide d by th length as well
        this.d = d/l;
    }

    public float distance(Vector3f vector) {
        return (d + Vector3f.dot(normal, vector));
    }

}
