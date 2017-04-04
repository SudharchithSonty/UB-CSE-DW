package part2;

import java.io.IOException;
import org.apache.commons.lang.StringUtils;
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
 * This code use the second csv file bina_classchedule2.csv as input
    This code generates the following output
 * This code use the second csv file bina_classchedule2.csv as input
 
 MR -> Extract enrollment/capacity for each department for every semester/year
 EXPECTED OUTPUT:
 -	SPRING 2014  ENG  134/600
 -	FALL 2014 ENG   565/600
*/

public class Driver4 {
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
				// Setting keyWord
				String keyWord = lines[3] + " " + lines[4];

				int totalSeats = 0; 
				int seatCount = 0; 
				IntWritable numberOfSeats;
				IntWritable seatCapacity;
				if (lines.length > 10 && (lines[9].trim().length() > 0 && lines[10].trim().length() > 0)) {
					if(StringUtils.isNumeric(lines[9].trim())) {
						seatCount = Integer.parseInt(lines[9].trim());
						numberOfSeats = new IntWritable(seatCount);
					}
					if(StringUtils.isNumeric(lines[10].trim())) {
						totalSeats = Integer.parseInt(lines[10].trim());
						seatCapacity = new IntWritable(totalSeats);
					}
				} else
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
		private Text result1 = new Text();
		@Override
		public void reduce(Text key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			int sum_enrollment = 0, sum_capacity = 0;

			for (Text val : values) {
				String[] str = val.toString().split("_");
				sum_capacity += Integer.parseInt(str[0].trim());
				sum_enrollment += Integer.parseInt(str[1].trim());
			}
			Text result = new Text(String.valueOf(sum_enrollment)
					+"/"
					+String.valueOf(sum_capacity));
			context.write(key,result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(Driver4.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setReducerClass(reducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
