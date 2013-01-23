package org.deri.nettopo.node;

import org.eclipse.swt.graphics.RGB;
import org.deri.nettopo.util.Util;
import org.deri.nettopo.util.FormatVerifier;

public class SensorNode implements VNode {
	
	private int id;
	private int energy;
	private int tr; // transmission radius
	private int maxTR; // maximum transmission radius
	private int streamRate;
	private int bandwidth;
	private boolean available;
	private boolean active;
	//-----------------------------------
	private boolean searched;
	//-----------------------------------
	private String[] attrNames;
	private String errorMsg;
	private RGB color;
	private boolean isMPR;
	public SensorNode(){
		id = 0;
		energy = 0;
		tr = 0;
		maxTR = 0;
		streamRate = 0;
		bandwidth = 0;
		available = true;
		active = true;
		searched=false;
		this.isMPR=false;
		attrNames  = new String[]{"Energy", "Max TR", "Bandwidth"};
		errorMsg = null;
		color = NodeConfiguration.SensorNodeColorRGB;
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	public void setEnergy(int energy){
		this.energy = energy;
	}
	
	public void setTR(int tr){
		this.tr = tr;
	}
	
	public void setMaxTR(int maxTR){
		this.maxTR = maxTR;
	}
	
	public void setStreamRate(int streamRate){
		this.streamRate = streamRate;
	}
	
	public void setBandwidth(int bandwidth){
		this.bandwidth = bandwidth;
	}
	
	public void setAvailable(boolean available){
		this.available = available;
	}
	
	public void setIsMPR(boolean isMPR){
		this.isMPR=isMPR;
	}
	
	public void setErrorMsg(String msg){
		this.errorMsg = msg;
	}
	
	public String getErrorMsg(){
		return this.errorMsg;
	}
	
	public void setColor(RGB color){
		this.color = color;
	}
	
	public int getID(){
		return id;
	}
	
	public boolean IsMPR(){
		return this.isMPR;
	}
	public int getEnergy(){
		return energy;
	}
	
	public int getTR(){
		return tr;
	}
	
	public int getMaxTR(){
		return maxTR;
	}
	
	public int getStreamRate(){
		return streamRate;
	}
	
	public int getBandWidth(){
		return bandwidth;
	}

	public boolean isAvailable(){
		return available;
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
	
	/**
	 * this method will help to verify the input in the wizard pages.
	 */
	public boolean setAttrValue(String attrName, String value){
		boolean isAttrValid = true;
		int index = Util.indexOf(attrNames, attrName);
		switch(index){
		case 0:
			if(FormatVerifier.isNotNegative(value)){
				setEnergy(Integer.parseInt(value));
			}else{
				errorMsg = "Energy must be a non-negative integer";
				isAttrValid = false;
			}
			break;
		case 1: 
			if(FormatVerifier.isNotNegative(value)){
				setMaxTR(Integer.parseInt(value));
			}else{
				errorMsg = "Transmission radius must be a non-negative integer";
				isAttrValid = false;
			}
			break;
		case 2:
			if(FormatVerifier.isNotNegative(value)){
				setBandwidth(Integer.parseInt(value));
			}else{
				errorMsg = "Bandwidth must be a non-negative integer";
				isAttrValid = false;
			}
			break;
		default:
			errorMsg = "No such argument";
			isAttrValid = false;
			break;
		}
		
		return isAttrValid;
	}
	
	public String getAttrValue(String attrName){
		int index = Util.indexOf(attrNames, attrName);
		switch(index){
		case 0:
			return String.valueOf(getEnergy());
		case 1:
			return String.valueOf(getMaxTR());
		case 2:
			return String.valueOf(getBandWidth());
		default:
			return null;
		}
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	//----------------------------------------------
	public boolean isSearched(){
		return this.searched;
	}
	public void setSearched(boolean searched){
		this.searched=searched;
	}
	//----------------------------------------------
	
}
