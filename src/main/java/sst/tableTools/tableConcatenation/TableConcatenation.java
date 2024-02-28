package sst.tableTools.tableConcatenation;

import org.testng.Assert;
import sst.table.Column;
import sst.table.DataType;
import sst.table.Table;
import sst.tableTools.TableTools;

import java.io.Serializable;
import java.util.List;

public class TableConcatenation {
    protected final Table receiver;
    protected final Table giver;

    public TableConcatenation( Table receiver, Table giver ) {
        this.receiver = receiver;
        this.giver = giver;
    }

    /**
     * append all columns in the 'giver' table to the 'receiver' table.
     */
    public void appendColumns() throws Exception {
        int receiverNRows = this.receiver.nRow();
        int giverNRows = this.giver.nRow();

        Assert.assertEquals(
                receiverNRows,
                giverNRows,
                "to append columns the number of rows of both tables needs to be equal."
        );

        for ( Column<? extends Serializable> column : this.giver ) {
            String columnName = column.getName();
            if ( ! this.receiver.getColumnNames().contains( columnName ) ) {
                TableTools.addColumnWithData( this.receiver, columnName, column.getType(), column.asList() );
            }
        }
    }

    public void appendRows() {
        List<String> receiverColumnNames = this.receiver.getColumnNames();
        List<String> giverColumnNames = this.giver.getColumnNames();

        Assert.assertTrue(
                giverColumnNames.containsAll( receiverColumnNames ),
                "the giver table needs to contains all columns existing in the receiver table."
        );

        for ( Column<? extends Serializable> column : this.giver ) {
            String giverColumnName = column.getName();
            Assert.assertEquals(
                    column.getType(),
                    this.receiver.getColumn( giverColumnName ).getType(),
                    "the type of both columns in the giver and the receiver needs to be the same."
            );
        }

        int giverNRows = this.giver.nRow();

        for ( int i = 0; i < giverNRows; i++ ) {
            int addedRowIndex = this.receiver.addRow();
            for ( Column<? extends Serializable> column : this.giver ) {
                String columnName = column.getName();
                DataType columnType = column.getType();
                Serializable value = column.get( i );
                Column<? extends Serializable> receiverColumn = this.receiver.getColumn( columnName );
                receiverColumn.set( addedRowIndex, value, receiverColumn.getType() );
            }
        }

    }
}
