import type {Edge, Node} from "@xyflow/react";
import {DagreLayout, ForceLayout, LayoutEdge, LayoutGroup, LayoutNode} from "./layout";
import type {TurboNodeData} from "./TurboNode";
import {TurboEdgeData} from "./TurboEdge";

const groupEdges = (edges: LayoutEdge[]): Array<LayoutEdge[]> => {
	const groups: Array<Set<string>> = []

	edges.forEach(edge => {
		const group = groups.find(group => group.has(edge.source.id) || group.has(edge.target.id))
		if (group) {
			group.add(edge.source.id)
			group.add(edge.target.id)
		} else {
			groups.push(new Set([edge.source.id, edge.target.id]))
		}
	})

	for (let thisIdx = groups.length - 1; thisIdx > 0; thisIdx -= 1) {
		const thisGroup = groups[thisIdx]
		for (let otherIdx = 0; otherIdx < thisIdx; otherIdx += 1) {
			const otherGroup = groups[otherIdx]
			if ([...thisGroup].some(x => otherGroup.has(x))) {
				thisGroup.forEach(x => otherGroup.add(x))
				groups.splice(thisIdx, 1)
				break
			}
		}
	}

	return groups.map(group => {
		return [...edges.filter(edge => group.has(edge.source.id) || group.has(edge.target.id))]
	})
}

export interface LayoutNodesParam {
	nodes: Array<Node<TurboNodeData>>
	edges: Array<Edge<TurboEdgeData>>
}

export const layoutNodes = (params: LayoutNodesParam): Array<Node<TurboNodeData>> => {
	const {nodes, edges} = params

	const layoutNodes: Array<LayoutNode> = nodes.map(node => {
		const {id, position, data} = node
		return {
			id, x: position.x, y: position.y, width: data.width, height: data.height
		}
	})

	const layoutEdges: Array<LayoutEdge> = edges.map(edge => {
		return {
			source: {id: edge.source}, target: {id: edge.target}
		}
	})

	const hasEdges = (node: LayoutNode): boolean => {
		return !!layoutEdges.find(edge => edge.source.id == node.id || edge.target.id == node.id)
	}

	const layoutGroups: LayoutGroup[] = groupEdges(layoutEdges).map(edges => {
		return {
			nodes: layoutNodes.filter(node => edges.some(edge => edge.source.id == node.id || edge.target.id == node.id)),
			edges
		}
	}).map(({nodes, edges}) => {
		return {
			nodes: ForceLayout().layout({nodes, edges}), edges
		}
	})

	layoutGroups.push({
		nodes: DagreLayout({direction: 'LR'}).layout({
			nodes: layoutNodes.filter(x => !hasEdges(x)), edges: []
		}),
		edges: []
	})

	let lastGroupBottom = 0
	layoutGroups.forEach(({nodes}) => {
		const left = Math.min(...nodes.map(({x = 0}) => x))
		const top = Math.min(...nodes.map(({y = 0}) => y))
		const bottom = Math.max(...nodes.map(({y = 0, height = 0}) => y + height))
		nodes.forEach(layoutNode => {
			layoutNode.x = (layoutNode.x || 0) - left
			layoutNode.y = (layoutNode.y || 0) - top + lastGroupBottom
		})
		lastGroupBottom = lastGroupBottom + (bottom - top) + 80
	})

	return layoutGroups.flatMap(layoutGroup => {
		return layoutGroup.nodes.map(layoutNode => {
			const node = nodes.find(x => x.id == layoutNode.id)
			if (node) {
				node.position = {
					x: layoutNode.x || 0,
					y: layoutNode.y || 0
				}
			}
			return node
		}).filter(x => !!x)
	})
}
