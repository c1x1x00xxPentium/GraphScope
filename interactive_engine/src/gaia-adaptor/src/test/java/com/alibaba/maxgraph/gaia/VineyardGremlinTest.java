package com.alibaba.maxgraph.gaia;

import com.alibaba.graphscope.gaia.config.GaiaConfig;
import com.alibaba.graphscope.gaia.idmaker.IdMaker;
import com.alibaba.graphscope.gaia.idmaker.IncrementalQueryIdMaker;
import com.alibaba.graphscope.gaia.idmaker.TagIdMaker;
import com.alibaba.graphscope.gaia.plan.PlanUtils;
import com.alibaba.graphscope.gaia.plan.translator.TraversalTranslator;
import com.alibaba.graphscope.gaia.plan.translator.builder.PlanConfig;
import com.alibaba.graphscope.gaia.plan.translator.builder.TraversalBuilder;
import com.alibaba.graphscope.gaia.processor.GaiaGraphOpProcessor;
import com.alibaba.graphscope.gaia.store.GraphStoreService;
import com.alibaba.maxgraph.common.cluster.InstanceConfig;
import com.alibaba.pegasus.builder.AbstractBuilder;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class VineyardGremlinTest {
    private static Logger logger = LoggerFactory.getLogger(VineyardGremlinTest.class);
    private static final GraphTraversalSource g = EmptyGraph.instance().traversal();

    public static void main(String[] args) {
        GaiaConfig config = new VineyardGaiaConfig(new InstanceConfig(new Properties()));
        GraphStoreService graphStore = new MockVineyardStore();
        IdMaker queryIdMaker = new IncrementalQueryIdMaker();
        test_1(config, graphStore, queryIdMaker);
    }

    public static void test_1(GaiaConfig config, GraphStoreService storeService, IdMaker queryIdMaker) {
        Traversal testTraversal = query_1();
        GaiaGraphOpProcessor.applyStrategy(testTraversal, config, storeService);
        long queryId = (long) queryIdMaker.getId(testTraversal.asAdmin());
        AbstractBuilder job = new TraversalTranslator((new TraversalBuilder(testTraversal.asAdmin()))
                .addConfig(PlanConfig.QUERY_ID, queryId)
                .addConfig(PlanConfig.TAG_ID_MAKER, new TagIdMaker(testTraversal.asAdmin()))
                .addConfig(PlanConfig.QUERY_CONFIG, PlanUtils.getDefaultConfig(queryId, config))).translate();
        PlanUtils.print(job);
    }

    public static Traversal query_1() {
        return g.V().as("a").out().where(P.eq("a")).by("name");
    }
}
