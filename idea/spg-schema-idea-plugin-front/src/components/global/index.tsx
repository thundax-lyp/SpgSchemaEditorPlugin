import React from 'react'

type GlobalProps = {
    children: React.ReactElement
}

const Global = (props: GlobalProps) => {

    return (
        <>
            {props.children}
        </>
    )
}

export default Global
