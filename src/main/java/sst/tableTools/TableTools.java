package sst.tableTools;

import org.testng.Assert;
import sst.table.Column;
import sst.table.DataType;
import sst.table.Table;
import sst.tableTools.tableConcatenation.ConcatenationType;
import sst.tableTools.tableConcatenation.TableConcatenation;
import sst.tableTools.tableSorting.SortingRule;
import sst.tableTools.tableSorting.TableSorting;

import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;

/**
 * class contains utilities to work with a table.
 */
public abstract class TableTools {

    /**
     * add a column to the table filling the rows of the created column with the supplied data.
     *
     * @apiNote in my mind i believe this should to be located in the 'Table' class, but i also believe
     * the 'Table' class need to be simple. so i don't know if someday i'll move this functionality to the
     * 'Table' class.
     */
    public static void addColumnWithData(Table table, String columnName, DataType dataType, List<? extends Serializable> data) {
        int tableNRow = table.nRow();
        int tableNCol = table.nCol();

        if (tableNCol == 0) {
            int dataSize = data.size();
            table.addColumn(columnName, dataType);
            for (int i = 0; i < dataSize; i++) {
                table.addRow();
            }
            tableNRow = table.nRow();
        }

        Assert.assertEquals(
                data.size(),
                tableNRow,
                "the number of elements in the provided 'data' need to be equal to the " +
                        "number of rows in the table '" + tableNRow + "'."
        );

        Column<? extends Serializable> addedColumn = table.addColumn(columnName, dataType);

        for (int i = 0; i < tableNRow; i++) {
            addedColumn.set(i, data.get(i), addedColumn.getType());
        }
    }

    /**
     * concatenate columns or rows.
     * if columns is chosen so will append the columns of 'giver' to the 'receiver' table if the columns
     * is not already present in the 'receiver' table.
     */
    public static void concatenate(Table receiver, Table giver, ConcatenationType concatenationType) {
        TableConcatenation tableConcatenationClass = new TableConcatenation(receiver, giver);

        switch (concatenationType) {
            case column -> tableConcatenationClass.appendColumns();
            case row -> tableConcatenationClass.appendRows();
            default -> throw new RuntimeException("unhandled concatenation type");
        }
    }

    /**
     * do a copy of a table but with 0 rows and the columns defined.
     *
     * @return a new table which contains 0 rows and the columns of the supplied.
     */
    public static Table emptyCopy(Table table) {
        Table newTable = new Table();
        for (Column<? extends Serializable> column : table) {
            String columnName = column.getName();
            DataType columnDataType = column.getType();
            newTable.addColumn(columnName, columnDataType);
        }
        return newTable;
    }

    /**
     * filter a table based on a condition
     *
     * @return a new table with same columns with rows filtered.
     * @implNote i really did want this method were mutable, but i need to return a new table
     * because the 'removeRow' method of ArrayList is very, very slow.
     * is a lot faster (very a lot) to create a new Table and add the columns and data
     * than using the 'removeRow' method. the removeRow method from 'Column' interface is slow
     * because i've implemented columns with ArrayList and i inherit their problems.
     * usage example of this method:
     * <pre>
     * {@code
     * filter(myTable, "col1", DataType.Integer, (Predicate<String>) (x) -> x >= 16 );
     * }
     * </pre>
     */
    public static Table filter(Table table, String columnName, DataType dataType, Predicate<? extends Serializable> filter) {
        Column<? extends Serializable> column = table.getColumn(columnName);
        int columnSize = column.size();
        DataType columnType = column.getType();
        Assert.assertEquals(
                columnType,
                dataType,
                "column of wrong dataType, you have supplied the dataType '" + dataType + "' but the " +
                        "column is of the type '" + columnType + "'."
        );

        Table newTable = new Table();

        for (Column<? extends Serializable> tableColumn : table) {
            String tableColumnName = tableColumn.getName();
            DataType tableColumnDataType = tableColumn.getType();
            newTable.addColumn(tableColumnName, tableColumnDataType);
        }

        for (int i = 0; i < columnSize; i++) {
            Serializable value = column.get(i);
            Boolean matchCondition = switch (dataType) {
                case String -> ((Predicate<String>) filter).test((String) value);
                case Integer -> ((Predicate<Integer>) filter).test((Integer) value);
                case Long -> ((Predicate<Long>) filter).test((Long) value);
                case Float -> ((Predicate<Float>) filter).test((Float) value);
                case Double -> ((Predicate<Double>) filter).test((Double) value);
                case Boolean -> ((Predicate<Boolean>) filter).test((Boolean) value);
                default -> throw new RuntimeException("unhandled datatype.");
            };

            // if the condition is true then add a new row in the 'newTable' and set the values of all columns
            // of 'table' for that row.
            if (matchCondition) {
                int newRowIndex = newTable.addRow();
                for (Column<? extends Serializable> tableColumn : table) {
                    String tableColumnName = tableColumn.getName();
                    DataType tableColumnDataType = tableColumn.getType();
                    Column<? extends Serializable> newTableColumn = newTable.getColumn(tableColumnName);
                    newTableColumn.set(newRowIndex, tableColumn.get(i), newTableColumn.getType());
                }
            }
        }

        return newTable;
    }

    /**
     * return a subset of the table with only the selected row indexes.
     */
    public static Table rows(Table table, int... rowIndexes) {
        int tableSize = table.nRow();
        for (int rowIndex : rowIndexes) {
            Assert.assertTrue(
                    rowIndex <= tableSize - 1,
                    "the supplied index '" + rowIndex + "' is not present in the table which has nRow = '" + tableSize + "'."
            );
        }
        Table newTable = emptyCopy(table);
        for (int rowIndex : rowIndexes) {
            int addedRowIndex = newTable.addRow();
            for (Column<? extends Serializable> column : table) {
                String columnName = column.getName();
                DataType columnType = column.getType();
                Serializable value = column.get(rowIndex);
                newTable.getColumn(columnName).set(addedRowIndex, value, columnType);
            }
        }
        return newTable;
    }

    /**
     * return a subset of the table with only the selected columns.
     */
    public static Table columns(Table table, String... columnNames) {
        Table newTable = new Table();
        for (String columnName : columnNames) {
            Column<? extends Serializable> column = table.getColumn(columnName);
            DataType columnType = column.getType();
            if (newTable.nRow() == 0) {
                Column<?> addedColumn = newTable.addColumn(columnName, columnType);
                int columnSize = column.size();
                for (int i = 0; i < columnSize; i++) {
                    int addedRowIndex = newTable.addRow();
                    Serializable value = column.get(i);
                    addedColumn.set(addedRowIndex, value, columnType);
                }
            } else {
                addColumnWithData(newTable, columnName, columnType, column.asList());
            }
        }
        return newTable;
    }

    /**
     * sort the table based on {@link SortingRule rules}.
     *
     * @param rules the rules.
     * @return a copy of the provided table however sorted.
     */
    public static Table sort(Table table, List<SortingRule> rules) {
        TableSorting ts = new TableSorting(table, rules);
        return ts.sort();
    }
}
