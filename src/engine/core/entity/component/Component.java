package engine.core.entity.component;

public interface Component {
	Component clone();
	ID getID(); 		  //The ID of the component
}
