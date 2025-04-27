import {useMemo} from 'react'
import {useRoutes} from 'react-router-dom'
import {webRouter} from '@/routers'

function App() {

    const routers: Array<any> = useMemo(() => {
        return [...webRouter]
    }, [])

    return useRoutes([...routers])
}

export default App
