
public class TreeNode implements Cloneable {

	public int getSplit_point() {
		return split_point;
	}

	public void setSplit_point(int split_point) {
		this.split_point = split_point;
	}


	public String getChoice() {
		return Choice;
	}

	public void setChoice(String choice) {
		Choice = choice;
	}

	public double getLabel() {
		return label;
	}

	public void setLabel(float label) {
		this.label = label;
	}

	public TreeNode getLeftNode() {
		return leftNode;
	}

	public void setLeftNode(TreeNode leftNode) {
		this.leftNode = leftNode;
	}

	public TreeNode getRighNode() {
		return righNode;
	}

	public void setRighNode(TreeNode righNode) {
		this.righNode = righNode;
	}

	public double getColumn_splitValue() {
		return splitValue;
	}

	public void setColumn_splitValue(double column_splitValue) {
		this.splitValue = splitValue;
	}
	
	


	int split_point;
	String Choice;
	double label = -1;
	TreeNode leftNode;
	TreeNode righNode;
	double splitValue = 0.80;


	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
		}
	@Override
	public String toString() {
		return "Classname: " + this.leftNode + " distance " + this.righNode;
	}
}
