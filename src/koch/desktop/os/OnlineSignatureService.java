package koch.desktop.os;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Determine pmf of being online
 * 24 samples / day
 * @author wil
 *
 */
public class OnlineSignatureService extends TimerTask{

	
	private static final String DELIMITER = ",";
	private static final int HOURS_IN_DAY = 24;
	private static final long ONE_HOUR_IN_MILLISECONDS = 1000 * 20;//60 * 60;
	
	private static final String FILE_NAME = "time.dat";
	private TimeData [] mTimeData;
	private long mLastDate;
	private int mTotalDays;
	
	class TimeData {
		private double probability;
		private int days;
		public void update(){
			this.probability = (days * 1.0) / mTotalDays;
		}
	}
	
	public OnlineSignatureService(){
		mTimeData = new TimeData[HOURS_IN_DAY];
		Timer timer = new Timer();
		//Schedule timer to go off on the hour
		timer.scheduleAtFixedRate(this, getNextHour(), ONE_HOUR_IN_MILLISECONDS);
	}
	
	/**
	 * Get next on the hour
	 * @return
	 */
	private Date getNextHour(){
		  Calendar today = new GregorianCalendar();
		  int currentHour = today.get(Calendar.HOUR_OF_DAY);
		  int nextHour = currentHour + 1;
		  Calendar result = new GregorianCalendar(
				  today.get(Calendar.YEAR),
				  today.get(Calendar.MONTH),
				  today.get(Calendar.DATE),
				  nextHour,
			      0
			    );
			    return result.getTime();	}
	/**
	 * Read data to file
	 * @param dataFilePath
	 */
	private void readData(String dataFilePath){
		FileReader reader = null;
	    BufferedReader input = null;
	    try {
			reader = new FileReader(dataFilePath);
			input =  new BufferedReader(reader);
			String  sLastDate = input.readLine();
			if (sLastDate!=null){
				mLastDate = Long.parseLong(sLastDate);
			}
			String  sTotalDays = input.readLine();
			if (sTotalDays!=null){
				mTotalDays = Integer.parseInt(sTotalDays);
			}
			String line = null;
			for (int i=0; i<HOURS_IN_DAY; i++){
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
	private void writeData(String dataFilePath){
		FileWriter fstream = null;
		BufferedWriter out = null;
		
		try {
			fstream = new FileWriter(dataFilePath);
			out = new BufferedWriter(fstream);
			
			//header stuff
			out.write(mLastDate+"");
			out.write("\r\n");
			out.write(mTotalDays+"");
			out.write("\r\n");

			//write out all data
			for (int i=0; i<HOURS_IN_DAY;i++){
				TimeData td = mTimeData[i];
				out.write(td.probability + DELIMITER + td.days);
				out.write("\r\n");
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fstream != null) {
				try {
					fstream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	/**
	 * Executed by timer
	 */
	@Override
	public void run() {
		
		
		//Should be about on the hour
		Calendar today = new GregorianCalendar();
		int currentHour = today.get(Calendar.HOUR_OF_DAY);
		
		//update record
		TimeData td = mTimeData[currentHour];
		if (hasInternet()){
			
		}
		td.update();

		readData(FILE_NAME);
		
		writeData(FILE_NAME);
	}
	
	
	private long getTime(){
		  Calendar today = new GregorianCalendar();
		  return today.getTimeInMillis();
	}
	
	private boolean hasInternet(){
		return true;
	}
	
	private void updateTotalDays(){
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new OnlineSignatureService();
	}

	

}
