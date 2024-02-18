package sst.tableTools.tableSorting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import sst.table.Column;
import sst.table.DataType;
import sst.table.Table;
import sst.tableTools.TableTools;
import sst.util.BasicUtil;

import java.io.Serializable;
import java.util.*;

/**
 * class to do the sorting of a table. this class will be capable to sort a table
 * with on one or more sorting columns.
 */
public class TableSorting {
    private static final Logger logger = LoggerFactory.getLogger(TableSorting.class);
    protected final List<SortingRule> rules;
    protected Table table;

    public TableSorting(Table table, List<SortingRule> rules) {
        this.table = table;
        this.rules = rules;
    }

    /**
     * sort the table.
     *
     * @return a copy of the table with the sorted values.
     */
    public Table sort() {
        int rulesSize = this.rules.size();

        SortingRule rule = this.rules.get(0);
        Column<? extends Serializable> column = this.table.getColumn(rule.columnName());
        List<Integer> ordering = this.order(
                column.asList(),
                rule.comparator(),
                column.getType()
        );
        this.table = TableTools.rows(
                this.table,
                ordering.stream()
                        .mapToInt(Integer::intValue)
                        .toArray()
        );

        for (int i = 1; i < rulesSize; i++) {
            SortingRule rule_ = this.rules.get(i);
            SortingRule previousRule = this.rules.get(i - 1);
            if (!this.ruleHasDuplicates(previousRule)) {
                logger.debug("the previous rule executed had no duplicated values " +
                        "so is not needed to continue to sorting on more rules.");
                break;
            }
            int counter = 0;
            while (true) {
                UnsortedGroupResult unsortedGroup = this.getUnsortedGroupMultiple(this.rules.subList(0, i + 1), counter);
                List<Integer> sortedGroup = this.sortGroup(unsortedGroup.result(), rule_);
                this.sortTableByPartialOrder(sortedGroup);
                counter++;
                if (unsortedGroup.ended()) {
                    break;
                }
            }
        }
        return this.table;
    }

    /**
     * takes a list with row order and then order the sort the 'this.table' using the order
     * of the provided list.
     * example:
     * if the provided 'order' list is: "[4,3,7,5]"
     * then the 'this.table' will be sorted by these indexes: "[0,1,2,4,3,7,6,5]"
     * and this allows to order a table without generating all index numbers.
     */
    protected void sortTableByPartialOrder(List<Integer> order) {
        int tableSize = this.table.nRow();
        List<Integer> finalOrder = new ArrayList<>(tableSize);
        List<Integer> orderCopy = new ArrayList<>(order);
        for (int i = 0; i < tableSize; i++) {
            if (order.contains(i)) {
                finalOrder.add(orderCopy.get(0));
                orderCopy.remove(0);
            } else {
                finalOrder.add(i);
            }
        }
        this.table = TableTools.rows(
                this.table,
                finalOrder.stream()
                        .mapToInt(Integer::intValue)
                        .toArray()
        );
    }

    /**
     * sort a list based on the values of the column of a rule.
     * the list are row indexes of the table which wil be sorted base on the information of the rule.
     *
     * @param group the indexes which will be sorted
     * @param rule  the rule to use (will use the column of the rule and comparator).
     */
    protected List<Integer> sortGroup(List<Integer> group, SortingRule rule) {
        int groupSize = group.size();
        Column<? extends Serializable> column = this.table.getColumn(rule.columnName());
        List<Serializable> columnValues = new ArrayList<>(groupSize);
        for (int i = 0; i < groupSize; i++) {
            int index = group.get(i);
            Serializable value = column.get(index);
            columnValues.add(value);
        }
        List<Integer> columnValuesOrder = this.order(
                columnValues,
                rule.comparator(),
                column.getType()
        );
        return this.sortByOrder(group, columnValuesOrder);
    }

    /**
     * check if the column of a rule (column appointed by the {@link SortingRule#columnName() columnName})
     * method of the {@link SortingRule SortingRule} class contains duplicates.
     */
    protected boolean ruleHasDuplicates(SortingRule rule) {
        Column<? extends Serializable> column = this.table.getColumn(rule.columnName());
        int columnSize = column.size();
        for (int i = 1; i < columnSize; i++) {
            Serializable previousValue = column.get(i - 1);
            Serializable currentValue = column.get(i);
            boolean areEqual = this.areEqual(
                    rule.comparator(),
                    previousValue,
                    currentValue,
                    column.getType()
            );
            if (areEqual) {
                return true;
            }
        }
        return false;
    }

    /**
     * get the rows which are allowed to be sorted.
     * without changing the result of the sorting of the previous rule 0 to rules.size() - 1.
     * the logic of how this method works is:
     * "for i = 1; i {@literal <} this.table.size; i++"
     * compare the row of the 'this.table' for i - 1 and i
     * this way i'm be comparing each row of the table
     * to a row be allowed to be sorted the values of all columns of the rules
     * except the last one needs to be equal.
     *
     * @param rules       the rules to be applied.
     *                    remember that the last rule is the rule which you want to
     *                    apply but was not applied yet.
     * @param ignoreCount how much groups to ignore?
     *                    0 is disabled and for example, 1
     *                    mean the first group found will be ignored
     *                    returning the second group then.
     */
    protected UnsortedGroupResult getUnsortedGroupMultiple(List<SortingRule> rules, int ignoreCount) {
        Assert.assertTrue(
                rules.size() > 1
        );
        int rulesSize = rules.size();
        int tableSize = this.table.nRow();
        Set<Integer> temp = new LinkedHashSet<>();
        List<Integer> groupStorage = new ArrayList<>();
        int groupNumber = 1;
        for (int i = 1; i < tableSize; i++) {
            // the indexes which i need to decide if them are equal or not
            List<Integer> indexesInQuestion = List.of(i - 1, i);
            List<Boolean> results = new ArrayList<>(rulesSize);
            for (int j = 0; j < rulesSize; j++) {
                SortingRule rule = rules.get(j);
                Column<? extends Serializable> column = this.table.getColumn(rule.columnName());
                Serializable previousValue = column.get(indexesInQuestion.get(0));
                Serializable currentValue = column.get(indexesInQuestion.get(1));
                boolean equal = this.areEqual(
                        rule.comparator(),
                        previousValue,
                        currentValue,
                        column.getType()
                );
                if (equal) {
                    results.add(true);
                } else {
                    results.add(false);
                }
            }
            int resultsSize = results.size();
            List<Boolean> resultsExceptLast = results.subList(0, resultsSize - 1);
            boolean resultsExceptLastContainsFalse = resultsExceptLast.stream()
                    .anyMatch(x -> x.equals(false));
            if (!resultsExceptLastContainsFalse) {
                temp.addAll(indexesInQuestion);
            } else if (!temp.isEmpty()) {
                groupStorage.addAll(temp);
                if (groupNumber > ignoreCount) {
                    return new UnsortedGroupResult(groupStorage, false);
                } else {
                    groupStorage.clear();
                }
                temp.clear();
                groupNumber++;
            }
        }
        return new UnsortedGroupResult(temp.stream().toList(), true);
    }

    /**
     * checks if two values are equal taking in count their {@link DataType datatype}
     */
    protected boolean areEqual(Comparator<?> comparator, Serializable a, Serializable b, DataType dataType) {
        int comparison = this.compare(comparator, a, b, dataType);
        return comparison == 0;
    }

    /**
     * sort a list based in a index order.
     */
    protected <T> List<T> sortByOrder(List<T> data, List<Integer> order) {
        int dataSize = data.size();
        List<T> storage = new ArrayList<>(dataSize);
        for (int i = 0; i < dataSize; i++) {
            int index = order.get(i);
            T value = data.get(index);
            storage.add(value);
        }
        return storage;
    }

    /**
     * compare two values using the type of the columns which that values belongs to.
     */
    protected int compare(Comparator<?> comparator, Serializable a, Serializable b, DataType dataType) {
        return switch (dataType) {
            case String -> ((Comparator<String>) comparator).compare((String) a, (String) b);
            case Integer -> ((Comparator<Integer>) comparator).compare((Integer) a, (Integer) b);
            case Long -> ((Comparator<Long>) comparator).compare((Long) a, (Long) b);
            case Float -> ((Comparator<Float>) comparator).compare((Float) a, (Float) b);
            case Double -> ((Comparator<Double>) comparator).compare((Double) a, (Double) b);
            case Boolean -> ((Comparator<Boolean>) comparator).compare((Boolean) a, (Boolean) b);
            default -> throw new RuntimeException("unhandled dataType");
        };
    }

    protected List<Integer> order(List<? extends Serializable> data, Comparator<? extends Serializable> comparator, DataType dataType) {
        return switch (dataType) {
            case String -> BasicUtil.order(
                    (List<String>) data,
                    (Comparator<String>) comparator
            );

            case Integer -> BasicUtil.order(
                    (List<Integer>) data,
                    (Comparator<Integer>) comparator
            );

            case Long -> BasicUtil.order(
                    (List<Long>) data,
                    (Comparator<Long>) comparator
            );

            case Float -> BasicUtil.order(
                    (List<Float>) data,
                    (Comparator<Float>) comparator
            );

            case Double -> BasicUtil.order(
                    (List<Double>) data,
                    (Comparator<Double>) comparator
            );

            case Boolean -> BasicUtil.order(
                    (List<Boolean>) data,
                    (Comparator<Boolean>) comparator
            );

            default -> throw new RuntimeException("unhandled dataType");
        };
    }

}
