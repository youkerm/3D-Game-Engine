package engine.render.tools;

import engine.core.Camera;
import engine.core.entity.component.AABBox;
import engine.core.entity.component.Sphere;
import engine.core.tools.Maths;
import engine.render.MasterRender;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Frustum {

    private final static short TOP = 0;
    private final static short BOTTOM = 1;
    private final static short LEFT = 2;
    private final static short RIGHT = 3;
    private final static short FAR = 4;
    private final static short NEAR = 5;

    private Plane[] planes = new Plane[6];

    private Matrix4f projectionMatrix;

    public Frustum(Matrix4f projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
        for (int i = 0; i < 6; i++) {
            planes[i] = new Plane();
        }
    }

    /* For debug use only
    *  - Creates a Projection Matrix with the FOV of 50 degree instead of 70 degrees.
    *  - Allows use to see objects being rendered and not render on the sides of the screen.
    */
    private Matrix4f createProjection() {
        float FOV = 50f; //Default 70 degrees
        float aspect = Display.getWidth() / Display.getHeight();

        float y_scale = (float) ((1F / Math.tan(Math.toRadians(FOV / 2F))) * aspect);
        float x_scale = y_scale / aspect;
        float frustrum_length = MasterRender.FAR_PLANE - MasterRender.NEAR_PLANE;

        Matrix4f matrix = new Matrix4f();
        matrix.m00 = x_scale;
        matrix.m11 = y_scale;
        matrix.m22 = -((MasterRender.FAR_PLANE - MasterRender.NEAR_PLANE) / frustrum_length);
        matrix.m23 = -1;
        matrix.m32 = -((2 * MasterRender.FAR_PLANE * MasterRender.NEAR_PLANE) / frustrum_length);
        matrix.m33 = 0;
        return matrix;
    }

//    private void updatePlanes(Camera camera) {
//        Vector3f position = camera.getPosition();
//        Vector3f direction = camera.getDirection();
//        Vector3f up = camera.getUpVector();
//        Vector3f right = camera.getRightVector();
//
//        float ratio = (Display.getWidth() / Display.getHeight());
//
//        float tang = (float)Math.tan(Math.toRadians(70) * 0.5) ;
//
//        float nearH = tang * MasterRender.NEAR_PLANE;
//        float nearW = nearH * ratio;
//        float farH = tang * MasterRender.FAR_PLANE;
//        float farW = farH * ratio;
//
//
//        Vector3f fc = Vector3f.sub(position, (Vector3f) direction.scale(MasterRender.FAR_PLANE), null);
//
//        Vector3f upScaled = (Vector3f) up.scale(farH/2);
//        Vector3f rightScaled = (Vector3f) right.scale(farW/2);
//
//        Vector3f ftl = Vector3f.add(fc,  upScaled, null);
//                 ftl = Vector3f.sub(ftl, rightScaled, null);
//        Vector3f ftr = Vector3f.add(fc, upScaled, null);
//                 ftr = Vector3f.add(ftr, rightScaled, null);
//        Vector3f fbl = Vector3f.sub(fc, upScaled, null);
//                 fbl = Vector3f.sub(fbl, rightScaled, null);
//        Vector3f fbr = Vector3f.sub(fc, upScaled, null);
//                 fbr = Vector3f.add(fbr, rightScaled, null);
//
//        Vector3f nc = Vector3f.sub(position, (Vector3f) direction.scale(MasterRender.NEAR_PLANE), null);
//
//        upScaled = (Vector3f) up.scale(nearH/2);
//        rightScaled = (Vector3f) right.scale(nearW/2);
//
//        Vector3f ntl = Vector3f.add(nc, upScaled, null);
//                 ntl = Vector3f.sub(ntl, rightScaled, null);
//        Vector3f ntr = Vector3f.add(nc, upScaled, null);
//                 ntr = Vector3f.add(ntr, rightScaled, null);
//        Vector3f nbl = Vector3f.sub(nc, upScaled, null);
//                 nbl = Vector3f.sub(nbl, rightScaled, null);
//        Vector3f nbr = Vector3f.sub(nc, upScaled, null);
//                 nbr = Vector3f.add(nbr, rightScaled, null);
//
//        planes[TOP].update(ntr,ntl,ftl);
//        planes[BOTTOM].update(nbl,nbr,fbr);
//        planes[LEFT].update(ntl,nbl,fbl);
//        planes[RIGHT].update(nbr,ntr,fbr);
//        planes[NEAR].update(ntl,ntr,nbr);
//        planes[FAR].update(ftr,ftl,fbl);
//    }

//    public void update(Camera camera) {
//        updatePlanes(camera);
//    }

    public void update(Camera camera) {
        Matrix4f a = Matrix4f.mul(projectionMatrix, Maths.createViewMatrix(camera), null);
        // top
        planes[TOP].update(
                a.m03 - a.m01,
                a.m13 - a.m11,
                a.m23 - a.m21,
                a.m33 - a.m31);
        // bottom
        planes[BOTTOM].update(
                a.m03 + a.m01,
                a.m13 + a.m11,
                a.m23 + a.m21,
                a.m33 + a.m31);
        // left
        planes[LEFT].update(
                a.m03 + a.m00,
                a.m13 + a.m10,
                a.m23 + a.m20,
                a.m33 + a.m30);
        // right
        planes[RIGHT].update(
                a.m03 - a.m00,
                a.m13 - a.m10,
                a.m23 - a.m20,
                a.m33 - a.m30);
        // near
        planes[NEAR].update(
                a.m03 + a.m02,
                a.m13 + a.m12,
                a.m23 + a.m22,
                a.m33 + a.m32);
        // far
        planes[FAR].update(
                a.m03 - a.m02,
                a.m13 - a.m12,
                a.m23 - a.m22,
                a.m33 - a.m32);
    }

    public float distance(Sphere sphere) {
        if (sphere != null) {
            Vector3f center = sphere.getCenter();

            float distance = 0;
            for (int i = 0; i < 6; i++) {
                distance = planes[i].distance(center);
                if (distance <= -sphere.getRadius()) {
                    return 0;
                }
            }
            return distance + sphere.getRadius();
        }
        return 0;
    }

    public boolean contains(Sphere sphere) {
        if (sphere != null) {
            Vector3f center = sphere.getCenter();

            float distance;
            for (int i = 0; i < 6; i++) {
                distance = planes[i].distance(center);
                if (distance <= -sphere.getRadius()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean contains(AABBox box) {
        if (box != null) {
            for (Vector3f point:box.getBounds()) {
                if (contains(point)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean contains(Vector3f v) {
        for (int i = 0; i < 6; i++) {
            if (planes[i].distance(v) < 0.0f) {
                return false;
            }
        }
        return true;
    }

}