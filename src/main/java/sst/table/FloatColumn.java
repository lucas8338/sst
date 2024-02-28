package sst.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * implements a column which store float values.
 */
public class FloatColumn extends AbstractColumn<Float> {

    private final List<Float> storage = new ArrayList<>();

    public FloatColumn() {
        this.dataType = DataType.Float;
    }

    /**
     * return the data of the column at position.
     *
     * @param index
     */
    public Float get( int index ) {
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
    public List<Float> asList() {
        return Collections.unmodifiableList(
                this.storage
        );
    }

    protected void add( Float data ) {
        this.storage.add( data );
    }

    protected void remove( int index ) {
        this.storage.remove( index );
    }

    protected void set( int index, Float data ) {
        this.storage.set( index, data );
    }

    public void setFloat( int index, Float data ) {
        this.set( index, data );
    }
}
