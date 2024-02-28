package sst.tableTools.tableSorting;

import java.io.Serializable;
import java.util.Comparator;

public record SortingRule( String columnName, Comparator<? extends Serializable> comparator ) {
}
