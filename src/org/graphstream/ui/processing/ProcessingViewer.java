package org.graphstream.ui.processing;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
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

		data = new DataSet(source);
		engine = new ProcessingEngine(data);
		layout = new SpringBox();

		source.addSink(layout);
		layout.addAttributeSink(data.getXYZUpdater());

		engine.setLayout(layout);

		if (pipe != null)
			engine.addProxyPipeToPump(pipe);
		
		engine.init();
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

	public static void main(String ... args) throws Exception {
		testStyle();
		//testGenerator();
		//test();
	}
	
	public static void testGenerator(String... args) throws Exception {
		BarabasiAlbertGenerator gen = new BarabasiAlbertGenerator();
		ProcessingViewer viewer = new ProcessingViewer(gen);

		gen.begin();

		viewer.openFrame();

		for (int i = 0; i < 1000; i++) {
			gen.nextEvents();
			Thread.sleep(50);
		}

		gen.end();
	}
	
	public static void test() throws Exception {
		StyleSheet sheet = new StyleSheet();
		String stylesheet = "node { fill-color: #FF0000; stroke-color: #0000FF; } node.spe { fill-color: #00FF00; }";
		
		try {
			sheet.load(stylesheet);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		System.out.printf("[default] fill-color   : %X\n", sheet.nodeRules.defaultRule.style.getFillColor(0).getRGB());
		System.out.printf("[default] stroke-color : %X\n", sheet.nodeRules.defaultRule.style.getStrokeColor(0).getRGB());
		System.out.printf("[class]   fill-color   : %X\n", sheet.nodeRules.byClass.get("spe").style.getFillColor(0).getRGB());
		System.out.printf("[class]   stroke-color : %X\n", sheet.nodeRules.byClass.get("spe").style.getStrokeColor(0).getRGB());
	}
	
	public static void testStyle() {
		AdjacencyListGraph g = new AdjacencyListGraph("g"); 
		ProcessingViewer viewer = new ProcessingViewer(g);

		viewer.openFrame();
		
		g.addAttribute("ui.stylesheet", "node { fill-color: #FF0000; }");
		g.addNode("A");
		g.addNode("B");
		g.addNode("C");
		g.addEdge("AB", "A", "B");
		g.addEdge("BC", "B", "C");
		g.addEdge("CA", "C", "A");
	}
}
