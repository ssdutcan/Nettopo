package org.deri.nettopo.node;

import org.eclipse.swt.graphics.RGB;

public class Hole implements VNode {
	private int id;
	private RGB color;
	private boolean available = false;
	private String[] attrNames = {};
	private String errorMsg;
	
	public Hole(){
		id = 0;
		color = NodeConfiguration.BlackHoleNodeColorRGB;
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	public void setErrorMsg(String msg){
		this.errorMsg = msg;
	}
	
	public void setColor(RGB color){
		this.color = color;
	}
	
	public int getID(){
		return id;
	}
	
	public String[] getAttrNames(){
		return attrNames;
	}
	
	public String getAttrErrorDesciption(){
		return errorMsg;
	}
	
	public RGB getColor(){
		return color;
	}
	
	public boolean setAttrValue(String attrName, String value){
		return true;
	}
	
	public String getAttrValue(String attrName){
		return null;
	}

	@Override
	public void setAvailable(boolean available) {
		this.available = available;
	}
	
	public boolean getAvailable() {
		return this.available;
	}
}
