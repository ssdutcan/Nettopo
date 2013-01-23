package org.deri.nettopo.algorithm.mmgr.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.display.Painter;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.SinkNode;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.node.tpgf.SourceNode_TPGF;
import org.deri.nettopo.util.Coordinate;

public class MMGR_ConnectMPRs implements AlgorFunc{
	private Algorithm algorithm;
	private Coordinate sourcePos;
	private SourceNode_TPGF node_source;
	private WirelessSensorNetwork wsn;
	private Painter painter;
	private NetTopoApp app;
	public MMGR_ConnectMPRs(Algorithm algorithm){
		this.algorithm=algorithm;
	}
	public MMGR_ConnectMPRs(){
		this.algorithm=null;
	}
	public Algorithm getAlgorithm(){
		return this.algorithm;
	}
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
	public void connectMPRs(boolean needPainting){
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
			if(nodes_source.size()>0){
				node_source = (SourceNode_TPGF)nodes_source.iterator().next();
				this.sourcePos=wsn.getCoordianteByID(node_source.getID());
				node_source.setAvailable(true);
			}
		}
		
//		Collection<VNode> nodes_source = wsn.getNodes("org.deri.nettopo.node.tpgf.SourceNode_TPGF",true);
//		SourceNode_TPGF node_source = (SourceNode_TPGF)nodes_source.iterator().next();
//		this.sourcePos=wsn.getCoordianteByID(node_source.getID());
		
		Collection<VNode> sensorNodes = wsn.getNodes("org.deri.nettopo.node.tpgf.SensorNode_TPGF",true);
		sensorNodes = getAvailableSensorNode(sensorNodes);
		SensorNode_TPGF[] nodes = new SensorNode_TPGF[sensorNodes.size()];
		nodes = (SensorNode_TPGF[])sensorNodes.toArray(nodes);//nodes°üº¬ÁËsourceºÍsensorNode
//		for(int i=0;i<nodes.length;i++){
//			System.out.println(nodes[i].getID());
//			System.out.println(nodes.length);
//		}
		
		
		for(int i=0;i<nodes.length;i++){
			int id_i = nodes[i].getID();
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
//			System.out.println("id"+id_i);
//			for(int j=0;j<neighborList_i.size();j++){
//				System.out.println("neighbor"+neighborList_i.get(j));
//			}
			
//			System.out.println(id_i+" "+Integer.valueOf(neighborList_i.get(0)));
			
//			if(needPainting)
//			painter.paintConnection(id_i,Integer.valueOf(neighborList_i.get(0)));
		}
//		if(needPainting){
//			app.getDisplay().asyncExec(new Runnable(){
//				public void run() {
//					app.refresh();
//				}
//			});
//		}
		findMPRs(node_source);
	} 
	public  boolean findMPRs(SensorNode_TPGF node){
		if(node.IsMPR()==true){
			return true;
		}
		node.setIsMPR(true);
		int numMPR=3;
		int nodeID=node.getID();
		List<Integer> MPRsID=node.getMPRs();
		List<Integer> neighborsID = node.getNeighbors();
		painter.paintCircle(wsn.getCoordianteByID(nodeID), 60);
//		System.out.println(nodeID+" "+neighborsID.size());
		for(int i=0;i<numMPR&&i<neighborsID.size();i++){
			MPRsID.add(Integer.valueOf(neighborsID.get(i)));
			painter.paintConnection(nodeID, Integer.valueOf(neighborsID.get(i)));
		}
		for(int i=0;i<MPRsID.size();i++){
			System.out.println(nodeID+" "+MPRsID.get(i));
		}
		if(noFartherNeighbor(node)){
			return true;
		}
		for(int i=0;i<numMPR&&i<neighborsID.size();i++){
			
			findMPRs((SensorNode_TPGF)wsn.getNodeByID(MPRsID.get(i)));
		}
		app.getDisplay().asyncExec(new Runnable(){
			public void run() {
				app.refresh();
			}
		});
		return false;
//		List<Integer> MPRsID=node.getMPRs();
//		List<Integer> neighborsID = node.getNeighbors();
//		int id_farthest=((Integer)neighborsID.get(0)).intValue();
//		int id_relay=node.getID();
//		Coordinate c_farthest=wsn.getCoordianteByID(id_farthest);
//		Coordinate c_relay=wsn.getCoordianteByID(id_relay);
//		double dis_farthest=c_farthest.distance(this.sourcePos);
//		double dis_relay=c_relay.distance(this.sourcePos);
//		
//		int numMPRs=3;
//		for(int i=0;i<numMPRs;i++){
//			MPRsID.add(Integer.valueOf(neighborsID.get(i)));
//			painter.paintConnection(id_relay, Integer.valueOf(neighborsID.get(i)));
//		}
//		if(dis_farthest<dis_relay)
//			return true;
//		else{
//			return findMPRs((SensorNode_TPGF)wsn.getNodeByID(MPRsID.get(0)))
//			&&findMPRs((SensorNode_TPGF)wsn.getNodeByID(MPRsID.get(1)))
//			&&findMPRs((SensorNode_TPGF)wsn.getNodeByID(MPRsID.get(2)));
//		}
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
		connectMPRs(true);
//		System.out.println("FUCK!");
	}
}
