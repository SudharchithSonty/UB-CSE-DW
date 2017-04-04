import java.math.*;
import java.util.*;
import java.io.*;

public class DecisionTreeFinal {

	static TreeNode td = new TreeNode();
	static Record rd = new Record();
	static double splitvalue = td.splitValue;
	public static List<points> inputdata = new ArrayList<>();
	public static String INPUT = "/Users/Admin/Desktop/DataMining/Project3/project3_dataset1.txt";
	public static String IN = "/Users/Admin/Desktop/DataMining/Project3/project3_dataset4.txt";
	public static int pruning_size = 5;
	public static void main(String[] args) throws CloneNotSupportedException {
		List<points> input = readfile(INPUT);
		List<int[]> st = new ArrayList<>();
		HashMap<Integer, int[]> finalstats = new HashMap<>();
		int[] stats = new int[4];
		ArrayList<List<points>> splitted = new ArrayList<>(split_data(input));
		Random random = new Random();
		for (int i = 0; i < splitted.size(); i++) {
			List<points> testing = splitted.get(i);
			List<points> training = new ArrayList<>();
			for (List<points> l : splitted)
				if (!l.equals(testing)) {
					for (points p : l) {
						training.add(p);
					}
				}
			ArrayList<Features> finalattributes = get_attributes(input);
			TreeNode root = build_Decision_tree(training, finalattributes, pruning_size);
			stats = classify_testing_data(testing, finalattributes, (TreeNode) root.clone());
			finalstats.put(i, stats);
		}
		int a = 0, b = 0, c = 0, d = 0;
		int[] num = new int[4];
		for (int i : finalstats.keySet()) {
			a += finalstats.get(i)[0];
			b += finalstats.get(i)[1];
			c += finalstats.get(i)[2];
			d += finalstats.get(i)[3];
		}
		num[0] = a;
		num[1] = b;
		num[2] = c;
		num[3] = d;

		getstats(num);
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

	public static void getstats(int[] x) {

		int a = x[0];
		int b = x[1];
		int c = x[2];
		int d = x[3];
		double accuracy = (double) (a + d) / (a + b + c + d);
		double recall = (double) a / (a + b);
		double precision = (double) a / (a + c);
		double fmeasure = (double) (2 * precision * recall) / (precision + recall);
		System.out.println("---DECISION TREE---");
		System.out.println("Accuracy: " + accuracy);
		System.out.println("Precision: " + precision);
		System.out.println("Recall: " + recall);
		System.out.println("F-Measure: " + fmeasure);

	}

	private static TreeNode build_Decision_tree(List<points> training_data, ArrayList<Features> finalattributes,
			int pruning_size) {

		ArrayList<Double> uniquevalues = new ArrayList<>();
		for (points p : training_data) {
			if (!uniquevalues.contains(p.classname))
				uniquevalues.add((double) p.classname);
		}
		TreeNode node = new TreeNode();

		if (uniquevalues.size() == 1) {
			node.label = uniquevalues.get(0);
		} else if (training_data.size() < 5) {
			node.label = get_majority_element(training_data);
		}

		else {
			int index = get_split_index(training_data, finalattributes);
			String choice = get_split_choice(training_data, finalattributes);
			if (index < 0) {
				node.label = get_majority_element(training_data);
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
				if (data_left.size() == 0) {
					node.label = get_majority_element(data_right);
				} else if (data_right.size() == 0) {
					node.label = get_majority_element(data_left);
				} else {
					node.leftNode = build_Decision_tree(data_left, finalattributes, 5);
					node.righNode = build_Decision_tree(data_right, finalattributes, 5);
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

	public static ArrayList<List<points>> split_data(List<points> input) {
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
		return splits;
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

}
