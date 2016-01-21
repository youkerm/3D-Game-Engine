package engine.render.manager;

import engine.core.hud.component.Component;
import engine.render.texture.HudTexture;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitch on 11/7/2015.
 */
public class HUDManager {

    private static List<HudTexture> fps_huds = new ArrayList<HudTexture>();
    private static List<HudTexture> gui_huds = new ArrayList<HudTexture>();

    public static void addComponent(Component component) {
        if (component.isDisplayInFPS()) {
            fps_huds.add(component);
            fps_huds.addAll(component.getChildren());
        } else {
            gui_huds.add(component);
            gui_huds.addAll(component.getChildren());
        }
    }

    public static void removeComponent(Component component) {
        if (component.isDisplayInFPS()) {
            fps_huds.removeAll(component.getChildren());
            fps_huds.remove(component);
        } else {
            gui_huds.removeAll(component.getChildren());
            gui_huds.remove(component);
        }
    }

    public static List<HudTexture> getFPSHUDs() {
        return fps_huds;
    }

    public static List<HudTexture> getGUIHUDs() {
        return gui_huds;
    }
}
