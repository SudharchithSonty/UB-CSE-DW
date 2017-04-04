import java.util.ArrayList;
import java.util.List;


public class Transaction {
	List <String> transaction;
	
	public Transaction() {
		this.transaction = new ArrayList<String>();
	}

	public List<String> getTransaction() {
		return transaction;
	}

	public void setTransaction(List<String> transaction) {
		this.transaction = transaction;
	}
}
