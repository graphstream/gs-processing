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

public class ProcessingEngine extends PApplet implements Observer {
	private static final long serialVersionUID = 5361578111229195307L;

	final DataSet data;
	final Camera camera;
	Layout layout;
	LinkedList<ProxyPipe> pumpBeforeDraw;

	public ProcessingEngine(DataSet data) {
		this.data = data;
		this.layout = null;
		this.camera = new Camera(this);

		pumpBeforeDraw = new LinkedList<ProxyPipe>();
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

	@Override
	public void setup() {
		size(500, 500);
	}

	@Override
	public void draw() {
		for (int i = 0; i < pumpBeforeDraw.size(); i++)
			pumpBeforeDraw.get(i).pump();

		layout.compute();
		background(222, 222, 222);

		for (int i = 0; i < data.getEdgeDataCount(); i++)
			draw(data.getEdgeData(i));

		for (int i = 0; i < data.getNodeDataCount(); i++)
			draw(data.getNodeData(i));
	}

	protected void draw(NodeData data) {
		if (!data.isVisible())
			return;

		fill(data.rgb, data.a);

		if (data.stroke == 0)
			noStroke();
		else
			stroke(data.strokeRGB, data.strokeA);

		ellipse(camera.xToScreenX(data.x), camera.yToScreenY(data.y), 15, 15);
	}

	protected void draw(EdgeData data) {
		if (!data.isVisible())
			return;

		float x1, y1, x2, y2;
		x1 = camera.xToScreenX(data.src.x);
		x2 = camera.xToScreenX(data.trg.x);
		y1 = camera.yToScreenY(data.src.y);
		y2 = camera.yToScreenY(data.trg.y);
		
		stroke(data.rgb, data.a);
		line(x1, y1, x2, y2);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		redraw();
	}
}
