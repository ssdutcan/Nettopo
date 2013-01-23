package org.deri.nettopo.algorithm.dutycycle.function;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.mctpgf.Algor_McTPGF;
import org.deri.nettopo.algorithm.mctpgf.function.McTPGF_FindOnePath;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.WirelessSensorNetwork;

/*
 *  @author implemented by Can Ma
 */

public class DutyCycle_McTPGF_FindOnePath implements AlgorFunc{

	private Algorithm algorithm;
	private WirelessSensorNetwork wsn;
	private NetTopoApp app;
	private DutyCycle_MAIN dutyCycle;
	private McTPGF_FindOnePath findOnePath;
	private HashMap<Integer,Integer> ranks;
	private HashMap<Integer,Integer> leftTimes;
	
	public DutyCycle_McTPGF_FindOnePath(Algorithm algorithm){
		this.algorithm = algorithm;
		dutyCycle = new DutyCycle_MAIN();
		findOnePath = new McTPGF_FindOnePath(new Algor_McTPGF());
	}
	
	public DutyCycle_McTPGF_FindOnePath(){
		this(null);
	}
	
	public void initializeRank(){
		this.ranks = new HashMap<Integer,Integer>();
		int[] ids = wsn.getAllSensorNodesID();
		Random r = new Random();
		for(int i=0;i<ids.length;i++){
			int id = ids[i];
			int rank = r.nextInt(10);
			ranks.put(new Integer(id), new Integer(rank));
		}
	}
	
	public void setLeftTime(){
		this.leftTimes = new HashMap<Integer,Integer>();
		int[] ids = wsn.getAllSensorNodesID();
		for(int i=0;i<ids.length;i++){
			int id = ids[i];
			int leftTime=((10-(Integer)ranks.get(id))%10)*5;
			leftTimes.put(new Integer(id), new Integer(leftTime));
		}
	}
	
	public void resetRankAfterDutyCycle(){
		int[] ids = wsn.getAllSensorNodesID();
		for(int i=0;i<ids.length;i++){
			int id = ids[i];
			int rank = ((Integer)ranks.get(id)+1)%10;
			ranks.put(new Integer(id), new Integer(rank));
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		app = NetTopoApp.getApp();
		wsn = app.getNetwork();
		initializeRank();
		setLeftTime();
		
		Timer timer = app.getTimer_func();
		if(timer != null)
			timer.cancel();
		app.setTime_func(new Timer());
		app.setTimertask_func(new TimerTask(){
			public void run() {
				entry();
			}
		});
		app.getTimer_func().schedule(app.getTimertask_func(), 0, app.getFunc_INTERVAL() * 100);
	}
	
	public void entry() {
		dutyCycle.setRankForAllNodes(ranks);
		dutyCycle.setLeftTimeForAllNodes(leftTimes);
		findOnePath.setLeftTimeForAllNodes(leftTimes);
		dutyCycle.run();
		findOnePath.run();
		findOnePath.sleepDelayAdd();
		resetRankAfterDutyCycle();
		setLeftTime();
	}
	
	public Algorithm getAlgorithm(){
		return algorithm;
	}

}
