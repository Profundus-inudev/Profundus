package tech.inudev.profundus.utils;

import java.util.UUID;

import tech.inudev.profundus.utils.DatabaseUtil.Table;

public abstract class PFAgent extends PFID{

	String screenName;
	
	public PFAgent(Table type,String name) {
		super(type);
		screenName = name;
	}
	
	public PFAgent() {}
	
	public abstract void sendMessage(String str, Boolean sendOnLogin);

}
