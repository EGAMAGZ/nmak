package com.egamagz.nmak.scanner

import com.egamagz.nmak.exception.PortScannerError
import com.egamagz.nmak.util.Util
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.assertThrows

class PortScannerTest {
    private var portScanner: PortScanner = PortScanner()

    @Test
    fun `Test invalid path`() {
        assertThrows<PortScannerError> {
            PortScanner(listOf("invalid-nmap"))
        }
    }

    @Test
    fun `Test scan with wrong arguments`() {
        assertThrows<PortScannerError> {
            runBlocking {
                portScanner.scan(arguments = "--wrong-args")
            }
        }
    }

    @Test
    fun `Test scan with sudo on Windows`() {
        assumeTrue(Util.isWindows())

        assertThrows<PortScannerError> {
            runBlocking {
                portScanner.scan(sudo = true)
            }
        }
    }
}