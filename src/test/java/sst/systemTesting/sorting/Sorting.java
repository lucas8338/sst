package sst.systemTesting.sorting;

import com.esotericsoftware.yamlbeans.YamlReader;
import org.apache.commons.csv.CSVFormat;
import org.testng.Assert;
import org.testng.annotations.Test;
import sst.importing.Importing;
import sst.table.DataType;
import sst.table.Table;
import sst.tableTools.TableTools;
import sst.tableTools.tableSorting.SortingRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * test the capacity of sort a table
 */
public class Sorting {
    private Table table;

    private ReadData yamlDocument;

    private Table answer;

    private List<SortingRule> rules;

    @Test(groups = {"0"})
    public void test_readDatas() throws IOException {
        String readFile = new String(ClassLoader.getSystemResourceAsStream("sortingTable.yaml").readAllBytes());
        YamlReader yamlReader = new YamlReader(readFile);
        ReadData yamlDocument = yamlReader.read(ReadData.class);
        this.yamlDocument = yamlDocument;
        Table data = Importing.csv(
                yamlDocument.data,
                CSVFormat.DEFAULT,
                List.of(DataType.Integer, DataType.Integer, DataType.Integer, DataType.Integer, DataType.Integer)
        );
        this.table = data;
        Table answer = Importing.csv(
                yamlDocument.result,
                CSVFormat.DEFAULT,
                List.of(DataType.Integer, DataType.Integer, DataType.Integer, DataType.Integer, DataType.Integer)
        );
        this.answer = answer;
    }

    @Test(groups = {"1"}, dependsOnGroups = {"0"})
    public void test_definingRules() {
        List<SortingRule> rules = new ArrayList<>();
        SortingRule rule1 = new SortingRule("col1", Comparator.comparingInt(Integer::intValue));
        SortingRule rule2 = new SortingRule("col2", Comparator.comparingInt(Integer::intValue));
        SortingRule rule3 = new SortingRule("col3", Comparator.comparingInt(Integer::intValue));
        SortingRule rule4 = new SortingRule("col4", Comparator.comparingInt(Integer::intValue));
        rules.add(rule1);
        rules.add(rule2);
        rules.add(rule3);
        rules.add(rule4);
        this.rules = rules;
        Assert.assertEquals(this.rules.size(), 4);
    }

    @Test(dependsOnGroups = {"1"})
    public void test_sortCol1() {
        Table sorted = TableTools.sort(this.table, List.of(this.rules.get(0)));
        Assert.assertEquals(
                sorted.getColumn("index").asIntegerColumn().asList(),
                this.yamlDocument.sortedCol1
        );
    }

    @Test(dependsOnGroups = {"1"})
    public void test_sortCol2() {
        Table sorted = TableTools.sort(this.table, List.of(this.rules.get(1)));
        Assert.assertEquals(
                sorted.getColumn("index").asIntegerColumn().asList(),
                this.yamlDocument.sortedCol2
        );
    }

    @Test(dependsOnGroups = {"1"})
    public void test_sortCol1Col2() {
        Table sorted = TableTools.sort(this.table, List.of(this.rules.get(0), this.rules.get(1)));
        Assert.assertEquals(
                sorted.getColumn("index").asIntegerColumn().asList(),
                this.yamlDocument.sortedCol1Col2
        );
    }

    /**
     * test if is capable to sort on three columns once.
     * but will pass 4 rules, the last one needs to be ignored.
     */
    @Test(dependsOnGroups = {"1"})
    public void test_sortAll() {
        Table sorted = TableTools.sort(
                this.table,
                this.rules
        );
        Assert.assertEquals(
                sorted.printAll(),
                this.answer.printAll()
        );
    }
}
