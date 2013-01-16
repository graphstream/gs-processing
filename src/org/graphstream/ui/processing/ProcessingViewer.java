package org.graphstream.ui.processing;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.Source;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.graphicGraph.stylesheet.StyleSheet;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.processing.data.DataSet;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

public class ProcessingViewer {
	public static enum ThreadingModel {
		GRAPH_IN_ANOTHER_THREAD, GRAPH_IN_VIEWER_THREAD
	}

	Source source;

	ProcessingEngine engine;
	Frame frame;
	DataSet data;
	Layout layout;
	ProxyPipe pipe;

	public ProcessingViewer(Source source) {
		this(source, ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
	}

	public ProcessingViewer(Source source, ThreadingModel threadingModel) {
		if (threadingModel == ThreadingModel.GRAPH_IN_ANOTHER_THREAD) {
			pipe = new ThreadProxyPipe(source);
			source = pipe;
		}

		this.source = source;

		data = new DataSet(source);
		engine = new ProcessingEngine(data);

		if (pipe != null)
			engine.addProxyPipeToPump(pipe);

		source.addAttributeSink(data.getXYZUpdater());
		
		engine.init();
	}

	public void enableLayout() {
		enableLayout(new SpringBox());
	}
	
	public void enableLayout(Layout layout) {
		if (this.layout != null) {
			source.removeSink(this.layout);
			this.layout.removeAttributeSink(data.getXYZUpdater());
		}

		this.layout = layout;
		source.addSink(layout);
		layout.addAttributeSink(data.getXYZUpdater());
		engine.setLayout(layout);
	}

	public Frame openFrame() {
		if (frame == null) {
			frame = new Frame();
			frame.setLayout(new BorderLayout());
			frame.add(BorderLayout.CENTER, engine);
			frame.setResizable(true);
			frame.setSize(600, 600);

			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent arg0) {
					System.exit(0);
				}
			});
		}

		frame.setVisible(true);

		return frame;
	}

	public static void main(String... args) throws Exception {
		testStyle();
		// test();
	}

	public static void test() throws Exception {
		StyleSheet sheet = new StyleSheet();
		String stylesheet = "node { fill-color: #FF0000, #00FF00; fill-mode: dyn-plain; stroke-color: #0000FF; } node.spe { fill-color: #00FF00; }";

		try {
			sheet.load(stylesheet);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		System.out.printf("[default] fill-color   : %X\n",
				sheet.nodeRules.defaultRule.style.getFillColor(0).getRGB());
		System.out.printf("[default] stroke-color : %X\n",
				sheet.nodeRules.defaultRule.style.getStrokeColor(0).getRGB());
		System.out.printf("[class]   fill-color   : %X\n",
				sheet.nodeRules.byClass.get("spe").style.getFillColor(0)
						.getRGB());
		System.out.printf("[class]   stroke-color : %X\n",
				sheet.nodeRules.byClass.get("spe").style.getStrokeColor(0)
						.getRGB());
	}

	public static void testStyle() throws Exception {
		AdjacencyListGraph g = new AdjacencyListGraph("g");
		ProcessingViewer viewer = new ProcessingViewer(g);

		viewer.openFrame();

		g.addAttribute("ui.stylesheet",
				"node { fill-color: #FF0000, #00FF00; fill-mode: dyn-plain; }");
		g.addNode("A");
		g.addNode("B");
		g.addNode("C");
		g.addEdge("AB", "A", "B");
		g.addEdge("BC", "B", "C");
		g.addEdge("CA", "C", "A");

		g.getNode("A").setAttribute("ui.color", 0);
		g.getNode("B").setAttribute("ui.color", 0);
		g.getNode("C").setAttribute("ui.color", 0);

		double da, db, dc;
		da = 0.2;
		db = 0.05;
		dc = 0.1;

		double a, b, c;
		a = 0;
		b = 0;
		c = 0;

		while (true) {
			a += da;
			b += db;
			c += dc;

			if (a < 0) {
				a = 0;
				da *= -1;
			} else if (a > 1) {
				a = 1;
				da *= -1;
			}

			if (b < 0) {
				b = 0;
				db *= -1;
			} else if (b > 1) {
				b = 1;
				db *= -1;
			}

			if (c < 0) {
				c = 0;
				dc *= -1;
			} else if (c > 1) {
				c = 1;
				dc *= -1;
			}

			g.getNode("A").setAttribute("ui.color", a);
			g.getNode("B").setAttribute("ui.color", b);
			g.getNode("C").setAttribute("ui.color", c);

			Thread.sleep(50);
		}
	}
}
