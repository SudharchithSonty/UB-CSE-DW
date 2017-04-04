package AlgorithmCode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

public class AprioriAlgorithm {

	static Map<String, HashSet<Integer>> oneset;
	static Map<String, StringBuilder> gene_expression;
	static Map<Integer, List<int[]> >prevCount;
	static List<Integer> FrequentDataSet;
	static List<int[]> frequentset;
	static float support;
	static float supportValue;
	static float confidence;
	static int flag;
	static int frequentDataSetCount = 0;
	static int associationRuleCount = 0;
	static int lines = 0;

	public static float getsupport(float n) {
		support = ((60 * lines) / 100);
		return support;
	}

	public static void main(String[] args) {
		try {
			gene_expression = new TreeMap<>();
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(
					new FileReader("/Users/Admin/Documents/workspace/AprioriAlgorithm/src/gene_expression.txt"));
			String nextRow = br.readLine();
			while (nextRow != null) {
				StringTokenizer st = new StringTokenizer(nextRow, "\t");
				String key = null;
				StringBuilder tokens = new StringBuilder();
				int count = 0;
				while (st.hasMoreElements()) {
					String temp = st.nextToken();
					if (count == 0)
						key = temp;
					else if (temp.equalsIgnoreCase("UP") || temp.equalsIgnoreCase("Down")) {
						tokens.append("G").append(count).append("_" + temp + " ");
					}
					count++;
				}
				gene_expression.put(key, tokens);
				nextRow = br.readLine();
				lines++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Support is set to be " + getsupport(lines));
		generateFrequentSets();		
		prevCount = new HashMap<Integer, List<int[]>>();
		for(int k =1; k < FrequentDataSet.size(); k++)
		{
		frequentset = generate(k);
		
		prevCount.put(k, frequentset);
		}		
		for(int k : prevCount.keySet())
		{
			if (prevCount.get(1).size() == 2)
			{
				
			}
		}


	}
	private static void generateFrequentSets() {

		List<Integer> upCount = new ArrayList<Integer>();
		List<Integer> downCount = new ArrayList<Integer>();
		boolean firstIter = true;
		for (StringBuilder l : gene_expression.values()) {
			String temp[] = l.toString().split(" ");
			int k = 0;
			for (String s : temp) {
				if (firstIter) {				
					
					if (s.split("_")[1].equals("UP")) {
						upCount.add(1);
						downCount.add(0);
					} else {
						upCount.add(0);
						downCount.add(1);
					}
				} else {
					if (s.split("_")[1].equals("UP")) {
						upCount.set(k, upCount.get(k) + 1);
					} else {
						downCount.set(k, downCount.get(k) + 1);
					}
				}
				k++;
			}
			firstIter = false;
		}
		FrequentDataSet = new ArrayList<Integer>();
		for (int i = 0; i < upCount.size(); i++) {
			if (upCount.get(i) >= getsupport(support))
				FrequentDataSet.add(i + 1);
			if (downCount.get(i) >= getsupport(support))
				FrequentDataSet.add(i + 1);
		}
		System.out.println(Arrays.toString(FrequentDataSet.toArray()));
		System.out.println("Number of Length:1 Frequent Item Sets :" + FrequentDataSet.size());
	}
public static List<int[]> generate(int r)
{
	int[] f = new int[FrequentDataSet.size()];
	for (int x = 0; x < f.length; x++)
		f[x] = FrequentDataSet.get(x);
	int k =r;                             // sequence length   
	List<int[]> subsets = new ArrayList<>();
	int[] s = new int[k];
	 int[] result = new int[s.length];// here we'll keep indices // pointing to elements in input array
	if (k <= f.length) {
	    for (int i = 0; (s[i] = i) < k - 1; i++);  	    
	    for (int i = 0; i < s.length; i++) 
	        result[i] = f[s[i]];
	   // System.out.println(Arrays.toString(result));
	    subsets.add(result);
	    for(;;) {
	        int i;
	        for (i = k - 1; i >= 0 && s[i] == f.length - k + i; i--); 
	        if (i < 0) {
	            break;
	        } else {
	            s[i]++;  
	            for (++i; i < k; i++) {  	            
	                s[i] = s[i - 1] + 1; 
	            }	
	            int[] result1 = new int[s.length]; 
	    	    for (int j = 0; j < s.length; j++) 
	    	        result1[j] = f[s[j]];
	    	
	    	    subsets.add(result1);	                        	                                 
	        }
	    }	    	 
	}	
	return subsets;
}
	
}



