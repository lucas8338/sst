package sst.table;

import java.io.Serializable;
import java.util.Iterator;

/**
 * a abstract class which helps the implementation of a column.
 * */
public abstract class AbstractColumn<T extends Serializable> implements Column<T> {
    /**
     * store the name of the column.
     */
    protected String name;

    /**
     * store the type of the column (type of the data)
     */
    protected DataType dataType;

    public String getName() {
        assert this.name != null : "the name of the column cant be null.";
        return this.name;
    }

    protected void setName(String newName) {
        this.name = newName;
    }

    protected abstract void add(T data);

    protected abstract void remove(int index);

    protected abstract void set(int index, T data);

    public String toString() {
        return this.asList().toString();
    }

    public Iterator<T> iterator() {
        return this.asList().iterator();
    }
}
