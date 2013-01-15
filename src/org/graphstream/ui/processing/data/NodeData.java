package org.graphstream.ui.processing.data;

public class NodeData extends ElementData {
	public static enum Shape {
		CIRCLE, SQUARE
	}
	
	public float x;
	public float y;
	public float z;
	
	public Shape shape;
	
	public int fillARGB;
	
	public float width;
	public float height;
	
	String label;
	
	public double uiColor;
	
	public NodeData(String nodeId) {
		super(nodeId);
		
		fillARGB = 0x40111111;
		width = 20;
		height = 20;
		shape = Shape.CIRCLE;
		uiColor = 0;
	}
	
	@Override
	public String toString() {
		return String.format("NodeData<%s>@(%f;%f;%f)", id, x, y, z);
	}
}
