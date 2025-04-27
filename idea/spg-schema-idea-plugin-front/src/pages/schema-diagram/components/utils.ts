import {Rect} from "@xyflow/system/dist/esm/types/utils";
import type {InternalNode} from "@xyflow/react/dist/esm/types";
import {Position, XYPosition} from "@xyflow/react";

const inflateRect = (rect: Rect, padding: number): Rect => {
	const {x, y, width, height} = rect
	return {
		x: x - padding,
		y: y - padding,
		width: width + padding * 2,
		height: height + padding * 2,
	}
}

// this helper function returns the intersection point
// of the line between the center of the intersectionNode and the target node
const getNodeIntersection = (intersectionNode: InternalNode, targetNode: InternalNode, padding = 0): XYPosition => {
	// https://math.stackexchange.com/questions/1724792/an-algorithm-for-finding-the-intersection-point-between-a-center-of-vision-and-a
	// const intersectionNodeWidth = (intersectionNode.measured.width || 0) + padding * 2
	// const intersectionNodeHeight = (intersectionNode.measured.height || 0) + padding * 2
	// const intersectionNodePosition = translation(intersectionNode.internals.positionAbsolute, {x: -padding, y: -padding});

	const intersectionNodeRect: Rect = inflateRect({
		...intersectionNode.internals.positionAbsolute,
		width: intersectionNode.measured.width || 0,
		height: intersectionNode.measured.height || 0,
	}, padding)

	const targetNodeRect: Rect = inflateRect({
		...targetNode.internals.positionAbsolute,
		width: intersectionNode.measured.width || 0,
		height: intersectionNode.measured.height || 0,
	}, padding)

	const w = intersectionNodeRect.width / 2;
	const h = intersectionNodeRect.height / 2;

	const x2 = intersectionNodeRect.x + w;
	const y2 = intersectionNodeRect.y + h;
	const x1 = targetNodeRect.x + (targetNodeRect.width) / 2;
	const y1 = targetNodeRect.y + (targetNodeRect.height) / 2;

	const xx1 = (x1 - x2) / (2 * w) - (y1 - y2) / (2 * h);
	const yy1 = (x1 - x2) / (2 * w) + (y1 - y2) / (2 * h);
	const a = 1 / (Math.abs(xx1) + Math.abs(yy1));
	const xx3 = a * xx1;
	const yy3 = a * yy1;
	const x = w * (xx3 + yy3) + x2;
	const y = h * (-xx3 + yy3) + y2;

	return {x, y};
}

// returns the position (top,right,bottom or right) passed node compared to the intersection point
const getEdgePosition = (node: InternalNode, intersectionPoint: XYPosition): Position => {
	const n = {...node.internals.positionAbsolute, ...node};
	const nx = Math.round(n.x);
	const ny = Math.round(n.y);
	const px = Math.round(intersectionPoint.x);
	const py = Math.round(intersectionPoint.y);

	if (px <= nx + 1) {
		return Position.Left;
	}
	if (px >= nx + (n.measured.width || 0) - 1) {
		return Position.Right;
	}
	if (py <= ny + 1) {
		return Position.Top;
	}
	if (py >= n.y + (n.measured.height || 0) - 1) {
		return Position.Bottom;
	}

	return Position.Top;
}

// returns the parameters (sx, sy, tx, ty, sourcePos, targetPos) you need to create an edge
export const getEdgeParams = (source: InternalNode, target: InternalNode, padding = 0) => {
	const sourceIntersectionPoint = getNodeIntersection(source, target, padding);
	const targetIntersectionPoint = getNodeIntersection(target, source, padding);

	const sourcePos = getEdgePosition(source, sourceIntersectionPoint);
	const targetPos = getEdgePosition(target, targetIntersectionPoint);

	return {
		sourceX: sourceIntersectionPoint.x,
		sourceY: sourceIntersectionPoint.y,
		targetX: targetIntersectionPoint.x,
		targetY: targetIntersectionPoint.y,
		sourcePos,
		targetPos,
	};
}

export const fissionAnchor = (anchor: XYPosition, arc = 40.0): [XYPosition, XYPosition] | undefined => {
	const radius = Math.sqrt(anchor.x ** 2 + anchor.y ** 2)
	if (radius <= 20) {
		return undefined
	}
	const delta = Math.PI * arc / radius

	let alpha = Math.asin(Math.abs(anchor.y) / radius)
	alpha = anchor.x < 0 ? Math.PI - alpha : alpha
	alpha = anchor.y < 0 ? -alpha : alpha

	const scale = 1.6
	return [{
		x: radius * Math.cos(alpha + delta) * scale,
		y: radius * Math.sin(alpha + delta) * scale,
	}, {
		x: radius * Math.cos(alpha - delta) * scale,
		y: radius * Math.sin(alpha - delta) * scale,
	}]
}
