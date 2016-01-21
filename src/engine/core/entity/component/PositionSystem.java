package engine.core.entity.component;

import org.lwjgl.util.vector.Vector3f;

public class PositionSystem implements Component {

	private Vector3f position; //Position (x,y,z); x = left and right, y= up and down, z= forward and backwards
	private Vector3f rotation;
	private float scale;
	
	public PositionSystem(Vector3f position, Vector3f rotation, float scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}
	
	public void setRotationX(float value) {
		this.rotation.x = value;
	}
	
	public void setRotationY(float value) {
		this.rotation.y = value;
	}
	
	public void setRotationZ(float value) {
		this.rotation.z = value;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public void increasePosition(float dx, float dy, float dz) {
		this.position.x+=dx;
		this.position.y+=dy;
		this.position.z+=dz;
	}
	
	public void increaseRotation(float dx, float dy, float dz) {
		this.rotation.x+=dx;
		this.rotation.y+=dy;
		this.rotation.z+=dz;
	}

	@Override
	public Component clone() {
		return new PositionSystem(new Vector3f(position.x, position.y, position.z), new Vector3f(rotation.x, rotation.y, rotation.z), scale);
	}

	@Override
	public ID getID() {
		return ID.POSITION_SYSTEM;
	}

}
