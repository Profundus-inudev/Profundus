package tech.inudev.profundus.utils;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;

/**
 * 物や土地の売買を行うクラス。
 * 
 * @author kidocchy
 *
 */
public class TransactionHandler{
	
	protected UUID transID;		//DB=UUID
	protected PFAgent seller;	//DB=UUID
	protected PFAgent buyer;	//DB=UUID NULLABLE
	@Getter
	private int price;			//DB=INT
	protected Payment payMethod;//DB=VARCHAR NULLABLE
	protected Boolean onSale;	//DB=BIT
	protected String description;//DB=VARCHAR
	protected Instant createdAt;//DB=TIMESTAMP
	protected Instant closedAt;	//DB=TIMESTAMP NULLABLE
	protected Result transactionResult;//DB=TRANSACTION NULLABLE
	
	enum Payment{
		WALLET,
		BANK
	}
	
	enum Result{
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
	
	TransactionHandler setBuyer(PFAgent u) {
		buyer = u;
		return this;
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
	
	Boolean setSale() {
		if(price>=0) {
			onSale = true;
			return true;
		}
		return false;
	}
	
	void removeSale() {
		onSale = false;
	}
	
	void executeTransaction(Payment pay){
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


