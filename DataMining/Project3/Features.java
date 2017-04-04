import java.util.ArrayList;

public class Features {

	ArrayList<String> choices = new ArrayList<>();
	double mean;
	String type;
	public Features()
	{
		mean = 0.0;
		type = null;
	
	}
	
	public ArrayList<String> getChoices() {
		return choices;
	}

	public void setChoices(ArrayList<String> choices) {
		this.choices = choices;
	}

	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Features(ArrayList<Double> input) {

		double sum = 0;
		if (!input.isEmpty()) {
			for (Double value : input) {
				sum += value;
			}
			this.mean = sum / input.size();
		}
	}
	//public Features(ArrayList<String> input)
	
	
}
