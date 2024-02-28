package sst.systemTesting.simpleLongTypeTest;

import org.testng.Assert;
import org.testng.annotations.Test;
import sst.table.DataType;
import sst.table.Table;
import sst.tableTools.TableTools;
import sst.tableTools.tableSorting.SortingRule;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class SimpleLongTypeTest {
    private Table table;

    @Test( groups = {"0"} )
    public void test_creation() throws Exception {
        Table table = new Table();
        TableTools.addColumnWithData(
                table,
                "col1",
                DataType.Long,
                List.of( 5L, 4L, 3L, 2L, 1L )
        );
        this.table = table;
    }

    @Test( groups = {"1"}, dependsOnGroups = {"0"} )
    public void test_sorting() {
        SortingRule rule = new SortingRule( "col1", Comparator.comparingLong( Long::longValue ) );
        Table sorted = TableTools.sort(
                this.table,
                List.of( rule )
        );
        Assert.assertEquals(
                sorted.getColumn( "col1" ).asLongColumn().asList(),
                List.of( 1L, 2L, 3L, 4L, 5L )
        );
    }

    @Test( dependsOnGroups = {"0"} )
    public void test_filtering() {
        Table filtered = TableTools.filter(
                this.table,
                "col1",
                DataType.Long,
                (Predicate<Long>) x -> x >= 3
        );
        Assert.assertEquals(
                filtered.getColumn( "col1" ).asLongColumn().asList(),
                List.of( 5L, 4L, 3L )
        );
    }
}
