package org.deri.nettopo.algorithm.dutycyclex.function;

import java.util.ArrayList;
import java.util.HashMap;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.util.Coordinate;

public class DutyCyclex_MAIN implements AlgorFunc{

	private Algorithm algorithm;
	private HashMap<Integer,Boolean> awakeOrAsleep;
	private WirelessSensorNetwork wsn;
	
	private void setAwakeOrAsleep(int id, boolean isAwake){
		Integer ID = new Integer(id);
		awakeOrAsleep.put(ID, isAwake);
		if(wsn.nodeSimpleTypeNameOfID(id).contains("Sensor")){
			((SensorNode)wsn.getNodeByID(id)).setActive(isAwake);
			((SensorNode)wsn.getNodeByID(id)).setAvailable(true);
		}
	}
	private Integer[] getNeighbor(int id){
		int[] ids = wsn.getAllSensorNodesID();
		ArrayList<Integer> neighbor = new ArrayList<Integer>();
		int maxTR = Integer.parseInt(wsn.getNodeByID(id).getAttrValue("Max TR"));
		Coordinate coordinate = wsn.getCoordianteByID(id);
		for(int i=0;i<ids.length;i++){
			Coordinate tempCoordinate = wsn.getCoordianteByID(ids[i]);
			if(ids[i] != id && Coordinate.isInCircle(tempCoordinate, coordinate, maxTR)){
				neighbor.add(new Integer(ids[i]));
			}
		}
		return neighbor.toArray(new Integer[neighbor.size()]);
	}
	
	/*
	 * 将每个node的初始化为醒的状态
	 */
	private void initializeAwake(){
		int ids[] = wsn.getAllSensorNodesID();
		for(int i=0;i<ids.length;i++){
			if(((SensorNode_TPGF)wsn.getNodeByID(ids[i])).IsMPR())
				setAwakeOrAsleep(ids[i],true);
		}
	}
	public DutyCyclex_MAIN(Algorithm algorithm){
		this.algorithm = algorithm;
		awakeOrAsleep = new HashMap<Integer,Boolean>();
	}
	public DutyCyclex_MAIN(){
		this(null);
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
