package sst.tablePrinter;

import org.apache.commons.rng.UniformRandomProvider;
import sst.table.Column;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * class to estimate the width of a column.
 * */
public class ColumnWidthEstimation {
    protected Column<? extends Serializable> column;

    protected UniformRandomProvider rng = new org.apache.commons.rng.simple.JDKRandomWrapper(new Random(1L));

    List<Integer> lengths = new ArrayList<>(1000);

    public ColumnWidthEstimation(Column<? extends Serializable> column, int n) {
        int columnSize = column.size();
        this.column = column;
        for (int i = 0; i < n; i++) {
            int suggestedRng = this.rng.nextInt(0, columnSize);
            Serializable value = column.get(suggestedRng);
            String valueString = value != null ? value.toString() : TablePrinter.nullReplacement;
            if (valueString.isBlank()) {
                continue;
            } else {
                this.lengths.add(valueString.length());
            }
        }
    }

    /**
     * use the item with the biggest length as size of the width.
     * */
    public int estimateMax() {
        int max = this.lengths.parallelStream()
                .max(Integer::compare)
                .orElse(0);
        return Integer.max(max, this.column.getName().length());
    }

    /**
     * calculate the mean of the lengths of the elements.
     * */
    public int estimateAverage() {
        double average = this.lengths.parallelStream()
                .mapToDouble(x -> (double) x)
                .average()
                .orElse(0);
        int ceiledAverage = (int) Math.ceil(average);
        return Integer.max(ceiledAverage, this.column.getName().length());
    }

    /**
     * calculate the 'trimmed mean' but only for up. of the values.
     * */
    public int estimatePercentile80() {
        List<Integer> sortedLengths = this.lengths.parallelStream()
                .sorted(Integer::compare)
                .toList();
        List<Integer> sortedLengthsCutted = sortedLengths.subList(
                0,
                ((int) Math.ceil(sortedLengths.size() * 0.8))
        );
        int max = sortedLengthsCutted.get(sortedLengthsCutted.size() - 1);
        return Math.max(column.getName().length(), max);
    }
}
