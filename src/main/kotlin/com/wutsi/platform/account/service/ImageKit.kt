package com.wutsi.platform.account.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ImageKit(
    @Value("\${wutsi.image-kit.origin-url}") private val originUrl: String,
    @Value("\${wutsi.image-kit.endpoint-url}") private val endpoint: String
) {
    fun transform(url: String, width: Int? = null, height: Int? = null, faceCrop: Boolean = false): String {
        if (!accept(url)) {
            return url
        }

        val xurl = endpoint + url.substring(originUrl.length)
        val i = xurl.lastIndexOf('/')
        val prefix = xurl.substring(0, i)
        val suffix = xurl.substring(i)
        val tr = transformations(width, height, faceCrop)
        return prefix + tr + suffix
    }

    private fun accept(url: String) = url.startsWith(originUrl)

    private fun transformations(width: Int? = null, height: Int? = null, faceCrop: Boolean): String {
        if (width == null && height == null) {
            return ""
        }
        val sb = StringBuilder()
        if (width != null) {
            sb.append("w-$width")
        }
        if (height != null) {
            if (sb.isNotEmpty()) {
                sb.append(",")
            }
            sb.append("h-$height")
        }
        if (faceCrop) {
            if (sb.isNotEmpty()) {
                sb.append(",")
            }
            sb.append("fo-face")
        }
        return "/tr:$sb"
    }
}
