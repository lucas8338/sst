package sst.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * implements a column which store integers.
 */
public class IntegerColumn extends AbstractColumn<Integer> {

    private final List<Integer> storage = new ArrayList<>();

    public IntegerColumn() {
        this.dataType = DataType.Integer;
    }

    /**
     * return the data of the column at position.
     *
     * @param index
     */
    public Integer get( int index ) {
        return this.storage.get( index );
    }

    /**
     * get the type of the column.
     */
    public DataType getType() {
        return this.dataType;
    }

    /**
     * retrieve the size of the column.
     */
    public int size() {
        return this.storage.size();
    }

    /**
     * return the column as a unmodifiable list.
     */
    public List<Integer> asList() {
        return Collections.unmodifiableList(
                this.storage
        );
    }

    protected void add( Integer data ) {
        this.storage.add( data );
    }

    protected void remove( int index ) {
        this.storage.remove( index );
    }

    protected void set( int index, Integer data ) {
        this.storage.set( index, data );
    }

    public void setInteger( int index, Integer data ) {
        this.set( index, data );
    }
}
