import java.util.*;
/**
 * Created by Amair on 07-12-2015.
 */
public class DecisionTree {

	double[][] training_matrix;
	static int total_columns;
	static int total_rows;
	static float test_ratio = 0.1f;
	ArrayList<ArrayList<Double>> test_data = new ArrayList<ArrayList<Double>>();
	ArrayList<Integer> ranRowIndex =  new ArrayList<Integer>();

	double a;
	double b;
	double c;
	double d;

	public Tree processData(ArrayList<ArrayList<Double>> entireData, boolean isRandomForest, int numRandom,int numTree, ArrayList<Integer> usedData) {
		double final_precision=0;
		double final_recall=0;
		double final_accuracy=0;
		double final_fmeasure=0;
		a=0;
		b=0;
		c=0;
		d=0;
		if(!usedData.isEmpty()){
			for(int index: usedData){
				ranRowIndex.add(index);
			}
		}
		int train_size = (int) (entireData.size() * test_ratio);
		//lets take 60-40 ratio for the start
		ArrayList<ArrayList<Double>> training_data = new ArrayList<ArrayList<Double>>();


		//use 60% of the data for training purpose
		for (int row = 0; row < train_size; row++) {
			test_data.add(entireData.get(row));
			// training_data.add(entireData.get(row));
		}
		//use remaining 40% for testing purpose
		for (int row = train_size; row < entireData.size(); row++) {
			training_data.add(entireData.get(row));
			//test_data.add(entireData.get(row));
		}

		training_matrix = new double[training_data.size()][training_data.get(1).size()];
		total_columns = training_data.get(1).size();
		total_rows = training_data.size();

		if (isRandomForest) {
			ArrayList<ArrayList<Double>> randomTrainData = new ArrayList<ArrayList<Double>>();

			while(ranRowIndex.size() < training_data.size()/numTree){
				//int n = maxCol - minCol + 1;
				int num = (int) (Math.random() * ( (training_data.size()-1) - 0 ));//rn.nextInt() % n;
				if(ranRowIndex.contains(num)){
					continue;
				}else{
					ranRowIndex.add(num);
					randomTrainData.add(training_data.get(num));
				}
			}
			for (int row = 0; row < randomTrainData.size(); row++) {
				ArrayList<Double> current_row = training_data.get(row);
				for (int column = 0; column < randomTrainData.get(1).size(); column++) {
					training_matrix[row][column] = current_row.get(column);
				}
			}
			createRule(0, training_data.size()-1, isRandomForest, numRandom);
		}
		else {
			for (int row = 0; row < training_data.size(); row++) {
				ArrayList<Double> current_row = training_data.get(row);
				for (int column = 0; column < training_data.get(1).size(); column++) {
					training_matrix[row][column] = current_row.get(column);
				}
			}
			createRule(0, training_data.size()-1, isRandomForest, numRandom);
		}
		if (isRandomForest) {
			return decision_tree;
		}
		HashMap<Character, Double>  stats = calculateAccuracy(test_data);
		final_accuracy = stats.get('a');
		final_fmeasure = stats.get('f');
		final_precision = stats.get('p');
		final_recall = stats.get('r');
		System.out.println("---DECISION TREE---");
		System.out.println("Accuracy: "+final_accuracy);
		System.out.println("Precision: "+final_precision);
		System.out.println("Recall: "+final_recall);
		System.out.println("F-Measure: "+final_fmeasure);
		return decision_tree;
	}

	public HashMap<Character, Double> processCrossValidatedData(ArrayList<ArrayList<Double>> training,ArrayList<ArrayList<Double>> test, boolean isRandomForest, int numRandom,int numTree, ArrayList<Integer> usedData) {
		training_matrix = new double[training.size()][training.get(1).size()];
		total_columns = training.get(1).size();
		total_rows = training.size();
		for (int row = 0; row < training.size(); row++) {
			ArrayList<Double> current_row = training.get(row);
			for (int column = 0; column < training.get(1).size(); column++) {
				// System.out.println("Row: "+row+"Column: "+column);
				training_matrix[row][column] = current_row.get(column);
			}
		}
		createRule(0, training.size()-1, isRandomForest, numRandom);
		return calculateAccuracy(test);

	}

	public ArrayList<ArrayList<Double>> getTestData(ArrayList<ArrayList<Double>> entireData){
		int train_size = (int) (entireData.size() * test_ratio);
		//use 60% of the data for training purpose
		for (int row = 0; row < train_size; row++) {
			test_data.add(entireData.get(row));
			// training_data.add(entireData.get(row));
		}
		return test_data;
	}


	public ArrayList<Integer> getUsedData(){
		return ranRowIndex;
	}


	public HashMap<Character, Double> calculateAccuracy(ArrayList<ArrayList<Double>> testData){
		HashMap<Character, Double> stats = new HashMap<Character, Double>();
		for(int i=0;i<testData.size();i++){
			ArrayList<Double> current_row = testData.get(i);
			int predicted_result =(int) getPrediction(current_row);
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
		// System.out.println("Accuracy: " + accuracy * 100);
	}

	public int getPrediction(ArrayList<Double> test_row){
		Node current_node = decision_tree.root;
		int predicted_result=-1;
		while(current_node != null) {
			int column = current_node.column;
			if(current_node.isLeaf){
				break;
			}

			double testValue = test_row.get(column);
			double splitValue = current_node.column_splitValue;
			if (testValue <= splitValue) {
				if(current_node.leftNode != null)
					current_node = current_node.leftNode;
				else
					return current_node.leftLabel;
			}
			else{
				if(current_node.righNode !=null)
					current_node = current_node.righNode;
				else
					return current_node.rightLabel;
			}
		}

		if (test_row.get(current_node.column) <= current_node.column_splitValue) {
			predicted_result = current_node.leftLabel;
		} else {
			predicted_result = current_node.rightLabel;
		}


		return (int) predicted_result;
	}

	Tree decision_tree = new Tree();
	public void recurse_data(int first_start, int first_end, int second_start, int second_end, boolean isRandom, int numColumns){
		if(first_start < first_end)
			createRule(first_start,first_end,isRandom, numColumns);
		if(second_start < second_end)
			createRule(second_start,second_end, isRandom, numColumns);
	}

	HashSet<Integer> done_columns = new HashSet<Integer>();
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
	public void createRule(int begin, int end, boolean isRandom, int numRandom){
		column_gini.clear();
		ArrayList<Integer> random_columns = new ArrayList<Integer>();
		if(isRandom){
			Random random = new Random();
			for(int i=0; i< numRandom ; i++){
				int random_column = random.nextInt();
				if(random_columns.contains(random_column)){
					continue;
				}
				else{
					random_columns.add(random.nextInt(total_columns-1));
				}
			}
			for(int column : random_columns){
				CalculateGini(column, begin, end);
			}
		}
		else {
			for (int column = 0; column < total_columns - 1; column++) {
				CalculateGini(column, begin, end);
			}
		}
		//Now we have column-wise GINI, select the column with the least GINI and sort it with respect to that column
		if(column_gini.size() == 0){
			return;
		}
		Node least_gini = column_gini.poll() ;

		// System.out.println("Column: "+least_gini.column);
		decision_tree.addNode(least_gini);
		if(least_gini.isLeaf){
			return;
		}
		sortColumnWise(least_gini.column, least_gini.begin, least_gini.end);
		for (int row = begin; row < total_rows; row++) {
			training_matrix[row][least_gini.column] = -10000; //just a random number
		}
		recurse_data(least_gini.begin, least_gini.split_point, least_gini.split_point, least_gini.end, isRandom, numRandom);

	}

	public boolean sortColumnWise(int column, int begin,int end){
		for (int i = begin; i < end; i++) { //dirty sorting of O(n^2)
			for (int j = begin; j < end - 1; j++) {
				if(training_matrix[j][column] == -10000){
					return false;
				}
				if (training_matrix[j][column] > training_matrix[j + 1][column]) {
					//swap two rows
					double[] temp = training_matrix[j];
					training_matrix[j] = training_matrix[j + 1];
					training_matrix[j + 1] = temp;
				}
			}
		}
		return true;
	}
	public boolean isSortable(int column, int begin, int end) {
		if(training_matrix[begin][column] == -10000)
			return false;
		else
			return true;
	}
	public void CalculateGini(int column, int begin, int end){
		int columns = training_matrix[0].length;
		//arrange the column in ascending order, so that split can be made easily
		//calculate the Gini at each split
		if(!isSortable(column, begin, end)){
			return;
		}
		sortColumnWise(column,begin,end);
		//calculate Gini
		//1. Create COUNT Matrix
		//2. Linearly scan these values, each time updating the count matrix and computing gini index
		//3. Choose the split position that has the least gini index
		PriorityQueue<Node> min_heap = new PriorityQueue<Node>(new Comparator<Node>() {
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
		for (int row = begin; row < end-1; row++) {
			if (training_matrix[row][column] < training_matrix[row + 1][column]) {
				int[][] count_matrix = new int[2][2];
				//count class labels before the assumed split
				int countZeroes_before = 0;
				int countOnes_before = 0;
				for (int i = begin; i <= row; i++) { //yahan dekho row equal nai hai
					if (training_matrix[i][columns - 1] == 0.0) {
						countZeroes_before++;
					} else if (training_matrix[i][columns - 1] == 1.0) {
						countOnes_before++;
					} else {
						System.out.println("Problem in Class label before split");
					}
				}
				count_matrix[0][0] = countZeroes_before;
				count_matrix[0][1] = countOnes_before;

				//count class labels after the assumed split
				int countZeroes_after = 0;
				int countOnes_after = 0;
				for (int j= row + 1; j < end; j++) { //yahan ka start bhi dekhlo
					if (training_matrix[j][columns - 1] == 0.0) {
						countZeroes_after++;
					} else if (training_matrix[j][columns - 1] == 1.0) {
						countOnes_after++;
					} else {
						System.out.println("Problem in Class label after split" + training_matrix[j][columns - 1]);
					}
				}
				count_matrix[1][0] = countZeroes_after;
				count_matrix[1][1] = countOnes_after;

				//Now calculate Gini using the formulae
				double total_before = Double.valueOf(count_matrix[0][0] + count_matrix[0][1]);
				double total_after = Double.valueOf(count_matrix[1][0] + count_matrix[1][1]);
				double GINI_BeforeSplit = 1 - Math.pow((count_matrix[0][0] / total_before), 2) - Math.pow((count_matrix[0][1] / total_before), 2);
				double GINI_AfterSplit = 1 - Math.pow((count_matrix[1][0] / total_after), 2) - Math.pow((count_matrix[1][1] / total_after), 2);
				double gini_beforePart = (total_before / (total_after + total_before)) * GINI_BeforeSplit;
				double gini_afterPart = (total_after / (total_after + total_before)) * GINI_AfterSplit;
				double GINI = gini_afterPart + gini_beforePart;

				//if this is a leaf, what is the right and left
				double column_splitValue = (training_matrix[row][column] + training_matrix[row + 1][column]) / 2;
				Node splitNode;
				int left = 0;
				int right = 0;
				if (GINI == 0) {
					if (count_matrix[0][0] == 0) {
						left = 1;
						right = 0;
					} else {
						left = 0;
						right = 1;
					}
					splitNode = new Node(GINI, row, column, begin, end, true, 0, right, left); //leaf of decision tree
				} else {
					//check if the zeroes are more than ones
					if (count_matrix[0][0] > count_matrix[0][1]) {
						left = 0;
					}
					if (count_matrix[0][1] > count_matrix[0][0]) {
						left = 1;
					}
					if (count_matrix[1][0] > count_matrix[1][1]) {
						right = 0;
					}
					if (count_matrix[1][1] > count_matrix[1][0]) {
						right = 1;
					}


					splitNode = new Node(GINI, row, column, begin, end, false, column_splitValue, right, left); //not a leaf
				}

				min_heap.add(splitNode);
			}
		}
		if(!(min_heap.size() == 0)){
			column_gini.add(min_heap.poll());
		}
	}


}
