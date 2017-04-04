import java.util.ArrayList;
import java.util.List;

public class Rule {
	private List<String> body;
	private List<String> head;
	
	public Rule(){
		this.body = new ArrayList<String>();
		this.head = new ArrayList<String>();
	}

	public List<String> getBody() {
		return body;
	}

	public List<String> getHead() {
		return head;
	}

	public void setBody(List<String> body) {
		this.body = body;
	}

	public void setHead(List<String> head) {
		this.head = head;
	}
}
