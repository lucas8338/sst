package sst.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * implements a column that stores long values.
 * */
public class LongColumn extends AbstractColumn<Long> {
    private final List<Long> storage = new ArrayList<>();

    public LongColumn() {
        this.dataType = DataType.Long;
    }

    /**
     * return the data of the column at position.
     *
     * @param index
     */
    public Long get(int index) {
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
    public List<Long> asList() {
        return Collections.unmodifiableList(
                this.storage
        );
    }

    protected void add(Long data) {
        this.storage.add(data);
    }

    protected void remove(int index) {
        this.storage.remove(index);
    }

    protected void set(int index, Long data) {
        this.storage.set(index, data);
    }

    public void setLong(int index, Long data) {
        this.set(index, data);
    }
}
