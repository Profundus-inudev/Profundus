package tech.inudev.profundus.utils;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;

import lombok.Getter;
import tech.inudev.profundus.utils.DatabaseUtil.Table;

public class PFChunk {
		private static Map<String,PFChunk> storedList = new HashMap<String,PFChunk>();
		
		UUID worldUUID;		//DB
		int chunkX;			//DB
		int chunkZ;			//DB
		@Getter
		PFAgent owner;		//DB nullable
		PFAgent editor;		//DB nullable
		Instant timestamp; 	//DB
		Chunk chunk;
		Block saleSign;		//DB=x,y,z nullable
		TransactionHandler trans;	//DB=trans.transID nullable
		
		public PFChunk(Chunk c) {
			chunkX = c.getX();
			chunkZ = c.getZ();
			worldUUID = c.getWorld().getUID();
			owner = null;
			editor = null;
			timestamp = Instant.now();
			chunk = c;
			saleSign = null;
			trans = null;
			DBUChunk.insert(this);
		}
		
		public PFChunk(){
			
		}
		
		//Instance getter
		public static PFChunk getByChunk(Chunk c) {
			PFChunk r = new PFChunk(c);
			if(storedList.containsKey(r.toString())){
				r = storedList.get(r.toString());
			}else {
				r.chunk = c;
				r = DBUChunk.fetch(r); //return as is if no DB
				//TODO r.trans
			}
			return r;
		}
		
		//Instance getter
		public static PFChunk getByCoord(World w, int x, int z) {
			return getByChunk(w.getChunkAt(x, z));
		}

		
		//Checking owner/editor
		public Boolean isOwner(PFAgent u) {
			if(owner.getType() != Table.USER) {return false;}
			return owner.getPfid() == u.getPfid();
		}
		public Boolean isOwner(Player p) {
			return isOwner(User.getByPlayer(p));
		}
		
		public Boolean canEdit(PFAgent u) {
			if(isOwner(u)) {return true;}
			if(editor.getType() != Table.USER) {return false;}
			return editor.getPfid() == u.getPfid();
		}
		public Boolean canEdit(Player p) {
			return canEdit(User.getByPlayer(p));
		}
		
		
		public void setOwner(PFAgent u) {
			owner = u;
			updateDB();
		}
		
		// Authorize someone Edit this chunk. NULL for unauthorized;
		void authEdit(UUID editor /*null for cancel*/) {}
		
		
		
		//Calculate geological heights (for use of populating SaleSign)
		int getHighestBlockY() {
			int highest = -256;
			for(int cx=0;cx<16;cx++) {
				for(int cz=0;cz<16;cz++) {
					Block block = chunk.getBlock(cx, 0, cz);
					highest=Math.max(chunk.getWorld().getHighestBlockYAt(block.getX(), block.getZ()), highest);
				}
			}
			return highest;
		}
		int getHighestBlock3x3Y() {
			int highest = -256;
			for(int cx=0;cx<3;cx++) {
				for(int cz=0;cz<3;cz++) {
					Block block = chunk.getBlock(cx, 0, cz);
					highest=Math.max(chunk.getWorld().getHighestBlockYAt(block.getX(), block.getZ()), highest);
				}
			}
			return highest;
		}
		double getAverageHeightY() {
			int average = 0;
			for(int cx=0;cx<16;cx++) {
				for(int cz=0;cz<16;cz++) {
					Block block = chunk.getBlock(cx, 0, cz);
					average += chunk.getWorld().getHighestBlockYAt(block.getX(), block.getZ());
				}
			}
			return average/256D;
		}

		
		//Real estate Transaction Handling
		public PFChunk createTransaction() {
			if(trans == null) {trans = new TransactionHandler(PFID.getByPFID(owner.pfid),this.toString());}
			return this;
		}
		
		public PFChunk setPrice(int amount) {
			if(trans == null) {createTransaction();}
			trans.setPrice(amount);
			return this;
		}
		
		@SuppressWarnings("deprecation")
		public Boolean startSelling(PFAgent nullable) {
			trans.setBuyer(nullable);
			if(!trans.setSale()) {return false;} //sell start check
			updateSaleSign();
			return true;
		}
		
		@SuppressWarnings("deprecation")
		void updateSaleSign() {
			
			if(saleSign == null) {saleSign = chunk.getBlock(0, getHighestBlock3x3Y(), 0);}
			saleSign.setType(Material.BIRCH_SIGN);
			BlockState bs = saleSign.getState();
			
			if(!trans.setSale()) {
				((Sign) bs).setLine(0, "SALE PENDING");
				((Sign) bs).setLine(1, "");
				((Sign) bs).setLine(2, "");

			} else {
				((Sign) bs).setLine(0, ">> FOR SALE <<");
				((Sign) bs).setLine(1, String.valueOf(trans.getPrice()));
				if(trans.buyer != null) {
					((Sign) bs).setLine(2, trans.buyer.screenName);
				}else {
					((Sign) bs).setLine(2,"");
				}
			}
			((Sign) bs).setEditable(false);
			((Rotatable) bs.getBlockData()).setRotation(BlockFace.NORTH_WEST);
			bs.update(true);			
		}
		
		void removeSellTarget() {
			if(saleSign == null) {return;}
			saleSign.setType(Material.AIR);
			saleSign = null;
		}
		
		public Boolean isForSale() {
			return trans.onSale;
		}
		
		void sold() {}

		private void updateDB() {
			DBUChunk.update(this);
		}
		
		
		public String toString() {
			return "Chunk:"+ chunk.getWorld().getName() +"@"+chunk.getX() + "," + chunk.getZ();
		}
	
}
