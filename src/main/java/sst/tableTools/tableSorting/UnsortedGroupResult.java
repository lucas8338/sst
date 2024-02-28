package sst.tableTools.tableSorting;

import java.util.List;

/**
 * @param result
 *         the unsorted group
 * @param ended
 *         if all data in the rule or column was checked.
 *         with this information you should to stop of calling this method.
 */
public record UnsortedGroupResult( List<Integer> result, boolean ended ) {
}
