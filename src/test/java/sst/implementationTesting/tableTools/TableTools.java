package sst.implementationTesting.tableTools;

import org.testng.Assert;
import org.testng.annotations.Test;
import sst.data.Tables;
import sst.table.DataType;
import sst.table.Table;

import java.util.Arrays;
import java.util.List;

public class TableTools {
    @Test
    public void test_moveColumn() throws Exception {
        Table table = Tables.simpleIntegerTable();

        sst.tableTools.TableTools.moveColumn(
                table,
                "col4",
                "col1"
        );

        Assert.assertEquals(
                table.getColumnNames(),
                List.of( "col1", "col4", "col2", "col3", "col5" )
        );

        Assert.assertEquals(
                table.getColumn( "col1" ).asIntegerColumn().asList(),
                List.of( 1, 2, 3 )
        );

        Assert.assertEquals(
                table.getColumn( "col4" ).asIntegerColumn().asList(),
                List.of( 10, 11, 12 )
        );

        Assert.assertEquals(
                table.getColumn( "col2" ).asIntegerColumn().asList(),
                List.of( 4, 5, 6 )
        );

        sst.tableTools.TableTools.moveColumn(
                table,
                "col5",
                ""
        );

        Assert.assertEquals(
                table.getColumnNames(),
                List.of( "col5", "col1", "col4", "col2", "col3" )
        );

        Assert.assertEquals(
                table.getColumn( "col5" ).asIntegerColumn().asList(),
                List.of( 13, 14, 15 )
        );
    }

    @Test
    public void test_addColumnWithDataAt() throws Exception {
        Table table = Tables.simpleIntegerTable();

        sst.tableTools.TableTools.addColumnWithDataAt(
                table,
                "col0",
                DataType.Integer,
                List.of( - 2, - 1, 0 ),
                ""
        );

        Assert.assertEquals(
                table.getColumnNames(),
                List.of( "col0", "col1", "col2", "col3", "col4", "col5" )
        );

        Assert.assertEquals(
                table.getColumn( "col0" ).asIntegerColumn().asList(),
                List.of( - 2, - 1, 0 )
        );

        sst.tableTools.TableTools.addColumnWithDataAt(
                table,
                "col6",
                DataType.Integer,
                List.of( 16, 17, 18 ),
                "col5"
        );

        Assert.assertEquals(
                table.getColumnNames(),
                List.of( "col0", "col1", "col2", "col3", "col4", "col5", "col6" )
        );

        sst.tableTools.TableTools.addColumnWithDataAt(
                table,
                "col4.5",
                DataType.Double,
                List.of( 12.3, 12.6, 12.9 ),
                "col4"
        );

        Assert.assertEquals(
                table.getColumnNames(),
                List.of( "col0", "col1", "col2", "col3", "col4", "col4.5", "col5", "col6" )
        );
    }

    @Test
    public void addColumnAt() throws Exception {
        Table table = Tables.simpleIntegerTable();

        sst.tableTools.TableTools.addColumnAt(
                table,
                "col2.5",
                DataType.Integer,
                "col2"
        );

        Assert.assertEquals(
                table.getColumnNames(),
                List.of( "col1", "col2", "col2.5", "col3", "col4", "col5" )
        );

        Assert.assertEquals(
                table.getColumn( "col2.5" ).asIntegerColumn().asList(),
                Arrays.asList( null, null, null )
        );

        sst.tableTools.TableTools.addColumnAt(
                table,
                "col0",
                DataType.Integer,
                ""
        );

        Assert.assertEquals(
                table.getColumnNames(),
                List.of( "col0", "col1", "col2", "col2.5", "col3", "col4", "col5" )
        );

        Assert.assertEquals(
                table.getColumn( "col0" ).asIntegerColumn().asList(),
                Arrays.asList( null, null, null )
        );
    }
}
