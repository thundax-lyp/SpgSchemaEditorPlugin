export const traverseJSON = (schema: any | Array<any>, fn: (_: any) => void | boolean) => {
	if (!schema) {
		return
	}

	if (Array.isArray(schema)) {
		for (let idx = 0; idx < schema.length; idx += 1) {
			traverseJSON(schema[idx], fn)
		}

	} else if (typeof schema === 'object') {
		fn(schema)

		for (const prop in schema) {
			traverseJSON(schema[prop], fn);
		}
	}
}
