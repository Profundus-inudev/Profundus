package tech.inudev.profundus.utils;

import java.util.UUID;


public class PFID {
	/**
	 *Convert Minecraft Player UUID to Profundus user UUID;
	 *This may enable secure management of transactions;
	 *
	 * @param uuid
	 * @return uuid
	 */
	static UUID getPFID(UUID uuid) {
		//search DB for PFID that matches Player UUID;
		//If none, create random UUID;
		
		return UUID.randomUUID();
	}
}
