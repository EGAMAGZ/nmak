package com.egamagz.nmak.scanner

import com.egamagz.nmak.exception.PortScannerError
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
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
}