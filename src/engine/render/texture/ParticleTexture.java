package engine.render.texture;

/**
 * Created by Mitch on 12/13/2015.
 */
public class ParticleTexture {

    private int textureID;
    private int numberOfRows;
    private boolean isAdditive = false;

    public ParticleTexture(int textureID, int numberOfRows, boolean isAdditive) {
        this.textureID = textureID;
        this.numberOfRows = numberOfRows;
        this.isAdditive = isAdditive;
    }

    public int getTextureID() {
        return textureID;
    }

    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public boolean isAdditive() {
        return isAdditive;
    }
}
