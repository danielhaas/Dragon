package hk.warp.apps;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;


/**
 * A simple graph drawing class which supports multiple series, selection and calculation of minimum, maximum, variance, etc.
 * @author shlui1
 *
 */
public class GraphDrawer {

	private final int size_grow = 1000;
	private final List<long[]> times = new ArrayList<>();
	private final List<double[][]> values = new ArrayList<>();
	private long[] last_times;
	private double[][] last_values;
	private int index;
	private long time_min;
	private final int series_count;
	private final String[] titles;
	private Window window;
	private JSlider js_granual;
	private TimeSeries[] ts;
	private final int IDEAL_COUNT = 1000;
	private JFreeChart chart;
	private final JTextArea jta_summary = new JTextArea();
	private JTextField jt_item_count;
	private int granulity;
	private int max_granulity;

	/**
	 * Create a <code>GraphDrawer</code> with a single series
	 */
	public GraphDrawer() {
		this(1);
	}

	/**
	 * Create a <code>GraphDrawer</code> with the specified series, name of each series is automatically generated
	 * @param series_count number of series
	 */
	public GraphDrawer(final int series_count) {
		this(getNames(series_count));
	}

	/**
	 * Create a <code>GraphDrawer</code> with the series of the specified names
	 * @param names names of the series
	 */
	public GraphDrawer(final String... names) {
		series_count = names.length;
		titles = names;
		grow();
	}

	/**
	 * Adding a value to the graph for the first series
	 * @param time the time (x-axis value)
	 * @param value the value  (y-axis value)
	 */
	public void addValue(final long time, final double value) {
		addValue(time, value, 0);
	}

	/**
	 * Adding a value to the graph for the specified series
	 * @param time the time (x-axis value)
	 * @param value the value  (y-axis value)
	 * @param series series to add the value
	 */
	public void addValue(final long time, double value, final int series) {
		if (index > 0 && last_times[index - 1] == time) {
			index--;
		} else {
			checkIndex();
			if (time_min == 0)
				time_min = time;
			last_times[index] = time;
		}
		if (value == 0) {
			value = 1;
		}
		last_values[series][index] = value;
		index++;
	}

	/**
	 * Display the graph
	 * @param modal use <code>true</code> for displaying with a <code>JDialog</code>; <code>false</code> for displaying with a <code>JFrame</code>
	 */
	public void show(final boolean modal) {
		show(modal, true);
	}

	/**
	 * Display the graph
	 * @param modal use <code>true</code> for displaying with a <code>JDialog</code>; <code>false</code> for displaying with a <code>JFrame</code>
	 */
	public void show(final boolean modal, final boolean useGranuality) {

		if (getN() < 100 || !useGranuality)
			granulity = 0;
		else {
			granulity = getN() / IDEAL_COUNT;

			granulity = Math.max(granulity, 1);
		}


		max_granulity = granulity;

		ts = new TimeSeries[series_count];
		final TimeSeriesCollection coll = new TimeSeriesCollection();
		for (int i = 0; i < ts.length; i++) {
			ts[i] = new TimeSeries(titles[i]);
			coll.addSeries(ts[i]);
		}

		updateSeries(true);

		chart = ChartFactory.createTimeSeriesChart(null, null, null, coll, // data
				true, // include legend
				true, // tooltips
				false // urls
				);

		chart.addChangeListener(event -> {
			if (event instanceof PlotChangeEvent) {
				final PlotChangeEvent pce = (PlotChangeEvent) event;
				if (pce.getType() == ChartChangeEventType.GENERAL)
					updateSeries(false);
			}
		});

		if (modal) {
			final JDialog jd = new JDialog();
			window = jd;
			jd.setModal(true);
			jd.setContentPane(getPanel(chart));
			jd.pack();
			jd.setVisible(true);
		} else {
			final JFrame myFrame = new JFrame();
			window = myFrame;
			myFrame.setContentPane(getPanel(chart));
			myFrame.pack();
			myFrame.setVisible(true);
		}
	}

	private static final String[] getNames(final int aCount) {
		final String[] titles = new String[aCount];
		for (int i = 0; i < titles.length; i++) {
			titles[i] = "Values " + i;
		}
		return titles;
	}

	private void grow() {
		last_times = new long[size_grow];
		last_values = new double[series_count][size_grow];
		times.add(last_times);
		values.add(last_values);
		index = 0;
	}

	private JPanel getPanel(final JFreeChart chart) {
		final JPanel myPanel = new JPanel();

		myPanel.setLayout(new BorderLayout());

		myPanel.add(new ChartPanel(chart), BorderLayout.CENTER);

		myPanel.add(createBottomPanel(), BorderLayout.SOUTH);

		myPanel.add(createRightSidePanel(), BorderLayout.EAST);

		return myPanel;
	}

	private Component createRightSidePanel() {
		final JPanel myPanel = new JPanel();

		myPanel.add(jta_summary);
		return myPanel;
	}

	private JPanel createBottomPanel() {
		final JPanel myPanel = new JPanel();

		final JButton myCloseButton = new JButton(new AbstractAction("Close") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				window.dispose();
			}

		});

		if (granulity > 0) {
			js_granual = new JSlider(1, max_granulity, granulity);

			js_granual.addChangeListener(e -> {
				final int new_granual = js_granual.getValue();
				if (new_granual != granulity) {
					granulity = new_granual;
					updateSeries(true);
					fillItemCount();
				}
			});

			myPanel.add(new JLabel("Granuality:"));
			myPanel.add(js_granual);
		}
		myPanel.add(new JLabel("Item Count:"));
		jt_item_count = new JTextField("        ");
		jt_item_count.setEnabled(false);
		fillItemCount();

		myPanel.add(jt_item_count);

		myPanel.add(myCloseButton);

		return myPanel;
	}

	private void fillItemCount() {

		int count;

		if (granulity == 0)
			count = getN();
		else
			count = getN() / granulity;
		jt_item_count.setText("" + count);
	}

	private void updateSeries(final boolean updatePlot) {
		final long min, max;
		if (chart == null) {
			min = -1;
			max = -1;
		} else {
			final ValueAxis axis = chart.getXYPlot().getDomainAxis();
			min = (long) axis.getLowerBound() * 1000;
			max = (long) axis.getUpperBound() * 1000;
		}
		final StringBuffer myDetails = new StringBuffer();

		for (int i2 = 0; i2 < ts.length; i2++) {
			final SummaryStatistics ss = new SummaryStatistics();
			if (updatePlot)
				ts[i2].clear();

			long lt = 0;
			int counter = 0;
			for (int i = 0; i < times.size(); i++) {
				final long[] times_ = times.get(i);
				final double[] values_ = values.get(i)[i2];

				final int length;

				if (times_ == last_times) {
					length = index;
				} else {
					length = times_.length;
				}

				for (int j = 0; j < length; j++) {
					long mt = times_[j];

					if (values_[j] == 0)
						continue;

					if (granulity > 1 && counter++ % granulity != 0)
						continue;

					if (updatePlot) {
						if (mt<=lt)
						{
							mt= lt+1;
						}
						/*						while (mt <= lt)
							mt++;*/
						lt = mt;
						final FixedMillisecond myTime = new FixedMillisecond(mt);
						ts[i2].add(myTime, values_[j], false);
					}
					if (min > 0) {
						if (mt < min || mt > max)
							continue;
					}

					ss.addValue(values_[j]);
				}
			}
			if (updatePlot)
				ts[i2].fireSeriesChanged();

			if (i2 > 0)
				myDetails.append("\n\n");
			myDetails.append(ts[i2].getKey());
			myDetails.append("\n");
			myDetails.append(ss.toString());
		}

		jta_summary.setText(myDetails.toString());
	}

	private void checkIndex() {
		if (index >= size_grow) {
			grow();
		}
	}

	public int getN() {
		return (times.size() - 1) * size_grow + index;
	}
}
