package org.deri.nettopo.algorithm.tpgf.function;

import java.util.*;

import org.eclipse.swt.graphics.RGB;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.node.tpgf.SourceNode_TPGF;
import org.deri.nettopo.display.*;

public class TPGF_FindAllPaths implements AlgorFunc {
	private Algorithm algorithm;

	private WirelessSensorNetwork wsn = null;

	private Painter painter = null;

	private int pathNum;

	private int orHopNum;

	private int opHopNum;

	public TPGF_FindAllPaths(Algorithm algorithm) {
		this.algorithm = algorithm;
		pathNum = 0;
		orHopNum = 0;
		opHopNum = 0;
	}
	
	public TPGF_FindAllPaths() {
		this(null);
	}

	public Algorithm getAlgorithm() {
		return this.algorithm;
	}

	public void run() {
		NetTopoApp.getApp().getNetwork().resetAllNodesAvailable();
		findAllPaths(true);
	}

	public float getAverageOrHopNum() {
		if (pathNum > 0) {
			return (float) orHopNum / pathNum;
		} else {
			return 0;
		}
	}

	public float getAverageOpHopNum() {
		if (pathNum > 0) {
			return (float) opHopNum / pathNum;
		} else {
			return 0;
		}
	}

	public int getOrHopNum() {
		return orHopNum;
	}

	public int getOpHopNum() {
		return opHopNum;
	}

	public int findAllPaths(boolean needPainting) {
		wsn = NetTopoApp.getApp().getNetwork();
		painter = NetTopoApp.getApp().getPainter();
		pathNum = 0;
		opHopNum = 0;
		orHopNum = 0;

		Collection<VNode> sinkNodes = wsn.getNodes("org.deri.nettopo.node.SinkNode");
		Collection<VNode> sourceNodes = wsn.getNodes("org.deri.nettopo.node.tpgf.SourceNode_TPGF");
		if (sinkNodes.size() <= 0 || sourceNodes.size() <= 0)
			return pathNum;

		SourceNode_TPGF source = (SourceNode_TPGF) sourceNodes.iterator().next();

		TPGF_ConnectNeighbors func_connectNeighbors = (TPGF_ConnectNeighbors) getAlgorithm().getFunctions()[0];
		func_connectNeighbors.connectNeighbors(false);

		TPGF_FindOnePath func_find = (TPGF_FindOnePath) getAlgorithm().getFunctions()[1];
		TPGF_OptimizeOnePath func_op = (TPGF_OptimizeOnePath) getAlgorithm().getFunctions()[2];

		
		while (func_find.findOnePath(false)) {
			pathNum++;
			orHopNum = orHopNum + func_find.getHopNum();
			func_op.optimizeOnePath(false);
			List<Integer> opPath = func_op.getOpPath();
			opHopNum = opHopNum + func_op.getHopNum();
			if (opPath == null)
				return pathNum;
			if (needPainting) {
				/* change the color of the intermediate node on the path */
				for (int p = 1; p < opPath.size() - 1; p++) {
					int id1 = ((Integer) opPath.get(p)).intValue();
					painter.paintNode(id1, new RGB(205, 149, 86));
				}

				/* paint the optimized path stored in opPath */
				for (int j = 0; j < opPath.size() - 1; j++) {
					int id1 = ((Integer) opPath.get(j)).intValue();
					int id2 = ((Integer) opPath.get(j + 1)).intValue();
					painter.paintConnection(id1, id2, new RGB(240, 56, 208));
				}

				/* Add log info */
				final StringBuffer message = new StringBuffer("Path: ");
				for (int p = opPath.size() - 1; p >= 0; p--) {
					message.append(opPath.get(p));
					message.append(" ");
				}
				message.append("\tHops: " + func_op.getHopNum());
				NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
					public void run() {
						NetTopoApp.getApp().addLog(message.toString());
					}
				});
			}
			/* If source node is one hop from sink node, just find one path. */
			if(func_find.inOneHop(source))
				break;
		}
		if (needPainting) {
			final StringBuffer message = new StringBuffer("Number of searched paths: ");
			message.append(pathNum);
			message.append("\tAverage hops: ");
			message.append(getAverageOpHopNum());
			NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
				public void run() {
					NetTopoApp.getApp().refresh();
					NetTopoApp.getApp().addLog(message.toString());
				}
			});
		}
		return pathNum;
	}
}
