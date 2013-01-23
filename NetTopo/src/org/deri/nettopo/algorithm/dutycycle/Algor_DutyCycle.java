package org.deri.nettopo.algorithm.dutycycle;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.dutycycle.function.DutyCycle_MMGR_CoverTopo;
import org.deri.nettopo.algorithm.dutycycle.function.DutyCycle_TPGF_FindOnePath;
import org.deri.nettopo.algorithm.dutycycle.function.DutyCycle_McTPGF_FindOnePath;

/*
 * @author implemented by Can Ma
 */
public class Algor_DutyCycle implements Algorithm{
	
	AlgorFunc[] functions ;
	
	public Algor_DutyCycle(){
		functions = new AlgorFunc[3];
		functions[0]=new DutyCycle_TPGF_FindOnePath(this);
		functions[1]=new DutyCycle_McTPGF_FindOnePath(this);
		functions[2]=new DutyCycle_MMGR_CoverTopo(this);
	}
	@Override
	public AlgorFunc[] getFunctions() {
		// TODO Auto-generated method stub
		return functions;
	}

}
