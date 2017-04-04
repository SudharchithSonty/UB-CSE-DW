/*
 *    NaiveBayesClassifier.java
 *    @Author: Abhijit
 *
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NaiveBayesClassifier {

	/** Input file path. */
	private static String STR_INPUT_FILE = "C://Users//Abhijit//Desktop//dataset3//project3_dataset1.txt";

	/** String delimiter for the input files. */
	private static String STR_DELIMITER = "\t";

	/** Ratio of test data*/
	private static double dbTest = 0.10;

	/** Means for numeric attributes. */
	private static double [][] m_Means;

	/** Standard deviations for numeric attributes. */
	private static double [][] m_Devs;

	/** Columns with nominal values. */
	private static List<Integer> nominalIndices = new ArrayList<>();
	private static Map<String, Double> nominalMap = new HashMap<>();
	private static Map<String, NominalMap> nominals = new HashMap<>();

	/** Instances used for training. */
	private static double [][] m_TrainData;

	/** Instances used for testing. */
	private static double [][] m_TestData;

	/** Total number of ones and zeroes. */
	private static int numZeroes = 0;
	private static int numOnes = 0;
	
	/** Measures */
	private static List<Double> accuracy = new ArrayList<>();
	private static List<Double> recall = new ArrayList<>();
	private static List<Double> precision = new ArrayList<>();
	private static List<Double> fmeasure = new ArrayList<>();

	/**
	 * Generates the classifier.
	 *
	 * @param empty
	 * @return void
	 * @exception Exception if the classifier has not been generated successfully
	 */
	public static void train() throws Exception {

		try{
			List<Double> zeroes = null;
			List<Double> ones = null;
			double sumZeroes, sumOnes;

			m_Means = new double[m_TrainData[0].length-1][2];
			m_Devs = new double[m_TrainData[0].length-1][2];

			for(int i=0; i<m_TrainData[0].length-1 ; i++){
				zeroes = new ArrayList<>();
				ones = new ArrayList<>();
				sumOnes = 0.0;
				sumZeroes = 0.0;
				for(int j=0; j<m_TrainData.length; j++){
					if(m_TrainData[j][m_TrainData[0].length-1]==1.0){		
						ones.add(m_TrainData[j][i]);
					} else {
						zeroes.add(m_TrainData[j][i]);
					}
				}
				if(!nominalIndices.contains(i)){
					for(double db : zeroes){
						sumZeroes += db;
					}
					for(double db : ones){
						sumOnes += db;
					}
					m_Means[i][0] = mean(sumZeroes, zeroes.size());
					m_Means[i][1] = mean(sumOnes, ones.size());

					double sumSqZeroes = 0.0;
					double sumSqOnes = 0.0;
					for(double dbZeroes: zeroes) {
						sumSqZeroes += Math.pow(dbZeroes - m_Means[i][0], 2);
					}
					for(double dbOne: ones) {
						sumSqOnes += Math.pow(dbOne - m_Means[i][1], 2);
					}

					m_Devs[i][0] = standardDeviation(sumSqZeroes, zeroes.size());
					m_Devs[i][1] = standardDeviation(sumSqOnes, ones.size());
				} else {
					double temp1 = ones.stream().filter(e -> e ==0.0).count();
					if(temp1 == 0.0) temp1 = 1.0;
					temp1 /= numOnes;
					double temp2 = zeroes.stream().filter(e -> e ==0.0).count();
					if(temp2 == 0.0) temp2 = 1.0;
					temp2 /= numZeroes;
					nominals.put(getKeyFromValue(0.0), new NominalMap(temp1, temp2));

					temp1 = ones.stream().filter(e -> e ==1.0).count();
					if(temp1 == 0.0) temp1 = 1.0;
					temp1 /= numOnes;
					temp2 = zeroes.stream().filter(e -> e ==1.0).count();
					if(temp2 == 0.0) temp2 = 1.0;
					temp2 /= numZeroes;
					nominals.put(getKeyFromValue(1.0), new NominalMap(temp1, temp2));
				}
			}
		}catch( Exception e ){
			e.printStackTrace();
		}

	}

	/**
	 * Tests the classifier.
	 *
	 * @param empty
	 * @return void
	 * @exception Exception if the classifier could not test.
	 */
	public static void test(int iter) throws Exception {

		int numOnesTest = 0;

		try{
			double[] prior = new double[m_TestData.length];
			double[] posterior = new double[m_TestData.length];
			for(int i=0; i<m_TestData.length;i++){
				double zeroesPdf = 1.0;
				double onesPdf = 1.0;
				if(m_TestData[i][m_TestData[i].length-1]==1.0) numOnesTest++;

				for(int j=0; j<m_TestData[0].length-1; j++) {
					if(!nominalIndices.contains(j)){
						zeroesPdf *= gaussianPdf(m_Means[j][0], m_Devs[j][0], m_TestData[i][j]);
						onesPdf *= gaussianPdf(m_Means[j][1], m_Devs[j][1], m_TestData[i][j]);
					} else {
						NominalMap mapp = nominals.get(getKeyFromValue(m_TestData[i][j]));
						zeroesPdf *= mapp.zero;
						onesPdf *= mapp.one;
					}
				}

				prior[i] = m_TestData[i][m_TestData[0].length-1];
				if(onesPdf > zeroesPdf)
					posterior[i] = 1.0;
				else
					posterior[i] = 0.0;
			}

			//Calculating accuracy, precision and recall.
			int matches = 0;
			int onesFound = 0;
			int trueOnes = 0;
			for(int i=0; i<prior.length; i++){
				if(prior[i]==posterior[i]){
					matches++;
					if(posterior[i] == 1.0) trueOnes++; 
				}
				if(posterior[i] == 1.0) onesFound++;
			}
			double accurcy =(matches*100)/prior.length;
			double precsn = (trueOnes*100)/onesFound;
			double recl = (trueOnes*100)/numOnesTest;
			double fmeas = (2*precsn*recl) / (precsn + recl);
			accuracy.add(accurcy); precision.add(precsn); recall.add(recl); fmeasure.add(fmeas);
			/*System.out.println("For iteration " + (iter+1) + " the results are:");
			System.out.println("The accuracy is = " + accurcy + "%");
			System.out.println("The precision is = " + precsn + "%");
			System.out.println("The recall is = " + recl + "%");
			System.out.println("The F-measure is = " + fmeas);
			System.out.println("===========================================================");*/

		}catch(Exception e){
			e.printStackTrace();
		}
	}


	/**
	 * Main method for testing.
	 *
	 * @param argv the options
	 * @return void
	 */
	public static void main(String [] argv) {
		String line;
		BufferedReader reader = null;
		List<String> input = new ArrayList<>();
		String[] tempSplit = null;
		double nominalIndex = 0.0;
		int numLines = 0;
		int numAttributes = 0;
		int trainDataSize = 0;
		int testDataSize =0;

		try 
		{
			//Evaluate the number of lines in the files and the number of attributes.
			reader = new BufferedReader(new FileReader(STR_INPUT_FILE));
			boolean check = false;
			while((line=reader.readLine())!=null)
			{	
				input.add(line);
				if(!check){
					tempSplit = line.split(STR_DELIMITER);
					numAttributes = tempSplit.length;
					for(int i = 0; i< numAttributes; i++){
						if(isNominal(tempSplit[i])) nominalIndices.add(i);
					}
					check = true;
				}
				numLines++;
			}
			reader.close();
			testDataSize = (int) (numLines*dbTest);
			trainDataSize = numLines - testDataSize;

			//Initializing the training data and test data sets.
			m_TrainData = new double[trainDataSize][numAttributes];
			m_TestData = new double[testDataSize][numAttributes];


			for(int iter=0; iter<10; iter++){

				//Preparing the training and test data.
				for(int i=0,k=0,l=0; i<input.size(); i++){
					tempSplit = input.get(i).split(STR_DELIMITER);

					if(i>=iter*testDataSize && i<((iter+1)*testDataSize)){
						for(int j=0; j<numAttributes;j++){
							if(nominalIndices.contains(j)){
								if(!nominalMap.containsKey(tempSplit[j])){
									nominalMap.put(tempSplit[j], nominalIndex++);
								}
								m_TestData[k][j]= nominalMap.get(tempSplit[j]);
							} else {
								m_TestData[k][j] = Double.valueOf(tempSplit[j]);
							}
						}
						k++;
					}else {

						if(tempSplit[numAttributes - 1].trim().equals("1")){
							numOnes++;
						} else {
							numZeroes++;
						}

						for(int j=0; j<numAttributes;j++){
							if(nominalIndices.contains(j)){
								if(!nominalMap.containsKey(tempSplit[j])){
									nominalMap.put(tempSplit[j], nominalIndex++);
								}
								m_TrainData[l][j]= nominalMap.get(tempSplit[j]);
							} else {
								if(l==513){
									System.out.println();
								}
								m_TrainData[l][j] = Double.valueOf(tempSplit[j]);
							}
						}
						l++;
					}
				}
				//Building the classifier.
				train(); 
				
				//Testing the classifier.		
				test(iter);
			}
			System.out.println("10-fold accuracy is: ");
			System.out.println("The accuracy is = " + accuracy.stream().mapToDouble(e->e).average().getAsDouble() + "%");
			System.out.println("The precision is = " + precision.stream().mapToDouble(e->e).average().getAsDouble() + "%");
			System.out.println("The recall is = " + recall.stream().mapToDouble(e->e).average().getAsDouble() + "%");
			System.out.println("The F-measure is = " + fmeasure.stream().mapToDouble(e->e).average().getAsDouble());
		} 
		catch ( Exception e ) 
		{
			e.printStackTrace();
		}

	}

	/**
	 * Checks if the input string is in String format.
	 *
	 * @param String
	 * @return boolean
	 */
	private static boolean isNominal(String str){
		return str.matches("[a-zA-Z]+");
	}

	/**
	 * Calculates the mean for the input.
	 *
	 * @param double, int
	 * @return double
	 */
	private static double mean(double sum,int n)  {
		return sum/n;
	}

	/**
	 * Calculates the standard deviation for the input.
	 *
	 * @param double, int
	 * @return double
	 */
	private static double standardDeviation(double sumSq,int n)  {
		return Math.sqrt(sumSq/(n-1));
	}

	/**
	 * Calculates the gaussian pdf for the input.
	 *
	 * @param double, double, double
	 * @return double
	 */
	private static double gaussianPdf(double mean,double standardDev,double x) {
		return (1/Math.sqrt(2*Math.PI*standardDev*standardDev))*
				Math.exp(-1*Math.pow(x-mean,2)/(2*standardDev*standardDev));
	}

	/**
	 * Returns the key from the input value from nominalMap.
	 *
	 * @param double
	 * @return String
	 */
	private static String getKeyFromValue(double db){
		String rv = null;
		for(Entry<String, Double> entry : nominalMap.entrySet()) {
			if(entry.getValue() == db){
				rv = entry.getKey();
				break;
			}
		}
		return rv;
	}

	/**
	 * A map class to store the probabilities for each nominal parameter.
	 */
	static class NominalMap {
		double one;
		double zero;
		public NominalMap(double one, double zero) {
			this.one = one;
			this.zero = zero;
		}	
	}
}