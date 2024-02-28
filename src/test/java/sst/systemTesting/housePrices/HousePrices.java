package sst.systemTesting.housePrices;

import org.apache.commons.csv.CSVFormat;
import org.testng.Assert;
import org.testng.annotations.Test;
import sst.importing.Importing;
import sst.table.DataType;
import sst.table.Table;
import sst.tableTools.TableTools;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class HousePrices {
    private Table table;

    @Test( groups = "0" )
    public void test_read() throws IOException {
        String readedCsv = new String(
                ClassLoader.getSystemResourceAsStream( "usHousePrices.csv" ).readAllBytes()
        );

        Table imported = Importing.csv(
                readedCsv,
                CSVFormat.DEFAULT,
                List.of(
                        DataType.String,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double,
                        DataType.Double
                )
        );

        Assert.assertEquals(
                imported.nRow(),
                348
        );

        Assert.assertEquals(
                imported.nCol(),
                24
        );

        // the expected last row of the read data
        List<Serializable> expected = List.of( "2015-12-01", 157.22, 240.54, 218.0, 217.9, 174.34, 210.27, 206.05, 176.54, 125.72, 129.84, 182.82, 103.77, 146.94, 135.4, 145.31, 180.48, 108.72, 190.19, 156.49, 186.64, 197.21, 182.75, 175.65 );
        int index = 347;

        for ( int i = 0; i < imported.nCol(); i++ ) {
            String columnName = imported.getColumnNames().get( i );
            Serializable value = imported.getColumn( columnName ).get( index );
            Serializable expectedValue = expected.get( i );
            Assert.assertEquals(
                    value,
                    expectedValue
            );
        }

        this.table = imported;
    }

    @Test( dependsOnGroups = {"0"} )
    public void test_columnsSelection() throws Exception {
        String[] columnsToSelect = {"Date", "FL-Miami"};

        Table columnsSelected = TableTools.columns( this.table, columnsToSelect );
        Assert.assertEquals(
                columnsSelected.nRow(),
                this.table.nRow()
        );

        Assert.assertEquals(
                columnsSelected.getColumnNames().toArray( String[]::new ),
                columnsToSelect
        );
    }

    @Test( dependsOnGroups = {"0"} )
    public void test_rowSelection() {
        int[] rowsToSelect = {0, 1, 3, 5, 7};
        Table rowsSelected = TableTools.rows( this.table, rowsToSelect );

        List<List<Serializable>> expected = List.of(
                Arrays.asList( "1987-01-01", null, 59.33, 54.67, 46.61, 50.2, 64.11, 68.5, 77.33, null, 53.55, 70.04, null, null, 63.39, 66.36, 74.42, 53.53, 41.05, null, null, 62.82, null, 63.75 ),
                Arrays.asList( "1987-02-01", null, 59.65, 54.89, 46.87, 49.96, 64.77, 68.76, 77.93, null, 54.64, 70.08, null, null, 63.94, 67.03, 75.43, 53.5, 41.28, null, null, 63.39, null, 64.15 ),
                Arrays.asList( "1987-04-01", null, 60.81, 55.85, 47.69, 50.55, 66.4, 69.2, 77.56, null, 54.88, 70.7, null, null, 64.81, 67.88, 77.34, 53.75, 40.96, null, null, 64.57, null, 64.99 ),
                Arrays.asList( "1987-06-01", null, 62.71, 56.86, 48.83, 50.5, 68.7, 69.31, 78.71, null, 56.39, 72.32, null, null, 65.55, 66.48, 80.84, 55.12, 41.45, null, null, 66.59, null, 66.24 ),
                Arrays.asList( "1987-08-01", null, 64.56, 57.69, 49.94, 50.38, 70.62, 70.16, 79.14, null, 58.37, 73.79, null, null, 66.08, 65.14, 83.01, 56.1, 41.9, null, null, 68.25, null, 67.29 )
        );

        Assert.assertEquals(
                rowsSelected.nRow(),
                rowsToSelect.length
        );

        Assert.assertEquals(
                rowsSelected.nCol(),
                this.table.nCol()
        );

        for ( int i = 0; i < rowsSelected.nCol(); i++ ) {
            String columnName = rowsSelected.getColumnNames().get( i );
            for ( int j = 0; j < rowsToSelect.length; j++ ) {
                Serializable value = rowsSelected.getColumn( columnName ).get( j );
                Serializable expectedValue = expected.get( j ).get( i );
                Assert.assertEquals(
                        value,
                        expectedValue,
                        String.format(
                                """
                                        error:
                                            'i' value = %d
                                            'j' value = %d
                                        """,
                                i,
                                j
                        )
                );
            }
        }

    }

}
