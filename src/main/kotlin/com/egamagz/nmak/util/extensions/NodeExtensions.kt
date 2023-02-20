package com.egamagz.nmak.util.extensions

import org.w3c.dom.Node

fun Node.getAttribute(name: String): String =
    this.attributes.getNamedItem(name).nodeValue