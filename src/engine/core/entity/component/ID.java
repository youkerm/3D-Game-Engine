package engine.core.entity.component;

public enum ID {
	
	/**
     * Used for identifying which component each system represents
	 * */
    IDENTIFICATION(1),
    POSITION_SYSTEM(1),
    MODEL_SYSTEM(1),
    LIGHT_SYSTEM(4),
    AABB_BOX(1),
    SHPERE(1);

    private int maxSystem; //The max number of components an entity can allow

    ID(int maxComponents) {
        this.maxSystem = maxComponents;
    }

    public int getMaxSystemSize() {
        return maxSystem;
    }

}