import {Schema, SchemaEntity} from "./types"

const API_PREFIX = `/openspg/api`

interface ResponseWrapper {
	code: number
	message: string
	data: SchemaVO
}

interface SchemaVO {
	namespace?: NamespaceVO
	entities?: EntityVO[]
}

interface NamespaceVO {
	value?: string
}

interface EntityVO {
	name?: string
	aliasName?: string
	types?: string[]
	properties?: PropertyVO[]
}

interface PropertyVO {
	name?: string
	value?: string
	children?: EntityVO[]
}

const normalizeEntity = ({name, aliasName, types, properties}: EntityVO, index: number, prefix = ''): SchemaEntity => {
	const entity: SchemaEntity = {
		id: `${prefix}${index}`, name, aliasName, types
	}
	properties?.forEach(({name, value, children = []}) => {
		if (name === 'desc' && value) {
			entity.desc = value.trim()

		} else if (name === 'index' && value) {
			entity.index = value.trim()

		} else if (name === 'hypernymPredicate' && value) {
			entity.hypernymPredicate = value.trim()

		} else if (name === 'regular' && value) {
			entity.regular = value.trim()

		} else if (name === 'spreadable' && typeof value !== "undefined") {
			entity.spreadable = value.trim()

		} else if (name === 'autoRelate' && value) {
			entity.autoRelate = value.trim()

		} else if (name === 'properties' && children?.length > 0) {
			entity.properties = children.map((x, index) => normalizeEntity(x, index, `${entity.id}_`))

		} else if (name === 'relations' && children?.length > 0) {
			entity.relations = children.map((x, index) => normalizeEntity(x, index, `${entity.id}_`))
		}
	})
	return entity;
}


export const requestSchema = async (): Promise<Schema> => {
	const response = await fetch(`${API_PREFIX}/schema/fetch`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({})
	})

	const responseBody: ResponseWrapper = await response.json()

	const {namespace = {}, entities = []} = responseBody?.data || {}
	return Promise.resolve({
		namespace: {
			value: namespace.value
		},
		entities: entities.map((x, index) => normalizeEntity(x, index, 'schema-')).filter(x => x)
	})
}

export const activateEntities = async (entities: SchemaEntity[]): Promise<boolean> => {
	const response = await fetch(`${API_PREFIX}/schema/focus`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(entities.map(({id}) => ({id})))
	})

	const responseBody: ResponseWrapper = await response.json()

	return Promise.resolve(responseBody.code === 0)
}

export const requestCss = async (): Promise<string> => {
	const response = await fetch(`/schema-theme.css`)

	const responseBody: string = await response.text()

	return Promise.resolve(responseBody)
}

export default {
	requestSchema,
	activateEntities,
	requestCss,
}
