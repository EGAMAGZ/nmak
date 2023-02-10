package com.egamagz.nmak.util.extensions

import com.github.pgreze.process.ProcessResult

fun ProcessResult.outputToString(): String =
    this.output.joinToString(separator = " ")