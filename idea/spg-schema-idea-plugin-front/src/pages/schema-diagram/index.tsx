import React, {useCallback, useEffect, useState} from 'react'
import useDebounce from '@/hooks/use-debounce';
import Turbo from "./components/Turbo";
import {SchemaEntity, SchemaNamespace} from "./types";
import service from "./service"

import styles from "./index.module.less"

const SchemaDiagramPage = () => {

    const [namespace, setNamespace] = useState<SchemaNamespace>()
    const [entities, setEntities] = useState<SchemaEntity[]>([])
    const [selectedEntities, setSelectedEntities] = useState<SchemaEntity[]>()

    const [schemaVersion, setSchemaVersion] = useState<number>(0);
    const debouncedSchemaVersion = useDebounce<number>(schemaVersion, 500);

    const [cssStyle, setCssStyle] = useState<string>("");

    const requestSchema = () => {
        service.requestSchema().then(({namespace, entities = []}) => {
            setNamespace(namespace)
            setEntities([...entities])
            console.log(namespace?.value, entities)
        })
    }

    const onRefreshCssClick = useCallback(() => {
        service.requestCss().then((x) => {
            setCssStyle(x)
        })
    }, [])

    const onRefreshSchemaClick = useCallback(() => {
        setSchemaVersion(new Date().getTime())
    }, [])

    const onActivateEntityClick = useCallback(() => {
        // @ts-ignore
        const currentEntities = entities.filter(x => x.name === window['activeEntityName'])
        setSelectedEntities([...currentEntities])
    }, [entities])

    useEffect(() => {
        if (debouncedSchemaVersion > 0) {
            requestSchema()
        }
    }, [debouncedSchemaVersion]);

    useEffect(() => {
        requestSchema()
    }, [])

    const toolbarItemStyle = {
        padding: "8px",
        border: "solid 1px #00f",
    }

    return (
        <>
            <div style={{display: "none"}}>
                <a id="schema-diagram-refresh-css-button" onClick={() => onRefreshCssClick()} style={toolbarItemStyle}>refresh css</a>
                <a id="schema-diagram-refresh-button" onClick={() => onRefreshSchemaClick()} style={toolbarItemStyle}>refresh schema</a>
                <a id="schema-diagram-active-entity-button" onClick={() => onActivateEntityClick()} style={toolbarItemStyle}>activate entity</a>
                <a style={toolbarItemStyle}>Name: {namespace?.value}</a>
            </div>
            <div className={styles.diagramContainer}>
                <Turbo
                    initialEntities={entities}
                    selection={selectedEntities}
                    onSelectionChange={(entities) => {
                        setSelectedEntities(entities)
                        service.activateEntities(entities).then(() => {
                            console.log('activate entities success')
                        })
                    }}
                />
            </div>
            <style>
                {cssStyle}
            </style>
        </>
    )
}

export default SchemaDiagramPage
