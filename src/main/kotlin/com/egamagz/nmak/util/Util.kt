package com.egamagz.nmak.util

object Util {
    fun isWindows(): Boolean = System.getProperty("os.name").lowercase().contains("windows")
}