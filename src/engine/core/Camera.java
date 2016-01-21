package engine.core;

import engine.core.entity.Player;
import engine.core.tools.Settings;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

    private float distanceFromPlayer = 60;

    private float angleAroundPlayer = 0;

    private Vector3f position = new Vector3f(0, 0, 0);

    private float pitch = 30;
    private float yaw = 0;
    private float offX;
    private float offZ;

    private Player player;

    public Camera(Player player) {
        this.player = player;
    }

    public void move() {
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getPitch() {
        return pitch;
    }

    public void invertPitch() {
        this.pitch = -pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return yaw;
    }

    public float getZoom() {
        return distanceFromPlayer;
    }

    public void setZoom(float zoom) {
        distanceFromPlayer = zoom;
    }

    private void calculateCameraPosition(float horizDistance, float verticDistance) {
        float thata = player.getPositionSystem().getRotation().y + angleAroundPlayer;
        float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(thata)));
        float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(thata)));

        if (offsetX == offX && offsetZ == offZ) {
            this.yaw = (180 - (thata)) % 360; //Simple calculation so Yaw doesn't go over 360

            float targetX = player.getPositionSystem().getPosition().x - offsetX;
            float targetY = player.getPositionSystem().getPosition().y + verticDistance;
            float targetZ = player.getPositionSystem().getPosition().z - offsetZ;


            position.x += 0.5f * (targetX - position.x);
            position.y += 0.5f * (targetY - position.y);
            position.z += 0.5f * (targetZ - position.z);
        } else {
            this.yaw = (180 - (thata)) % 360; //Simple calculation so Yaw doesn't go over 360

            float targetX = player.getPositionSystem().getPosition().x - offsetX;
            float targetY = player.getPositionSystem().getPosition().y + verticDistance;
            float targetZ = player.getPositionSystem().getPosition().z - offsetZ;


            position.x = targetX;
            position.y = targetY;
            position.z = targetZ;
        }
        offX = offsetX;
        offZ = offsetZ;
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateZoom() {
        float zoomLevel = (Mouse.getDWheel() * 0.1f);
        if (zoomLevel > 60) {
            zoomLevel = 60;
        } else if (zoomLevel < -60) {
            zoomLevel = -60;
        }
        float targetDistanceFromPlayer = distanceFromPlayer - zoomLevel;
        if (targetDistanceFromPlayer < 100 && targetDistanceFromPlayer > 15) {
                distanceFromPlayer += 0.2f * (targetDistanceFromPlayer - distanceFromPlayer);
        }
    }

    private void calculatePitch() {
        if (Mouse.isButtonDown(1) || Mouse.isButtonDown(2)) {
            float pitchChange = Mouse.getDY() * 0.2f;
            float targetPitchChange = pitch - pitchChange;
            if (targetPitchChange < 90 && targetPitchChange > 10) {
                pitch += 0.5f * (targetPitchChange - pitch);
            }
        }
    }

    private void calculateAngleAroundPlayer() {
        float SENSITIVITY = 200;
        float SMOOTHING_SENSITIVITY = .25f;

        if (angleAroundPlayer != 0 && Settings.isFPSMode()) {
            float targetAngleChange = angleAroundPlayer/1.125f;
            angleAroundPlayer += 0.5 * (targetAngleChange - angleAroundPlayer);
            angleAroundPlayer = angleAroundPlayer % 360;
        }

        float mouseX = Mouse.getDX() % SENSITIVITY;
        float angleChange = mouseX * 0.5f;

        if (Settings.isFPSMode()) {
            float targetAngleChange = player.getPositionSystem().getRotation().y - angleChange;
            player.getPositionSystem().getRotation().y += SMOOTHING_SENSITIVITY * (targetAngleChange - player.getPositionSystem().getRotation().y);
            player.getPositionSystem().getRotation().y = player.getPositionSystem().getRotation().y % 360;
        } else if (Mouse.isButtonDown(0)) {
            float targetAngleChange = angleAroundPlayer - angleChange;
            angleAroundPlayer += 0.5 * (targetAngleChange - angleAroundPlayer);
            angleAroundPlayer = angleAroundPlayer % 360;
        }
    }

    public Vector3f getDirection() {
        return Vector3f.sub(position, player.getPositionSystem().getPosition(), null).normalise(null);
    }

    public Vector3f getRightVector() {
        return Vector3f.cross(new Vector3f(0, 1, 0), getDirection(), null).normalise(null);
    }

    public Vector3f getUpVector() {
        return Vector3f.cross(getDirection(), getRightVector(), null);
    }
}