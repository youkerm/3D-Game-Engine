package engine.core.model;

import java.util.List;

public class RawModel {

	private int vaoID;
	private List<Integer> vbosID;
	private int vertexCount;
	
	public RawModel(int vaoID, List<Integer> vbos, int vertextCount) {
		this.vaoID = vaoID;
		this.vbosID = vbos;
		this.vertexCount = vertextCount;
	}

	public int getVaoID() {
		return vaoID;
	}

	public List<Integer> getVbosID() {
		return vbosID;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
}
