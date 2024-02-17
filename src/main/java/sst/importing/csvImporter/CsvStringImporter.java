package sst.importing.csvImporter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.testng.Assert;
import sst.importing.GenericStringImporter;
import sst.table.DataType;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CsvStringImporter implements GenericStringImporter {
    protected final String csvString;

    protected final CSVFormat csvFormat;

    protected final List<DataType> columnTypes;

    protected List<CSVRecord> csvRecords;

    public CsvStringImporter(String csvString, CSVFormat csvFormat, List<DataType> columnTypes) throws IOException {
        this.csvString = csvString;
        this.csvFormat = csvFormat;
        this.columnTypes = columnTypes;

        this.parseCsv();
    }

    protected void parseCsv() throws IOException {
        StringReader reader = new StringReader(this.csvString);
        CSVParser parser = new CSVParser(reader, this.csvFormat);
        this.csvRecords = parser.getRecords();
    }

    /**
     * the data of the importing data.
     * the key is the name of the column and their value is a list with the column vales.
     */
    public Map<String, List<String>> getData() {
        List<String> columnNames = this.csvRecords.get(0).toList();
        int columnNamesSize = columnNames.size();
        List<DataType> columnTypes = this.getTypes();
        Assert.assertEquals(
                columnNamesSize,
                columnTypes.size(),
                "the number of supplied columnTypes (" + columnTypes.size() + ") is different than the " +
                        "number of columns in the data (" + columnNamesSize + ")."
        );
        final int nRow = this.csvRecords.size();
        Map<String, List<String>> data = new LinkedHashMap<>(nRow);
        for (int i = 0; i < columnNamesSize; i++) {
            String columnName = columnNames.get(i);
            DataType columnType = this.columnTypes.get(i);
            data.put(columnName, new ArrayList<String>(nRow));
            for (int j = 1; j < nRow; j++) {
                CSVRecord record = this.csvRecords.get(j);
                String cellString = record.get(i);
                data.get(columnName).add(cellString);
            }
        }

        return data;
    }

    /**
     * the types of each imported data.
     */
    public List<DataType> getTypes() {
        return this.columnTypes;
    }
}
