package com.egamagz.nmak.scanner

import com.egamagz.nmak.exception.PortScannerError
import com.egamagz.nmak.util.extensions.outputToString
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
    private var nmapVersion: Pair<Int, Int> = Pair(0, 0)

    init {
        var validPath: String? = null

        runBlocking {
            for (path in searchPaths) {
                try {
                    val nmapProcess = process(
                        command = arrayOf(path, "-V"),
                        stdout = Redirect.CAPTURE,
                        stderr = Redirect.CAPTURE,
                        charset = Charsets.UTF_8,
                    )
                    validPath = path
                    lastOutput = nmapProcess.outputToString()
                    break
                } catch (e: IOException) {
                    continue
                }
            }
        }

        nmapPath = validPath ?: throw PortScannerError("Nmap program was not found in path.")

        extractVersion()
    }

    private fun extractVersion() {
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
            ?: throw PortScannerError("Nmap program was not found in path.")
        val version = regexVersion.find(output)?.value?.toInt() ?: 0
        val subVersion = regexSubVersion.find(output)?.value?.replace(".", "")?.toInt() ?: 0

        nmapVersion = Pair(version, subVersion)
    }

    fun getNmapVersion() = nmapVersion

    fun getLastOutput() = lastOutput

    suspend fun scan(
        hosts: String = "127.0.0.1",
        ports: String? = null,
        arguments: String = "-sV",
        sudo: Boolean = false,
    ): Map<String, Any> {
        val hostsArguments = hosts.split(" ")
        val command = mutableListOf(nmapPath, "-oX", "-").also { list ->
            list.addAll(hostsArguments)
            ports?.let {
                list.addAll(it.split(" "))
            }
            list.addAll(arguments.split(" "))
        }

        if (sudo)
            command.add(0, "sudo")

        val processResult = process(
            command = command.toTypedArray(),
            stdout = Redirect.CAPTURE,
            charset = Charsets.UTF_8,
            stderr = Redirect.CAPTURE,
        ).also {
            lastOutput = it.outputToString()
        }
        // TODO: GET WARNINGS AND ERRORS
        if (processResult.resultCode != 0)
            throw PortScannerError(processResult.outputToString())

        return analyzeNmapXML(
            nmapXMLOutput = processResult.outputToString(),
        )
    }

    fun analyzeNmapXML(nmapXMLOutput: String): Map<String, Any> {
        val scanResult = mutableMapOf<String, Any>()
        println(nmapXMLOutput)
        /*val document = nmapXMLOutput.toXmlDocument()
        val nmaprunNode = document.getElementsByTagName("nmaprun").let { if (it.length > 0) it.item(0) else null }
        println(nmaprunNode?.attributes?.getNamedItem("args")?.nodeValue)*/
        return scanResult
    }
}

fun main() {
    val portScanner = PortScanner()
    println(portScanner.getNmapVersion())

}