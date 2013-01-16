package org.graphstream.ui.processing.data;

import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.VisibilityMode;
import org.graphstream.ui.processing.XYConverter;

import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;

public abstract class ElementData {

	public final String id;

	public int stroke;
	public int strokeARGB;

	VisibilityMode visibility;

	public String uiClass;

	public double uiColor;

	public String label;
	public int labelAlignX;
	public int labelAlignY;
	public int labelARGB;
	public float labelSize;
	public PFont labelFont;
	public boolean showLabel;

	public ElementData(String id) {
		this.id = id;

		stroke = 1;
		strokeARGB = 0xFF333333;
		visibility = VisibilityMode.NORMAL;

		showLabel = false;
		labelAlignX = PConstants.CENTER;
		labelAlignY = PConstants.CENTER;
		labelARGB = 0xFF222222;
		labelSize = 16;
		labelFont = null;
	}

	public boolean isVisible() {
		return visibility != VisibilityMode.HIDDEN;
	}

	public void hide() {
		visibility = VisibilityMode.HIDDEN;
	}

	public void show() {
		visibility = VisibilityMode.NORMAL;
	}


	public void draw(PGraphics g, XYConverter camera) {
		if (!isVisible())
			return;

		drawShape(g, camera);
		drawLabel(g, camera);
	}
	
	protected abstract void drawShadow(PGraphics g, XYConverter camera);
	
	protected abstract void drawShape(PGraphics g, XYConverter camera);

	protected abstract float getLabelAnchorX(XYConverter camera);
	
	protected abstract float getLabelAnchorY(XYConverter camera);
	
	protected void drawLabel(PGraphics g, XYConverter camera) {
		if (!showLabel)
			return;

		float x = getLabelAnchorX(camera);
		float y = getLabelAnchorY(camera);
		
		g.pushStyle();
		g.fill(labelARGB);
		if (labelFont != null)
			g.textFont(labelFont);
		g.textSize(labelSize);
		g.textAlign(labelAlignX, labelAlignX);
		g.text(label, x, y);
		g.popStyle();
	}
}
