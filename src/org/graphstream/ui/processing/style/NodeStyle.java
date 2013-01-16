package org.graphstream.ui.processing.style;

import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.FillMode;
import org.graphstream.ui.processing.data.NodeData;
import org.graphstream.ui.processing.data.NodeData.Shape;

public class NodeStyle extends ElementStyle {

	Shape shape;
	float shapeRadius;
	int[] fillARGBs;
	FillMode fillMode;

	boolean shouldFill;
	boolean shouldShape;

	public NodeStyle() {
		shouldFill = false;
		shouldShape = false;
	}

	public void defaults() {
		super.defaults();

		shouldFill = true;
		shouldShape = true;
		shape = Shape.CIRCLE;
		fillARGBs = new int[] { 0xF0222222 };
		fillMode = FillMode.PLAIN;
	}

	public void applyTo(NodeData data) {
		super.applyTo(data);

		if (shouldFill) {
			switch (fillMode) {
			case DYN_PLAIN:
				data.fillARGB = getDynamicColor(data.uiColor, fillARGBs);
				break;
			default:
				data.fillARGB = fillARGBs[0];
				break;
			}
		}
	}

	public static int getDynamicColor(double uiColor, int[] fillARGBs) {
		uiColor = Math.min(1, Math.max(0, uiColor));
		int idx1 = (int) (uiColor * (fillARGBs.length - 1));
		int idx2 = idx1 + 1;

		if (idx1 == idx2 || idx2 == fillARGBs.length)
			return fillARGBs[idx1];

		double s = 1.0 / (fillARGBs.length - 1);
		double d = (uiColor - idx1 * s) / s;

		int a1 = (fillARGBs[idx1] & 0xFF000000) >> 24;
		int r1 = (fillARGBs[idx1] & 0x00FF0000) >> 16;
		int g1 = (fillARGBs[idx1] & 0x0000FF00) >> 8;
		int b1 = (fillARGBs[idx1] & 0x000000FF);

		int a2 = (fillARGBs[idx2] & 0xFF000000) >> 24;
		int r2 = (fillARGBs[idx2] & 0x00FF0000) >> 16;
		int g2 = (fillARGBs[idx2] & 0x0000FF00) >> 8;
		int b2 = (fillARGBs[idx2] & 0x000000FF);

		a1 = a1 + (int) ((a2 - a1) * d);
		r1 = r1 + (int) ((r2 - r1) * d);
		g1 = g1 + (int) ((g2 - g1) * d);
		b1 = b1 + (int) ((b2 - b1) * d);

		return (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
	}
}
