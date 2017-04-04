package part2;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
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
 * Note: Drivers 1 and 2 use bina_classschedule.csv as input while Drivers 3,4 & 5 use bina_classschedule2.csv as input. 
 **/

/**
    * This code generates the following output
    MR -> Extract Enrollment/Capacity for each hall, every sem.
 EXPECTED OUTPUT:
 -	NSC SPRING 2014   345 420
 -	BALDY SPRING 2014 333 400


 **/
public class Driver1 {

	public static class TokenizerMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

			String lines[] = value.toString().split(",");
			String roomName = lines[2].toLowerCase();
			String[] roomNo = lines[2].split(" ");

			if (roomName.equals("unknown") || roomName.equals("ukwn") || roomName.equals("unkwn") || roomNo[0].equalsIgnoreCase("arr"))
				return;
			else
			{
				String keyWord = roomNo[0] + "_" + lines[1];

				int totalSeats = 0; 
				int seatCount = 0; 

				if (lines.length == 9) {
					totalSeats = Integer.parseInt(lines[8]);
					seatCount = Integer.parseInt(lines[7]);
				} else if (lines.length != 9)
					return;

				String enrollment = String.valueOf(totalSeats);
				String capacity = String.valueOf(seatCount);

				Text outvalue = new Text();
				outvalue.set(enrollment + "_" + capacity);

				word.set(keyWord);
				context.write(word, outvalue);
			}
		}
	}

	public static class reducer extends Reducer<Text, Text, Text, Text> {
		@Override
		public void reduce(Text key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			int sum_enrollment = 0;
			int sum_capacity = 0;

			for (Text val : values) {
				String[] str = val.toString().split("_");
				sum_capacity += Integer.parseInt(str[0].trim());
				sum_enrollment += Integer.parseInt(str[1].trim());
			}
			context.write(key, new Text(String.valueOf(sum_enrollment)+"\t"+String.valueOf(sum_capacity)));
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(Driver1.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setReducerClass(reducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
