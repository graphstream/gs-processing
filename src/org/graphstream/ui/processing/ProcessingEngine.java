package org.graphstream.ui.processing;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import org.graphstream.stream.ProxyPipe;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.processing.data.DataSet;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class ProcessingEngine extends PApplet implements Observer {
	private static final long serialVersionUID = 5361578111229195307L;

	public static final float DEFAULT_FRAME_RATE = 60;

	public static enum Hook {
		SETUP_START, SETUP_END, DRAW_START, BEFORE_BACKGROUND_DRAWING, BEFORE_ELEMENT_DRAWING, DRAW_END
	}

	final DataSet data;
	final Camera camera;
	Layout layout;
	LinkedList<ProxyPipe> pumpBeforeDraw;

	int backgroundRGB;
	PImage backgroundImage;

	final float[] drawnEdgePoints = { 0, 0, 0, 0 };

	EnumMap<Hook, List<Hookable>> hooks;

	public ProcessingEngine(DataSet data) {
		this.data = data;

		layout = null;
		camera = new Camera(this);
		pumpBeforeDraw = new LinkedList<ProxyPipe>();
		backgroundImage = null;
		backgroundRGB = 0xFFEDEDED;
		hooks = new EnumMap<Hook, List<Hookable>>(Hook.class);

		data.addObserver(this);
	}

	public void addProxyPipeToPump(ProxyPipe pipe) {
		pumpBeforeDraw.add(pipe);
	}

	public void addHook(Hook hook, Hookable hookable) {
		if (!hooks.containsKey(hook))
			hooks.put(hook, new LinkedList<Hookable>());

		hooks.get(hook).add(hookable);
	}

	public void removeHook(Hook hook, Hookable hookable) {
		if (hooks.containsKey(hook))
			hooks.get(hook).remove(hookable);
	}

	protected void trigger(Hook hook) {
		if (!hooks.containsKey(hook))
			return;

		List<Hookable> h = hooks.get(hook);

		for (int i = 0; i < h.size(); i++)
			h.get(i).trigger(hook, this);
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
		trigger(Hook.SETUP_START);

		size(600, 600);
		hint(ENABLE_OPTIMIZED_STROKE);
		smooth(8);
		frameRate(DEFAULT_FRAME_RATE);

		setDefaultFont();

		trigger(Hook.SETUP_END);
	}

	protected void setDefaultFont() {
		InputStream in = ProcessingEngine.class
				.getResourceAsStream("resource/DefaultFont.vlw");

		if (in == null)
			System.err.printf("default font can not be found.\n");
		else {
			PFont font;

			try {
				font = new PFont(in);
				textFont(font, 12);

				in.close();
			} catch (IOException e) {
				System.err.printf("default font can not be loaded : %s\n",
						e.getMessage());
			}
		}
	}

	@Override
	public void draw() {
		trigger(Hook.DRAW_START);

		for (int i = 0; i < pumpBeforeDraw.size(); i++)
			pumpBeforeDraw.get(i).pump();

		if (layout != null
				&& layout.getStabilization() < layout.getStabilizationLimit())
			layout.compute();

		trigger(Hook.BEFORE_BACKGROUND_DRAWING);

		if (backgroundImage != null)
			background(backgroundImage);
		else
			background(backgroundRGB);

		trigger(Hook.BEFORE_ELEMENT_DRAWING);

		data.draw(g, camera);

		trigger(Hook.DRAW_END);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		redraw();
	}

	public static interface Hookable {
		void trigger(Hook hook, ProcessingEngine engine);
	}
}
