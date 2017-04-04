import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class test {
	public static String IN = "/Users/Admin/Desktop/DataMining/Project3/project3_dataset4.txt";
	public static List<points> traininglist = new ArrayList<>();
	static TreeNode td = new TreeNode();
	static Record rd = new Record();
	static double splitvalue = td.splitValue;
	public static List<points> inputdata = new ArrayList<>();
	static double final_accuracy = 1;
	static double final_precision = 1;
	static double final_recall = 1;
	static double final_measure =1;

	public static void main(String[] args) throws IOException, CloneNotSupportedException {
		Map<String, Integer> input1 = new HashMap<>();
		input1 = getdata(IN);
		System.out.println(input1);
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(IN));
		while ((line = reader.readLine()) != null) {
			ArrayList<Double> expressionValues = new ArrayList<Double>();
			String[] elements = line.split("\\s+");
			for (int i = 0; i < elements.length - 1; i++) {
				expressionValues.add((double) input1.get(elements[i]));
			}
			traininglist.add(new points(expressionValues, Integer.valueOf(elements[elements.length - 1])));
		}
		List<points> training_data = new ArrayList<>();
		List<points> testing_data = new ArrayList<>();
		ArrayList<Features> finalattributes = get_attributes(traininglist);
		training_data = gettrainingdata(traininglist, splitvalue);
		testing_data = gettestingdata(traininglist, splitvalue);
		TreeNode root = build_Decision_tree(training_data, finalattributes);
		System.out.println(root);
		int[] stats = classify_testing_data(testing_data, finalattributes, (TreeNode) root.clone());
		get_statistics(stats[0], stats[1], stats[2], stats[3]);
	}

	public static Map<String, Integer> getdata(String IN) throws IOException {

		String line;
		BufferedReader reader = new BufferedReader(new FileReader(IN));
		List<String> values = new ArrayList<>();
		Map<String, Integer> map = new TreeMap<>();
		while ((line = reader.readLine()) != null) {
			ArrayList<Double> expressionValues = new ArrayList<Double>();
			String[] elements = line.split("\\s+");
			for (int i = 0; i < elements.length - 1; i++) {
				if (!values.contains(elements[i]))
					values.add(elements[i]);
			}
			for (int i = 0; i < values.size(); i++)
				map.put(values.get(i), i);

		}
		return map;
	}

	private static int[] classify_testing_data(List<points> testing_data, ArrayList<Features> finalattributes,
			TreeNode root) throws CloneNotSupportedException {
		int TP = 0;
		int TN = 0;
		int FP = 0;
		int FN = 0;
		int[] stats = new int[4];
		for (points p : testing_data) {
			TreeNode node = (TreeNode) root.clone();
			while (node.label < 0.0) {
				int index = node.split_point;
				if (p.attributes.get(index) < finalattributes.get(index).mean)
					node = node.leftNode;
				else
					node = node.righNode;
				if (p.classname == node.label) {
					if (node.label == 1.0)
						TP++;
					else
						TN++;
				} else {
					if (node.label == 1.0)
						FP++;
					else
						FN++;

				}
				stats[0] = TP;
				stats[1] = FN;
				stats[2] = FP;
				stats[3] = TN;
			}

		}
		return stats;
	}

	private static TreeNode build_Decision_tree(List<points> training_data, ArrayList<Features> finalattributes) {

		ArrayList<Double> uniquevalues = new ArrayList<>();
		for (points p : training_data) {
			if (!uniquevalues.contains(p.classname))
				uniquevalues.add((double) p.classname);
		}
		System.out.println(uniquevalues);
		TreeNode node = new TreeNode();

		if (uniquevalues.size() == 1) {
			node.label = uniquevalues.get(0);
		} else if (training_data.size() < 5) {
			node.label = get_majority_element(training_data);
			System.out.println(node.label);
		}

		else {
			int index = get_split_index(training_data, finalattributes);
			System.out.println(index);
			String choice = get_split_choice(training_data, finalattributes);
			if (index < 0) {
				node.label = get_majority_element(training_data);
				System.out.println(node.label);
			} else {
				node.split_point = index;
				node.Choice = choice;
				List<points> data_left = new ArrayList<points>();
				List<points> data_right = new ArrayList<points>();
				for (points p : training_data) {
					if (p.attributes.get(node.split_point) < finalattributes.get(node.split_point).mean) {
						data_left.add(p);
					}
					if (p.attributes.get(node.split_point) >= finalattributes.get(node.split_point).mean) {
						data_right.add(p);
					}
				}
				// }
				// else{
				// for(points p: training_data){
				// if(p.attributes.get(node.split_point).equals(choice))
				// data_left.add(p);
				// }
				// for(points p: training_data){
				// if(p.attributes.get(node.split_point).equals(choice))
				// data_left.add(p);
				// }

				if (data_left.size() == 0) {
					node.label = get_majority_element(data_right);
				} else if (data_right.size() == 0) {
					node.label = get_majority_element(data_left);
				} else {
					node.leftNode = build_Decision_tree(data_left, finalattributes);
					node.righNode = build_Decision_tree(data_right, finalattributes);
				}
			}
		}
		return node;
	}

	private static String get_split_choice(List<points> training_data, ArrayList<Features> finalattributes) {
		Double minimum = 1.0;
		ArrayList<Integer> inputs_left = new ArrayList<Integer>();
		ArrayList<Integer> inputs_right = new ArrayList<Integer>();
		return null;
	}

	private static double get_majority_element(List<points> data) {

		ArrayList<Integer> a1 = new ArrayList<>();

		for (points p : data)
			a1.add(p.classname);
		int[] a = new int[a1.size()];
		for (int i = 0; i < a1.size(); i++)
			a[i] = a1.get(i);
		List<Integer> modes = new ArrayList<Integer>();
		int maxCount = 0;
		for (int i = 0; i < a.length; ++i) {
			int count = 0;
			for (int j = 0; j < a.length; ++j) {
				if (a[j] == a[i])
					++count;
			}
			if (count > maxCount) {
				maxCount = count;
				modes.clear();
				modes.add(a[i]);
			} else if (count == maxCount) {
				modes.add(a[i]);
			}
		}
		Random randomizer = new Random();
		int random = modes.get(randomizer.nextInt(modes.size()));
		return (double) random;
	}

	public static int get_split_index(List<points> inputdata, ArrayList<Features> finalattributes) {
		int index = -1;
		Double minimum = 1.0;
		ArrayList<Integer> inputs_left = new ArrayList<Integer>();
		ArrayList<Integer> inputs_right = new ArrayList<Integer>();
		for (int i = 0; i < finalattributes.size(); i++) {

			for (points p : inputdata) {
				if (p.attributes.get(i) < finalattributes.get(i).mean)
					inputs_left.add(p.classname);
			}
			for (points p : inputdata) {
				if (p.attributes.get(i) >= finalattributes.get(i).mean)
					inputs_right.add(p.classname);
			}
			if (inputs_left.size() == 0 || inputs_right.size() == 0) {
				index = i;
				break;
			}
			Double split_measure = get_classification_error_for_split(inputs_left, inputs_right);
			if (split_measure < minimum) {
				minimum = split_measure;
				index = i;
			}
		}
		System.out.println(index);
		return index;

	}

	private static Double get_classification_error_for_split(ArrayList<Integer> inputs_left,
			ArrayList<Integer> inputs_right) {
		return Math.min(get_classification_error(inputs_left), get_classification_error(inputs_right));
	}

	private static double get_classification_error(ArrayList<Integer> inputs) {
		int positives = Collections.frequency(inputs, 1);
		int negatives = inputs.size() - positives;
		double error = 1 - Math.max(positives, negatives);
		return error;
	}

	public static ArrayList<Features> get_attributes(List<points> input) {

		ArrayList<Features> values = new ArrayList<Features>();
		for (int j = 0; j < input.get(0).attributes.size(); j++) {
			ArrayList<Double> temp = new ArrayList<>();
			for (int i = 0; i < input.size(); i++) {
				temp.add(input.get(i).attributes.get(j));
			}
			values.add(new Features(temp));
		}
		return values;
	}

	public static List<points> gettrainingdata(List<points> input1, double splitvalue) {
		List<points> training_data = new ArrayList<>();
		int start = 0;
		int end = (int) (splitvalue * input1.size());
		training_data = input1.subList(start, end);
		return training_data;
	}

	public static List<points> gettestingdata(List<points> input1, double splitvalue) {
		List<points> testing_data = new ArrayList<>();
		int start = input1.size() - (int) (splitvalue * input1.size());
		testing_data = input1.subList(start, input1.size());
		return testing_data;
	}

	public static void get_statistics(int a, int b, int c, int d) {
		double accuracy = (double) (a + d) / (a + b + c + d);
		double recall = (double) a / (a + b);
		double precision = (double) a / (a + c);
		double fmeasure = (double) (2 * precision * recall) / (precision + recall);
		System.out.println("---DECISION TREE---");
		System.out.println("Accuracy: " + final_accuracy);
		System.out.println("Precision: " + final_precision);
		System.out.println("Recall: " + final_recall);
		System.out.println("F-Measure: " + final_measure);
	}

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
				inputdata.add(new points(expressionValues, Integer.valueOf(elements[elements.length - 1])));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputdata;
	}

	public ArrayList<List<points>> split_data(List<points> input) {
		ArrayList<List<points>> splits = new ArrayList<List<points>>();
		int size = inputdata.size();
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
		return null;
	}

	static class points {
		ArrayList<Double> attributes = new ArrayList<>();
		int classname;

		public points(ArrayList<Double> expressionValues, int classname) {
			this.classname = classname;
			this.attributes = expressionValues;
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
