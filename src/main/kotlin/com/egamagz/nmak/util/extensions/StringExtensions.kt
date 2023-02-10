package com.egamagz.nmak.util.extensions

import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

fun String.toXmlDocument(): Document = DocumentBuilderFactory
    .newInstance()
    .newDocumentBuilder()
    .parse(InputSource(StringReader(this)))