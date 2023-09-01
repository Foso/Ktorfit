package com.example.ktorfittest

actual class Platform actual constructor() {
    actual val platform: String
        get() = "JS"
}