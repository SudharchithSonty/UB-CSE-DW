package part2;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
TEAM DETAILS:
 * @author1 sauravmo - Saurav Mohapatra - 50101106 - sauravmo@buffalo.edu - 
 * @author2 sudharch - Sudharchith Sonty - 50169912 -sudharch@buffalo.edu - 
 **/

/**
  * The output of the following code should be like this. It uses bina_classschedule2.csv as input.
 EXPECTED OUTPUT:
	1993	169619
    1994	369562
 */
public class Driver5 {

	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {

		private Text word = new Text();

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

			String lines[] = value.toString().split(",");
			String roomName = lines[2].toLowerCase();
			String[] roomNo = lines[2].split(" ");

			if (roomName.equals("unknown") 
					|| roomName.equals("ukwn") 
					|| roomName.equals("unkwn") 
					|| roomNo[0].equalsIgnoreCase("arr"))
				return;
			else
			{
				String keyWord = lines[3].split(" ")[1];
				IntWritable numberOfSeats = new IntWritable(0);
				if (lines.length == 11) {
					int seatCount = Integer.parseInt(lines[10]);
					numberOfSeats = new IntWritable(seatCount);
				} else if (lines.length != 11)
					return;
				word.set(keyWord);
				context.write(word, numberOfSeats);
			}
		}
	}

	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(Driver5.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
