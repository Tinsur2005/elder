/*
 * ============================================================
 *
 *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *      ╚═╝   ╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝
 *
 *  项目名称 : 智慧社区养老系统
 *  源码作者 : Tinsur (tinsur.cn)
 *  作者主页 : https://tinsur.cn
 *  联系方式 : me@tinsur.cn
 *  开源协议 : GPL 3.0
 *
 * ============================================================
 */
import {showToast} from 'vant'
import router from '@/router'
import {useTokenStore} from '@/store/token.js'

const baseURL = '/app/api'

const chatApi = {
    // 流式AI对话：POST + SSE 无法使用 EventSource（只支持 GET），用 fetch 读取响应流
    // 收到一段回复通过 onMessage 回调传出（结束标记已在内部处理），流结束后 Promise 才完成
    chatStream(message, onMessage) {
        return new Promise((resolve, reject) => {
            const tokenStore = useTokenStore()
            fetch(`${baseURL}/chat/chatStream?message=${encodeURIComponent(message)}`, {
                method: 'POST',
                headers: {Authorization: tokenStore.token || ''}
            }).then(response => {
                if (!response.ok) {
                    if (response.status === 401) {
                        //登录失效与 request.js 保持一致的处理方式
                        tokenStore.removeToken()
                        showToast('登录失效，请先登录')
                        router.push('/login')
                    } else {
                        showToast('服务异常')
                    }
                    reject(new Error('服务异常'))
                    return
                }
                const reader = response.body.getReader()
                const decoder = new TextDecoder('utf-8')
                //SSE事件可能被网络分包截断，用缓冲区拼齐后再按空行拆分事件
                let buffer = ''
                //处理一个完整的SSE事件：内容含换行时会被拆成多个 data: 行，需按行还原
                const handleEvent = (eventText) => {
                    const content = eventText.split('\n')
                        .filter(line => line.startsWith('data:'))
                        .map(line => line.slice(5))
                        .join('\n')
                    if (!content) {
                        return
                    }
                    if (content === '[END]') {
                        //后端约定的流结束标记，不再传给页面
                        resolve()
                        return
                    }
                    onMessage(content)
                }
                const pump = () => {
                    reader.read().then(({done, value}) => {
                        if (done) {
                            //流正常关闭但未收到结束标记时也算完成
                            resolve()
                            return
                        }
                        buffer += decoder.decode(value, {stream: true})
                        const events = buffer.split('\n\n')
                        //最后一段可能是不完整事件，留到下一轮拼接
                        buffer = events.pop()
                        events.forEach(handleEvent)
                        pump()
                    }).catch(reject)
                }
                pump()
            }).catch(error => {
                showToast('服务异常')
                reject(error)
            })
        })
    }
}
export default chatApi