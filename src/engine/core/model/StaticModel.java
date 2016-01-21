package engine.core.model;

public class StaticModel {

    public static final int MAX_LEVEL_OF_DETAIL = 0;
    public static final int MIN_LEVEL_OF_DETAIL = 4;

    protected RawModel[] LOD_MODELS = new RawModel[5]; //Declaring an array of Raw Models with 5 elements
    protected float[] LOD_DISTANCES = new float[5]; //Declaring an array of Raw Models with 5 elements

    public StaticModel(RawModel[] models, float[] distances){
        for (int i = 0; i < models.length; i++) {
            LOD_MODELS[i] = models[i];
        }
        for (int i = 0; i < distances.length; i++) {
            LOD_DISTANCES[i] = distances[i];
        }
    }

    //Delete eventually after you get full LOD implemented
    public StaticModel(RawModel model){
        LOD_MODELS[MAX_LEVEL_OF_DETAIL] = model;
    }
 
    public RawModel[] getModels() {
        return LOD_MODELS;
    }

    public float[] getLodDistances() {
        return LOD_DISTANCES;
    }
 
    public RawModel getModel(float distance) {
        if (LOD_MODELS[1] != null) {
            for (int i = 0; i < LOD_DISTANCES[i]; i++) {
                if (distance < LOD_DISTANCES[0]) {
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
