package sst.tableTools.tableSorting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IndexStorage {
    protected final List<Integer> indexes = new ArrayList<>();

    public IndexStorage( List<Integer> indexes ) {
        this.indexes.addAll( indexes );
    }

    public List<Integer> indexes() {
        return Collections.unmodifiableList( this.indexes );
    }

    public int min() {
        return this.indexes().stream()
                .min( Integer::compare )
                .orElseThrow();
    }

    public int max() {
        return this.indexes().stream()
                .max( Integer::compare )
                .orElseThrow();
    }

    public int size() {
        return this.indexes.size();
    }

    public Integer get( int index ) {
        return this.indexes.get( index );
    }

    public void update( List<Integer> updated ) {
        assert updated.size() == this.indexes.size();
        assert updated.containsAll( this.indexes );
        this.indexes.clear();
        this.indexes.addAll( updated );
    }

    public String toString() {
        return this.indexes.toString();
    }
}
