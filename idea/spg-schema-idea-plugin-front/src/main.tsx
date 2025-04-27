import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './pages/App'
import {BrowserRouter} from 'react-router-dom'
import Global from './components/global'

import '@/styles/global.less'
import Loading from "@/components/loading";


ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
	// @ts-ignore
	<BrowserRouter basename={import.meta.env.BASE_URL}>
		<Global>
			<React.Suspense
				fallback={(
					<div
						style={{
							width: '100vw',
							height: '100vh',
							display: 'flex',
							alignItems: 'center',
							justifyContent: 'center'
						}}
					>
						<Loading rotate width="3em" height="3em"/>
					</div>
				)}
			>
				<App/>
			</React.Suspense>
		</Global>
	</BrowserRouter>
)
