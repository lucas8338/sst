package sst.systemTesting.renamingColumns;

import org.apache.commons.csv.CSVFormat;
import org.testng.Assert;
import org.testng.annotations.Test;
import sst.importing.Importing;
import sst.table.DataType;
import sst.table.IntegerColumn;
import sst.table.Table;

import java.io.IOException;
import java.util.List;

/**
 * class to test the new method which allow to rename columns of a table.
 */
public class RenamingColumns {
    private final List<DataType> columnTypes = List.of(
            DataType.Integer,
            DataType.String,
            DataType.String,
            DataType.String,
            DataType.Integer,
            DataType.Integer,
            DataType.Integer,
            DataType.Integer,
            DataType.Integer,
            DataType.Integer,
            DataType.Integer,
            DataType.Integer,
            DataType.Boolean
    );
    protected Table table;
    protected List<String> originalColumnNames;

    @Test( groups = {"0"} )
    public void test_loadTable() throws IOException {
        String loadedString = new String( ClassLoader.getSystemResourceAsStream( "pokemonList.csv" ).readAllBytes() );

        Table loadedTable = Importing.csv(
                loadedString,
                CSVFormat.DEFAULT,
                this.columnTypes
        );

        this.table = loadedTable;
        this.originalColumnNames = this.table.getColumnNames();
    }

    @Test( groups = {"1"}, dependsOnGroups = {"0"} )
    public void test_rename() {
        IntegerColumn originalFirstColumn = this.table.getColumn( "#" ).asIntegerColumn();
        this.table.renameColumn( "#", "id" );
        Assert.assertEquals(
                this.table.getColumn( "id" ).asIntegerColumn().asList(),
                originalFirstColumn.asList(),
                "the values of the modified column is not the same."
        );

        Assert.assertEquals(
                this.table.getColumnNames().subList( 1, this.table.getColumnNames().size() ),
                this.originalColumnNames.subList( 1, this.originalColumnNames.size() ),
                "the names of the index 1+ needs to keep the same."
        );

        Assert.assertEquals(
                this.table.getColumn( "id" ).getName(),
                "id"
        );
    }

    @Test( groups = {"2"}, dependsOnGroups = {"1"} )
    public void test_rename2() {
        IntegerColumn hpColumn = this.table.getColumn( "HP" ).asIntegerColumn();

        this.table.renameColumn( "HP", "health points" );

        Assert.assertEquals(
                this.table.getColumn( "health points" ).asIntegerColumn().asList(),
                hpColumn.asList(),
                "the renamed and the saved column are not the same in values."
        );

        Assert.assertEquals(
                this.table.getColumnNames(),
                List.of( "id", "Name", "Type 1", "Type 2", "Total", "health points", "Attack", "Defense", "Sp. Atk", "Sp. Def", "Speed", "Generation", "Legendary" ),
                "the name of the table and the names which should not be touched do not match " +
                        "so this mean some column name which should not be changed was modified."
        );
    }
}
