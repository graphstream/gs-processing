package org.graphstream.ui.processing.data;

import org.graphstream.ui.processing.XYConverter;

import processing.core.PGraphics;

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

	public NodeData(String nodeId) {
		super(nodeId);

		fillARGB = 0x40111111;
		width = 20;
		height = 20;
		shape = Shape.CIRCLE;
		uiColor = 0;
	}

	protected void drawShape(PGraphics g, XYConverter camera) {
		g.pushStyle();

		g.fill(fillARGB);

		if (stroke == 0)
			g.noStroke();
		else {
			g.stroke(strokeARGB);
			g.strokeWeight(stroke);
		}

		switch (shape) {
		case CIRCLE:
			g.ellipse(camera.xToScreenX(x), camera.yToScreenY(y), width, height);
			break;
		case SQUARE:
			g.rect(camera.xToScreenX(x) - width / 2.0f, camera.yToScreenY(y)
					- height / 2.0f, width, height);
			break;
		}

		g.popStyle();
	}
	
	@Override
	public String toString() {
		return String.format("NodeData<%s>@(%f;%f;%f)", id, x, y, z);
	}

	protected void drawShadow(PGraphics g, XYConverter camera) {
		// TODO Auto-generated method stub
		
	}

	protected float getLabelAnchorX(XYConverter camera) {
		return x;
	}

	protected float getLabelAnchorY(XYConverter camera) {
		return y;
	}
}
