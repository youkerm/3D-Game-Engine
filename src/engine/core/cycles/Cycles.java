package engine.core.cycles;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Mitch on 10/18/2015.
 */
public enum Cycles {
    DAWN("DAWN", new Vector3f(0.5444f, 0.62f, 0.69f)), DAY("DAY", new Vector3f(0.74f, 0.72f, 0.65f)), DUSK("DUSK", new Vector3f(.10f, .10f, .10f)), NIGHT("NIGHT", new Vector3f(.10f, .10f, .10f));

    public String TEXTURE_FILES;
    public Vector3f FOG_COLOR;

    Cycles(String textureFiles, Vector3f fog) {
        this.TEXTURE_FILES = textureFiles;
        this.FOG_COLOR = fog;
    }
}
