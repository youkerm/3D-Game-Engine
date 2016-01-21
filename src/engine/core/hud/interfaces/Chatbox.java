package engine.core.hud.interfaces;

import engine.core.hud.component.Text;
import engine.core.hud.component.TextField;
import engine.core.hud.containers.Window;
import engine.render.manager.TextManager;
import engine.render.texture.DefaultTextures;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitch on 1/19/2016.
 */
public class Chatbox extends Window {

    private final int MAX_LINES = 8;

    private List<String> text = new ArrayList<String>();
    private List<Text> lines = new ArrayList<Text>();
    private TextField input;

    public Chatbox(DefaultTextures textures, Vector2f position, Vector2f scale) {
        super(textures, position, scale, "Chat", false);
        Vector2f pos = new Vector2f(-1,-1);
        for (int i = 0; i < MAX_LINES; i++) {
            Text temp = new Text("temp", 1f, TextManager.getFont("candara"), new Vector2f(pos), (scale.x+.05f), false);
            add(temp);
            lines.add(temp);
            pos.y =+ .125f;
        }
        input = new TextField(new Vector2f(0f, -.85f), new Vector2f(scale.x-.02f, .025f), new Vector2f(0,.5f), "Username: ");
        add(input);
    }


    public void setText(int index, String text) {
        lines.get(index).setTextString(text);
    }
}
