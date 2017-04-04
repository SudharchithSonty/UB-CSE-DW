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
  *
 This code generates the follwing output
 MR -> Extract Enrollment/Capacity for each semester every year
 
 EXPECTED OUTPUT:
 -	SPRING 2014 3456 3600
 -	FALL 2014 3000 3600

 **/

public class Driver2 {

	public static class TokenizerMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

			String lines[] = value.toString().split(",");
			String[] roomNo = lines[2].split(" ");

			if (roomNo[0].equalsIgnoreCase("arr"))
				return;
			else
			{
				String keyWord = lines[1];

				int totalSeats = 0; 
				int seatCount = 0; 

				IntWritable seatCapacity;
				if (lines.length == 9) {
					totalSeats = Integer.parseInt(lines[8]);
					seatCapacity = new IntWritable(totalSeats);
				} else if (lines.length != 9)
					return;

				IntWritable numberOfSeats;
				if (lines.length == 9) {
					seatCount = Integer.parseInt(lines[7]);
					numberOfSeats = new IntWritable(seatCount);
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
		private Text result = new Text();
		@Override
		public void reduce(Text key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			//result = null;
			int sum_enrollment = 0;
			int sum_capacity = 0;

			for (Text val : values) {
				String[] str = val.toString().split("_");
				sum_enrollment += Integer.parseInt(str[1].trim());
				sum_capacity += Integer.parseInt(str[0].trim());
			}
			
			String enrollmentStr = String.valueOf(sum_enrollment);
			String capacityStr = String.valueOf(sum_capacity);
			
			Text result1 = new Text(enrollmentStr + "\t" + capacityStr);
			
			context.write(key, result1);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(Driver2.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setReducerClass(reducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
