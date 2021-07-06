/*
 * Copyright 2020 Alibaba Group Holding Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.maxgraph.gaia;

import com.alibaba.graphscope.gaia.broadcast.channel.RpcChannelFetcher;
import com.alibaba.graphscope.gaia.config.GaiaConfig;
import com.alibaba.graphscope.gaia.processor.GaiaGraphOpProcessor;
import com.alibaba.graphscope.gaia.store.GraphStoreService;

public class ExtGaiaGraphOpProcessor extends GaiaGraphOpProcessor {
    public ExtGaiaGraphOpProcessor(GaiaConfig config, GraphStoreService graphStore, RpcChannelFetcher fetcher) {
        super(config, graphStore, fetcher);
    }

    @Override
    public String getName() {
        return "gaia";
    }
}
