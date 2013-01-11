package org.graphstream.ui.processing.data;

public class NodeData extends ElementData {
	public static enum Shape {
		CIRCLE, SQUARE
	}
	
	public float x;
	public float y;
	public float z;
	
	Shape shape;
	
	public int stroke;
	public int strokeRGB;
	public float strokeA;
	
	public double width;
	public double height;
	
	String label;
	
	public NodeData(String nodeId) {
		super(nodeId);
	}
	
	@Override
	public String toString() {
		return String.format("NodeData<%s>@(%f;%f;%f)", id, x, y, z);
	}
}
