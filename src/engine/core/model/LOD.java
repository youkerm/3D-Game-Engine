package engine.core.model;

/**
 * Created by Mitch on 9/29/2015.
 */
public class LOD {

    public final int MAX_LEVEL_OF_DETAIL = 0;
    public final int MIN_LEVEL_OF_DETAIL = 4;

    private RawModel[] LOD_MODELS = new RawModel[5]; //Declaring an array of Raw Models with 5 elements
    private float[] LOD_DISTANCE = new float[5]; //Declaring an array of Raw Models with 5 elements

    public LOD(RawModel[] models, float[] distances) {
        for (int i = 0; i < models.length; i++) {
            LOD_MODELS[i] = models[i];
        }
        for (int i = 0; i < distances.length; i++) {
            LOD_DISTANCE[i] = distances[i];
        }
    }

    public RawModel getModel(float distance) {
        if (LOD_MODELS[1] != null) {
            for (int i = 0; i < LOD_DISTANCE[i]; i++) {
                if (distance < LOD_DISTANCE[0]) {
                    return getLevelOfDetail(i);
                }
            }
            return getLevelOfDetail(MIN_LEVEL_OF_DETAIL);
        }
        return getLevelOfDetail(MAX_LEVEL_OF_DETAIL);
    }

    public RawModel getLevelOfDetail(int detail_level) {
        if (LOD_MODELS.length >= detail_level) {
            return LOD_MODELS[detail_level];
        }
        return null;//detail level was greater than LOD_MODELS length
    }

}
