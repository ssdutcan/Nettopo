package org.deri.nettopo.display;

import java.util.Vector;

import org.deri.nettopo.util.AttractorField;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.DistrictedArea;
import org.eclipse.swt.graphics.RGB;

public class Painter_3D implements Painter {

	@Override
	public void paintAllNodes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintConnection(int id1, int id2, RGB rgb) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintConnection(int id1, int id2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintMobileNodes(double[][] path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintNode(int id, RGB rgb) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintNode(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintNodeFocus(Coordinate c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintNodeFocus(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintNodes(int[] nodes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeNode(Coordinate c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeNode(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeNodeFocus(Coordinate c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeNodeFocus(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAllNodeFocus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rePaintAllNodes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		String output = "3D has not been created";
		System.out.println(output);
		return output;
	}

	@Override
	public void paintAttractors(AttractorField fields) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintDistrictedArea(DistrictedArea area) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAttractors(AttractorField fields) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeDistrictedArea(DistrictedArea area) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void removeDistrictedAreas(Vector<DistrictedArea> areas) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintDistrictedAreas(Vector<DistrictedArea> areas) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintCircle(Coordinate c, int radius) {
		// TODO Auto-generated method stub
		
	}
	
}
