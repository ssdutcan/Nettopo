package org.deri.nettopo.algorithm.mmgr;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.mmgr.function.MMGR_ConnectMPRs;
import org.deri.nettopo.algorithm.mmgr.function.MMGR_ConnectNeighbors;
import org.deri.nettopo.algorithm.mmgr.function.MMGR_CoverTopo;
import org.deri.nettopo.algorithm.mmgr.function.MMGR_FindAllMMCs;

/*
 * @author implemented by Can Ma
 */
public class Algor_MMGR implements Algorithm{

	AlgorFunc[] functions;
	
	public Algor_MMGR(){
		functions=new AlgorFunc[4];
		functions[0]=new MMGR_ConnectNeighbors(this);
		functions[1]=new MMGR_CoverTopo(this);
		functions[2]=new MMGR_ConnectMPRs(this);
		functions[3]=new MMGR_FindAllMMCs(this);
	}
	@Override
	public AlgorFunc[] getFunctions() {
		// TODO Auto-generated method stub
		return functions;
	}
	

}
