package koch.desktop.os;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;

import koch.desktop.os.DataStore.TimeData;

/**
 * Determine pmf of being online
 * 24 samples / day
 * @author wil
 *
 */
public class OnlineSignatureService extends TimerTask{	
	private static final String LOCK_FILE = ".lock";
	private final int mSamplesInHour;//4;
	
	//TODO add to config or command line
	//TODO this might be down but still have internet access
	private static final String HOST_URL = "github.com";
	private DataStore mDS;
	private Timer mTimer = new Timer();
	private File mLockFile  = new File(LOCK_FILE);
	
	public OnlineSignatureService(int samplesInHour){
		
	

		mSamplesInHour = samplesInHour;
		mDS = new DataStore(mSamplesInHour);
		
		//Use zulu time so signatures can be compared at any time zone, were actually using millisec since epoch so its ok
		//DateTimeZone.setDefault( DateTimeZone.UTC);
		long period = 1000 * 60 * 60 / mSamplesInHour;
		
		//Schedule timer to go off when ever a sample is to be taken
		mTimer.scheduleAtFixedRate(this, getStartTime(), period);
	}
	
	/**
	 * Depending on the sample rate 
	 * @return
	 */
	private Date getStartTime(){
		
		DateTime now = new DateTime();
		
		int minuteIncrement = 60/mSamplesInHour;
		
		int minuteOfHour = now.getMinuteOfHour();
		int hour = now.getHourOfDay();

		int minute = ((minuteOfHour/minuteIncrement) + 1) * minuteIncrement;
		if (minute == 60){
			minute = 0;
			hour++;
		}
		
		DateTime startTime = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hour, minute);
		System.out.println("Starting sample at time = "+startTime.toString());
		return startTime.toDate();
	}
	
	/**
	 * Executed by timer
	 */
	@Override
	public void run() {
		//test if stop file is present
		
		//write lock file
		createLockFile();
		
		DateTime currentTime = new DateTime();

		System.out.println("Sampling at " + currentTime.toString());
		
		mDS.readData(DataStore.DEFAULT_FILE_NAME);
		
		
		updateTotalDays();
		
		//Should be about on the hour
		int currentMinute = currentTime.getMinuteOfDay();
		
		int index = currentMinute / (60 / mSamplesInHour);
		//update record
		
		TimeData td = mDS.getTimeData()[index];
		if (isInternetReachable(HOST_URL)){
			td.incDays();
		}
		td.update();

		//clean up
		mDS.setLastDate(currentTime.getMillis());
		
		mDS.writeData(DataStore.DEFAULT_FILE_NAME);
		//System.out.println("Writing results");
		//System.out.println("");
		
		//remove lock file
		removeLockFile();
		
	}
	
	//TODO not meant for file locking but I think it is ok in this situation, check on this
	private void createLockFile(){
		try {
			mLockFile.createNewFile();
		} catch (IOException e) {
			System.err.println("IO Exception: " + e.getMessage());
		}
	}
	private void removeLockFile(){
		mLockFile.delete();
	}

	private static boolean isInternetReachable(String hostUrl){
		Socket socket = null;
		boolean reachable = false;
		try {
		    socket = new Socket(InetAddress.getByName(hostUrl), 80);
		    reachable = true;
		} catch (UnknownHostException e) {
			System.err.println("UnknownHostException: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("IO Exception: " + e.getMessage());
		} finally {            
		    if (socket != null) try { socket.close(); } catch(IOException e) {}
		}
		return reachable;
	}
	
	private void updateTotalDays(){
		
		DateTime lastDate = new DateTime(mDS.getLastDate());
		DateTime today = new DateTime();

		int daysBetween = Days.daysBetween(today.toLocalDate(), lastDate.toLocalDate()).getDays();
		mDS.setTotalDays(mDS.getTotalDays()+ daysBetween);
		
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new OnlineSignatureService(60);
	}

	

}
