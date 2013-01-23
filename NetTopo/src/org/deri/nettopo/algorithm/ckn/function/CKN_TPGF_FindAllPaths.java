package org.deri.nettopo.algorithm.ckn.function;

import java.util.Timer;
import java.util.TimerTask;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.tpgf.Algor_TPGF;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_FindAllPaths;
import org.deri.nettopo.app.NetTopoApp;

public class CKN_TPGF_FindAllPaths implements AlgorFunc {

	private Algorithm algorithm;
	private CKN_MAIN ckn;
	private TPGF_FindAllPaths findAllPaths;
	
	public CKN_TPGF_FindAllPaths(Algorithm algorithm){
		this.algorithm = algorithm;
		ckn = new CKN_MAIN();
		findAllPaths = new TPGF_FindAllPaths(new Algor_TPGF());
	}
	
	public CKN_TPGF_FindAllPaths(){
		this(null);
	}
	
	@Override
	public void run() {
		NetTopoApp app = NetTopoApp.getApp();
		Timer timer = app.getTimer_func();
		if(timer != null)
			timer.cancel();
	
		app.setTime_func(new Timer());
		app.setTimertask_func(new TimerTask(){
			public void run() {
				entry();
			}
		});
		app.getTimer_func().schedule(app.getTimertask_func(), 0, app.getFunc_INTERVAL() * 1000);
	}
	
	
	public void entry() {
		ckn.run();
		findAllPaths.run();
	}

	public Algorithm getAlgorithm(){
		return algorithm;
	}
}
