package sst.importing;

import sst.table.Column;
import sst.table.DataType;
import sst.table.Table;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * a class which is capable to generate
 * a table from a class that implements the {@link GenericStringImporter GenericStringImporter} interface.
 * */
public class GenericStringImporterTableCreator {

    protected final GenericStringImporter importer;

    protected Table table;

    public GenericStringImporterTableCreator(GenericStringImporter importer) {
        this.importer = importer;
    }

    /**
     * create a table from the importer
     * */
    public Table create(){
        this.createTable();
        this.fillTable();
        return this.table;
    }

    protected void createTable() {
        this.table = new Table();
    }

    protected void fillTable() {
        Map<String, List<String>> data = this.importer.getData();
        int dataSize = data.size();
        List<DataType> dataTypes = this.importer.getTypes();

        for (int i = 0; i < dataSize; i++) {
            String columnName = data.keySet().toArray(String[]::new)[i];
            DataType columnType = dataTypes.get(i);
            Column<? extends Serializable> column = this.table.addColumn(columnName, columnType);
            List<String> values = data.get(columnName);
            int valuesSize = values.size();
            for (int j = 0; j < valuesSize; j++) {
                int rowIndex;
                if (this.table.nRow() - 1 < j) {
                    rowIndex = this.table.addRow();
                } else {
                    rowIndex = j;
                }

                String value = values.get(j);

                column.setFromString(rowIndex, value, columnType);
            }
        }
    }
}
