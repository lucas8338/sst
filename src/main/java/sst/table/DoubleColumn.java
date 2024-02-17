package sst.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * implement a column which store 'double' values.
 * */
public class DoubleColumn extends AbstractColumn<Double> {

    private final List<Double> storage = new ArrayList<>();

    /**
     * return the data of the column at position.
     *
     * @param index
     */
    public Double get(int index) {
        return this.storage.get(index);
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
    public List<Double> asList() {
        return Collections.unmodifiableList(
                this.storage
        );
    }

    protected void add(Double data) {
        this.storage.add(data);
    }

    protected void remove(int index) {
        this.storage.remove(index);
    }

    protected void set(int index, Double data) {
        this.storage.set(index, data);
    }

    public void setDouble(int index, Double data) {
        this.set(index, data);
    }
}
