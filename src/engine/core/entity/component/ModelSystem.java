package engine.core.entity.component;

import engine.core.model.RawModel;
import engine.core.model.StaticModel;
import engine.render.texture.ModelTexture;

public class ModelSystem extends StaticModel implements Component {

	private ModelTexture texture;
	private int textureIndex = -1;

	public ModelSystem(RawModel model, ModelTexture texture) {
		super(model);
		this.texture = texture;
	}

	public ModelSystem(RawModel[] models, float[] distances, ModelTexture texture) {
		super(models, distances);
		this.texture = texture;
	}
	
	public ModelSystem(RawModel[] models, float[] distances, ModelTexture texture, int textureIndex) {
		this(models, distances, texture);
		this.textureIndex = textureIndex; //Sets the texture to use if more than on is present
	}

	public void setTextureIndex(int index) {
		this.textureIndex = index;
	}

	public void setTexture(ModelTexture texture) {
		this.texture = texture;
	}

    public ModelTexture getTexture() {
        return texture;
    }


	public float getTextureXOffset() {
		int column = textureIndex % texture.getNumberOfRows();
		return (float)column / (float) texture.getNumberOfRows();
	}
	
	public float getTextureYOffset() {
		int row = textureIndex % texture.getNumberOfRows();
		return (float)row / (float) texture.getNumberOfRows();
	}

	@Override
	public Component clone() {
		if (super.getModels()[1] != null) {

			RawModel[] models = new RawModel[5];
			float[] distances = new float[5];

			for (int i = 0; i < super.LOD_MODELS.length; i++) {
				models[i] = super.LOD_MODELS[i];
				distances[i] = super.LOD_DISTANCES[i];
			}
			if (textureIndex == 0) {
				return new ModelSystem(models, distances, texture);
			} else  {
				return new ModelSystem(models, distances, texture, textureIndex);
			}
		}
		return new ModelSystem(super.LOD_MODELS[0], texture);
	}

	@Override
	public ID getID() {
		return ID.MODEL_SYSTEM;
	}
}
