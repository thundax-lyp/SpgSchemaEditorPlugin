import {Layout, LayoutEdge, LayoutGroup, LayoutNode} from "./types";
import * as d3 from "d3";
import {SimulationLinkDatum, SimulationNodeDatum} from "d3-force";

const MANY_BODY_STRENGTH = -80
const LINK_DISTANCE = 80
const COLLISION_OFFSET = 40

interface NodeDatum extends SimulationNodeDatum {
    id: string
}

interface LinksDatum extends SimulationLinkDatum<NodeDatum> {
    source: number
    target: number
}

export interface ForceLayoutProps {
    manyBodyStrength?: number
    linkDistance?: number
    collisionOffset?: number
}

export const ForceLayout = (props?: ForceLayoutProps): Layout => {
    const {
        manyBodyStrength = MANY_BODY_STRENGTH,
        linkDistance = LINK_DISTANCE,
        collisionOffset = COLLISION_OFFSET,
    } = props || {}

    const normalizeLayoutGroup = (nodes: LayoutNode[], edges: LayoutEdge[]): [NodeDatum[], LinksDatum[]] => {
        const layoutNodes: NodeDatum[] = nodes.map(({id, x = 0, y = 0, width = 0, height = 0}) => ({
            id, x: x + width / 2, y: y + height / 2,
        }))

        const layoutEdges: LinksDatum[] = edges
            .filter(({source, target}) => source.id != target.id)
            .map(({source, target}) => {
                return {
                    source: nodes.findIndex(x => x.id == source.id),
                    target: nodes.findIndex(x => x.id == target.id),
                }
            }).filter(({source, target}) => source >= 0 && target >= 0)
        return [layoutNodes, layoutEdges]
    }

    const layout = (param: LayoutGroup): Array<LayoutNode> => {
        const {nodes, edges} = param
        if (nodes.length == 0) {
            return []
        }

        const [layoutNodes, layoutEdges] = normalizeLayoutGroup(nodes, edges)

        const simulation = d3.forceSimulation(layoutNodes)
            .force("charge", d3.forceManyBody().strength(manyBodyStrength))
            .force("center", d3.forceCenter(300, 300))
            .force("collision", d3.forceCollide(({id}) => {
                const node = nodes.find(x => x.id == id)
                const {width = 0, height = 0} = node || {}
                return Math.sqrt(width ** 2 + height ** 2) / 2 + collisionOffset
            }))
            .force("link", d3.forceLink(layoutEdges).distance(linkDistance))
            .force("y", d3.forceY(60))

        simulation.tick(100)

        return simulation.nodes()
            .map(({id, x, y}) => {
                const node = nodes.find(x => x.id == id)
                return node && {
                    ...node,
                    x: (x || 0) - (node.width || 0) / 2,
                    y: (y || 0) - (node.height || 0) / 2,
                }
            })
            .filter(x => !!x)
    }

    return {
        name: 'ForceLayout',
        layout
    }
}
