package com.wutsi.platform.account.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ImageKitTest {
    private val service = ImageKit("http://www.google.com", "http://www.imagekit.io/43043094")

    @Test
    fun transformWidthAndHeight() {
        val url = "http://www.google.com/img/a/b/1.png"
        val result = service.transform(url, 200, 150)

        assertEquals("http://www.imagekit.io/43043094/img/a/b/tr:w-200,h-150/1.png", result)
    }

    @Test
    fun transformWidth() {
        val url = "http://www.google.com/img/a/b/1.png"
        val result = service.transform(url, 200)

        assertEquals("http://www.imagekit.io/43043094/img/a/b/tr:w-200/1.png", result)
    }

    @Test
    fun transformHeight() {
        val url = "http://www.google.com/img/a/b/1.png"
        val result = service.transform(url, null, 150)

        assertEquals("http://www.imagekit.io/43043094/img/a/b/tr:h-150/1.png", result)
    }

    @Test
    fun transformWithFaceCrop() {
        val url = "http://www.google.com/img/a/b/1.png"
        val result = service.transform(url, 400, 150, true)

        assertEquals("http://www.imagekit.io/43043094/img/a/b/tr:w-400,h-150,fo-face/1.png", result)
    }

    @Test
    fun transformNone() {
        val url = "http://www.google.com/img/a/b/1.png"
        val result = service.transform(url)

        assertEquals("http://www.imagekit.io/43043094/img/a/b/1.png", result)
    }

    @Test
    fun transformInvalidOrigin() {
        val url = "http://www.yo.com/img/a/b/1.png"
        val result = service.transform(url)

        assertEquals(url, result)
    }
}
