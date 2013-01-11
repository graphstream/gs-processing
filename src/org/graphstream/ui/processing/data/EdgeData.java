package org.graphstream.ui.processing.data;

public class EdgeData extends ElementData {
	public final NodeData src;
	public final NodeData trg;

	boolean directed;
	int width;

	public EdgeData(String edgeId, NodeData src, NodeData trg, boolean directed) {
		super(edgeId);

		this.src = src;
		this.trg = trg;
		this.directed = directed;
	}
	
	@Override
	public String toString() {
		return String.format("EdgeData<%s;%s;%s>", id, src.id, trg.id);
	}
}
