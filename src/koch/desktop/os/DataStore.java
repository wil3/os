package koch.desktop.os;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Interface used to store data
 * File format:
 * 
 * Line 1: Last date the service was run
 * Line 2: Total number of days since the beginning of the experiement
 * Line 3+: Sample taken, first value is overal probability, second value days reachable
 * 
 * @author wil
 *
 */
public class DataStore {
	//private static final int HOURS_IN_DAY = 24;

	private static final String DELIMITER = ",";
	//TODO allow to specify location
	public static final String DEFAULT_FILE_NAME = "time.dat"; 
	private TimeData [] mTimeData;
	private long mLastDate = new Date().getTime();
	private int mNumberOfSamples;
	private int mSamplesInHour;
	private int mTotalDays = 1;
	
	/**
	 * 
	 * @param samplesInHour Default samples an hour if there is already not a data store with the sample requirements, if that is found it is overwritten.
	 */
	public DataStore(int samplesInHour){
		mSamplesInHour = samplesInHour;
		
	}
	/**
	 * Default, read samples straight from data store
	 */
	public DataStore(){
		
	}
	private void initializeData(){
		mNumberOfSamples = mSamplesInHour * 24;
		mTimeData = new TimeData[mNumberOfSamples];
		for (int i=0; i<mNumberOfSamples;i++){
			mTimeData[i] = new TimeData();
		}
	}
	/**
	 * Read data to file
	 * @param dataFilePath
	 */
	public void readData(String dataFilePath){
		if (!new File(dataFilePath).exists()){ //not initialized yet
			initializeData();
			return;
		}
		FileReader reader = null;
	    BufferedReader input = null;
	    try {
			reader = new FileReader(dataFilePath);
			input =  new BufferedReader(reader);
			String sSample = input.readLine();
			if (sSample != null){
				//Overwrite
				mSamplesInHour = Integer.parseInt(sSample);
			} else { throw new IOException("No sample size.");}
			
			mNumberOfSamples = mSamplesInHour * 24;
			mTimeData = new TimeData[mNumberOfSamples];
			
			
			String  sLastDate = input.readLine();
			if (sLastDate!=null){
				mLastDate = Long.parseLong(sLastDate);
			} else {throw new IOException("No last date specified.");}
			String  sTotalDays = input.readLine();
			if (sTotalDays!=null){
				mTotalDays = Integer.parseInt(sTotalDays);
			} else {throw new IOException("No total days specified.");}
			
			String line = null;
			for (int i=0; i<mNumberOfSamples; i++){
		        if (( line = input.readLine()) != null){ //should be 24 lines here
		        	String [] fields = line.split(DELIMITER);
		        	TimeData td = new TimeData();
		        	td.probability= Double.parseDouble(fields[0]);
		        	td.days = Integer.parseInt(fields[1]);
		        	mTimeData[i] = td;
		        } else {
		        	throw new IOException("Not enough hours");
		        }
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found exception: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("IO Exception: " + e.getMessage());
		} finally {
			if (input != null){
				try {
					input.close();
				} catch (IOException e) {
					System.err.println("IO Exception closing stream: " + e.getMessage());
				}
			}
			if (reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					System.err.println("IO Exception closing stream: " + e.getMessage());
				}
			}
		}
	}
	/**
	 * Write out the data to a file
	 * @param dataFilePath
	 */
	public void writeData(String dataFilePath){
		FileWriter fstream = null;
		BufferedWriter out = null;
		
		try {
			fstream = new FileWriter(dataFilePath);
			out = new BufferedWriter(fstream);
			
			//header stuff
			out.write(mSamplesInHour+"");
			out.write("\r\n");
			out.write(mLastDate+"");
			out.write("\r\n");
			out.write(mTotalDays+"");
			out.write("\r\n");

			//write out all data
			for (int i=0; i<mNumberOfSamples;i++){
				TimeData td = mTimeData[i];
				out.write(td.probability + DELIMITER + td.days);
				out.write("\r\n");
			}
			
		} catch (IOException e) {
			System.err.println("IO Exception: " + e.getMessage());
		} finally {

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					System.err.println("IO Exception closing stream: " + e.getMessage());
				}
			}
			if (fstream != null) {
				try {
					fstream.close();
				} catch (IOException e) {
					System.err.println("IO Exception closing stream: " + e.getMessage());
				}
			}
			
		}
	}
	
	public TimeData[] getTimeData() {
		return mTimeData;
	}
	public void setTimeData(TimeData[] timeData) {
		this.mTimeData = timeData;
	}
	public long getLastDate() {
		return mLastDate;
	}
	public void setLastDate(long lastDate) {
		this.mLastDate = lastDate;
	}
	public int getTotalDays() {
		return mTotalDays;
	}
	public void setTotalDays(int totalDays) {
		this.mTotalDays = totalDays;
	}
	public int getSamplesInHour() {
		return mSamplesInHour;
	}
	public void setSamplesInHour(int samplesInHour) {
		this.mSamplesInHour = samplesInHour;
	}
	class TimeData {
		private double probability = 0;
		private int days = 0;
		public void setDays(int days){
			this.days = days;
		}
		public void incDays(){
			this.days++;
		}
		public int getDays(){
			return this.days;
		}
		public void setProbability(double probability){
			this.probability = probability;
		}
		public double getProbability(){
			return this.probability;
		}
		public void update(){
			this.probability = (days * 1.0) / mTotalDays;
		}
		
	}
	
}
