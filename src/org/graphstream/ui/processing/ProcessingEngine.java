package org.graphstream.ui.processing;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import org.graphstream.stream.ProxyPipe;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.processing.data.DataSet;
import org.graphstream.ui.processing.data.EdgeData;
import org.graphstream.ui.processing.data.NodeData;

import processing.core.PApplet;
import processing.core.PImage;

public class ProcessingEngine extends PApplet implements Observer {
	private static final long serialVersionUID = 5361578111229195307L;

	public static final float DEFAULT_FRAME_RATE = 40;

	final DataSet data;
	final Camera camera;
	Layout layout;
	LinkedList<ProxyPipe> pumpBeforeDraw;

	int backgroundRGB;
	PImage backgroundImage;

	final float[] drawnEdgePoints = { 0, 0, 0, 0 };

	public ProcessingEngine(DataSet data) {
		this.data = data;

		layout = null;
		camera = new Camera(this);
		pumpBeforeDraw = new LinkedList<ProxyPipe>();
		backgroundImage = null;
		backgroundRGB = 0xFFEDEDED;

		data.addObserver(this);
	}

	public void addProxyPipeToPump(ProxyPipe pipe) {
		pumpBeforeDraw.add(pipe);
	}

	public DataSet getDataSet() {
		return data;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public void setBackgroundImage(String path) {
		backgroundImage = loadImage(path);
	}

	public void setBackgroundColor(int argb) {
		backgroundRGB = argb;
		backgroundImage = null;
	}

	@Override
	public void setup() {
		size(600, 600);
		hint(ENABLE_OPTIMIZED_STROKE);
		smooth(8);
		frameRate(DEFAULT_FRAME_RATE);
	}

	@Override
	public void draw() {
		for (int i = 0; i < pumpBeforeDraw.size(); i++)
			pumpBeforeDraw.get(i).pump();

		if (layout.getStabilization() < layout.getStabilizationLimit())
			layout.compute();

		if (backgroundImage != null)
			background(backgroundImage);
		else
			background(backgroundRGB);

		for (int i = 0; i < data.getEdgeDataCount(); i++)
			draw(data.getEdgeData(i));

		for (int i = 0; i < data.getNodeDataCount(); i++)
			draw(data.getNodeData(i));
	}

	protected void draw(NodeData data) {
		if (!data.isVisible())
			return;

		pushStyle();

		fill(data.fillARGB);

		if (data.stroke == 0)
			noStroke();
		else {
			stroke(data.strokeARGB);
			strokeWeight(data.stroke);
		}

		switch (data.shape) {
		case CIRCLE:
			ellipse(camera.xToScreenX(data.x), camera.yToScreenY(data.y),
					data.width, data.height);
			break;
		case SQUARE:
			rect(camera.xToScreenX(data.x) - data.width / 2.0f,
					camera.yToScreenY(data.y) - data.height / 2.0f, data.width,
					data.height);
			break;
		}

		popStyle();
	}

	protected void draw(EdgeData data) {
		if (!data.isVisible())
			return;

		computeConnector(data.src, data.trg, drawnEdgePoints);

		pushStyle();

		stroke(data.strokeARGB);
		strokeWeight(data.stroke);

		line(drawnEdgePoints[0], drawnEdgePoints[1], drawnEdgePoints[2],
				drawnEdgePoints[3]);
		popStyle();
	}

	protected void computeConnector(NodeData from, NodeData to, float[] xy) {
		switch (from.shape) {
		case CIRCLE:
			computeConnectorCircle(from, to, xy);
			break;
		case SQUARE:
			computeConnectorSquare(from, to, xy);
			break;
		}
	}

	protected void computeConnectorCircle(NodeData from, NodeData to,
			float[] points) {
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

	protected void computeConnectorSquare(NodeData from, NodeData to, float[] xy) {

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		redraw();
	}
}
