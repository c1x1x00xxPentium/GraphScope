package com.alibaba.maxgraph.gaia;

import com.alibaba.graphscope.gaia.store.GraphStoreService;

public class MockVineyardStore extends GraphStoreService {
    public MockVineyardStore() {
        super(VineyardGraphStore.MODERN_PROPERTY_RESOURCE);
    }

    @Override
    public long getLabelId(String label) {
        return 11111;
    }

    @Override
    public String getLabel(long labelId) {
        return "test_label";
    }

    @Override
    public long getGlobalId(long labelId, long propertyId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPropertyId(String propertyName) {
        return 22222;
    }

    @Override
    public String getPropertyName(int propertyId) {
        return "test_property";
    }

    @Override
    public long getSnapshotId() {
        return 0;
    }
}
