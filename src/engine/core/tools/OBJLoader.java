package engine.core.tools;

import engine.core.Light;
import engine.core.entity.Entity;
import engine.core.entity.component.*;
import engine.core.loaders.GLLoader;
import engine.core.model.ModelData;
import engine.core.model.RawModel;
import engine.core.model.Vertex;
import engine.render.texture.ModelTexture;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OBJLoader {

    private static final String RES_OBJECTS = "res/objects/";
    private static final String RES_MODELS = "res/models/";

    public static boolean entityExists(int id) {
        File file = new File(RES_MODELS + id + "/info.csv");
        return file.exists();
    }

    public static Entity loadEntity(int id, GLLoader loader) {
        File file = new File(RES_MODELS + id + "/info.csv");
        Entity entity = null;
        if (!file.exists()) {
            System.err.println("Could not load entity: " + id);
        } else {
            try {
                FileReader file_reader = new FileReader(file);
                BufferedReader reader = new BufferedReader(file_reader);

                String name = "";
                int numberOfRows = 1;
                int textureIndex = 0;
                float scale = 1f;
                float rotationX = 0f;
                float rotationY = 0f;
                float rotationZ = 0f;
                boolean transparent = false;
                boolean fakeLight = false;
                List<Light> lights = new ArrayList<Light>();

                float[] vertices = null;
                float[] textureCoords = null;
                float[] normals = null;
                int[] indices = null;
                float furthestPoint = -1.0f;

                String line;
                while(true) {
                    line = reader.readLine();
                    if (!line.startsWith("#")) {
                        if(!line.isEmpty()) {
                            if(line.contains("name:")) {
                                name = line.substring(line.indexOf(':')+1);
                            } else if(line.contains("texture_rows:")) {
                                numberOfRows = Integer.parseInt(line.substring(line.indexOf(':') + 1));
                            } else if(line.contains("texture_id:")) {
                                textureIndex = Integer.parseInt(line.substring(line.indexOf(':') + 1));
                            } else if(line.contains("scale:")) {
                                scale = Float.parseFloat(line.substring(line.indexOf(':') + 1));
                            } else if(line.contains("rotx:")) {
                                rotationX = Float.parseFloat(line.substring(line.indexOf(':')+1));
                            } else if(line.contains("roty:")) {
                                rotationY = Float.parseFloat(line.substring(line.indexOf(':')+1));
                            } else if (line.contains("rotz:")) {
                                rotationZ = Float.parseFloat(line.substring(line.indexOf(':')+1));
                            } else if(line.contains("transparent:")) {
                                transparent = Boolean.parseBoolean(line.substring(line.indexOf(':') + 1));
                            } else if(line.contains("fakelight:")) {
                                fakeLight = Boolean.parseBoolean(line.substring(line.indexOf(':')+1));
                            } else if(line.contains("light:")) {
                                line = reader.readLine();
                                String[] components = line.split(",");
                                Vector3f pos = new Vector3f(Float.parseFloat(components[0]), Float.parseFloat(components[1]), Float.parseFloat(components[2]));

                                line = reader.readLine();
                                String[] rbg = line.split(",");
                                Vector3f color = new Vector3f(Float.parseFloat(rbg[0]), Float.parseFloat(rbg[1]), Float.parseFloat(rbg[2]));

                                line = reader.readLine();
                                String[] att = line.split(",");
                                Vector3f attenuation = new Vector3f(Float.parseFloat(att[0]), Float.parseFloat(att[1]), Float.parseFloat(att[2]));
                                lights.add(new Light(pos, color, attenuation));
                            } else if(line.contains("vertices:")) {
                                vertices = new float[Integer.parseInt(line.substring(line.indexOf('(')+1, line.indexOf(')')))];
                                line = line.substring(line.indexOf(')') + 1);
                                String[] vertices_str = line.split(",");
                                for(int i = 0; i< vertices_str.length; i++) {
                                    vertices[i] = Float.parseFloat(vertices_str[i]);
                                }
                            } else if(line.contains("textures:")) {
                                textureCoords = new float[Integer.parseInt(line.substring(line.indexOf('(')+1, line.indexOf(')')))];
                                line = line.substring(line.indexOf(')')+1);
                                String[] texture_str = line.split(",");
                                for(int i = 0; i<texture_str.length; i++) {
                                    textureCoords[i] = Float.parseFloat(texture_str[i]);
                                }
                            } else if(line.contains("normals:")) {
                                normals = new float[Integer.parseInt(line.substring(line.indexOf('(')+1, line.indexOf(')')))];
                                line = line.substring(line.indexOf(')')+1);
                                String[] normals_str = line.split(",");
                                for (int i = 0; i<normals_str.length; i++) {
                                    normals[i] = Float.parseFloat(normals_str[i]);
                                }
                            } else if(line.contains("indices:")) {
                                indices = new int[Integer.parseInt(line.substring(line.indexOf('(')+1, line.indexOf(')')))];
                                line = line.substring(line.indexOf(')')+1);
                                String[] indices_str = line.split(",");
                                for(int i = 0; i<indices_str.length; i++) {
                                    indices[i] = Integer.parseInt(indices_str[i]);
                                }
                            } else if(line.contains("farpoint:")) {
                                furthestPoint = Float.parseFloat(line.substring(line.indexOf(':')+1));
                                break;
                            }
                        }
                    }
                }
                reader.close();
                if (vertices != null && textureCoords != null && normals != null && indices != null && furthestPoint != -1.0f) {
                    ModelData data = new ModelData(vertices, textureCoords, normals, indices, furthestPoint);
                    RawModel rawModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), indices);
                    ModelTexture modelTexture = new ModelTexture(loader.loadTexture(id));
                    modelTexture.setUseFakeLighting(fakeLight);
                    modelTexture.setTransparent(transparent);
                    modelTexture.setNumberOfRows(numberOfRows);


                    entity = new Entity(new Identification(id, name));
                    ModelSystem modelSystem = new ModelSystem(rawModel, modelTexture);
                    modelSystem.setTextureIndex(textureIndex);
                    entity.addComponent(modelSystem);
                    PositionSystem positionSystem = new PositionSystem(new Vector3f(0,0,0), new Vector3f(rotationX,rotationY,rotationZ), scale);
                    entity.addComponent(positionSystem);
                    for (Light light: lights) {
                        entity.addComponent(new LightSystem(new Vector3f(0,0,0), light.getPosition(), light.getColour(), light.getAttenuation()));
                    }
                    entity.addComponent(new AABBox(data.getVertices(), positionSystem, loader));
                    entity.addComponent(new Sphere(data.getVertices(), positionSystem, loader));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }


    public static RawModel loadNormalMapOBJ(String objFileName, GLLoader loader) {
        FileReader isr = null;
        File objFile = new File(RES_OBJECTS + objFileName + ".obj");
        try {
            isr = new FileReader(objFile);
        } catch (FileNotFoundException e) {
            System.err.println("File not found in res; don't use any extention");
        }
        BufferedReader reader = new BufferedReader(isr);
        String line;
        List<Vertex> vertices = new ArrayList<Vertex>();
        List<Vector2f> textures = new ArrayList<Vector2f>();
        List<Vector3f> normals = new ArrayList<Vector3f>();
        List<Integer> indices = new ArrayList<Integer>();
        try {
            while (true) {
                line = reader.readLine();
                if (line.startsWith("v ")) {
                    String[] currentLine = line.split(" ");
                    Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]),
                            (float) Float.valueOf(currentLine[3]));
                    Vertex newVertex = new Vertex(vertices.size(), vertex);
                    vertices.add(newVertex);

                } else if (line.startsWith("vt ")) {
                    String[] currentLine = line.split(" ");
                    Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    String[] currentLine = line.split(" ");
                    Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]),
                            (float) Float.valueOf(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f ")) {
                    break;
                }
            }
            while (line != null && line.startsWith("f ")) {
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                Vertex v0 = processNormalVertex(vertex1, vertices, indices);
                Vertex v1 = processNormalVertex(vertex2, vertices, indices);
                Vertex v2 = processNormalVertex(vertex3, vertices, indices);
                calculateTangents(v0, v1, v2, textures);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading the file");
        }
        removeUnusedVertices(vertices);
        float[] verticesArray = new float[vertices.size() * 3];
        float[] texturesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        float[] tangentsArray = new float[vertices.size() * 3];
        float furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
                texturesArray, normalsArray, tangentsArray);
        int[] indicesArray = convertIndicesListToArray(indices);

        return loader.loadToVAO(verticesArray, texturesArray, normalsArray, tangentsArray, indicesArray);
    }

    public static ModelData loadOBJ(String objFileName) {
        FileReader isr = null;
        File objFile = new File(RES_OBJECTS + objFileName + ".obj");
        try {
            isr = new FileReader(objFile);
        } catch (FileNotFoundException e) {
            System.err.println(objFileName + ".obj not found in res folder!");
            System.exit(-1);
        }
        BufferedReader reader = new BufferedReader(isr);
        String line;
        List<Vertex> vertices = new ArrayList<Vertex>();
        List<Vector2f> textures = new ArrayList<Vector2f>();
        List<Vector3f> normals = new ArrayList<Vector3f>();
        List<Integer> indices = new ArrayList<Integer>();
        try {
            while (true) {
                line = reader.readLine();
                if (line.startsWith("v ")) {
                    String[] currentLine = line.split(" ");
                    Vector3f vertex = new Vector3f(Float.valueOf(currentLine[1]),
                            Float.valueOf(currentLine[2]),
                            Float.valueOf(currentLine[3]));
                    Vertex newVertex = new Vertex(vertices.size(), vertex);
                    vertices.add(newVertex);

                } else if (line.startsWith("vt ")) {
                    String[] currentLine = line.split(" ");
                    Vector2f texture = new Vector2f(Float.valueOf(currentLine[1]),
                            Float.valueOf(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    String[] currentLine = line.split(" ");
                    Vector3f normal = new Vector3f(Float.valueOf(currentLine[1]),
                            Float.valueOf(currentLine[2]),
                            Float.valueOf(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f ")) {
                    break;
                }
            }
            while (line != null && line.startsWith("f ")) {
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                processVertex(vertex1, vertices, indices);
                processVertex(vertex2, vertices, indices);
                processVertex(vertex3, vertices, indices);
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("Error reading " + objFileName + ".obj");
            System.exit(-1);
        }

        float[] verticesArray = new float[vertices.size() * 3];
        float[] texturesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        float furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
                texturesArray, normalsArray);
        int[] indicesArray = convertIndicesListToArray(indices);
        return new ModelData(verticesArray, texturesArray, normalsArray, indicesArray, furthest);
    }

    private static void calculateTangents(Vertex v0, Vertex v1, Vertex v2,
                                          List<Vector2f> textures) {
        Vector3f delatPos1 = Vector3f.sub(v1.getPosition(), v0.getPosition(), null);
        Vector3f delatPos2 = Vector3f.sub(v2.getPosition(), v0.getPosition(), null);
        Vector2f uv0 = textures.get(v0.getTextureIndex());
        Vector2f uv1 = textures.get(v1.getTextureIndex());
        Vector2f uv2 = textures.get(v2.getTextureIndex());
        Vector2f deltaUv1 = Vector2f.sub(uv1, uv0, null);
        Vector2f deltaUv2 = Vector2f.sub(uv2, uv0, null);

        float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
        delatPos1.scale(deltaUv2.y);
        delatPos2.scale(deltaUv1.y);
        Vector3f tangent = Vector3f.sub(delatPos1, delatPos2, null);
        tangent.scale(r);
        v0.addTangent(tangent);
        v1.addTangent(tangent);
        v2.addTangent(tangent);
    }

    private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
        int index = Integer.parseInt(vertex[0]) - 1;
        Vertex currentVertex = vertices.get(index);
        int textureIndex = Integer.parseInt(vertex[1]) - 1;
        int normalIndex = Integer.parseInt(vertex[2]) - 1;
        if (!currentVertex.isSet()) {
            currentVertex.setTextureIndex(textureIndex);
            currentVertex.setNormalIndex(normalIndex);
            indices.add(index);
        } else {
            dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
                    vertices);
        }
    }

    private static Vertex processNormalVertex(String[] vertex, List<Vertex> vertices,
                                          List<Integer> indices) {
        int index = Integer.parseInt(vertex[0]) - 1;
        Vertex currentVertex = vertices.get(index);
        int textureIndex = Integer.parseInt(vertex[1]) - 1;
        int normalIndex = Integer.parseInt(vertex[2]) - 1;
        if (!currentVertex.isSet()) {
            currentVertex.setTextureIndex(textureIndex);
            currentVertex.setNormalIndex(normalIndex);
            indices.add(index);
            return currentVertex;
        } else {
            return dealWithAlreadyProcessedNormalVertex(currentVertex, textureIndex, normalIndex, indices,
                    vertices);
        }
    }

    private static int[] convertIndicesListToArray(List<Integer> indices) {
        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indicesArray.length; i++) {
            indicesArray[i] = indices.get(i);
        }
        return indicesArray;
    }

    private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures,
                                             List<Vector3f> normals, float[] verticesArray, float[] texturesArray,
                                             float[] normalsArray) {
        float furthestPoint = 0;
        for (int i = 0; i < vertices.size(); i++) {
            Vertex currentVertex = vertices.get(i);
            if (currentVertex.getLength() > furthestPoint) {
                furthestPoint = currentVertex.getLength();
            }
            Vector3f position = currentVertex.getPosition();
            Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
            Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            texturesArray[i * 2] = textureCoord.x;
            texturesArray[i * 2 + 1] = 1 - textureCoord.y;
            normalsArray[i * 3] = normalVector.x;
            normalsArray[i * 3 + 1] = normalVector.y;
            normalsArray[i * 3 + 2] = normalVector.z;
        }
        return furthestPoint;
    }

    private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures,
                                             List<Vector3f> normals, float[] verticesArray, float[] texturesArray,
                                             float[] normalsArray, float[] tangentsArray) {
        float furthestPoint = 0;
        for (int i = 0; i < vertices.size(); i++) {
            Vertex currentVertex = vertices.get(i);
            if (currentVertex.getLength() > furthestPoint) {
                furthestPoint = currentVertex.getLength();
            }
            Vector3f position = currentVertex.getPosition();
            Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
            Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
            Vector3f tangent = currentVertex.getAverageTangent();
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            texturesArray[i * 2] = textureCoord.x;
            texturesArray[i * 2 + 1] = 1 - textureCoord.y;
            normalsArray[i * 3] = normalVector.x;
            normalsArray[i * 3 + 1] = normalVector.y;
            normalsArray[i * 3 + 2] = normalVector.z;
            tangentsArray[i * 3] = tangent.x;
            tangentsArray[i * 3 + 1] = tangent.y;
            tangentsArray[i * 3 + 2] = tangent.z;

        }
        return furthestPoint;
    }

    private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,
                                                       int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.getIndex());
        } else {
            Vertex anotherVertex = previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
                dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
                        indices, vertices);
            } else {
                Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
                duplicateVertex.setTextureIndex(newTextureIndex);
                duplicateVertex.setNormalIndex(newNormalIndex);
                previousVertex.setDuplicateVertex(duplicateVertex);
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.getIndex());
            }

        }
    }

    private static Vertex dealWithAlreadyProcessedNormalVertex(Vertex previousVertex, int newTextureIndex,
                                                           int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.getIndex());
            return previousVertex;
        } else {
            Vertex anotherVertex = previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
                return dealWithAlreadyProcessedNormalVertex(anotherVertex, newTextureIndex,
                        newNormalIndex, indices, vertices);
            } else {
                Vertex duplicateVertex = previousVertex.duplicate(vertices.size());//NEW
                duplicateVertex.setTextureIndex(newTextureIndex);
                duplicateVertex.setNormalIndex(newNormalIndex);
                previousVertex.setDuplicateVertex(duplicateVertex);
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.getIndex());
                return duplicateVertex;
            }
        }
    }

    private static void removeUnusedVertices(List<Vertex> vertices) {
        for (Vertex vertex : vertices) {
            vertex.averageTangents();
            if (!vertex.isSet()) {
                vertex.setTextureIndex(0);
                vertex.setNormalIndex(0);
            }
        }
    }

}