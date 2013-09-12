package koch.desktop.os;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class OnlineSignatureService {

	private long mLastDate;
	private int mTotalDays;
	private static final String DELIMITER = ",";
	
	private void readData(String dataFile){
		FileReader reader = null;
	    BufferedReader input = null;
	    try {
			reader = new FileReader(dataFile);
			input =  new BufferedReader(reader);
			String  sLastDate = input.readLine();
			String  sTotalDays = input.readLine();
			String line = null;
	        while (( line = input.readLine()) != null){
	        	String [] fields = line.split(DELIMITER);
	        }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void writeData(){
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
