package com.egamagz.nmak.scanner

import com.egamagz.nmak.exception.PortScannerError
import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import kotlinx.coroutines.runBlocking
import java.io.IOException

val defaultNmapPaths = listOf(
    "nmap",
    "/usr/bin/nmap",
    "/usr/local/bin/nmap",
    "/sw/bin/nmap",
    "/opt/local/bin/nmap",
)

class PortScanner(
    private val searchPaths: List<String> = defaultNmapPaths,
) {
    private var nmapPath: String
    private var lastOutput: String? = null

    init {
        var validPath: String? = null

        runBlocking {
            for (path in searchPaths) {
                try {
                    val nmapProcess = process(
                        command = arrayOf(path, "-V"),
                        stdout = Redirect.CAPTURE,
                        charset = Charsets.UTF_8,
                    )
                    validPath = path
                    lastOutput = nmapProcess.output.joinToString()
                    break
                } catch (e: IOException) {
                    continue
                }
            }
        }
        nmapPath = validPath ?: throw PortScannerError("Nmap program was not found in path.")
    }

    fun getNmapVersion(): Pair<Int, Int> {
        val regex = "Nmap version [0-9]*\\.[0-9]*[^ ]* \\( http(|s)://.* \\)".toRegex()
        val regexVersion = "[0-9]+".toRegex()
        val regexSubVersion = "\\.[0-9]+".toRegex()

        val processResult = runBlocking {
            return@runBlocking process(
                command = arrayOf(nmapPath, "-V"),
                stdout = Redirect.CAPTURE,
                charset = Charsets.UTF_8,
            ).also {
                lastOutput = it.output.joinToString()
            }
        }
        val output = processResult.output.find { regex.matches(it) }
            ?: throw PortScannerError("Nmap version was not found in the path.")
        val version = regexVersion.find(output)?.value?.toInt() ?: 0
        val subVersion = regexSubVersion.find(output)?.value?.replace(".", "")?.toInt() ?: 0

        return Pair(version, subVersion)
    }

    fun getLastOutput() = lastOutput
}

fun main() {
    val portScanner = PortScanner()
    println(portScanner.getNmapVersion())
    print(portScanner.getLastOutput())
}