import React, {memo, ReactNode} from 'react';

import {Handle, type Node, type NodeProps, Position} from '@xyflow/react';
import {SchemaEntity} from "../types";
import {ConceptIcon, EntityIcon, EventIcon, FloatIcon, IntegerIcon, PropertyIcon, RelationIcon, TextIcon} from "./icons";

export type TurboNodeData = {
	entity?: SchemaEntity
	width?: number
	height?: number
};

const BUILTIN_TYPE_ICONS: {
	[key: string]: ReactNode
} = {
	'ConceptType': <ConceptIcon/>,
	'EntityType': <EntityIcon/>,
	'EventType': <EventIcon/>,
	'Float': <FloatIcon/>,
	'Integer': <IntegerIcon/>,
	'Text': <TextIcon/>,
}

const isBuiltinType = (type: string) => {
	return Object.keys(BUILTIN_TYPE_ICONS).includes(type)
}

const TurboNode = memo((props: NodeProps<Node<TurboNodeData>>) => {
	const {data = {}} = props

	const {entity = {}} = data;
	const builtinTypes = (entity.types || []).filter(x => isBuiltinType(x))

	const handleTypeClick = (type: string) => {
		if (!isBuiltinType(type)) {
			console.log(`${type} click`)
		}
	}

	const properties = (entity.properties || []).filter(property => {
		return (property.types || []).filter(x => isBuiltinType(x)).length > 0
	})

	const relations = (entity.relations || []).filter(relation => {
		return (relation.types || []).filter(x => isBuiltinType(x)).length > 0
	})

	return (
		<>
			<div className="turbo-clouds">
				{builtinTypes.map((type, idx) => {
					return <div className="turbo-cloud gradient" key={idx}>
						<div>
							{BUILTIN_TYPE_ICONS[type]}
						</div>
					</div>
				})}
			</div>
			<div className="turbo-body-wrapper gradient">
				<div className="inner">
					<div className="turbo-body">
						<div className="turbo-title-container">
							<div className="turbo-title">{entity.name}</div>
							<div className="turbo-subtitle">{entity.aliasName}</div>
						</div>
						{properties.length > 0 && <div className="turbo-properties-container">
							{properties.map((property) => {
								const {id, name, aliasName, types} = property
								return <div key={id} className="turbo-property">
									<div className="turbo-property-icon"><PropertyIcon/></div>
									<div className="turbo-property-title">{name}</div>
									<div className="turbo-property-subtitle">{aliasName}</div>
									<div className="turbo-property-types">
										{types?.map((type, idx) => {
											return <div
												key={idx}
												className={`turbo-property-type ${isBuiltinType(type) && "builtin"}`}
												onClick={() => handleTypeClick(type)}
											>
												{type}
											</div>
										})}
									</div>
								</div>
							})}
						</div>}
						{relations.length > 0 && <div className="turbo-relations-container">
							{relations.map((relation) => {
								const {id, name, aliasName, types} = relation
								return <div key={id} className="turbo-property">
									<div className="turbo-relation-icon"><RelationIcon/></div>
									<div className="turbo-relation-title">{name}</div>
									<div className="turbo-relation-subtitle">{aliasName}</div>
									<div className="turbo-relation-types">
										{types?.map((type, idx) => {
											return <div
												key={idx}
												className={`turbo-property-type ${isBuiltinType(type) && "builtin"}`}
												onClick={() => handleTypeClick(type)}
											>
												{type}
											</div>
										})}
									</div>
								</div>
							})}
						</div>}
					</div>
					<Handle type="source" position={Position.Right}/>
					<Handle type="target" position={Position.Left}/>
				</div>
			</div>
		</>
	);
});

export default TurboNode
