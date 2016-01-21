package engine.core.entity.component;

public class Identification implements Component {

	private int id;      //ID of the Entity
	private String name; // Name of the Entity
	
	public Identification(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getEntityID() {
		return id;
	}

	public String getEntityName() {
		return name;
	}

	@Override
	public Component clone() {
		return new Identification(id, name);
	}

	@Override
	public ID getID() {
		return ID.IDENTIFICATION;
	}

}
