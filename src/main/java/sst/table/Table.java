package sst.table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import sst.tablePrinter.AutoColumnWidthType;
import sst.tablePrinter.TablePrinter;

import java.io.Serializable;
import java.util.*;

/**
 * implements a table functionality.
 * ans: what is a table?
 * R: a table is just a group of columns and a column is just an array a group of data.
 * so a table is just a list of lists.
 * ans: but if a table is just a 'list of lists' why not just to use a map?
 * R: is true that a table is just a 'list of lists' for someone which is looking for the raw thing.
 * in true a table is a storage format which store data in a rectangle or square storage of data.
 * so YOU NEED TO HAVE THE SAME NUMBER OF COLUMN EACH ROW vice-versa, otherwise a table is a stable
 * storage of data. so i need to make sure all rows contains the same columns vise-versa.
 */
public class Table implements Serializable, Cloneable, Iterable<Column<? extends Serializable>> {
    private static final Logger logger = LoggerFactory.getLogger(Table.class);

    private final Map<String, AbstractColumn<? extends Serializable>> columns = new LinkedHashMap<>();

    /**
     * will store the number of rows which a table needs to have.
     */
    private int nRow;

    /**
     * get the number of rows in the table
     */
    public int nRow() {
        return this.nRow;
    }

    /**
     * get the number of columns in the table
     */
    public int nCol() {
        return this.columns.size();
    }

    /**
     * get the name of the columns in the table
     */
    public List<String> getColumnNames() {
        return Collections.unmodifiableList(
                this.columns.keySet().stream().toList()
        );
    }

    public Column<? extends Serializable> getColumn(String columnName) {
        return this.columns.get(columnName);
    }

    /**
     * get all columns in the table, untyped.
     */
    public Map<String, Column<? extends Serializable>> getColumns() {
        return Collections.unmodifiableMap(
                this.columns
        );
    }

    public Map<String, DataType> getColumnTypes() {
        Map<String, DataType> dataTypes = new LinkedHashMap<>();
        for (String columnName : this.getColumnNames()) {
            Column<? extends Serializable> column = this.getColumn(columnName);
            dataTypes.put(columnName, column.getType());
        }
        return Collections.unmodifiableMap(dataTypes);
    }

    /**
     * add a column with nulls to the table.
     */
    public Column<? extends Serializable> addColumn(String columnName, DataType dataType) {
        AbstractColumn<? extends Serializable> proposedNewColumn = switch (dataType) {
            case String -> new StringColumn();
            case Integer -> new IntegerColumn();
            case Long -> new LongColumn();
            case Float -> new FloatColumn();
            case Double -> new DoubleColumn();
            case Boolean -> new BooleanColumn();
            default -> throw new RuntimeException("not handled dataType");
        };

        proposedNewColumn.setName(columnName);

        proposedNewColumn.dataType = dataType;

        for (int i = 0; i < nRow; i++) {
            proposedNewColumn.add(null);
        }

        assert proposedNewColumn.size() == this.nRow : "the number of elements in the generated colum needs to be " +
                "equal to the number of elements in the 'nRow' instance variable.";

        this.columns.put(columnName, proposedNewColumn);

        return this.columns.get(columnName);
    }

    /**
     * add a new row to the end of the table
     */
    public int addRow() {
        int nCol = this.nCol();

        List<String> columnNames = this.getColumnNames();

        Assert.assertTrue(
                nCol > 0,
                "cant add a row for a table with '0' columns."
        );
        for (String columnName : columnNames) {
            this.columns.get(columnName).add(null);
        }
        this.nRow++;

        return this.nRow - 1;
    }

    /**
     * remove the specified rowIndex from the table.
     */
    public void removeRow(int index) throws Exception {
        for (String columnName : this.getColumnNames()) {
            AbstractColumn<? extends Serializable> column = this.columns.get(columnName);
            try {
                column.remove(index);
            } catch (Exception e) {
                String message = "was not possible to delete the row index '" + index + "'.";
                logger.error(message);
                throw new Exception(message);
            }
        }
        this.nRow--;
    }

    /**
     * remove the specified column from the table.
     */
    public void removeColumn(String columnName) throws Exception {
        Serializable deletionResult = this.columns.remove(columnName);
        if (deletionResult == null) {
            String message = "was not possible to delete the column '" + columnName + "'.";
            logger.error(message);
            throw new Exception(message);
        }
    }

    /**
     * @param nRows              the maximum number of rows allowed.
     * @param nRowsEstimateWidth the maximum number of rows to estimate the columns width.
     * @param columnWidthType    the column width type estimation.
     */
    public String print(int nRows, int nRowsEstimateWidth, AutoColumnWidthType columnWidthType) {
        int limit = nRows;
        int trueLimit;
        int tableNRow = this.nRow();
        if (tableNRow > limit) {
            trueLimit = limit;
        } else {
            trueLimit = tableNRow;
        }

        TablePrinter tablePrinterClass = new TablePrinter(this);
        tablePrinterClass.estimateColumnsWidth(columnWidthType, nRowsEstimateWidth);
        StringBuilder sb = new StringBuilder();
        tablePrinterClass.printDecorator();
        tablePrinterClass.printColumnNames();
        tablePrinterClass.printDecorator();
        for (int i = 0; i < trueLimit; i++) {
            tablePrinterClass.printRow(i);
        }
        tablePrinterClass.printDecorator();
        sb.append(tablePrinterClass.getText());
        sb.append("showing '" + trueLimit + "' rows of '" + tableNRow + "' x '" + this.getColumns().size() + "' columns.\n");
        return sb.toString();
    }

    public String printAll() {
        int limit = this.nRow();
        return this.print(limit, 300, AutoColumnWidthType.percentile80);
    }

    public String toString() {
        int limit = 100;
        return this.print(limit, 100, AutoColumnWidthType.average);
    }

    public Table clone() {
        try {
            Table clone = (Table) super.clone();
            clone.columns.putAll(this.columns);
            clone.nRow = this.nRow;

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("error while creating a clone of a table.");
        }
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    public Iterator<Column<? extends Serializable>> iterator() {
        TableIterator iterator = new TableIterator(this);
        return iterator;
    }
}
