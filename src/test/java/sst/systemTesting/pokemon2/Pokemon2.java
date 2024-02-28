package sst.systemTesting.pokemon2;

import org.apache.commons.csv.CSVFormat;
import org.testng.Assert;
import org.testng.annotations.Test;
import sst.importing.Importing;
import sst.table.DataType;
import sst.table.Table;
import sst.tableTools.TableTools;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

public class Pokemon2 {
    private Table table;

    @Test( groups = "0" )
    public void test_readCsv() throws IOException {
        String csvString = new String(
                ClassLoader.getSystemResourceAsStream( "pokemonItems.csv" ).readAllBytes()
        );
        Table imported = Importing.csv( csvString, CSVFormat.DEFAULT, List.of( DataType.String, DataType.String, DataType.String ) );
        Assert.assertEquals(
                imported.nRow(),
                1473
        );
        this.table = imported;
    }

    @Test( dependsOnGroups = {"0"} )
    public void test_filtering() {
        Table filtered = TableTools.filter(
                this.table,
                "Effect",
                DataType.String,
                (Predicate<String>) x -> x.contains( "any supereffective" )
        );
        List<String> expected = List.of( "Enigma Berry", "Berries", "If held by a Pokémon, it restores its HP if it is hit by any supereffective attack." );
        Assert.assertEquals(
                filtered.nRow(),
                1
        );
        int index = 0;
        for ( int i = 0; i < filtered.nCol(); i++ ) {
            String columnName = filtered.getColumnNames().get( i );
            String value = filtered.getColumn( columnName ).get( index ).toString();
            String expectedValue = expected.get( i );
            Assert.assertEquals(
                    value,
                    expectedValue
            );
        }
    }
}
