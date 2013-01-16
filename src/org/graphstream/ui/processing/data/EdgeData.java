package org.graphstream.ui.processing.data;

import org.graphstream.ui.processing.XYConverter;

import processing.core.PGraphics;

public class EdgeData extends ElementData {
	public static enum PointsType {
		RELATIVE, ABSOLUTE, ABSOLUTE_SCREEN
	}

	public final NodeData src;
	public final NodeData trg;

	boolean directed;
	int width;

	float[] points;

	public EdgeData(String edgeId, NodeData src, NodeData trg, boolean directed) {
		super(edgeId);

		this.src = src;
		this.trg = trg;
		this.directed = directed;
		this.points = new float[4];
	}

	@Override
	public String toString() {
		return String.format("EdgeData<%s;%s;%s>", id, src.id, trg.id);
	}

	protected void computeConnector(NodeData from, NodeData to, float[] xy,
			XYConverter camera) {
		switch (from.shape) {
		case CIRCLE:
			computeConnectorCircle(from, to, xy, camera);
			break;
		case SQUARE:
			computeConnectorSquare(from, to, xy, camera);
			break;
		}
	}

	protected void computeConnectorCircle(NodeData from, NodeData to,
			float[] points, XYConverter camera) {
		float px1 = camera.xToScreenX(from.x);
		float py1 = camera.yToScreenY(from.y);
		float px2 = camera.xToScreenX(to.x);
		float py2 = camera.yToScreenY(to.y);

		points[0] = px1;
		points[1] = py1;
		points[2] = px2;
		points[3] = py2;

		float w = from.width / 2.0f + from.stroke / 2.0f;
		float h = from.height / 2.0f + from.stroke / 2.0f;
		float delta = (float) Math.atan2(py1 - py2, px1 - px2);

		points[0] -= w * Math.cos(delta);
		points[1] -= h * Math.sin(delta);

		w = to.width / 2.0f + to.stroke / 2.0f;
		h = from.height / 2.0f + from.stroke / 2.0f;
		delta = (float) Math.atan2(py2 - py1, px2 - px1);

		points[2] -= w * Math.cos(delta);
		points[3] -= h * Math.sin(delta);
	}

	protected void computeConnectorSquare(NodeData from, NodeData to,
			float[] xy, XYConverter camera) {

	}

	protected void drawShadow(PGraphics g, XYConverter camera) {
		// TODO Auto-generated method stub
		
	}

	protected void drawShape(PGraphics g, XYConverter camera) {
		computeConnector(src, trg, points, camera);

		g.pushStyle();

		g.stroke(strokeARGB);
		g.strokeWeight(stroke);

		g.line(points[0], points[1], points[points.length - 2],
				points[points.length - 1]);

		g.popStyle();
	}

	protected float getLabelAnchorX(XYConverter camera) {
		return 0;
	}

	protected float getLabelAnchorY(XYConverter camera) {
		return 0;
	}
}
