package org.deri.nettopo.algorithm.mmgr.function;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.display.Painter;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.NodeConfiguration;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.node.tpgf.SourceNode_TPGF;
import org.deri.nettopo.util.Coordinate;
import org.eclipse.swt.graphics.RGB;

public class MMGR_FindAllMMCs implements AlgorFunc{
	private Algorithm algorithm;
	private WirelessSensorNetwork wsn;
	private NetTopoApp app;
	private SourceNode_TPGF node_source;
	private Coordinate sourcePos;
	private Painter painter;
	
	private Collection<VNode> getAvailableSensorNode(Collection<VNode> sensorNodes){
		Collection<VNode> result =  new LinkedList<VNode>();
		Iterator<VNode> iter = sensorNodes.iterator();
		while(iter.hasNext()){
			SensorNode node = (SensorNode)iter.next();
			if(node.isAvailable()){
				result.add(node);
			}
		}
		return result;
	}
	
	public MMGR_FindAllMMCs(Algorithm algorithm){
		this.algorithm=algorithm;
	}
	public MMGR_FindAllMMCs(){
		this.algorithm=null;
	} 
	public void findAllMMCs(){
		wsn = NetTopoApp.getApp().getNetwork();
		painter = NetTopoApp.getApp().getPainter();
		app = NetTopoApp.getApp();
		if(NetTopoApp.getApp().isFileModified()){
			wsn.resetAllNodesAvailable();
			NetTopoApp.getApp().setFileModified(false);
		}
		MMGR_ConnectNeighbors func_connectNeighbors = new MMGR_ConnectNeighbors();
		func_connectNeighbors.connectNeighbors(false);
		
		if(wsn!=null){
			Collection<VNode> nodes_source = wsn.getNodes("org.deri.nettopo.node.tpgf.SourceNode_TPGF",true);
			Collection<VNode> nodes_sink = wsn.getNodes("org.deri.nettopo.node.SinkNode",true);
			if(nodes_source.size()>0){
				node_source = (SourceNode_TPGF)nodes_source.iterator().next();
				sourcePos=wsn.getCoordianteByID(node_source.getID());
				node_source.setAvailable(true);
			}
		}
		
		Collection<VNode> sensorNodes = wsn.getNodes("org.deri.nettopo.node.tpgf.SensorNode_TPGF",true);
		sensorNodes = getAvailableSensorNode(sensorNodes);
		SensorNode_TPGF[] nodes = new SensorNode_TPGF[sensorNodes.size()];
		nodes = (SensorNode_TPGF[])sensorNodes.toArray(nodes);//nodes°üº¬ÁËsourceºÍsensorNode
		for(int i=0;i<nodes.length;i++){
			List<Integer> neighborList_i = nodes[i].getNeighbors();
			for(int j=neighborList_i.size()-1;j>0;j--){
				for(int h=0;h<j;h++){
					int id1=((Integer)neighborList_i.get(h)).intValue();
					int id2=((Integer)neighborList_i.get(h+1)).intValue();
					Coordinate c1=wsn.getCoordianteByID(id1);
					Coordinate c2=wsn.getCoordianteByID(id2);
					double dis1=c1.distance(sourcePos);
					double dis2=c2.distance(sourcePos);
					if(dis1<dis2){
						Integer swap =neighborList_i.get(h);
						neighborList_i.set(h, neighborList_i.get(h+1));
						neighborList_i.set(h+1, swap);
					}
				}
			}
		}
		recursion(node_source);
		int count=0;
		System.out.println("The MPRs are ");
		for(int i=0;i<nodes.length;i++){
			if(nodes[i].IsMPR()==false){
				int nodeID=nodes[i].getID();
				count++;
				System.out.print(nodeID+" ");
//				nodes[i].setAvailable(false);
//				nodes[i].setActive(false);
//				painter.paintNode(nodeID, new RGB(205,149,86));
			}
		}
		System.out.println();
		System.out.println("count "+count);
		
	}
	
	public boolean recursion(SensorNode_TPGF node){
		if(node.IsMPR()==true){
			return true;
		}
		node.setIsMPR(true);
		int numMPR=3;
		int nodeID=node.getID();
		List<Integer> MPRsID=node.getMPRs();
		List<Integer> neighborsID = node.getNeighbors();
		painter.paintCircle(wsn.getCoordianteByID(nodeID), 60);
		for(int i=0;i<numMPR&&i<neighborsID.size();i++){
			if(Integer.valueOf(neighborsID.get(i))!=2)
				MPRsID.add(Integer.valueOf(neighborsID.get(i)));
		}
		if(noFartherNeighbor(node)){
			return true;
		}
		for(int i=0;i<MPRsID.size();i++){
			recursion((SensorNode_TPGF)wsn.getNodeByID(MPRsID.get(i)));
			painter.paintConnection(nodeID, Integer.valueOf(neighborsID.get(i)));
		}
		app.getDisplay().asyncExec(new Runnable(){
			public void run() {
				app.refresh();
			}
		});
		return false;
	}
	
	public boolean noFartherNeighbor(SensorNode_TPGF node){
		List<Integer> neighborsID = node.getNeighbors();
		int id_farthest=((Integer)neighborsID.get(0)).intValue();
		int id_relay=node.getID();
		Coordinate c_farthest=wsn.getCoordianteByID(id_farthest);
		Coordinate c_relay=wsn.getCoordianteByID(id_relay);
		double dis_farthest=c_farthest.distance(this.sourcePos);
		double dis_relay=c_relay.distance(this.sourcePos);
		if(dis_farthest<dis_relay)
			return true;
		return false;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		findAllMMCs();
	}

}
