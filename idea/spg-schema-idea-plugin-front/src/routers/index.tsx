import React from 'react'
import type {RouteObject} from 'react-router-dom'

export interface RouteOptions extends Omit<Omit<RouteObject, 'children'>, 'index'> {
	index?: boolean
	children?: RouteOptions[]
}

const SchemaDiagramPage = React.lazy(() => import('@/pages/schema-diagram'))
const Page404 = React.lazy(() => import('@/pages/404'))

export const webRouter: RouteOptions[] = [{
	id: 'preview',
	path: '/',
	element: <SchemaDiagramPage/>,
	children: [],
}, {
	id: '404',
	path: '/404',
	element: <Page404/>,
	children: [],
}, {
	id: 'others',
	path: '*',
	element: <Page404/>,
	children: [],
}]

export default {}
