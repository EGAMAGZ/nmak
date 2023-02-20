package com.egamagz.nmak.util.extensions

import org.w3c.dom.Node
import org.w3c.dom.NodeList

/*operator fun NodeList.iterator(): Iterator<Node> =
    (0 until length).map(this::item).iterator()*/

fun NodeList.toList(): List<Node> =
    (0 until length).map(this::item)
