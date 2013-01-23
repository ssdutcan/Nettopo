package org.deri.nettopo.algorithm.mmgr.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.mctpgf.function.McTPGF_ConnectNeighbors;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.display.Painter;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.node.tpgf.SourceNode_TPGF;
import org.deri.nettopo.util.Coordinate;

/*
 *  @author implemented by Can Ma
 */
public class MMGR_CoverTopo implements AlgorFunc{
	
	private WirelessSensorNetwork wsn;
	private Painter painter;
	private Algorithm algorithm;
	private LinkedList<Integer> path;
	private ArrayList<Integer> searched;
	private Coordinate sourcePos;
	
	public MMGR_CoverTopo(){
		this(null);
	}
	public MMGR_CoverTopo (Algorithm algorithm){
		this.algorithm = algorithm;
	}
	public Algorithm getAlgorithm(){
		return this.algorithm;
	}
	public List<Integer> getPath(){
		return path;
	}
	
	public boolean coverTopo(boolean needPainting){
		wsn = NetTopoApp.getApp().getNetwork();
		painter = NetTopoApp.getApp().getPainter();
		path = new LinkedList<Integer>();
		searched = new ArrayList<Integer>();
		boolean canFind = false;
		
		if(NetTopoApp.getApp().isFileModified()){
			wsn.resetAllNodesAvailable();
			NetTopoApp.getApp().setFileModified(false);
		}
		MMGR_ConnectNeighbors func_connectNeighbors = new MMGR_ConnectNeighbors();
		func_connectNeighbors.connectNeighbors(false);
		
		if(wsn!=null){
			Collection<VNode> nodes_source = wsn.getNodes("org.deri.nettopo.node.tpgf.SourceNode_TPGF",true);
			if(nodes_source.size()>0){
				SourceNode_TPGF node_source = (SourceNode_TPGF)nodes_source.iterator().next(); // retrieve one source node
				node_source.setAvailable(true);
			}
		}
		return canFind;
	}
	
	public boolean coverTopoFromANode(SensorNode_TPGF node){
		if(!node.isAvailable())
			return false;
		boolean flag=false;
		
		List<Integer> neighborsID = node.getNeighbors();
		List<Integer> MPRsID=node.getMPRs();
		int numMPRs=3;
		
		//将relay node的neighbors按降序排列
		for(int i=neighborsID.size()-1;i>0;i--){
			for(int j=0;j<i;j++){
				int id1=((Integer)neighborsID.get(j)).intValue();
				int id2=((Integer)neighborsID.get(j+1)).intValue();
				Coordinate c1=wsn.getCoordianteByID(id1);
				Coordinate c2=wsn.getCoordianteByID(id2);
				double dis1=c1.distance(sourcePos);
				double dis2=c2.distance(sourcePos);
				if(dis1<dis2){
					Integer swap =neighborsID.get(j);
					neighborsID.set(j, neighborsID.get(j+1));
					neighborsID.set(j+1, swap);
				}
			}
		}
		for(int i=0;i<numMPRs;i++){
			MPRsID.add(Integer.valueOf(neighborsID.get(i)));
		}
		int id_farthest=((Integer)neighborsID.get(0)).intValue();
		int id_relay=node.getID();
		Coordinate c_farthest=wsn.getCoordianteByID(id_farthest);
		Coordinate c_relay=wsn.getCoordianteByID(id_relay);
		double dis_farthest=c_farthest.distance(sourcePos);
		double dis_relay=c_relay.distance(sourcePos);
		
		if(dis_farthest<dis_relay)
			return true;
		else{
			return coverTopoFromANode((SensorNode_TPGF)wsn.getNodeByID(MPRsID.get(0)))&&
			coverTopoFromANode((SensorNode_TPGF)wsn.getNodeByID(MPRsID.get(1)))&&
			coverTopoFromANode((SensorNode_TPGF)wsn.getNodeByID(MPRsID.get(2)));
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
