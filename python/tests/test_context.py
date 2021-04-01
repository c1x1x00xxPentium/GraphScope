#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#
# Copyright 2020 Alibaba Group Holding Limited. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

import os

import pandas as pd
import pytest
import vineyard
import vineyard.io

from graphscope import hits
from graphscope import lpa
from graphscope import property_bfs
from graphscope import property_sssp
from graphscope import sssp
from graphscope.framework.app import AppAssets


def test_simple_context_to_numpy(simple_context):
    out = simple_context.to_numpy("v.id")
    assert out.shape == (40521,)
    out = simple_context.to_numpy("v.data")
    assert out.shape == (40521,)
    # selector of `e` is not done yet.
    # out = simple_context.to_numpy('e.src')
    # out = simple_context.to_numpy('e.dst')
    # out = simple_context.to_numpy('e.data')
    out = simple_context.to_numpy("r")
    assert out.shape == (40521,)


def test_simple_context_to_dataframe(simple_context):
    out = simple_context.to_dataframe({"id": "v.id", "data": "v.data", "result": "r"})
    assert out.shape == (40521, 3)


def test_simple_context_to_vineyard_tensor(simple_context, p2p_project_directed_graph):
    out = simple_context.to_vineyard_tensor("v.id")
    assert out is not None
    out = simple_context.to_vineyard_tensor("r")
    assert out is not None

    has_path = AppAssets(algo="sssp_has_path")
    ctx = has_path(
        p2p_project_directed_graph._project_to_simple(), source=6, target=3728
    )
    assert ctx.to_vineyard_tensor(axis=0) is not None


def test_simple_context_to_vineyard_dataframe(
    simple_context, p2p_project_directed_graph
):
    out = simple_context.to_vineyard_dataframe(
        {"id": "v.id", "data": "v.data", "result": "r"}
    )
    assert out is not None


def test_property_context_to_numpy(property_context):
    out = property_context.to_numpy("v:v0.weight")
    assert out.shape == (40521,)
    out = property_context.to_numpy("r:v1.dist_1")
    assert out.shape == (40786,)


def test_property_context_to_dataframe(property_context):
    out = property_context.to_dataframe({"id": "v:v0.id", "result": "r:v0.dist_0"})
    assert out.shape == (40521, 2)
    out = property_context.to_dataframe({"id": "v:v1.id", "result": "r:v1.dist_1"})
    assert out.shape == (40786, 2)


def test_property_context_output(property_context):
    property_context.output_to_client(
        fd="/tmp/r0", selector={"id": "v:v0.id", "result": "r:v0.dist_0"}
    )
    out = pd.read_csv("/tmp/r0")
    assert out.shape == (40521, 2)


def test_property_context_to_vineyard_tensor(property_context):
    out = property_context.to_vineyard_tensor("v:v0.id")
    assert out is not None


def test_property_context_to_vineyard_dataframe(graphscope_session, property_context):
    out = property_context.to_vineyard_dataframe(
        {"id": "v:v0.id", "data": "v:v0.weight", "result": "r:v0.dist_0"}
    )
    assert out is not None

    # info = graphscope_session.info
    # conf = info["engine_config"]
    # vineyard_endpoint = conf["vineyard_rpc_endpoint"]
    # vineyard_ipc_socket = conf["vineyard_socket"]
    # print("run: vineyard_read_vineyard_dataframe {} {} 1 0".format(vineyard_ipc_socket, 'vineyard://'+str(out)))
    # comment out this part since the user have to custom ssh.sh and remove a line in the 01-stream.py:56
    # FIXME: DFToFile has problem again, caused by stale vineyard process
    # dfstream = vineyard.io.open(
    #     "vineyard://" + str(out),
    #     vineyard_ipc_socket=vineyard_ipc_socket,
    #     vineyard_endpoint=vineyard_endpoint,
    # )
    # vineyard.io.open(
    #     "file:///tmp/test_property_context_to_vineyard_dataframe",
    #     dfstream,
    #     mode="w",
    #     vineyard_ipc_socket=vineyard_ipc_socket,
    #     vineyard_endpoint=vineyard_endpoint,
    # )
    # assert out is not None


def test_add_column(arrow_property_graph, property_context):
    g2 = arrow_property_graph.add_column(
        property_context, {"result_0": "r:v0.dist_0", "result_1": "r:v1.dist_1"}
    )
    assert "result_0" in [p.name for p in g2.schema.get_vertex_properties("v0")]
    assert "result_1" in [p.name for p in g2.schema.get_vertex_properties("v1")]


def test_add_column_after_computation(arrow_property_graph):
    sg = arrow_property_graph.project(vertices={"v0": ["id"]}, edges={"e0": ["weight"]})
    ret = sssp(sg, 20)
    g2 = arrow_property_graph.add_column(
        ret, {"id_col": "v.id", "data_col": "v.data", "result_col": "r"}
    )
    assert "id_col" in [p.name for p in g2.schema.get_vertex_properties("v0")]
    assert "data_col" in [p.name for p in g2.schema.get_vertex_properties("v0")]
    assert "result_col" in [p.name for p in g2.schema.get_vertex_properties("v0")]


def test_lpa(arrow_property_graph_lpa):
    ret = (
        lpa(arrow_property_graph_lpa, max_round=20)
        .to_dataframe(
            {"node": "v:v0.id", "label0": "r:v0.label_0", "label1": "r:v0.label_1"}
        )
        .sort_values(by=["node"])
    )


def test_error_on_selector(property_context):
    with pytest.raises(KeyError, match="non_exist_label"):
        out = property_context.to_numpy("v:non_exist_label.id")
    with pytest.raises(KeyError, match="non_exist_prop"):
        out = property_context.to_numpy("v:v0.non_exist_prop")
    with pytest.raises(RuntimeError, match="selector cannot be None"):
        out = property_context.to_numpy(selector=None)
    with pytest.raises(ValueError, match="not enough values to unpack"):
        out = property_context.to_numpy("xxx")
    with pytest.raises(SyntaxError, match="Invalid selector"):
        out = property_context.to_numpy("xxx:a.b")
