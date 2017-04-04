import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class Sample {

	public static String IN = "/Users/Admin/Desktop/DataMining/Project3/project3_dataset3_train.txt";
	public static String INPUT = "/Users/Admin/Desktop/DataMining/Project3/project3_dataset3_test.txt";
	static int k = 10;
	public static List<points> traininglist = new ArrayList<>();
	public static List<points> testinglist = new ArrayList<>();

	public static List<points> readfile(String IN) {

		try {
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(IN));
			while ((line = reader.readLine()) != null) {
				ArrayList<Double> expressionValues = new ArrayList<Double>();
				String[] elements = line.split("\\s+"); // Extracting numbers in
														// each line. Nodes per
														// edge in our case.
				for (int i = 0; i < elements.length - 1; i++) {
					if (elements[i].equalsIgnoreCase("Present")) {
						expressionValues.add(1.0);
					} else if (elements[i].equalsIgnoreCase("Absent")) {
						expressionValues.add(0.0);
					} else {
						expressionValues.add(Double.parseDouble(elements[i]));
					}
				}
				traininglist.add(new points(expressionValues, Integer.valueOf(elements[elements.length - 1])));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return traininglist;
	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		
		int TP = 0;
		int TN =0;
		int FP =0;
		int FN = 0;
		HashMap<Integer, Integer> stats = new HashMap<>();
		//ArrayList<Double> expressionValues = new ArrayList<Double>();
		List<points> trainingdata = readfile(IN);
		try {
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(INPUT));
			while ((line = reader.readLine()) != null) {
				ArrayList<Double> expressionValues = new ArrayList<Double>();
				String[] elements = line.split("\\s+"); // Extracting numbers in
														// each line. Nodes per
														// edge in our case.
				for (int i = 0; i < elements.length - 1; i++) {
					if (elements[i].equalsIgnoreCase("Present")) {
						expressionValues.add(1.0);
					} else if (elements[i].equalsIgnoreCase("Absent")) {
						expressionValues.add(0.0);
					} else {
						expressionValues.add(Double.parseDouble(elements[i]));
					}
				}
				testinglist.add(new points(expressionValues, Integer.valueOf(elements[elements.length - 1])));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int count1 =0;
		int count2 =0;
		for( points p : testinglist)
		{
			int key = p.classname;
			System.out.println();
			count1 ++;
			ArrayList<Double> value = p.attributes;
			List<result> resultlist = new ArrayList<>();
			for(points q : trainingdata)
			{
				count2++;
				if(!value.equals(q.attributes))
				{
				double dist = CalculateDistance(value, q.attributes);
				resultlist.add(new result(dist, p.classname));				
				}				
			}
			Collections.sort(resultlist, new distcomparator());
			int ss[] = new int[k];
			for (int x = 0; x < k; x++) {
				ss[x] = resultlist.get(x).classname;
			}
			int majclass = findmajorityclass(ss);
			System.out.println(key + " --> " +majclass);
			if(key  == 0)
			{
				if(key ==  majclass)
					TN++;
				else
					FP++;
			}
			else if(key ==1)
			{
				if(key ==  majclass)
					TP++;
				else
					FN++;
			}
		}
		float accuracy = (float)(TP + TN) / (TP + TN + FP +FN);
		float recall = (float)TP/(TP + FN);
		float precision = (float)TP/ (TP +FP);
		float fmeasure = (float)(2*precision*recall) / (precision + recall);
		System.out.println("---KNN---");
		System.out.println("Accuracy: "+accuracy);
		System.out.println("Precision: "+precision);
		System.out.println("Recall: "+recall);
		System.out.println("F-Measure: "+ fmeasure);
		
}

	

	private static int findmajorityclass(int[] nums) {

		int majorityelement = 0;
		int count = 1;
		for (int i = 1; i < nums.length; i++) {
			if (nums[majorityelement] == nums[i]) {
				count++;
			} else
				count--;
			if (count == 0) {
				majorityelement = i;
				count = 1;
			}
		}
		return nums[majorityelement];
	        
	}

	static double CalculateDistance(ArrayList<Double> a, ArrayList<Double> b) {
		if ((a.size() != b.size())) {
			System.out.println("Something wrong with data");
		} else {
			double sumOfSquare = 0;
			for (int i = 0; i < a.size(); i++) {
				double diffSquared = Math.pow(a.get(i) - b.get(i), 2);
				sumOfSquare += diffSquared;
			}
			return Math.sqrt(sumOfSquare);
		}
		return 0;
	}

	static class points {
		ArrayList<Double> attributes = new ArrayList<>();
		int classname;

		public points(ArrayList<Double> attributes, int classname) {
			this.classname = classname;
			this.attributes = attributes;
		}

		@Override
		public String toString() {
			return "Classname: " + this.classname + " Attributes " + this.attributes;
		}

	}

	static class result {
		double distance;
		int classname;

		public result(double distance, int classname) {
			this.classname = classname;
			this.distance = distance;
		}

		@Override
		public String toString() {
			return "Classname: " + this.classname + " distance " + this.distance;
		}
	}

	static class distcomparator implements Comparator<result> {
		@Override
		public int compare(result a, result b) {
			return a.distance < b.distance ? -1 : a.distance == b.distance ? 0 : 1;
		}
	}
}
