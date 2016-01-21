package engine.core.hud.component;

import engine.input.InputEvent;
import org.lwjgl.util.vector.Vector2f;

/**
 * Created by Mitch on 10/27/2015.
 */
public class Sprite extends Component {



    public Sprite(int hoverOFF, int hoverON, Vector2f position, Vector2f scale) {
        super(hoverOFF, hoverON, position, scale);
    }

    public Sprite(int hoverOFF, Vector2f position, Vector2f scale) {
        super(hoverOFF, position, scale);
    }

    @Override
    public void onClick() {}

    @Override
    public void onRelease() {}

    @Override
    public void onMouseScroll() {}

    @Override
    public void onKeyPress() {}

    @Override
    public void onKeyRelease() {}

    @Override
    protected void onHover() {}

    @Override
    public void onKeyTyped() {

    }

    @Override
    public int priority() {
        return InputEvent.LOWEST_PRIORITY;
    }
}
