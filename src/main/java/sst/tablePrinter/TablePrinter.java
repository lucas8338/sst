package sst.tablePrinter;

import sst.table.Column;
import sst.table.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * class to print a table.
 */
public class TablePrinter {

    /**
     * how many spaces to have between the left delimiter of a column and the text.
     */
    protected static final int spaceAfter = 1;
    /**
     * how many spaces to have between the right delimiter of a column and the text.
     */
    protected static final int spaceBefore = 1;

    /*
     * bellow a couple of values defining characters, and strings which will define
     * the visual of the generated print text (spacers, lines etc.)
     * */

    protected static final char horizontalLine = '-';
    protected static final char verticalLine = '|';
    protected static final char horizontalContinuationChar = '…';
    protected static final char verticalContinuationChar = '⋮';

    /**
     * the text which will be used to replace 'null' values.
     * (this is required because 'null' is a primitive and dont have 'toString()' method).
     */
    protected static final String nullReplacement = "NULL";
    protected static final char lineCrossingChar = '+';

    protected final Table table;
    protected final StringBuilder text = new StringBuilder();
    protected Map<String, Integer> columnWidths = new LinkedHashMap<>();

    public TablePrinter( Table table ) {
        this.table = table;
    }

    /**
     * estimate the widths of the columns
     *
     * @param type
     *         the type of the width estimation, {@link AutoColumnWidthType the algorithm}.
     * @param n
     *         the number of rows which will be used to estimate. must be lower
     *         the number of rows in the table.
     */
    public void estimateColumnsWidth( AutoColumnWidthType type, int n ) {
        for ( Column<? extends Serializable> column : this.table ) {
            ColumnWidthEstimation estimation = new ColumnWidthEstimation( column, n );
            int estimated = switch ( type ) {
                case average -> estimation.estimateAverage();
                case max -> estimation.estimateMax();
                case percentile80 -> estimation.estimatePercentile80();
            };
            this.columnWidths.put( column.getName(), estimated );
        }
    }

    /**
     * prints a text decorator line.
     */
    public void printDecorator() {
        StringBuilder sb = new StringBuilder();
        sb.append( lineCrossingChar );
        List<Integer> widths = this.columnWidths.values().stream().toList();
        int widthsSize = widths.size();
        for ( int i = 0; i < widthsSize; i++ ) {
            if ( i != 0 ) {
                sb.append( lineCrossingChar );
            }
            int width = widths.get( i ) + spaceBefore + spaceAfter;
            sb.append( String.valueOf( horizontalLine ).repeat( width ) );
        }
        sb.append( lineCrossingChar );
        sb.append( "\n" );
        this.text.append( sb );
    }

    /**
     * print a row of the table.
     *
     * @param rowIndex
     *         the row of the table to print.
     */
    public void printRow( int rowIndex ) {
        int widthsSize = this.columnWidths.size();
        List<String> texts = new ArrayList<>( widthsSize );
        for ( Column<? extends Serializable> column : this.table ) {
            Serializable value = column.get( rowIndex );
            texts.add( value != null ? value.toString() : nullReplacement );
        }
        this.printTextRow( texts );
    }

    /**
     * print the name of the columns.
     */
    public void printColumnNames() {
        int widthsSize = this.columnWidths.size();
        List<String> texts = this.columnWidths.keySet().stream().toList();
        this.printTextRow( texts );
    }

    /**
     * print a list which contains text. this is a
     * basic block method which will be called by a wrapper.
     */
    protected void printTextRow( List<String> texts ) {
        StringBuilder sb = new StringBuilder();
        List<Integer> widths = this.columnWidths.values().stream().toList();
        int widthsSize = widths.size();
        for ( int i = 0; i < widthsSize; i++ ) {
            sb.append( verticalLine );
            sb.append( " ".repeat( spaceBefore ) );
            String text = texts.get( i );
            int textLength = text.length();
            int width = widths.get( i );
            boolean isBig = false;
            if ( textLength > width ) {
                text = text.substring( 0, width ) + horizontalContinuationChar;
                isBig = true;
            } else if ( textLength < width ) {
                text = text + " ".repeat( width - textLength );
            }
            sb.append( text );
            sb.append( " ".repeat( isBig ? spaceAfter - 1 : spaceAfter ) );
        }
        sb.append( verticalLine );
        sb.append( "\n" );
        this.text.append( sb );
    }

    /**
     * return the generated text
     */
    public String getText() {
        return this.text.toString();
    }
}
