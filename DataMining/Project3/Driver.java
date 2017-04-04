import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Amair on 07-12-2015.
 */
public class Driver {
	static ArrayList<ArrayList<Double>> entireData = new ArrayList<ArrayList<Double>>();

	public static void main(String[] args){

		readFile("/Users/Admin/Desktop/DataMining/Project3/project3_dataset3_train.txt");
		//DecisionTree decisionTree = new DecisionTree();
		//  decisionTree.processData(entireData,false,0,0, new ArrayList<Integer>());
		TenFoldCrossValidationDecisionTree(10);
		TenFoldCrossValidationRandomForest(10);
		System.out.println();	
	}



	public static void TenFoldCrossValidationDecisionTree(int folds){
		ArrayList<ArrayList<Double>> test;
		ArrayList<ArrayList<Double>> train;
		ArrayList<Double> accuracies = new ArrayList<Double>();
		double final_precision=0;
		double final_recall=0;
		double final_accuracy=0;
		double final_fmeasure=0;

		for(int iter=0; iter<folds; iter++){
			train = new ArrayList<ArrayList<Double>>();
			test=new ArrayList<ArrayList<Double>>();
			int startTest = (int) Math.round(iter*0.1*entireData.size());
			int endTest = startTest + (int) Math.round(0.1*entireData.size());
			for (int row = 0; row < entireData.size(); row++) {
				if(row>startTest && row<endTest) {
					test.add(entireData.get(row));
				}
				else{
					train.add(entireData.get(row));
				}
			}
			DecisionTree dT = new DecisionTree();
			HashMap<Character, Double> stats = dT.processCrossValidatedData(train, test, false, 0, 0, new ArrayList<Integer>());
			// System.out.println("Iteration: "+iter+" Accuracy: "+accuracy);
			double accuracy = stats.get('a');
			double fmeasure = stats.get('f');
			double precision = stats.get('p');
			double recall = stats.get('r');
			//System.out.println("Itr:"+iter+" Accruacy:"+accuracy);
			// final_accuracy = accuracy;
			/*final_fmeasure = stats.get('f');
            final_precision = stats.get('p');
            final_recall = stats.get('r');*/

			if(accuracy > final_accuracy){
				final_accuracy = accuracy;
				/* final_fmeasure = stats.get('f');
                final_precision = stats.get('p');
                final_recall = stats.get('r');*/
			}
			if(recall > final_recall){
				final_recall = stats.get('r');
			}
			if(precision > final_precision){
				final_precision = stats.get('p');
			}
			if(fmeasure > final_fmeasure){
				final_fmeasure = stats.get('f');
			}
			accuracies.add(final_accuracy);
		}

		System.out.println("---10 FOLD CROSS VALIDATION DECISION TREE---");
		System.out.println("accuracy: "+final_accuracy);
		System.out.println("Precision: "+final_precision);
		System.out.println("Recall: "+final_recall);
		System.out.println("F-Measure: "+final_fmeasure);
	}


	public static void TenFoldCrossValidationRandomForest(int folds){
		double final_precision=0;
		double final_recall=0;
		double final_accuracy=0;
		double final_fmeasure=0;
		ArrayList<ArrayList<Double>> test;
		ArrayList<ArrayList<Double>> train;
		ArrayList<Double> accuracies = new ArrayList<Double>();

		for(int iter=0; iter<folds; iter++){
			train = new ArrayList<ArrayList<Double>>();
			test=new ArrayList<ArrayList<Double>>();
			int startTest = (int) Math.round(iter*0.1*entireData.size());
			int endTest = startTest + (int) Math.round(0.1*entireData.size());
			for (int row = 0; row < entireData.size(); row++) {
				if(row>startTest && row<endTest) {
					test.add(entireData.get(row));
				}
				else{
					train.add(entireData.get(row));
				}
			}
			Forest forest = new Forest();
			HashMap<Character, Double> stats = forest.process(train);
			double accuracy = stats.get('a');

			if(accuracy > final_accuracy){
				final_accuracy = accuracy;
				final_fmeasure = stats.get('f');
				final_precision = stats.get('p');
				final_recall = stats.get('r');
			}
			accuracies.add(final_accuracy);
		}
		System.out.println("---10 FOLD CROSS VALIDATION RANDOM FOREST---");
		System.out.println("Accuracy: "+final_accuracy);
		System.out.println("Precision: "+final_precision);
		System.out.println("Recall: "+final_recall);
		System.out.println("F-Measure: "+final_fmeasure);
	}


	public static void readFile(String filePath){
		try {
			System.out.println("Reading file: " + filePath);
			String line="";
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			while((line = br.readLine())!=null){
				ArrayList<Double> row = new ArrayList<Double>();
				String[] elements = line.split("\\s+"); // Extracting numbers in each line. Nodes per edge in our case.
				for(int i=0;i<elements.length;i++){
					if(elements[i].equalsIgnoreCase("Present")) {
						row.add(1.0);
					}
					else if(elements[i].equalsIgnoreCase("Absent")) {
						row.add(0.0);
					}
					else {
						row.add(Double.parseDouble(elements[i]));
					}

				}
				entireData.add(row);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
