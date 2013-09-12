package koch.desktop.os;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import koch.desktop.os.DataStore.TimeData;

/**
 * Determine pmf of being online
 * 24 samples / day
 * @author wil
 *
 */
public class OnlineSignatureService extends TimerTask{

	
	private static final long ONE_HOUR_IN_MILLISECONDS = 1000 * 60 * 60;
	private static final int NUMBER_OF_SAMPLES  = 0;
	//TODO add to config
	//TODO this might be down but still have internet access
	private static final String HOST_URL = "http://github.com";
	private DataStore mDS = new DataStore();
	
	public OnlineSignatureService(){
		Timer timer = new Timer();
		//Schedule timer to go off when ever a sample is to be taken
		timer.scheduleAtFixedRate(this, getNextHour(), ONE_HOUR_IN_MILLISECONDS);
	}
	
	/**
	 * Get next on the hour
	 * @return
	 */
	private Date getNextHour(){
		//TODO this sampling storage needs to be in 
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
			   return result.getTime();
		 // return today.getTime();
	}
	
	/**
	 * Executed by timer
	 */
	@Override
	public void run() {
		System.out.println("check..");
		
		mDS.readData(DataStore.DEFAULT_FILE_NAME);
		
		
		updateTotalDays();
		
		//Should be about on the hour
		Calendar today = new GregorianCalendar();
		int currentHour = today.get(Calendar.HOUR_OF_DAY);
		
		//update record
		
		TimeData td = mDS.getTimeData()[currentHour];
		if (hasInternet(HOST_URL)){
			td.incDays();
		}
		td.update();

		//clean up
		mDS.setLastDate(getTime());
		
		mDS.writeData(DataStore.DEFAULT_FILE_NAME);
	}
	
	
	private long getTime(){
		  Calendar today = new GregorianCalendar();
		  return today.getTimeInMillis();
	}
	
	private boolean hasInternet(String hostUrl){
		Socket socket = null;
		boolean reachable = false;
		try {
		    socket = new Socket(hostUrl, 80);
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
		Date lastDate = new Date(mDS.getLastDate());
		Calendar lastDay = new GregorianCalendar();
		lastDay.setTime(lastDate);

		Calendar today = new GregorianCalendar();
		
		mDS.setTotalDays(mDS.getTotalDays()+daysBetween(today, lastDay));
		
	}

	/**
	 * Dont want to use 3rd party so taking performance hit
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private int daysBetween(Calendar startDate, Calendar endDate) {
		Calendar date = (Calendar) startDate.clone();
        int daysBetween = 0;
        while (date.before(endDate)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new OnlineSignatureService();
	}

	

}
