import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Itemset {
	
	private List<String> items;
	private int count;
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Itemset() {
		this.items = new ArrayList<String>();
	}

	public List<String> getItems() {
		return items;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		List<String> l1 = ((Itemset) obj).getItems();
		List<String> l2 = this.items;
	
		final Set<String> s1 = new HashSet<String>(l1);
	    final Set<String> s2 = new HashSet<String>(l2);

	    return s1.equals(s2);
		
	}
	
}
