package com.alibaba.maxgraph.v2.frontend.gaia.adaptor;

import com.alibaba.graphscope.gaia.store.GraphStoreService;
import com.alibaba.maxgraph.compiler.api.schema.GraphSchema;
import com.alibaba.maxgraph.compiler.api.schema.SchemaFetcher;

public class VineyardGraphStore extends GraphStoreService {
    public static final String MODERN_PROPERTY_RESOURCE = "modern.properties.json";
    private SchemaFetcher schemaFetcher;

    public VineyardGraphStore(SchemaFetcher schemaFetcher) {
        super(MODERN_PROPERTY_RESOURCE);
        this.schemaFetcher = schemaFetcher;
    }

    @Override
    public long getLabelId(String label) {
        GraphSchema graphSchema = this.schemaFetcher.getSchemaSnapshotPair().getLeft();
        return graphSchema.getElement(label).getLabelId();
    }

    @Override
    public String getLabel(long labelId) {
        GraphSchema graphSchema = this.schemaFetcher.getSchemaSnapshotPair().getLeft();
        return graphSchema.getElement((int) labelId).getLabel();
    }

    @Override
    public long getGlobalId(long labelId, long propertyId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPropertyId(String propertyName) {
        GraphSchema graphSchema = this.schemaFetcher.getSchemaSnapshotPair().getLeft();
        return graphSchema.getPropertyId(propertyName);
    }

    @Override
    public String getPropertyName(int propertyId) {
        GraphSchema graphSchema = this.schemaFetcher.getSchemaSnapshotPair().getLeft();
        return graphSchema.getPropertyName(propertyId);
    }

    @Override
    public long getSnapshotId() {
        long snapshotId = this.schemaFetcher.getSchemaSnapshotPair().getRight();
        return snapshotId;
    }
}
