package sst.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * implements a column which stores string.
 * */
public class StringColumn extends AbstractColumn<String> {

    private final List<String> storage = new ArrayList<>();

    public StringColumn() {
        this.dataType = DataType.String;
    }

    /**
     * return the data of the column at position.
     *
     * @param index
     */
    public String get(int index) {
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
    public List<String> asList() {
        return Collections.unmodifiableList(
                this.storage
        );
    }

    protected void add(String data) {
        this.storage.add(data);
    }

    protected void remove(int index) {
        this.storage.remove(index);
    }

    protected void set(int index, String data) {
        this.storage.set(index, data);
    }

    public void setString(int index, String data) {
        this.set(index, data);
    }
}
