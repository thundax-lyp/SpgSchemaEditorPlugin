import React from 'react';
import {type Edge, type EdgeProps, getBezierPath, type Node, useEdges, useInternalNode, useNodes, XYPosition} from '@xyflow/react';
import {getEdgeParams} from "./utils";
import {TurboNodeData} from "@/pages/schema-diagram/components/TurboNode";
import {InternalNode} from "@xyflow/react/dist/esm/types";

export type TurboEdgeData = {
	unused?: any
}

const BezierEdge = (props: EdgeProps<Edge<TurboEdgeData>>) => {
	const {id, source, target, label, markerStart, markerEnd, style} = props

	const sourceNode = useInternalNode(source);
	const targetNode = useInternalNode(target);

	if (!sourceNode || !targetNode) {
		return null;
	}

	const {sourceX, sourceY, targetX, targetY, sourcePos, targetPos} = getEdgeParams(sourceNode, targetNode, 5);
	if (!sourceX || !sourceY || !targetX || !targetY) {
		// console.log(sourceNode, targetNode)
		return null;
	}

	const [edgePath, labelX, labelY] = getBezierPath({
		sourceX: sourceX === targetX ? sourceX + 0.0001 : sourceX,
		sourceY: sourceY === targetY ? sourceY + 0.0001 : sourceY,
		sourcePosition: sourcePos,
		targetX,
		targetY,
		targetPosition: targetPos,
	});

	const labelText = label?.toString() || ''
	const labelTextWidth = (labelText.length + labelText.replaceAll(/[ -~]+/g, '').length) * 6
	const labelStart = labelX - labelTextWidth / 2
	const labelTop = labelY + 6

	return (
		<>
			<path
				id={id}
				style={style}
				className="react-flow__edge-path"
				d={edgePath}
				markerStart={markerStart}
				markerEnd={markerEnd}
			>
			</path>
			<text
				fontSize="12px"
				fill="var(--entity-name-color)"
				transform={`translate(${labelStart}, ${labelTop})`}
				style={{
					textShadow: '0 0 20px var(--keyword-color)',
				}}
			>
				{label}
			</text>
		</>
	);
}


const PI = Math.PI
const TAU = 2 * Math.PI

const CubicBezierEdge = (props: EdgeProps<Edge<TurboEdgeData>>) => {
	const {source, label, markerStart, markerEnd, style} = props

	const sourceNode = useInternalNode(source);
	if (!sourceNode) {
		return null;
	}

	const width = sourceNode.measured?.width || 0.1
	const height = sourceNode.measured?.height || 0.1

	const getNodeCenterXY = (node: InternalNode): XYPosition => {
		return {
			x: node.internals.positionAbsolute.x + (node.measured?.width || 0) / 2,
			y: node.internals.positionAbsolute.y + (node.measured?.height || 0) / 2,
		}
	}

	const getAlpha = (source: XYPosition, target: XYPosition): number => {
		const dx = target.x - source.x
		const dy = target.y - source.y
		const radius = Math.sqrt(dx ** 2 + dy ** 2)
		let alpha = Math.asin(Math.abs(dy) / radius)
		alpha = dx < 0 ? PI - alpha : alpha
		alpha = dy < 0 ? -alpha : alpha
		alpha = alpha < 0 ? alpha + TAU : alpha
		return alpha
	}

	const edges = useEdges<Edge<TurboEdgeData>>()
		.filter(x => x.source == source || x.target == source)
		.filter(x => x.source != source || x.target != source)

	const nodePositions = useNodes<Node<TurboNodeData>>()
		.filter(x => x.id != source && edges.find(e => e.source == x.id || e.target == x.id))
		.map(x => useInternalNode(x.id))
		.filter(x => !!x)
		.map(x => getNodeCenterXY(x))

	const originXY: XYPosition = getNodeCenterXY(sourceNode)
	const radius = Math.sqrt((sourceNode.measured?.width || 0) ** 2 + (sourceNode.measured?.height || 0) ** 2) / 2

	const alphas = nodePositions.map((x, index) => ({index, alpha: getAlpha(originXY, x)}))
		.sort((a, b) => a.alpha - b.alpha)
	if (alphas.length == 0) {
		alphas.push({index: 0, alpha: 0})
	} else {
		alphas.push({index: 0, alpha: alphas[0].alpha + TAU})
	}

	const wedgeDelta = {index: 0, delta: 0}
	for (let i = 0; i < alphas.length - 1; i++) {
		if (alphas[i].alpha < alphas[i + 1].alpha) {
			const delta = alphas[i + 1].alpha - alphas[i].alpha
			if (delta > wedgeDelta.delta) {
				wedgeDelta.index = i
				wedgeDelta.delta = delta
			}
		}
	}
	const wedgeAlpha = alphas[wedgeDelta.index].alpha + wedgeDelta.delta / 2
	const wedgeA = (wedgeAlpha - (40 / radius) + TAU) % TAU
	const wedgeB = (wedgeAlpha + (40 / radius) + TAU) % TAU

	const getCrossingPoint = (width: number, height: number, alpha: number): XYPosition => {
		const x1 = (alpha > (PI * .5) && alpha < (PI * 1.5) ? -1 : 1) * width / 2
		const y1 = x1 * Math.tan(alpha)
		const y2 = (alpha > PI && alpha < TAU ? -1 : 1) * height / 2
		const x2 = y2 / Math.tan(alpha)
		if (x1 ** 2 + y1 ** 2 < x2 ** 2 + y2 ** 2) {
			return {
				x: x1, y: y1
			}
		}
		return {
			x: x2, y: y2
		}
	}

	const sourceXY = getCrossingPoint(width, height, wedgeA)
	const tension = 60
	const passingA = {
		x: (radius + tension) * Math.cos(wedgeA),
		y: (radius + tension) * Math.sin(wedgeA),
	}
	const passingB = {
		x: (radius + tension) * Math.cos(wedgeB),
		y: (radius + tension) * Math.sin(wedgeB),
	}
	const targetXY = getCrossingPoint(width, height, wedgeB)

	const labelX = ((radius + tension) * 0.85) * Math.cos(wedgeAlpha)
	const labelY = ((radius + tension) * 0.85) * Math.sin(wedgeAlpha)

	const labelText = label?.toString() || ''
	const labelTextWidth = (labelText.length + labelText.replaceAll(/[ -~]+/g, '').length) * 6
	const labelStart = labelX - labelTextWidth / 2
	const labelTop = labelY + 6

	return (
		<g transform={`translate(${originXY.x}, ${originXY.y})`}>
			<path
				style={style}
				className="react-flow__edge-path"
				d={`M${sourceXY.x},${sourceXY.y} C${passingA.x},${passingA.y} ${passingB.x},${passingB.y} ${targetXY.x},${targetXY.y}`}
				markerStart={markerStart}
				markerEnd={markerEnd}
			/>
			<text
				fontSize="12px"
				fill="var(--entity-name-color)"
				transform={`translate(${labelStart}, ${labelTop})`}
				style={{
					textShadow: '0 0 20px var(--keyword-color)',
				}}
			>
				{label}
			</text>
		</g>
	);
}

const TurboEdge = (props: EdgeProps<Edge<TurboEdgeData>>) => {

	const {source, target} = props

	const sourceNode = useInternalNode(source);
	const targetNode = useInternalNode(target);
	if (!sourceNode || !targetNode) {
		return null;
	}


	if (source == target) {
		return <CubicBezierEdge {...props}/>
	}

	return <BezierEdge {...props}/>
}

export default TurboEdge
