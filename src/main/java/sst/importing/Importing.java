package sst.importing;

import org.apache.commons.csv.CSVFormat;
import sst.importing.csvImporter.CsvStringImporter;
import sst.table.DataType;
import sst.table.Table;

import java.io.IOException;
import java.util.List;

/**
 * contains methods to read data from outside.
 * */
public abstract class Importing {

    /**
     * parse a csv string into a table.
     * @param csvString the string of the csv.
     * @param format the format of the csv.
     *               for a standard csv format use {@link CSVFormat#DEFAULT CSVFormat.DEFAULT}
     *               if your csv format don't follow the standard format read the documentation
     *               of <a href="https://github.com/apache/commons-csv">apache commons csv</a> library.
     * @param columnTypes the types of the columns. listed in {@link DataType DataTypes class}.
     * */
    public static Table csv(String csvString, CSVFormat format, List<DataType> columnTypes) throws IOException {
        CsvStringImporter importer = new CsvStringImporter(csvString, format, columnTypes);
        GenericStringImporterTableCreator tableCreator = new GenericStringImporterTableCreator(importer);
        return tableCreator.create();
    }
}
