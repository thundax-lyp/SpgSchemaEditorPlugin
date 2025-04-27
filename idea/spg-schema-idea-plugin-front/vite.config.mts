import { defineConfig, loadEnv } from 'vite'
import { resolve } from 'path'
import react from '@vitejs/plugin-react-swc'
import {viteMockServe} from 'vite-plugin-mock'


export default defineConfig(({mode}) => {
	const {VITE_PUBLIC_PATH} = loadEnv(mode, process.cwd(), "");
	return {
		plugins: [
			react(),
			viteMockServe({
				mockPath: 'mock',
				enable: true,
				watchFiles: true
			})
		],
		build: {
			outDir: 'dist'
		},
		resolve: {
			alias: {
				'@': resolve(__dirname, './src'),
			},
		},
		css: {
			preprocessorOptions: {
				less: {
					javascriptEnabled: true
				}
			}
		},
		server: {
			host: '0.0.0.0',
			port: 9000,
			hmr: true,
			proxy: {
				'/openspg/': {
					target: 'http://127.0.0.1:19994/',
					changeOrigin: true,
					secure: false,
					ws: true,
				},
			},
		},
		base: `${VITE_PUBLIC_PATH}`
	}
})
