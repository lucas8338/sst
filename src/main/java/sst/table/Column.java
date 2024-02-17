package sst.table;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * a interface which describes a column of a table.
 * */
public interface Column<T extends Serializable> extends Serializable, Iterable<T> {
    Logger logger = LoggerFactory.getLogger(Column.class);

    /*
    * bellow there a couple of methods to transform a column to a specific typed column.
    * */

    default StringColumn asStringColumn() {
        return (StringColumn) this;
    }

    default IntegerColumn asIntegerColumn() {
        return (IntegerColumn) this;
    }

    default DoubleColumn asDoubleColumn() {
        return (DoubleColumn) this;
    }

    default FloatColumn asFloatColumn() {
        return (FloatColumn) this;
    }

    default BooleanColumn asBooleanColumn() {
        return (BooleanColumn) this;
    }

    /**
     * return the data of the column at position.
     */
    T get(int index);

    /**
     * get the name of the column.
     */
    String getName();

    /**
     * get the type of the column.
     */
    DataType getType();

    /**
     * retrieve the size of the column.
     */
    int size();

    /**
     * return the column as a {@link java.util.Collections#unmodifiableList(List) unmodifiable list} list.
     */
    List<T> asList();

    /*
    * bellow there a couple of methods to set the values of a column.
    * in standard way.
    * */

    default void setString(int rowIndex, String value) {
        String message = "setInteger not implemented";
        logger.error(message);
        throw new NotImplementedException(message);
    }

    default void setInteger(int index, Integer value) {
        String message = "setInteger not implemented";
        logger.error(message);
        throw new NotImplementedException(message);
    }

    default void setDouble(int index, Double value) {
        String message = "setDouble not implemented";
        logger.error(message);
        throw new NotImplementedException(message);
    }

    default void setFloat(int index, Float value) {
        String message = "setFloat not implemented";
        logger.error(message);
        throw new NotImplementedException(message);
    }

    default void setBoolean(int index, Boolean value) {
        String message = "setBoolean not implemented";
        logger.error(message);
        throw new NotImplementedException(message);
    }

    /**
     * set a value in a table programmatically (obtaining the type of the data through of an argument).
     * @param index the index to set the value.
     * @param value the value to be set.
     * @param type the {@link DataType type} of the 'value'.
     * */
    default void set(int index, Serializable value, DataType type) {
        try {
            switch (type) {
                case String -> this.setString(index, (String) value);
                case Integer -> this.setInteger(index, (Integer) value);
                case Float -> this.setFloat(index, (Float) value);
                case Double -> this.setDouble(index, (Double) value);
                case Boolean -> this.setBoolean(index, (Boolean) value);
                default -> throw new RuntimeException("unhandled dataType");
            }
        } catch (Exception e) {
            String message = String.format(
                    """
                            was not possible to set the '%s' value in the '%s' column at index '%d'
                            targetColumnType: '%s'
                            """,
                    value.toString(),
                    this.getName(),
                    index,
                    this.getType()
            );
            logger.error(message);
            throw e;
        }
    }

    /**
     * set the value in the column using a received string. if the string is empty then will
     * set 'null'.
     * */
    default void setFromString(int index, String value, DataType dataType) {
        switch (dataType) {
            case String -> this.setString(index, value);
            case Integer -> this.setInteger(index, !value.isEmpty() ? Integer.parseInt(value) : null);
            case Float -> this.setFloat(index, !value.isEmpty() ? Float.parseFloat(value) : null);
            case Double -> this.setDouble(index, !value.isEmpty() ? Double.parseDouble(value) : null);
            case Boolean -> this.setBoolean(index, !value.isEmpty() ? Boolean.parseBoolean(value) : null);
            default -> throw new RuntimeException("unhandled datatype");
        }
    }
}
