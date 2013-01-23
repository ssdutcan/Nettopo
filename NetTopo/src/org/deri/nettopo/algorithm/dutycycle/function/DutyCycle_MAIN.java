package org.deri.nettopo.algorithm.dutycycle.function;

import java.util.*;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.mmgr.Algor_MMGR;
import org.deri.nettopo.algorithm.mmgr.function.MMGR_FindAllMMCs;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.display.Painter;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.NodeConfiguration;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.Util;
/*
 * the description of the Duty-Cycle algorithm:
 * 每个node均有rank和leftTime两个属性。
 * rank的取值范围[0,1,2,3,4,5,6,7,8,9]。当rank=0，node醒；否则，node睡。
 * leftTime的取值根据rank，即leftTime=(10-rank)*5；我们这里暂时设置刷新时间为5秒
 *  @author implemented by Can Ma
 */
import org.eclipse.swt.graphics.RGB;


public class DutyCycle_MAIN implements AlgorFunc {
	private Algorithm algorithm;
	private WirelessSensorNetwork wsn;
	private NetTopoApp app;
	private HashMap<Integer,Integer> ranks;
	public HashMap<Integer,Integer> leftTimes;
	private HashMap<Integer,Integer[]> neighbors;
	private HashMap<Integer,Boolean> awake;
	private MMGR_FindAllMMCs findAllMMCs;
	private Painter painter;
	boolean needInitialization;
	
	
	public DutyCycle_MAIN(Algorithm algorithm){
		this.algorithm = algorithm;
		awake = new HashMap<Integer,Boolean>();
		neighbors = new HashMap<Integer,Integer[]>();
		needInitialization = true;
		findAllMMCs=new MMGR_FindAllMMCs(new Algor_MMGR());
	}
	
	public DutyCycle_MAIN(){
		this(null);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(isNeedInitialization()){
			initializeWork();
		}
//		findAllMMCs.findAllMMCs();
		DutyCycle_Function();
		resetColorAfterDutyCycle();
		app.getPainter().rePaintAllNodes();
		app.getDisplay().asyncExec(new Runnable(){
			public void run(){
				app.refresh();
			}
		});
		
	}
	
	public void runForStatistics(){
		if(isNeedInitialization()){
			initializeWork();
		}
		DutyCycle_Function();
		DutyCycle_Function();
	}
	
	public void setRankForAllNodes(HashMap<Integer,Integer> tempRanks){
		this.ranks=tempRanks;
	}
	
	public void setLeftTimeForAllNodes(HashMap<Integer,Integer> tempLeftTimes){
		this.leftTimes=tempLeftTimes;
	}
	/*
	 * 将每个node的初始化为醒的状态
	 */
	private void initializeAwake(){
		int ids[] = wsn.getAllSensorNodesID();
		for(int i=0;i<ids.length;i++){
//			if(((SensorNode_TPGF)wsn.getNodeByID(ids[i])).IsMPR())
				setAwake(ids[i],true);
		}
	}
	
	/*
	 * 设置node的active,available属性（即是否awake），将清醒node添加入awake。
	 */
	private void setAwake(int id, boolean isAwake){
		Integer ID = new Integer(id);
		awake.put(ID, isAwake);
		if(wsn.nodeSimpleTypeNameOfID(id).contains("Sensor")){
			((SensorNode)wsn.getNodeByID(id)).setActive(isAwake);
			((SensorNode)wsn.getNodeByID(id)).setAvailable(true);
		}
	}
	
	/*
	 * 初始化每个node的邻居，将id和neighbor存入neighbors。
	 */
	private void initializeNeighbors(){
		int[] ids = wsn.getAllSensorNodesID();
		for(int i=0;i<ids.length;i++){
			Integer ID = new Integer(ids[i]);
			Integer[] neighbor = getNeighbor(ids[i]);
			neighbors.put(ID, neighbor);
		}
	}
	
	/*
	 * 获取某个id节点的所有邻居id
	 */
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
	 * 初始化duty-cycle网络模型
	 */
	private void initializeWork(){
		app = NetTopoApp.getApp();
		wsn = app.getNetwork();
		initializeAwake();//at first all are true
		setNeedInitialization(false);
	}
	
	/************the above methods are to initialise the Duty-Cycle fields***************/
	/************the following methods are to be used in Duty-Cycle*************/
	
	/****************************************************************************/
	
	private void resetColorAfterDutyCycle(){
		Iterator<Integer> iter = awake.keySet().iterator();
		while(iter.hasNext()){
			Integer id = iter.next();
			Boolean isAwake = awake.get(id);
			if(isAwake.booleanValue()){
				wsn.resetNodeColorByID(id.intValue(), NodeConfiguration.AwakeNodeColorRGB);
			}else{
				if(((SensorNode_TPGF)wsn.getNodeByID(id.intValue())).IsMPR()==false)
					wsn.resetNodeColorByID(id.intValue(), NodeConfiguration.NotMPRNodeColorRGB);
				else
					wsn.resetNodeColorByID(id.intValue(), NodeConfiguration.SleepNodeColorRGB);
			}
		}
	}
	/*
	 * DutyCycle功能函数，即rank=0,node醒，否则，node睡。
	 */
	private void DutyCycle_Function(){
		initializeNeighbors();
		this.findAllMMCs.findAllMMCs();
		int[] disordered = Util.generateDisorderedIntArrayWithExistingArray(wsn.getAllSensorNodesID());
		for(int i=0;i<disordered.length;i++){
			int currentID = disordered[i];
			if(((SensorNode_TPGF)wsn.getNodeByID(currentID)).IsMPR()==false){
				this.setAwake(currentID, false);
			}
			else if((Integer)ranks.get(currentID)==0||(Integer)ranks.get(currentID)==1){
				this.setAwake(currentID, true);
			}
			else if(((SensorNode)wsn.getNodeByID(currentID)).isSearched()){
				this.setAwake(currentID, true);
			}
			else{
				this.setAwake(currentID, false);
			}
		}
	}
	public Algorithm getAlgorithm(){
		return algorithm;
	}
	
	public boolean isNeedInitialization() {
		return needInitialization;
	}
	
	public void setNeedInitialization(boolean needInitialization) {
		this.needInitialization = needInitialization;
	}
	
	public HashMap<Integer,Integer> getLeftTimes(){
		return this.leftTimes;
	}
	public WirelessSensorNetwork getWSN(){
		return this.wsn;
	}
	
}
