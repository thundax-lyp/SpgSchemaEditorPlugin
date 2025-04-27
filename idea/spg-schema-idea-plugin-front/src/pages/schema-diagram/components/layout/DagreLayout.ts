import dagre from "@dagrejs/dagre";

import {Layout, LayoutGroup, LayoutNode} from "./types";

export type Direction = 'TB' | 'LR' | 'RL' | 'BT';

const DEFAULT_WIDTH = 150
const DEFAULT_HEIGHT = 20
const OFFSET_X = 32

export interface DagreLayoutProps {
	direction?: Direction
	maxFreeNodeCount?: number
}

export const DagreLayout = (props?: DagreLayoutProps): Layout => {
	const {
		direction = 'LR',
		maxFreeNodeCount = 3,
	} = props || {}

	const layout = (param: LayoutGroup): Array<LayoutNode> => {
		const {nodes, edges} = param
		if (nodes.length == 0) {
			return []
		}

		const isHorizontal = direction === 'LR';

		const dagreGraph = new dagre.graphlib.Graph({
			directed: false
		}).setDefaultEdgeLabel(() => ({}));

		dagreGraph.setGraph({rankdir: direction});

		nodes.forEach(node => {
			const {width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT} = node
			dagreGraph.setNode(node.id, {
				width: width + OFFSET_X, height,
			});
		});

		edges.forEach((edge) => {
			dagreGraph.setEdge(edge.source.id, edge.target.id)
		});

		const freeNodes: Array<LayoutNode> = []

		const flushFreeNodes = () => {
			for (let i = 0; i < freeNodes.length - 1; i += 1) {
				dagreGraph.setEdge(freeNodes[i].id, freeNodes[i + 1].id)
			}
			freeNodes.splice(0, freeNodes.length)
		}

		nodes.forEach(node => {
			if (!edges.find(edge => edge.source.id === node.id || edge.target.id === node.id)) {
				freeNodes.push(node)
				if (freeNodes.length >= maxFreeNodeCount) {
					flushFreeNodes()
				}
			} else {
				flushFreeNodes()
			}
		})
		flushFreeNodes()

		dagre.layout(dagreGraph);

		return nodes.map((node) => {
			const nodeWithPosition = dagreGraph.node(node.id);
			const {width = 0, height = 0} = node
			return {
				...node,
				x: nodeWithPosition.x - (isHorizontal ? 0 : width / 2),
				y: nodeWithPosition.y - (isHorizontal ? height / 2 : 0),
			}
		});
	}

	return {
		name: 'DagreLayout',
		layout
	}
}
// targetPosition: isHorizontal ? 'left' : 'top',
// sourcePosition: isHorizontal ? 'right' : 'bottom',
