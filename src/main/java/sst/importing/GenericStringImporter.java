package sst.importing;

import sst.table.DataType;

import java.util.List;
import java.util.Map;

/**
 * interface that describes a importer of textual data. the data is obtained as text.
 * so textual data storages would work well with this interface (csv, json, xml, toml, yaml...).
 * but for semi-binary or similar. example: parquet, bison, smalltalk.
 * probably would work with this interface but you would got a completely overhead added because
 * these data formats already have a description of which type their data belongs to.
 */
public interface GenericStringImporter {
    /**
     * a linked map the map need to preserve the insertion order.
     * the data of the importing data.
     * the key is the name of the column and their value is a list with the column vales.
     */
    Map<String, List<String>> getData();

    /**
     * the types of each imported data.
     */
    List<DataType> getTypes();
}
