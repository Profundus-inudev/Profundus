package tech.inudev.profundus.database;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

/**
 * 物や土地の売買を行うクラス。
 * 
 * @author kidocchy
 *
 */
class TransactionHandler{
	UUID transID;		//DB=UUID
	PFAgent seller;	//DB=UUID
	PFAgent buyer;	//DB=UUID NULLABLE
	int price;			//DB=INT
	Payment payMethod;//DB=VARCHAR NULLABLE
	Boolean onSale;	//DB=BIT
	String description;//DB=VARCHAR
	Instant createdAt;//DB=TIMESTAMP
	Instant closedAt;	//DB=TIMESTAMP NULLABLE
	Result transactionResult;//DB=TRANSACTION NULLABLE
	
	public enum Payment{
		WALLET,
		BANK
	}
	
	public enum Result{
		CLOSED_NORMALLY,
		CANCELLED_BY_SELLER
	}
	
	TransactionHandler(PFAgent u, String s){
		transID = UUID.randomUUID();
		seller = u;
		price = -1;
		onSale = false;
		description = s;
		createdAt = Instant.now();
	}
	
	TransactionHandler(UUID id){
		transID = id;
	}
	
	static TransactionHandler getById(UUID id) {
		TransactionHandler th = new TransactionHandler(id);
		th = DBUTransaction.fetch(th);
		return th;		
	}
	
	TransactionHandler setPrice(int amount) {
		if(amount >= 0) {
			price = amount;
			seller.sendMessage("Price is set to" + amount, false);
		}else {
			price = -1;
			onSale = false;
		}
		return this;
	}
	
	TransactionHandler setBuyer(PFAgent nullable) {
		buyer = nullable;
		return this;
	}
	
	boolean setSale() {
		if(price>=0) {
			onSale = true;
			seller.sendMessage("Start to sale for" + price, false);
			return true;
		}
		return false;
	}
	
	void removeSale() {
		onSale = false;
	}
	
	public void executeTransaction(Payment pay){
		int buyerBalance = -1;
		switch(pay){
			case WALLET:
			case BANK:
				
		}
		//BalanceCheck
		if(buyerBalance < price) {}
		// move money from buyer->seller
		
		//SetNewOwner
		
		//complete;
		transactionResult = Result.CLOSED_NORMALLY;
		onSale = false;
		
	}
	
	
}


