package sst.visualization;


import sst.table.Table;
import sst.visualization.chartFxBased.LineChart;

import java.util.List;
import java.util.Optional;

/**
 * class to work with the visualization of a table (plotting, rendering etc.).
 * */
public abstract class Visualization {
    /**
     * plots a table.
     *
     * @param table        the table to be plotted only numeric columns will be plotted for hour. so
     *                     string and boolean columns will be ignored.
     * @param columns      if supplied is the columns which should be plotted
     * @param xLabelColumn the name of the column to be used as labels in the X scale.
     *                     WARNING: when this is used it can cause the rendering be very slow, because
     *                     the 'downsampling' algorithm for rendering cant be applied for this.
     */
    public static void plot(Table table, Optional<List<String>> columns, Optional<String> xLabelColumn) {
        LineChart lineChartPlot = new LineChart(table, columns, xLabelColumn);
        lineChartPlot.plot();
    }
}
