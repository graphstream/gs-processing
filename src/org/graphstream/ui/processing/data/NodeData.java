package org.graphstream.ui.processing.data;

public class NodeData extends ElementData {
	public static enum Shape {
		CIRCLE, SQUARE
	}
	
	public float x;
	public float y;
	public float z;
	
	public Shape shape;
	
	public int fillRGB;
	public float fillA;
	
	public float width;
	public float height;
	
	String label;
	
	public NodeData(String nodeId) {
		super(nodeId);
		
		fillA = 40;
		fillRGB = 0xFF111111;
		width = 10;
		height = 10;
		shape = Shape.CIRCLE;
	}
	
	@Override
	public String toString() {
		return String.format("NodeData<%s>@(%f;%f;%f)", id, x, y, z);
	}
}
