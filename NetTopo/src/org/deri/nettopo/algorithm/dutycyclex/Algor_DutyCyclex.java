package org.deri.nettopo.algorithm.dutycyclex;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;

public class Algor_DutyCyclex implements Algorithm{

	AlgorFunc[] functions ;
	
	public Algor_DutyCyclex(){
		functions = new AlgorFunc[3];
	}
	@Override
	public AlgorFunc[] getFunctions() {
		// TODO Auto-generated method stub
		return functions;
	}

}
