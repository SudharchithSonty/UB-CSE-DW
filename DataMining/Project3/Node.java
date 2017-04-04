/**
 * Created by Amair on 08-12-2015.
 */
public class Node {
    double GINI;
    int split_point;
    int column;
    int begin;
    int end;
    Node leftNode;
    Node righNode;
    int leftLabel;
    int rightLabel;
    double column_splitValue;
    boolean isLeaf=false;
    public Node(double GINI, int split_point, int column, int begin, int end, boolean isLeaf, double column_splitValue, int right, int left){
        this.begin = begin;
        this.GINI = GINI;
        this.split_point = split_point;
        this.column = column;
        this.end = end;
        this.isLeaf = isLeaf;
        this.column_splitValue = column_splitValue;
        this.leftLabel = left;
        this.rightLabel = right;
    }
}
