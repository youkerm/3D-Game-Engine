package engine.render.manager;

import engine.core.loaders.GLLoader;
import engine.render.FontRenderer;
import engine.render.font.FontType;
import engine.core.hud.component.Text;
import engine.render.font.TextMeshData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mitch on 11/1/2015.
 */
public class TextManager {

    private static GLLoader loader;
    private static FontRenderer renderer;

    private static Map<FontType, List<Text>> fps_texts = new HashMap<FontType, List<Text>>();
    private static Map<FontType, List<Text>> gui_texts = new HashMap<FontType, List<Text>>();

    private static Map<String, FontType> fonts = new HashMap<String, FontType>();

    public static void init(GLLoader theLoader) {
        renderer = new FontRenderer();
        loader = theLoader;
    }

    public static void render() {
        renderer.render(fps_texts, gui_texts);
    }

    public static void loadText(Text text) {
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        if (text.isDisplayInFPS()) {
            List<Text> textBatch = fps_texts.get(font);
            if (textBatch == null) {
                textBatch = new ArrayList<Text>();
                fps_texts.put(font, textBatch);
            }
            textBatch.add(text);
        } else {
            List<Text> textBatch = gui_texts.get(font);
            if (textBatch == null) {
                textBatch = new ArrayList<Text>();
                gui_texts.put(font, textBatch);
            }
            textBatch.add(text);
        }
    }

    public static void removeText(Text text) {
        List<Text> textBatch;
        if (text.isDisplayInFPS()) {
            textBatch = fps_texts.get(text.getFont());
        } else {
            textBatch = gui_texts.get(text.getFont());
        }
        if (textBatch != null) {
            textBatch.remove(text);
            if (textBatch.isEmpty()) {
                fps_texts.remove(text.getFont());
            }
        }
    }

    public static FontType getFont(String fontName) {
        FontType font = new FontType(loader.loadTexture(fontName), new File("res/objects/" + fontName + ".fnt"));
        fonts.put(fontName, font);
        return font;
    }

    public static void cleanUp() {
        renderer.cleanUp();
    }

}
