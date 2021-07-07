/**
 * Copyright 2020 Alibaba Group Holding Limited.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.maxgraph.tests.sdk;

import com.alibaba.maxgraph.v2.common.frontend.api.schema.GraphSchema;
import com.alibaba.maxgraph.v2.common.schema.GraphDef;
import com.alibaba.maxgraph.v2.sdk.Client;
import com.alibaba.maxgraph.v2.sdk.DataLoadTarget;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PrepareModernGraph {

    String host = "localhost";
    int port = 55555;
    Client client = new Client(host, port);

    @Test
    void testIngestData() {
        String path = "hdfs://100.69.96.93:9000/user/tianli/data/build_1g_p8";
        client.ingestData(path);
    }

    @Test
    void testCommitData() {
        long tableId = -4611686018427387871L;
        DataLoadTarget target = DataLoadTarget.newBuilder()
                .setLabel("person")
                .build();
        client.commitDataLoad(Collections.singletonMap(tableId, target));
    }

    @Test
    void testLoadSchema() throws URISyntaxException, IOException {
        Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource("modern.schema").toURI());
        String jsonSchemaRes = client.loadJsonSchema(path);
        System.out.println(jsonSchemaRes);
    }

    @Test
    void testGetSchema() {
        GraphSchema schema = client.getSchema();
        System.out.println(((GraphDef) schema).toProto().toString());
    }

    @Test
    void testAddData() throws Exception {
        Map<String, String> v1 = new HashMap<>();
        v1.put("id", "1");
        v1.put("name", "marko");
        v1.put("age", "29");
        client.addVertex("person", v1);

        Map<String, String> v2 = new HashMap<>();
        v2.put("id", "2");
        v2.put("name", "vadas");
        v2.put("age", "27");
        client.addVertex("person", v2);

        Map<String, String> v4 = new HashMap<>();
        v4.put("id", "4");
        v4.put("name", "josh");
        v4.put("age", "32");
        client.addVertex("person", v4);

        Map<String, String> v6 = new HashMap<>();
        v6.put("id", "6");
        v6.put("name", "peter");
        v6.put("age", "35");
        client.addVertex("person", v6);

        Map<String, String> v3 = new HashMap<>();
        v3.put("id", "3");
        v3.put("name", "lop");
        v3.put("lang", "java");
        client.addVertex("software", v3);

        Map<String, String> v5 = new HashMap<>();
        v5.put("id", "5");
        v5.put("name", "ripple");
        v5.put("lang", "java");
        client.addVertex("software", v5);

        Thread.sleep(1000);

        client.addEdge("knows",
                "person",
                "person",
                Collections.singletonMap("id", "1"),
                Collections.singletonMap("id", "2"),
                Collections.singletonMap("weight", "0.5"));

        client.addEdge("created",
                "person",
                "software",
                Collections.singletonMap("id", "1"),
                Collections.singletonMap("id", "3"),
                Collections.singletonMap("weight", "0.4"));

        client.addEdge("knows",
                "person",
                "person",
                Collections.singletonMap("id", "1"),
                Collections.singletonMap("id", "4"),
                Collections.singletonMap("weight", "1.0"));

        client.addEdge("created",
                "person",
                "software",
                Collections.singletonMap("id", "4"),
                Collections.singletonMap("id", "3"),
                Collections.singletonMap("weight", "0.4"));

        client.addEdge("created",
                "person",
                "software",
                Collections.singletonMap("id", "4"),
                Collections.singletonMap("id", "5"),
                Collections.singletonMap("weight", "1.0"));

        client.addEdge("created",
                "person",
                "software",
                Collections.singletonMap("id", "6"),
                Collections.singletonMap("id", "3"),
                Collections.singletonMap("weight", "0.2"));

        client.commit();
    }
}
