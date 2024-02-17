package sst.systemTesting.small;

import org.testng.Assert;
import org.testng.annotations.Test;
import sst.table.DataType;
import sst.table.Table;
import sst.tableTools.TableTools;
import sst.tableTools.tableConcatenation.ConcatenationType;

public class Small {
    private Table table;

    @Test(groups = "0")
    public void test_tableCreation() {
        Table newTable = new Table();
        newTable.addColumn("primes", DataType.Integer);
        newTable.addColumn("even", DataType.Boolean);

        int addedRow = newTable.addRow();

        newTable.getColumn("primes").setInteger(addedRow, 2);
        newTable.getColumn("even").setBoolean(addedRow, true);

        int addedRow2 = newTable.addRow();

        newTable.getColumn("primes").setInteger(addedRow2, 3);
        newTable.getColumn("even").setBoolean(addedRow2, false);

        int addedRow3 = newTable.addRow();

        newTable.getColumn("primes").setInteger(addedRow3, 5);
        newTable.getColumn("even").setBoolean(addedRow3, false);

        int addedRow4 = newTable.addRow();

        newTable.getColumn("primes").setInteger(addedRow4, 7);
        newTable.getColumn("even").setBoolean(addedRow4, false);

        String expected =
                """
                        +--------+-------+
                        | primes | even  |
                        +--------+-------+
                        | 2      | true  |
                        | 3      | false |
                        | 5      | false |
                        | 7      | false |
                        +--------+-------+
                        showing '4' rows of '4' x '2' columns.
                        """;

        Assert.assertEquals(
                newTable.toString(),
                expected
        );

        this.table = newTable;
    }

    /**
     * check the {@link TableTools#concatenate(Table, Table, ConcatenationType) concatenate} method.
     */
    @Test(dependsOnGroups = {"0"})
    public void concatenation() {
        Table table1 = TableTools.rows(this.table, 0, 1);

        Table table2 = TableTools.rows(this.table, 2, 3);

        Assert.assertEquals(table1.nRow(), 2);

        Assert.assertEquals(table2.nRow(), 2);

        TableTools.concatenate(table1, table2, ConcatenationType.row);

        Assert.assertEquals(
                table1.nRow(),
                4
        );

        String expected =
                """
                        +--------+-------+
                        | primes | even  |
                        +--------+-------+
                        | 2      | true  |
                        | 3      | false |
                        | 5      | false |
                        | 7      | false |
                        +--------+-------+
                        showing '4' rows of '4' x '2' columns.
                        """;

        Assert.assertEquals(
                table1.toString(),
                expected
        );
    }
}
