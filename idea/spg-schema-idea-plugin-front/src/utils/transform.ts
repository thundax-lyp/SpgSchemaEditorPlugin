export const centToYuan = (cent?: string | number) => {
    if (!cent) return 0
    return (Number(cent) / 100).toFixed(2)
}

