package sst.data;

import sst.table.DataType;
import sst.table.Table;

import java.util.List;

/**
 * package store tables to be used across the tests.
 * this class uses methods instead variables because when using methods
 * each return will be a new variable. this way there no risk of a method to change
 * the table values. for example.
 */
public abstract class Tables {
    public static Table simpleIntegerTable() throws Exception {
        Table table = new Table();
        sst.tableTools.TableTools.addColumnWithData(
                table,
                "col1",
                DataType.Integer,
                List.of( 1, 2, 3 )
        );

        sst.tableTools.TableTools.addColumnWithData(
                table,
                "col2",
                DataType.Integer,
                List.of( 4, 5, 6 )
        );

        sst.tableTools.TableTools.addColumnWithData(
                table,
                "col3",
                DataType.Integer,
                List.of( 7, 8, 9 )
        );

        sst.tableTools.TableTools.addColumnWithData(
                table,
                "col4",
                DataType.Integer,
                List.of( 10, 11, 12 )
        );

        sst.tableTools.TableTools.addColumnWithData(
                table,
                "col5",
                DataType.Integer,
                List.of( 13, 14, 15 )
        );

        return table;
    }
}
