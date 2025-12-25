import request from '@/utils/requestUtil'

// 获取关于我们的内容
export function fetchAboutUsContent() {
    return request({
        method: 'GET',
        url: '/content/aboutUs'
    })
}

// 获取联系信息
export function fetchContactInfo() {
    return request({
        method: 'GET',
        url: '/content/contactInfo'
    })
}

// 获取协议内容
export function fetchAgreementContent() {
    return request({
        method: 'GET',
        url: '/content/agreement'
    })
}

// 获取隐私政策内容
export function fetchPrivacyContent() {
    return request({
        method: 'GET',
        url: '/content/privacy'
    })
} 