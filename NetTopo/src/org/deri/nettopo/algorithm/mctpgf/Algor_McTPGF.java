package org.deri.nettopo.algorithm.mctpgf;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.mctpgf.function.McTPGF_ConnectNeighbors;
import org.deri.nettopo.algorithm.mctpgf.function.McTPGF_FindOnePath;

/*
 *  @author implemented by Can Ma
 */
public class Algor_McTPGF implements Algorithm{

	AlgorFunc[] functions;
	
	public Algor_McTPGF(){
		functions = new AlgorFunc[2];
		functions[0] = new McTPGF_ConnectNeighbors(this);
		functions[1] = new McTPGF_FindOnePath(this);
	}
	@Override
	public AlgorFunc[] getFunctions() {
		// TODO Auto-generated method stub
		return functions;
	}

}
