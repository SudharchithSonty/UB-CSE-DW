import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.WordCount;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.commons.io.FileUtils;

@SuppressWarnings("unused")
public class Kmeans {
    
    public static String OUT = "outfile";
    public static String IN = "/Users/Admin/Desktop/DataMining/Project-2/input/iyer.txt";
    public static String CENTROID = "/Users/Admin/Documents/workspace/KMeans/output/centroid.txt";
    public static String JOB_NAME = "KMeans";
    public static String SPLITTER = "\t";
    static int MAX_ITERATIONS = 10;
    static HashMap<Integer, ArrayList<Double>> input = new HashMap<>();
    static HashMap<Integer, ArrayList<Double>> initial_centroids = new HashMap<>();
    static HashMap<Integer, ArrayList<Integer>> Clusterwithdistance = new HashMap<>();
    static HashMap<Integer, ArrayList<Double>> inputdata = new HashMap<>(ParseInputData(IN));
    static HashMap<Integer, ArrayList<Double>> final_initial_centroids = new HashMap<>(getRandomCentroids(inputdata));
    static HashMap<Integer, String> resultmap =  new HashMap<>();
    
    // Method to get a map from the input data, at the pre processing stage so
    // that the map can be used to compute the initial set of random centroids
    public static Map<Integer, ArrayList<Double>> ParseInputData(String s) {
        
        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(IN));
            while ((line = reader.readLine()) != null) {
                ArrayList<Double> expressionValues = new ArrayList<Double>();
                String values[] = line.split("\t");
                for (int i = 2; i < values.length; i++) {
                    expressionValues.add(Double.valueOf(values[i]));
                }
                input.put(Integer.valueOf(values[0]), expressionValues);
            }
            reader.close();
        } catch (IOException e) {
            
        }
        return input;
    }
    
    // method to compute the random centroids for the first iteration of the
    // Map-Reduce program, returns a map with initial centroids and values
    
    public static Map<Integer, ArrayList<Double>> getRandomCentroids(HashMap<Integer, ArrayList<Double>> temp) {
        int k = 5;
        Random randomGenerator = new Random();
        for (int i = 1; i <= k;) {
            int randomRowId = randomGenerator.nextInt(150) + 1;
            if (input.containsKey(randomRowId)) {
                initial_centroids.put(i, input.get(randomRowId));
                i++;
            }
        }
        return initial_centroids;
    }
    
    // method to calculate the eucledian distance between a point and centroid
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
    
    // method to check if the centroids obtained in the previous step are the
    // same as the ones obtained in the current step
    public static boolean checkPreviousCentroidToCurrent(HashMap<Integer, ArrayList<Double>> prev,
                                                         HashMap<Integer, ArrayList<Double>> current) {
        for (int cluster : prev.keySet()) {
            ArrayList<Double> prev_centroid = prev.get(cluster);
            ArrayList<Double> curr_centroid = current.get(cluster);
            if (!curr_centroid.equals(prev_centroid)) {
                return false;
            }
        }
        return true;
    }
    
    static HashMap<Integer, ArrayList<Double>> computeCentroid() {
        initial_centroids.clear();
        double sumOfColumn = 0;
        for (int cluster : Clusterwithdistance.keySet()) {
            ArrayList<Integer> geneIds = Clusterwithdistance.get(cluster);
            ArrayList<Double> centroid = new ArrayList<Double>();
            ArrayList<ArrayList<Double>> listOfAllGenes = new ArrayList<ArrayList<Double>>();
            for (int geneId : geneIds) {
                if (input.containsKey(geneId)) {
                    listOfAllGenes.add(input.get(geneId));
                } else {
                    System.out.println("gene id nt present");
                }
            }
            int numOfExpColmn = listOfAllGenes.get(0).size();
            for (int col = 0; col < numOfExpColmn; col++) {
                sumOfColumn = 0;
                for (int i = 0; i < listOfAllGenes.size(); i++) {
                    sumOfColumn += listOfAllGenes.get(i).get(col);
                }
                centroid.add(sumOfColumn / listOfAllGenes.size());
            }
            initial_centroids.put(cluster, centroid);
        }
        return initial_centroids;
    }
    
    public static class KMeansMapper extends Mapper<Object, Text, Text, Text> {
        
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            
            int k = 5;
            String itr = value.toString();
            String[] lines = itr.split(SPLITTER);
            int point = Integer.parseInt(lines[0]);
            double min = Double.MAX_VALUE;
            int closestcluster = 0;
            ArrayList<Double> temp = new ArrayList<>();
            for (int i = 2; i < lines.length; i++) {
                temp.add(Double.valueOf(lines[i]));
            }
            for (Entry<Integer, ArrayList<Double>> entry : initial_centroids.entrySet()) {
                double dist = CalculateDistance(temp, entry.getValue());
                if (dist < min) {
                    min = dist;
                    closestcluster = entry.getKey();
                }
            }
            
            context.write(new Text(String.valueOf(closestcluster)), new Text(String.valueOf(point)));
        }
    }
    
    public static class KMeansReducer extends Reducer<Text, Text, Text, Text> {
        
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            StringBuilder sb = new StringBuilder();
            int size = 0;
            ArrayList<ArrayList<Double>> listOfAllGenes = new ArrayList<ArrayList<Double>>();
            Double sum = 0.0;
            for (Text txt : values) {
                int point = Integer.parseInt(txt.toString());
                ArrayList<Double> expressionvalues = new ArrayList<>(inputdata.get(point));
                double[] temp = new double[expressionvalues.size()];
                for (int i = 0; i < expressionvalues.size(); i++) {
                    temp[i] += expressionvalues.get(i);
                    size++;
                }
                for (int d = 0; d < temp.length; d++) {
                    temp[d] = temp[d] / size;
                }
                ArrayList<Double> centroid = new ArrayList<>();
                for (int i = 0; i < temp.length; i++) {
                    centroid.add(temp[i]);
                }
                final_initial_centroids.put(Integer.parseInt(key.toString()), centroid);
                String str = "\t" + key.toString();
                resultmap.put(point, str);
            }
        }
    }
    
    public static void main(String[] args)
    throws IllegalArgumentException, IOException, ClassNotFoundException, InterruptedException {
        
        for (int i = 0; i < MAX_ITERATIONS; i++) {			
            String filepath = OUT;
            //prev = current
            FileUtils.deleteDirectory(new File(filepath));
            Configuration conf = new Configuration();
            Job job = Job.getInstance(conf, "K-means");
            job.setJarByClass(WordCount.class);
            job.setMapperClass(KMeansMapper.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);
            job.setReducerClass(KMeansReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);
            FileInputFormat.addInputPath(job, new Path(IN));
            FileOutputFormat.setOutputPath(job, new Path(filepath));			
            job.waitForCompletion(true);
            //prev == current; break
        }
        PrintWriter out = new PrintWriter("result.txt");
        for(int i: resultmap.keySet())
        {
            String result = String.valueOf(i) + "\t " +  String.valueOf(resultmap.get(i));
            out.println(result);
        }
        out.flush();
        out.close();
    }
}
