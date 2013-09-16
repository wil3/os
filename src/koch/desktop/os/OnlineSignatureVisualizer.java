package koch.desktop.os;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


/**
 * Load from the data file and display results as a graph
 * @author wil
 *
 */
public class OnlineSignatureVisualizer extends ApplicationFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1724990394149391612L;
	
	
	
	private DataStore mDS;


		     /**
     * A custom renderer that returns a different color for each item in a single series.
     */
    class CustomRenderer extends XYBarRenderer {

        /** The colors. */
        private Paint color;

        /**
         * Creates a new renderer.
         *
         * @param colors  the colors.
         */
        public CustomRenderer(final Paint color) {
            this.color = color;
        }

        /**
         * Returns the paint for an item.  Overrides the default behaviour inherited from
         * AbstractSeriesRenderer.
         *
         * @param row  the series.
         * @param column  the category.
         *
         * @return The item color.
         */
        public Paint getItemPaint(final int row, final int column) {
            return this.color;
        }
    }

		    
		    /**
		     * Constructs the demo application.
		     *
		     * @param title  the frame title.
		     */
		    public OnlineSignatureVisualizer(final String title) {

		        super(title);
		        
		       mDS = new DataStore();
		       mDS.readData(DataStore.DEFAULT_FILE_NAME);

		        final TimeSeriesCollection data = createDataset();
		        //data.setDomainIsPointsInTime(false);
		        final JFreeChart chart = ChartFactory.createXYBarChart(
		            title,
		            "Time of day",
		            true,
		            "Probability",
		            data,
		            PlotOrientation.VERTICAL,
		            false, //ledgend
		            false,
		            false
		        );

		        final XYPlot plot = chart.getXYPlot();
		       
		        
		        final DateAxis axis = (DateAxis) plot.getDomainAxis();
		        axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
		        
		        final XYBarRenderer renderer = new CustomRenderer(Color.red);
		       
		        
		        plot.setRenderer(renderer);
		        XYBarRenderer.setDefaultShadowsVisible(false);

		        // add the chart to a panel...
		        final ChartPanel chartPanel = new ChartPanel(chart);
		        chartPanel.setPreferredSize(new java.awt.Dimension(500, 300));
		        setContentPane(chartPanel);

		       renderer.setShadowVisible(false);
		       renderer.setBarPainter(new StandardXYBarPainter());
		    }

		    public TimeSeriesCollection createDataset() {

		        final TimeSeries t = new TimeSeries("p");
		       for (int i=0; i<mDS.getTimeData().length; i++){
		    	   int minuteOfDay = i * (60 / mDS.getSamplesInHour());
		    	   
		    	   int hour = minuteOfDay/60;
		    	   int minute = minuteOfDay%60;
		    	   t.add(new Minute(minute, hour, 1, 1, 1984), mDS.getTimeData()[i].getProbability());
		       }
		        return new TimeSeriesCollection(t);

		    }
		    /**
		     * Starting point for the demonstration application.
		     *
		     * @param args  ignored.
		     */
		    public static void main(final String[] args) {

		        final OnlineSignatureVisualizer os = new OnlineSignatureVisualizer("PMF Access to Internet");
		        os.pack();
		        RefineryUtilities.centerFrameOnScreen(os);
		        os.setVisible(true);

		    }

}
