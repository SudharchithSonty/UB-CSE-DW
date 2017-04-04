import java.util.*;

/**
 * Created by Amair on 10-12-2015.
 */
public class Forest {
    static int total_columns;
    int numTree = 15;
    ArrayList<Tree> decisionTrees = new ArrayList<Tree>();
    int numRanCol = 5;
    Tree rand_tree;
    PriorityQueue<Node> column_gini = new PriorityQueue<Node>(new Comparator<Node>() {
        @Override
        public int compare(Node o1, Node o2) {
            if(o1.GINI > o2.GINI){
                return 1;
            }
            else if(o1.GINI < o2.GINI){
                return -1;
            }
            else{
                return 0;
            }
        }
    });

    public HashMap<Character, Double> process(ArrayList<ArrayList<Double>> dataSet) {
        decisionTrees = new ArrayList<Tree>();
        ArrayList<Integer> usedData = new ArrayList<Integer>();
        for (int i = 0; i < numTree; i++){
            DecisionTree decision_tree = new DecisionTree();
            rand_tree = new Tree();
            Tree rand_tree =   decision_tree.processData(dataSet, true, numRanCol, numTree, usedData);
            ArrayList<Integer> temp_used = decision_tree.getUsedData();
            for(int index: temp_used){
                usedData.add(index);
            }
            decisionTrees.add(rand_tree);
        }
      ArrayList<ArrayList<Double>> test = new DecisionTree().getTestData(dataSet);
        return calculateAccuracy(test);

    }
    public HashMap<Character, Double> calculateAccuracy(ArrayList<ArrayList<Double>> testData){
        double a=0;
        double b=0;
        double c=0;
        double d =0;
        HashMap<Character, Double> stats = new HashMap<Character, Double>();
        double correctOnes=0;
        double correct = 0;
        double wrong =0;
        double totalOnes = 0;
        double myOnes =0;
        total_columns = testData.get(0).size();
        for (int j = 0; j < decisionTrees.size(); j++) {
            if(decisionTrees.get(j).root == null){
                decisionTrees.remove(j);
            }
        }
        for (int i = 0; i < testData.size(); i++) {
            int countOfZero=0;
            int countOfOne=0;
            int predicted_result=-1;
            ArrayList<Double> current_row = testData.get(i);
            for (int j = 0; j < decisionTrees.size(); j++) {
                Tree decision_tree = decisionTrees.get(j);
                 predicted_result = getPrediction(decision_tree,current_row);
                if(predicted_result == 0){
                    countOfZero++;
                }
                else if(predicted_result == 1){
                    countOfOne++;
                }
            }
            if(countOfOne > countOfZero){
                predicted_result = 1;
            }
            else{
                predicted_result = 0;
            }
            if(predicted_result == current_row.get(total_columns-1)){
                if(predicted_result==0){
                    d++;
                }
                if(predicted_result==1){
                    a++;
                }
            }
            else{
                if(predicted_result==0){
                    b++;
                }
                if(predicted_result==1){
                    c++;
                }
            }
    }
        double accuracy = (a + d)/(a + b + c + d);
        double precision = (a)/(a + c); if((a+c) == 0) {precision = a;}
        double recall = (a/ (a + b)); if((a+b) == 0) {recall = a;}
        double fMeasure = ((2*recall*precision)/(recall + precision));  if(fMeasure == 0) {fMeasure = 2*recall*precision;}

        stats.put('a', accuracy);
        stats.put('r',recall);
        stats.put('p', precision);
        stats.put('f',fMeasure);
        return stats;
    }


    public int getPrediction(Tree decision_tree, ArrayList<Double> test_row) {

        Node current_node = decision_tree.root;
        int predicted_result = -1;
        if(current_node == null){
            return -1;
        }
        while (current_node != null) {
            int column = current_node.column;
            if (current_node.isLeaf) {
                break;
            }
            if (test_row.get(column) <= current_node.column_splitValue) {
                if(current_node.leftNode != null)
                    current_node = current_node.leftNode;
                else
                    return -1; // return current_node.leftLabel;

            } else {
                if(current_node.righNode !=null)
                    current_node = current_node.righNode;
                else
                    return -1; //return current_node.rightLabel;
            }
        }
        if (test_row.get(current_node.column) <= current_node.column_splitValue) {
            predicted_result = current_node.leftLabel;
        } else {
            predicted_result = current_node.rightLabel;
        }


        return predicted_result;
    }
}
