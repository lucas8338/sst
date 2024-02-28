package sst.table;

import java.io.Serializable;
import java.util.Iterator;

public class TableIterator implements Iterator<Column<? extends Serializable>> {
    protected final Table table;

    protected int showedColumnIndex = - 1;

    public TableIterator( Table table ) {
        this.table = table;
    }

    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    public boolean hasNext() {
        return this.showedColumnIndex < this.table.nCol() - 1;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     */
    public Column<? extends Serializable> next() {
        Column<? extends Serializable> column = this.table.getColumn( this.table.getColumnNames().get( this.showedColumnIndex + 1 ) );
        this.showedColumnIndex++;
        return column;
    }
}
