export interface LayoutNode {
	id: string
	x?: number
	y?: number
	width?: number
	height?: number
}

export interface LayoutEdge {
	source: LayoutNode
	target: LayoutNode
	weight?: number
}

export interface LayoutGroup {
	nodes: Array<LayoutNode>
	edges: Array<LayoutEdge>
}

export interface Layout {
	name: string
	layout: (props: LayoutGroup) => Array<LayoutNode>
}
