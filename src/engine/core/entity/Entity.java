package engine.core.entity;

import engine.core.entity.component.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity {

	private Map<ID, List<Object>> components = new HashMap<ID, List<Object>>();

	public Entity(Component... component) {
		for (Component c: component) {
			addComponent(c);
		}
	}

	public Entity(List<Component> components) {
		for (Component component: components) {
			addComponent(component);
		}
	}
	
	public int getID() {
		int id = -1;
		if (getIdentification() != null) {
			id = getIdentification().getEntityID();
		}
		return id;
	}

	public Identification getIdentification() {
		return (Identification) getSystem(ID.IDENTIFICATION);
	}
	
	public ModelSystem getModelSystem() {
		return (ModelSystem) getSystem(ID.MODEL_SYSTEM);
	}
	
	public PositionSystem getPositionSystem() {
		return (PositionSystem) getSystem(ID.POSITION_SYSTEM);
	}
	
	public AABBox getAABBox() {
		return (AABBox) getSystem(ID.AABB_BOX);
	}

	public Sphere getSphere() {
		return (Sphere) getSystem(ID.SHPERE);
	}

	public List<LightSystem> getLights() {
		List<LightSystem> lights = new ArrayList<LightSystem>();
		List<Object> tempLights = getSystems(ID.LIGHT_SYSTEM);
        if (tempLights != null) {
            for (Object light : tempLights) {
                lights.add((LightSystem) light);
            }
        }
		return lights;
	}

    public List<ID> getComponents() {
        List<ID> ids = new ArrayList<ID>();
        for (ID key : components.keySet()) {
            ids.add(key);
        }
        return ids;
    }

	private Object getSystem(ID id) {
		Object system = null;
		List<Object> tempSystem = getSystems(id);
		if (tempSystem != null) {
			if (tempSystem.size() <= 1) {
				system = tempSystem.get(0);
			} else {
				System.err.println("This system has multiple components");
			}
		}
		return system;
	}

	private List<Object> getSystems(ID id) {
		List<Object> systemList = components.get(id);
		return systemList;
	}

	/**
	 * Creates a deep copy of the entity.
	 * @return a new entity
	 */
	public Entity clone() {
		Entity entity = new Entity();
		for (ID id : getComponents()) {
			List<Object> systems =  getSystems(id);
			if (systems.size() < 1) {
				for (Object system: systems) {
					Component component = (Component) system;
					entity.addComponent((Component) component.clone());
				}
			} else {
				Component component = (Component) systems.get(0);
				entity.addComponent((Component) component.clone());
			}
		}
		return entity;
	}

	public void addComponent(Component component) {
		List<Object> batch = components.get(component.getID());
		if (batch != null) {
			if (batch.size() < component.getID().getMaxSystemSize()) {
				batch.add(component);
			} else {
				System.err.println("Maximum amount of components has already been reached. Can't add component!");
			}
		} else {
			List<Object> newBatch = new ArrayList<Object>();
			newBatch.add(component);
			components.put(component.getID(), newBatch);
		}
	}

    public void removeComponent(ID id) {
		List<Object> batch = components.get(id);
		components.remove(batch);
    }

	public void removeComponent(Component component) {
		ID id = component.getID();
		removeComponent(id);
	}
	
	public void removeAll() {
		components.clear();
    }

}
