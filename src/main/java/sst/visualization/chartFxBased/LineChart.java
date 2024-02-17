package sst.visualization.chartFxBased;

import io.fair_acc.chartfx.Chart;
import io.fair_acc.chartfx.XYChart;
import io.fair_acc.chartfx.axes.Axis;
import io.fair_acc.chartfx.axes.spi.CategoryAxis;
import io.fair_acc.chartfx.axes.spi.DefaultNumericAxis;
import io.fair_acc.chartfx.plugins.EditAxis;
import io.fair_acc.chartfx.plugins.Screenshot;
import io.fair_acc.chartfx.plugins.Zoomer;
import io.fair_acc.chartfx.renderer.spi.ReducingLineRenderer;
import io.fair_acc.dataset.DataSet;
import io.fair_acc.dataset.spi.DoubleDataSet;
import javafx.application.Platform;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sst.table.Column;
import sst.table.DataType;
import sst.table.StringColumn;
import sst.table.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * implements a plotting of a line based chart. which use
 * lines to plot the table data.
 * */
public class LineChart {
    protected final List<DataSet> datasets;

    protected final Optional<StringColumn> x;

    public LineChart(Table table, Optional<List<String>> columns, Optional<String> xLabelsColumn) {
        List<Column<? extends Number>> numberColumns = new ArrayList<>();
        for (Column<? extends Serializable> column : table) {
            String columnName = column.getName();
            DataType columnType = column.getType();
            if (columnType.equals(DataType.String) || columnType.equals(DataType.Boolean)) {
                continue;
            }
            if (columns.isPresent()) {
                if (columns.get().contains(columnName)) {
                    numberColumns.add((Column<? extends Number>) column);
                }
            } else {
                numberColumns.add((Column<? extends Number>) column);
            }
        }
        this.datasets = this.generateDatasets(numberColumns);
        if (xLabelsColumn.isPresent()) {
            this.x = Optional.of(table.getColumn(xLabelsColumn.get()).asStringColumn());
        } else {
            this.x = Optional.empty();
        }
    }

    /**
     * plot the chart.
     * */
    public void plot() {
        Platform.startup(() -> {
            ReducingLineRenderer renderer = new ReducingLineRenderer();
            renderer.addDataSets(this.datasets.toArray(DataSet[]::new));
            renderer.setCache(true);
            renderer.setCacheHint(CacheHint.SPEED);
            renderer.setMaxPoints(300);

            Axis yAxis = new DefaultNumericAxis("y");
            StringBuilder sb = new StringBuilder();
            for (DataSet dataSet : this.datasets) {
                if (!sb.isEmpty()) {
                    sb.append(", ");
                }
                sb.append(dataSet.getName());
            }
            yAxis.setUnit(sb.toString());

            Chart chart;
            if (this.x.isPresent()) {
                CategoryAxis catAxis = new CategoryAxis("x");
                catAxis.setCategories(this.x.get().asList());
                catAxis.setName(this.x.get().getName());
                chart = new XYChart(catAxis, yAxis);
            } else {
                chart = new XYChart(new DefaultNumericAxis("x"), yAxis);
            }
            chart.getPlugins().add(new Zoomer());
            chart.getPlugins().add(new EditAxis());
            chart.getPlugins().add(new Screenshot());

            chart.getRenderers().add(renderer);

            Scene scene = new Scene(chart, 700, 500);

            Stage stage = new Stage();

            stage.setScene(scene);

            stage.show();
        });
    }

    protected List<DataSet> generateDatasets(List<Column<? extends Number>> data) {
        List<DataSet> datasets = new ArrayList<>(data.size());
        for (Column<? extends Number> column : data) {
            String columnName = column.getName();
            int columnSize = column.size();
            DoubleDataSet dataSet = new DoubleDataSet(columnName);
            dataSet.setName(columnName);
            for (int i = 0; i < columnSize; i++) {
                dataSet.add(i, column.get(i).doubleValue());
            }
            datasets.add(dataSet);
        }
        return datasets;
    }
}
