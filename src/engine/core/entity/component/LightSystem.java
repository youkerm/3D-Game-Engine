package engine.core.entity.component;

import engine.core.Light;
import org.lwjgl.util.vector.Vector3f;

public class LightSystem extends Light implements Component {

	private Vector3f default_position; //The position of the Light if entity position was 0,0,0
	
	public LightSystem(Vector3f position, Vector3f default_position, Vector3f colour, Vector3f attenuation) {
        super(position, colour, attenuation);
        this.default_position = default_position;
	}

	public Vector3f getDefaultPosition() {
		return default_position;
	}

	public void setDefaultPosition(Vector3f position) {
		default_position = position;
	}


	@Override
	public Component clone() {
		Vector3f pos = new Vector3f(super.getPosition().x, super.getPosition().y, super.getPosition().z);
		Vector3f col = new Vector3f(super.getColour().x, super.getColour().y, super.getColour().z);
		Vector3f att = new Vector3f(super.getAttenuation().x, super.getAttenuation().y, super.getAttenuation().z);
		return new LightSystem(pos, default_position, col, att);
	}

	@Override
	public ID getID() {
		return ID.LIGHT_SYSTEM;
	}

}
