const useScroll = (scrollElement?: HTMLDivElement) => {

    const scrollToBottom = () => {
        if (scrollElement) {
            scrollElement.scrollTop = scrollElement.scrollHeight
        }
    }

    const scrollToTop = () => {
        if (scrollElement) {
            scrollElement.scrollTop = 0
        }
    }

    const scrollToBottomIfAtBottom = () => {
        if (scrollElement) {
            const threshold = 100 // 阈值，表示滚动条到底部的距离阈值
            const distanceToBottom = scrollElement.scrollHeight - scrollElement.scrollTop - scrollElement.clientHeight;
            if (distanceToBottom <= threshold) {
                scrollElement.scrollTop = scrollElement.scrollHeight
            }
        }
    }

    return {
        scrollElement,
        scrollToBottom,
        scrollToTop,
        scrollToBottomIfAtBottom,
    }
}

export default useScroll
