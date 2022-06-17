package tech.inudev.profundus.utils;

import java.util.*;


public class PFID {
	
	private static Map<UUID, UUID> storedPFIDList = new HashMap<UUID,UUID>();
	
	/**
	 *Convert Minecraft Player UUID to Profundus user UUID;
	 *This may enable secure management of transactions;
	 *
	 * @param uuid Minecraft Player UUID
	 * @return uuid Profundus User UUID
	 */
	static UUID getPFID(UUID uuid) {
		UUID yourID = newUUID(); //とりあえず，仮に発行しておく。
		//
		if(storedPFIDList.containsKey(uuid)) {
			yourID = storedPFIDList.get(uuid); //HashMapにあれば，それを返却。
			return yourID;
		}
		if(true) { 
			//search DB for PFID that matches Player UUID;
			//yourID = *getFromDB*
		} else {
				//add new entry to DB (e.g. new user)
		}
		storedPFIDList.put(uuid, yourID);
		//add to class variables(HashMap) for optimization
		
		return yourID;
	}
	
	static UUID newUUID() {
		//とりあえず，ランダムで発行。
		return UUID.randomUUID();
	}
	
	static void removeFromStoredList(UUID uuid) {
		if(storedPFIDList.containsKey(uuid)) {
			storedPFIDList.remove(uuid);
		}
	}
	
	
	
}
