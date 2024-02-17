package sst.table;

import org.testng.Assert;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * implements a column which store boolean column.
 * */
public class BooleanColumn extends AbstractColumn<Boolean> {

    /**
     * a bitSet can only store true or false, then i'll store the data in two bitsets
     * one will store only true values, and the another one will store only false values.
     * this way this will allows to store [true, false, null] using only 2 bit per entry.
     */
    private final BitSet trueStorage = new BitSet();

    private final BitSet falseStorage = new BitSet();

    private int size = 0;

    public BooleanColumn() {
        this.dataType = DataType.Boolean;
    }

    /**
     * return the data of the column at position.
     *
     * @param index
     */
    public Boolean get(int index) {
        if (this.trueStorage.get(index) && !this.falseStorage.get(index)) {
            return true;
        } else if (!this.trueStorage.get(index) && this.falseStorage.get(index)) {
            return false;
        } else {
            return null;
        }
    }

    /**
     * get the type of the column.
     */
    public DataType getType() {
        return this.dataType;
    }

    public int size() {
        return this.size;
    }

    /**
     * return the column as a unmodifiable list.
     */
    public List<Boolean> asList() {
        int size = this.size();
        List<Boolean> booleanList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            if (this.get(i) == null) {
                booleanList.add(null);
            } else if (this.get(i)) {
                booleanList.add(true);
            } else if (!this.get(i)) {
                booleanList.add(false);
            }
        }
        return Collections.unmodifiableList(
                booleanList
        );
    }

    protected void add(Boolean data) {
        int size = this.size();
        if (data == null) {
            // empty here
        } else if (data) {
            this.trueStorage.set(size, true);
        } else {
            this.falseStorage.set(size, false);
        }
        this.size++;
    }

    protected void remove(int index) {
        int size = this.size;
        Assert.assertTrue(
                index <= size
        );
        this.trueStorage.set(index, false);
        this.falseStorage.set(index, false);
        this.size--;
    }

    /**
     * set a value to the storage.
     */
    protected void set(int index, Boolean value) {
        Assert.assertTrue(
                index <= this.size(),
                "you can only set value for va"
        );

        if (value == null) {
            this.trueStorage.set(index, false);
            this.falseStorage.set(index, false);
        } else if (value) {
            this.trueStorage.set(index, true);
            this.falseStorage.set(index, false);
        } else if (!value) {
            this.trueStorage.set(index, false);
            this.falseStorage.set(index, true);
        }
    }

    public void setBoolean(int index, Boolean data) {
        this.set(index, data);
    }

}
