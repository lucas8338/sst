package sst.systemTesting.pokemon1;

import org.apache.commons.csv.CSVFormat;
import org.testng.Assert;
import org.testng.annotations.Test;
import sst.importing.Importing;
import sst.table.DataType;
import sst.table.Table;
import sst.tableTools.TableTools;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;

public class Pokemon1 {
    private final String pokemonString = new String(ClassLoader.getSystemResourceAsStream("pokemonList.csv").readAllBytes());
    private final PrintStream defaultStdOutPrintStream = System.out;
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
    private Table pokemonTable;

    public Pokemon1() throws IOException {
    }

    /**
     * test if it can read a csv and check for some rows if them are right
     */
    @Test(groups = "0")
    public void test_readCsv() throws IOException {
        this.pokemonTable = Importing.csv(this.pokemonString, CSVFormat.DEFAULT, this.columnTypes);
        int index = 10;
        List<Serializable> rowValues = List.of(8, "Wartortle", "Water", "", 405, 59, 63, 80, 65, 80, 58, 1, false);
        int pokemonTableColumnSize = this.pokemonTable.getColumnNames().size();
        Assert.assertEquals(pokemonTableColumnSize, rowValues.size());
        for (int i = 0; i < pokemonTableColumnSize; i++) {
            String columnName = this.pokemonTable.getColumnNames().get(i);
            Serializable value = this.pokemonTable.getColumn(columnName).get(index);
            Assert.assertEquals(
                    value,
                    rowValues.get(i),
                    "value in read row is different than should to be."
            );
        }

        int lastIndex = this.pokemonTable.nRow() - 1;
        List<Serializable> rowValuesLast = List.of(721, "Volcanion", "Fire", "Water", 600, 80, 110, 120, 130, 90, 70, 6, true);
        for (int i = 0; i < pokemonTableColumnSize; i++) {
            String columnName = this.pokemonTable.getColumnNames().get(i);
            Serializable value = this.pokemonTable.getColumn(columnName).get(lastIndex);
            Assert.assertEquals(
                    value,
                    rowValuesLast.get(i),
                    "value in read row is different than should to be."
            );
        }
    }

    @Test(dependsOnGroups = {"0"})
    public void test_printAll() throws IOException {
        String fullPrint = this.pokemonTable.printAll();
        String fullPrintTest = new String(
                ClassLoader.getSystemResourceAsStream("pokemonListExpectedPrintAll.txt").readAllBytes()
        );
        Assert.assertEquals(
                fullPrint,
                fullPrintTest
        );
    }

    /**
     * test the capacity to filter some rows
     */
    @Test(dependsOnGroups = {"0"})
    public void test_filtering() throws IOException {
        Table filteredGeneration = TableTools.filter(this.pokemonTable, "Generation", DataType.Integer, (Predicate<Integer>) x -> x <= 6);
        Assert.assertEquals(
                filteredGeneration.getColumn("Generation").asIntegerColumn().asList().stream().filter(x -> x > 6).count(),
                0
        );

        Table filteredType1 = TableTools.filter(filteredGeneration, "Type 1", DataType.String, (Predicate<String>) x -> x.equals("Normal"));
        Assert.assertEquals(
                filteredType1.getColumn("Type 1").asStringColumn().asList().stream().filter(x -> x.equals("Normal")).count(),
                filteredType1.nRow()
        );

        Table filteredLegendary = TableTools.filter(filteredType1, "Legendary", DataType.Boolean, (Predicate<Boolean>) x -> !x);
        Assert.assertEquals(
                filteredType1.getColumn("Legendary").asBooleanColumn().asList().stream().filter(x -> !x).count(),
                filteredLegendary.nRow()
        );

        Table filteredAttack = TableTools.filter(filteredLegendary, "Attack", DataType.Integer, (Predicate<Integer>) x -> x >= 130);
        List<Serializable> rowValues = List.of(217, "Ursaring", "Normal", "", 500, 90, 130, 75, 75, 75, 55, 2, false);
        int rowIndex = 0;
        for (int i = 0; i < filteredAttack.nCol(); i++) {
            Assert.assertEquals(
                    filteredAttack.getColumn(filteredAttack.getColumnNames().get(i)).get(rowIndex),
                    rowValues.get(i)
            );
        }

        String correct = new String(
                ClassLoader.getSystemResourceAsStream("pokemonListExpectedPrintFiltered.txt").readAllBytes()
        );

        Assert.assertEquals(
                filteredAttack.toString(),
                correct
        );
    }

    protected void captureStdOut() {
        StringBuilder storage = new StringBuilder();
        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                storage.append(b);
            }
        };
        PrintStream printStream = new PrintStream(outputStream, true);
        System.setOut(printStream);
    }

    protected void restoreStdOut() {

    }
}
