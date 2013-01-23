package org.deri.nettopo.algorithm.mctpgf.function;

import java.util.*;

import org.eclipse.swt.graphics.RGB;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.node.*;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.node.tpgf.SourceNode_TPGF;
import org.deri.nettopo.display.*;
import org.deri.nettopo.util.*;

/*
 *  @author implemented by Can Ma
 */
public class McTPGF_FindOnePath implements AlgorFunc{

	private WirelessSensorNetwork wsn;
	private Painter painter;
	private LinkedList<Integer> path;
	private ArrayList<Integer> searched;
	private SinkNode sink;
	private Coordinate sinkPos;
	private int sinkTR;
	private Algorithm algorithm;
	private HashMap<Integer,Integer> leftTimes;
	private HashMap<Integer,Double> weights;
	private int sleepDelay=0;
	private int totalDelay=0;
	private static int flag=0;
	
	
	public void setLeftTimeForAllNodes(HashMap<Integer,Integer> tempLeftTimes){
		this.leftTimes=tempLeftTimes;
	}
	
	public void sleepDelayAdd(){
		this.sleepDelay+=5;
	}
	
	public McTPGF_FindOnePath(){
		this(null);
	}
	
	public McTPGF_FindOnePath(Algorithm algorithm){
		this.algorithm = algorithm;
	}
	
	public Algorithm getAlgorithm(){
		return this.algorithm;
	}
	
	public List<Integer> getPath(){
		return path;
	}
	
	public int getHopNum(){
		return (path.size() - 1); 
	}
	
	public void setWeight(){
		this.weights = new HashMap<Integer,Double>();
		int[] ids = wsn.getAllSensorNodesID();
		for(int i=0;i<ids.length;i++){
			int id = ids[i];
			if(((SensorNode)wsn.getNodeByID(id)).isSearched()){
				this.weights.put(new Integer(id), new Double(0));
			}
			else if (((SensorNode_TPGF)wsn.getNodeByID(id)).IsMPR()==false){
				this.weights.put(new Integer(id), new Double(10000));
			}
			else{
				Coordinate c = wsn.getCoordianteByID(id);
				double dis = c.distance(sinkPos);
				double tempWeight=this.leftTimes.get(id)*1+dis;
				this.weights.put(new Integer(id), new Double(tempWeight));
			}
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		findOnePath(true);
	}
	
	public boolean findOnePath(boolean needPainting){
		wsn = NetTopoApp.getApp().getNetwork();
		painter = NetTopoApp.getApp().getPainter();
		path = new LinkedList<Integer>();
		searched = new ArrayList<Integer>();
		boolean canFind = false;
		if(NetTopoApp.getApp().isFileModified()){
			wsn.resetAllNodesAvailable();
			NetTopoApp.getApp().setFileModified(false);
		}
		
		McTPGF_ConnectNeighbors func_connectNeighbors = new McTPGF_ConnectNeighbors();
		func_connectNeighbors.connectNeighbors(false);
		
		if(wsn!=null){
			Collection<VNode> nodes_source = wsn.getNodes("org.deri.nettopo.node.tpgf.SourceNode_TPGF",true);
			Collection<VNode> nodes_sink = wsn.getNodes("org.deri.nettopo.node.SinkNode",true);
			if(nodes_sink.size()>0 && nodes_source.size()>0){
				SourceNode_TPGF node_source = (SourceNode_TPGF)nodes_source.iterator().next(); // retrieve one source node
				sink = (SinkNode)nodes_sink.iterator().next();
				sinkPos = wsn.getCoordianteByID(sink.getID());
				sinkTR = sink.getMaxTR();
				setWeight();
				node_source.setAvailable(true);
				
				if(canReachSink(node_source)){
					canFind = true;
					if(needPainting){
						int searchedNum=1;
						int hopNum=0;
						
						for(int i=path.size()-1;i>0;i--){
							int id1 = ((Integer)path.get(i)).intValue();
							int id2 = ((Integer)path.get(i-1)).intValue();
							
							if(wsn.nodeSimpleTypeNameOfID(id2).contains("Sensor")&&!((SensorNode)wsn.getNodeByID(id2)).isActive()){
								painter.paintNode(id2, new RGB(255,0,255));
								break;
							}
							searchedNum++;
							if(wsn.nodeSimpleTypeNameOfID(id2).contains("Sensor")){
								((SensorNode)wsn.getNodeByID(id2)).setSearched(true);
								painter.paintNode(id2, new RGB(205,149,86));
							}
							painter.paintConnection(id1, id2, new RGB(185,149,86));
						}
						hopNum=searchedNum-1;
						
						/* Add log info */
						final StringBuffer message = new StringBuffer("Path: ");
						for(int i=path.size()-1;i>=0;i--){
							message.append(path.get(i));
							message.append(" ");
							searchedNum--;
							if(searchedNum<=0){
								break;
							}
						}
						
						if(hopNum<path.size()-1){
							totalDelay=sleepDelay+hopNum*5;
							message.append("\tDelay: "+sleepDelay+"+"+hopNum*5);
						}else if(flag==0){
							totalDelay=sleepDelay+hopNum*5;
							flag=1;
							message.append("\tDelay: "+sleepDelay+"+"+hopNum*5);
						}else{
							sleepDelay-=5;
							totalDelay=sleepDelay+hopNum*5;
							message.append("\tDelay: "+sleepDelay+"+"+hopNum*5);
						}
						
						message.append("\tHops: " + hopNum);
						NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
							public void run() {
								NetTopoApp.getApp().addLog(message.toString());
								NetTopoApp.getApp().refresh();
							}
						});
					}
				}
				else{
					if(needPainting)
						NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
							public void run() {
								NetTopoApp.getApp().addLog("No more paths.");
							}
						});
				}
			}
		}
		return canFind;
	}
	public boolean canReachSink(SensorNode_TPGF node){
		
		if(!node.isAvailable())
			return false;
//		System.out.print(node.getID()+" ");
		searched.add(Integer.valueOf(node.getID()));
		
		/* If the distance between the current node and sinknode is 
		 * in both nodes' transmission radius, the node can reach the 
		 * sink. Return immediately */
		if(inOneHop(node)){
			path.add(Integer.valueOf(sink.getID()));
			path.add(Integer.valueOf(node.getID()));
			node.setAvailable(false); // the node cannot be used next time
			return true;
		}
		
		/* If the current node is not one-hop from sink, it search it's
		 * neighbor that is most near to sink and find out whether it can
		 * reach the sink. If not, it searches its' neighbor that is second
		 * most near to sink and go on, etc. The neighbors do not include 
		 * any already searched node that is not in one hope  */
		List<Integer> neighborsID = node.getNeighbors();
		
		/* First we remove all searched node id in the neighbor list */
		for(int i=0;i<searched.size();i++){
			neighborsID.remove(searched.get(i));
		}
		
		/* Then we sort the neighbor list into distance ascending order */
		for(int i=neighborsID.size()-1;i>0;i--){
			for(int j=0; j<i; j++){
				int id1 = ((Integer)neighborsID.get(j)).intValue();
				double weight1=(Double)this.weights.get(id1);
				
				int id2 = ((Integer)neighborsID.get(j+1)).intValue();
				double weight2=(Double)this.weights.get(id2);
				
				if(weight1>weight2){
					Integer swap = neighborsID.get(j);
					neighborsID.set(j, neighborsID.get(j+1));
					neighborsID.set(j+1, swap);
				}
			}
		}
		
		/* Then we search from the neighbor that is most near to sink to the neighbor
		 * that is least near to sink */		
		for(int i=0;i<neighborsID.size();i++){
			int neighborID = ((Integer)neighborsID.get(i)).intValue();
			SensorNode_TPGF neighbor = (SensorNode_TPGF)wsn.getNodeByID(neighborID);
				if(canReachSink(neighbor)){
					//System.out.println(neighbor.getID() + " can get sink");
					path.add(Integer.valueOf(node.getID()));
					node.setAvailable(false); // the node cannot be used next time
					return true;
				}
		}
		return false;
	}
	
	public boolean inOneHop(SensorNode_TPGF node){
		int nodeID = node.getID();
		Coordinate c = wsn.getCoordianteByID(nodeID);
		int tr = node.getMaxTR();
		double distance = 0;
		distance=c.distance(sinkPos);
		if(distance<=tr && distance<= sinkTR)
			return true;
		return false;
	}
}
