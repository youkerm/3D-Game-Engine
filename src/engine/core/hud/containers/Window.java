package engine.core.hud.containers;

import engine.DisplayManager;
import engine.core.hud.component.Component;
import engine.core.hud.component.Text;
import engine.render.manager.HUDManager;
import engine.render.texture.DefaultTextures;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

/**
 * Created by Mitch on 10/31/2015.
 */
public class Window extends Container {

    /* Constants */
    private final static float CORNER_SCALE_FACTOR = .04f;
    private final static float BAR_SCALE_FACTOR = 3.46f;

    private String title;

    public Window(DefaultTextures textures, Vector2f position, Vector2f scale, String title, boolean showCloseButton) {
        super(textures.getWindowBackgroundTexture(), position, scale);
        super.addComponents(textures.getWindowBarTexture(), textures.getWindowCornerTexture(), CORNER_SCALE_FACTOR, BAR_SCALE_FACTOR);
        this.title = title;
        if (showCloseButton) {
//            Button close = new Button(textures.getWindowCloseTexture(), new Vector2f(.99f, .98f), new Vector2f(getBarWidth() * .75f, getBarHeight() * .75f));
//            sprites.add(close);
//            super.add(close);
        }
        HUDManager.addComponent(this);
        //TEST M8
    }

    public void add(Container... containers) {
        for (Container container: containers) {
            add(container);
        }
    }

    @Override
    public void displaceInFPS(boolean enable) {
        super.displaceInFPS(enable);
        for (Component c: super.getChildren()) {
            c.displaceInFPS(enable);
            for (Text t: super.getTextChildren()) {
                t.displaceInFPS(enable);
            }
            for (Component c2: super.getChildren()) {
                c2.displaceInFPS(enable);
                for (Text t2: super.getTextChildren()) {
                    t2.displaceInFPS(enable);
                }
            }
        }
        for (Text t: super.getTextChildren()) {
            t.displaceInFPS(enable);
        }
    }

    public void add(Container container) {
        Vector2f position = container.getPosition();
        Vector2f scale = container.getScale();

        float newXPos = (float) ((getBounds().getWidth()/2.0f) * position.x) - (position.x * scale.x * DisplayManager.WIDTH/2.0f);
        float newYPos = (float) (((getBounds().getHeight() + 1)/2.0f) * position.y) - (position.y * scale.y * DisplayManager.HEIGHT/2.0f);

        float centerX = ((DisplayManager.WIDTH/2.0f) + ((DisplayManager.WIDTH/2.0f) * getPosition().x));
        float centerY = ((DisplayManager.HEIGHT/2.0f) + ((DisplayManager.HEIGHT/2.0f) * getPosition().y));

        position.x = ((centerX + newXPos) * 2.0f) / DisplayManager.WIDTH - 1;
        position.y = ((centerY + newYPos) * 2.0f)  / DisplayManager.HEIGHT - 1;

        for (Component child : container.getChildren()) {
            Vector2f childOrginalPosition = child.getOrginalPosition();
            Vector2f childPosition = child.getPosition();
            Vector2f childScale = child.getScale();

            float childNewXPos = (float) ((container.getBounds().getWidth()/2.0f) * childOrginalPosition.x) - (childOrginalPosition.x * childScale.x * DisplayManager.WIDTH/2.0f);
            float childNewYPos = (float) (((container.getBounds().getHeight() + 1)/2.0f) * childOrginalPosition.y) - (childOrginalPosition.y * childScale.y * DisplayManager.HEIGHT/2.0f);

            float childCenterX = ((DisplayManager.WIDTH/2.0f) + ((DisplayManager.WIDTH/2.0f) * position.x));
            float childCenterY = ((DisplayManager.HEIGHT/2.0f) + ((DisplayManager.HEIGHT/2.0f) * position.y));

            childPosition.x = ((childCenterX + childNewXPos) * 2.0f) / DisplayManager.WIDTH - 1;
            childPosition.y = ((childCenterY + childNewYPos) * 2.0f)  / DisplayManager.HEIGHT - 1;
        }

        for (Text text : container.getTextChildren()) {
            //Position in font rendering coordinate system
            float xPos = (text.getOriginalPosition().x) * Display.getWidth();
            float yPos = (1 - text.getOriginalPosition().y) * Display.getHeight();

            float destX = xPos - (DisplayManager.WIDTH/2 * text.getMaxLineSize());
            float destY = yPos;

            //Position in normalized device coordinate system
            Vector2f normalized = getNormalisedCoordinates(destX, destY);
            normalized = reposition2(container, normalized, new Vector2f(text.getMaxLineSize() * container.getScale().x, text.getLineHeight()));

            //Position back into font rendering coordinate system.
            normalized.x = (normalized.x+1.0f)/2.0f;
            normalized.y = 1 - ((normalized.y+1.0f)/2.0f);

            text.setPosition(normalized);
        }

        children.add(container);
        HUDManager.addComponent(container);
    }

    private Vector2f reposition2(Container container, Vector2f position, Vector2f updatedScale) {
        float newXPos = (float) ((container.getBounds().getWidth()/2.0f) * position.x) + (updatedScale.x * DisplayManager.WIDTH/2.0f);
        float newYPos = (float) (((container.getBounds().getHeight() + 1)/2.0) * position.y) + (updatedScale.y * DisplayManager.HEIGHT/2.0f);

        float centerX = ((DisplayManager.WIDTH/2.0f) + ((DisplayManager.WIDTH/2.0f) * container.getPosition().x));
        float centerY = ((DisplayManager.HEIGHT/2.0f) +((DisplayManager.HEIGHT/2.0f) * container.getPosition().y));

        Vector2f normalized = getNormalisedCoordinates((centerX + newXPos), (centerY + newYPos));

        position.x = normalized.x;
        position.y = normalized.y;

        return position;
    }

    private Vector2f reposition(Container container, Vector2f textPosition, Vector2f updatedScale) {
        float newXPos = (float) ((container.getBounds().getWidth()) * textPosition.x) - (textPosition.x * updatedScale.x * DisplayManager.WIDTH/2.0f);
        float newYPos = (float) (((container.getBounds().getHeight() + 1)) * (textPosition.y)) - (textPosition.y * updatedScale.y * DisplayManager.HEIGHT/2.0f);

        float centerX = ((DisplayManager.WIDTH/2.0f) + ((DisplayManager.WIDTH/2.0f) * container.getPosition().x));
        float centerY = ((DisplayManager.HEIGHT/2.0f) + ((DisplayManager.HEIGHT/2.0f) * container.getPosition().y));

        Vector2f normalized = getNormalisedCoordinates((centerX + newXPos), (centerY + newYPos));

        textPosition.x = normalized.x;
        textPosition.y = normalized.y;

        return textPosition;
    }

    private Vector2f getNormalisedCoordinates(float posX, float posY) {
        float x = (2.0f * posX) / Display.getWidth() - 1f;
        float y = (2.0f * posY) / Display.getHeight() - 1f;
        return new Vector2f(x, y);
    }

}
