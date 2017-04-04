import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class KNN {

	public static String INPUT = "/Users/Admin/Desktop/DataMining/Project3/project3_dataset2.txt";
	static int k = 9;
	public static List<points> traininglist = new ArrayList<>();
	public static List<points> testinglist = new ArrayList<>();

	public static List<points> readfile(String IN) {

		try {
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(IN));
			while ((line = reader.readLine()) != null) {
				ArrayList<Double> expressionValues = new ArrayList<Double>();
				String[] elements = line.split("\\s+");
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

	public static void main(String[] args) {
		int TP = 0;
		int TN = 0;
		int FP = 0;
		int FN = 0;
		ArrayList<Double> accuracies = new ArrayList<>();
		ArrayList<Double> recall_values = new ArrayList<>();
		ArrayList<Double> precision_values = new ArrayList<>();
		ArrayList<Double> measure_values = new ArrayList<>();
		testinglist = readfile(INPUT);
		ArrayList<List<points>> splitted = new ArrayList<List<points>>();
		splitted = splitdata(testinglist);
		Random random = new Random();
		// int count1 = 0;
		// int count2 = 0;
		int majclass = 2;
		for (int i = 0; i < splitted.size(); i++) {
			// int rand = i+ (int)(Math.random()*((splitted.size() -1 - i) +1))
			// ;
			int rand = random.nextInt((splitted.size() - 1 - i) + 1) + i;
			List<points> test = splitted.get(rand);

			for (points p : test) {
				ArrayList<Double> value = p.attributes;
				int key = p.classname;
				// count1++;
				if (!test.equals(splitted.get(i))) {
					List<result> resultlist = new ArrayList<>();
					for (points q : splitted.get(i + 1)) {
						// count2++;
						if (!value.equals(q.attributes)) {
							double dist = CalculateDistance(value, q.attributes);
							resultlist.add(new result(dist, p.classname));
						}
					}
					Collections.sort(resultlist, new distcomparator());
					int ss[] = new int[k];
					for (int x = 0; x < k; x++) {
						ss[x] = resultlist.get(x).classname;
					}
					majclass = findmajorityclass(ss);
				}
				System.out.println(key + " --> " + majclass);
				if (key == 0) {
					if(majclass == 0)
						TN++;
					else
						FP++;
				} else  {
					if (majclass == 1)
						TP++;
					else
						FN++;
				}
			}
			double accuracy = (double) (TP + TN) / (TP + TN + FP + FN);
			accuracies.add(accuracy);
			double recall = (double) TP / (TP + FN);
			recall_values.add(recall);
			double precision = (double) TP / (TP + FP);
			precision_values.add(precision);
			double fmeasure = (double) (2 * (precision * recall)) / (precision + recall);
			measure_values.add(fmeasure);
		}
		System.out.println("---KNN---");
		System.out.println("Accuracy: " + calculatestatistics(accuracies));
		System.out.println("Precision: " + calculatestatistics(precision_values));
		System.out.println("Recall: " + calculatestatistics(recall_values));
		System.out.println("F-Measure: " + calculatestatistics(measure_values));
	}

	private static double calculatestatistics(ArrayList<Double> list) {
		double sum = 0;
		if (!list.isEmpty()) {
			for (Double value : list) {
				sum += value;
			}
			return (double)sum / list.size();
		}
		return sum;
	}

	public static ArrayList<List<points>> splitdata(List<points> inputdata)

	{
		ArrayList<List<points>> splits = new ArrayList<List<points>>();
		int size = inputdata.size();
		System.out.println(size);
		int folds = 10;
		int sizeOfEach = size / folds + 1;
		int counter = 0;
		System.out.println(sizeOfEach);
		List<points> temp = new ArrayList<>();
		for (points p : inputdata) {

			temp.add(p);
			counter += 1;
			if (counter == sizeOfEach) {
				counter = 0;
				splits.add(new ArrayList<points>(temp));
				temp.clear();

			}
		}
		splits.add(temp);
		return splits;
	}

	private static int findmajorityclass(int[] nums) {

//		int majorityelement = 0;
//		int count = 1;
//		for (int i = 1; i < nums.length; i++) {
//			if (nums[majorityelement] == nums[i]) {
//				count++;
//			} else
//				count--;
//			if (count == 0) {
//				majorityelement = i;
//				count = 1;
//			}
//		}
//		return nums[majorityelement];

		 Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	        for (int i = 0; i < nums.length; i++) {
	            int curr = nums[i];
	            Integer count = 1;
	            if (map.containsKey(curr)) {
	                count = map.get(curr);
	                count++;
	            }
	            map.put(curr, count);
	        }
	        int[] rep = new int[2];
	        for (Integer key:map.keySet()) {
	            Integer count = map.get(key);
	            if (count > rep[0]) {
	                rep[0] = key;
	                Arrays.sort(rep);
	            }
	        }
	        Random rand = new Random();
	            return rep[rand.nextInt(rep.length)];
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
