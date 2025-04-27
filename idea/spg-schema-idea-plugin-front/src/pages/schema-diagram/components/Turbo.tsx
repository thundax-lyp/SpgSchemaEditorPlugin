import React, {useCallback, useEffect, useState} from 'react';
import {
    Background,
    ConnectionMode,
    Controls,
    type Edge,
    type Node,
    ReactFlow,
    ReactFlowProvider,
    useEdgesState,
    useNodesState,
    useOnSelectionChange,
    useReactFlow
} from '@xyflow/react';

import TurboNode, {type TurboNodeData} from './TurboNode';
import TurboEdge, {TurboEdgeData} from './TurboEdge';
import {layoutNodes} from "./layout-nodes";
import {SchemaEntity} from "../types";

import '@xyflow/react/dist/base.css';
import './turbo.css'

export type TurboFlowProps = {
    initialEntities?: SchemaEntity[]
    selection?: SchemaEntity[]
    onSelectionChange?: (entities: SchemaEntity[]) => void
};

const TurboFlow = (props: TurboFlowProps) => {
    const {initialEntities = [], selection = [], onSelectionChange} = props

    const reactFlow = useReactFlow();
    const [nodes, setNodes, onNodesChange] = useNodesState<Node<TurboNodeData>>([]);
    const [edges, setEdges, onEdgesChange] = useEdgesState<Edge<TurboEdgeData>>([]);

    //call fitView() after nodes have been initialized, this fitting run only once
    const [initialized, setInitialized] = useState<boolean>(false);

    useOnSelectionChange({
        onChange: useCallback(({nodes}) => {
            onSelectionChange?.(nodes.map(x => x.data.entity) as SchemaEntity[])
        }, []),
    })

    // initialize nodes and edges
    useEffect(() => {
        const normalizedNodes: Array<Node<TurboNodeData>> = []
        const normalizedEdges: Array<Edge<TurboEdgeData>> = []

        initialEntities.forEach(entity => {
            const {id = ''} = entity
            if (!normalizedNodes.find(x => x.id === id)) {
                const normalizedNode: Node<TurboNodeData> = {
                    id, position: {x: 0, y: 0}, type: 'turbo', data: {
                        entity,
                    },
                }
                const cachedNode = nodes.find(x => x.id === id)
                if (cachedNode) {
                    normalizedNode.position = cachedNode.position
                    normalizedNode.data.width = cachedNode.data.width
                    normalizedNode.data.height = cachedNode.data.height
                }
                normalizedNodes.push(normalizedNode)
            }
        })

        initialEntities.forEach(entity => {
            const {id = '', properties = [], relations = []} = entity
            const schemas = [...properties, ...relations]
            schemas.forEach(({aliasName, types}) => {
                types?.forEach(type => {
                    const target = initialEntities.find(x => x.name == type)
                    if (target?.id) {
                        const edgeId = `edge__${id}__${target.id}`
                        const targetEdge = normalizedEdges.find(x => x.id == edgeId);
                        if (targetEdge) {
                            targetEdge.label = `${targetEdge.label}、${aliasName}`
                        } else {
                            normalizedEdges.push({
                                id: `edge__${id}__${target.id}`,
                                source: `${id}`,
                                target: target.id,
                                label: `${aliasName}`,
                            })
                        }
                    }
                })
            })
        })

        setNodes(layoutNodes({nodes: normalizedNodes, edges: normalizedEdges}));
        setEdges([...normalizedEdges])
    }, [initialEntities]);

    // recalculate node position after HTML elements rendered
    useEffect(() => {
        let dirty = false
        const newNodes = nodes.map(node => {
            if (node.measured && (node.measured.width != node.data.width || node.measured.height != node.data.height)) {
                dirty = true
                return {
                    ...node, data: {
                        ...node.data,
                        width: node.measured.width,
                        height: node.measured.height
                    }
                }
            }
            return node
        })

        if (dirty) {
            setNodes(layoutNodes({nodes: newNodes, edges}));
            setInitialized(true)
        }
    }, [nodes])

    useEffect(() => {
        if (initialized && reactFlow) {
            nodes.forEach(node => {
                const selected = !!selection.find(x => x.id === node.id)
                if (node.selected !== selected) {
                    reactFlow.updateNode(node.id, {
                        selected
                    })
                }
            })
        }
    }, [initialized, reactFlow, selection])

    // auto-fitting layout
    useEffect(() => {
        if (initialized && reactFlow) {
            reactFlow.fitView({
                nodes: nodes.map(({id}) => ({id}))
            }).then(x => x)
        }
    }, [initialized, reactFlow]);

    return (
        <div className="schema-diagram">
            <div className="turbo-container">
                <ReactFlow
                    nodes={nodes}
                    edges={edges}
                    onNodesChange={onNodesChange}
                    onEdgesChange={onEdgesChange}
                    connectionMode={ConnectionMode.Loose}
                    fitView={false}
                    nodeTypes={{
                        turbo: TurboNode,
                    }}
                    edgeTypes={{
                        turbo: TurboEdge,
                    }}
                    defaultEdgeOptions={{
                        type: 'turbo',
                        markerStart: 'edge-circle',
                        markerEnd: 'edge-arrowhead',
                    }}
                >
                    <Background/>
                    <Controls showInteractive={true} position="top-left">
                        {/*<button type="button" className="react-flow__controls-button" title="save as image" aria-label="save as image" onClick={() => handleSave()}>*/}
                        {/*	<DownloadIcon/>*/}
                        {/*</button>*/}
                    </Controls>
                    <svg>
                        <defs>
                            <linearGradient id="edge-gradient">
                                <stop offset="0%" stopColor="#ae53ba"/>
                                <stop offset="100%" stopColor="#2a8af6"/>
                            </linearGradient>

                            <marker
                                id="edge-circle"
                                viewBox="-5 -5 10 10"
                                refX="0"
                                refY="0"
                                markerUnits="strokeWidth"
                                markerWidth="10"
                                markerHeight="10"
                                orient="auto"
                            >
                                <circle stroke="#2a8af6" strokeOpacity="0.75" r="2" cx="0" cy="0"/>
                            </marker>
                            <marker
                                id="edge-arrowhead"
                                className="react-flow__arrowhead"
                                markerWidth="16"
                                markerHeight="16"
                                viewBox="-10 -10 20 20"
                                markerUnits="strokeWidth"
                                orient="auto-start-reverse"
                                refX="0"
                                refY="0"
                            >
                                <polyline
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    fill="none"
                                    points="-5,-4 0,0 -5,4"
                                    style={{
                                        stroke: "#a853ba",
                                        strokeWidth: 1,
                                    }}
                                />
                            </marker>
                        </defs>
                    </svg>
                </ReactFlow>
            </div>
        </div>
    );
};

const TurboFlowWithProvider = (props: TurboFlowProps) => {
    return (
        <ReactFlowProvider>
            <TurboFlow {...props} />
        </ReactFlowProvider>
    );
}

export default TurboFlowWithProvider
