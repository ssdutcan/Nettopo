package org.deri.nettopo.algorithm.ckn.function;

import java.util.Timer;
import java.util.TimerTask;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.tpgf.Algor_TPGF;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_FindOnePath;
import org.deri.nettopo.app.NetTopoApp;

public class CKN_TPGF_FindOnePath implements AlgorFunc {

	private Algorithm algorithm;
	private CKN_MAIN ckn;
	private TPGF_FindOnePath findOnePath;
	
	public CKN_TPGF_FindOnePath(Algorithm algorithm){
		this.algorithm = algorithm;
		ckn = new CKN_MAIN();
		findOnePath = new TPGF_FindOnePath(new Algor_TPGF());
	}

	public CKN_TPGF_FindOnePath(){
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
		findOnePath.run();
	}

	public Algorithm getAlgorithm(){
		return algorithm;
	}
}
