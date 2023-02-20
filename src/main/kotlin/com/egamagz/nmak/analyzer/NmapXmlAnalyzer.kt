package com.egamagz.nmak.analyzer

import com.egamagz.nmak.model.*
import com.egamagz.nmak.util.extensions.getAttribute
import com.egamagz.nmak.util.extensions.toList
import com.egamagz.nmak.util.extensions.toXmlDocument
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class NmapXmlAnalyzer(
    xmlString: String
) {
    private val xmlDocument = xmlString.toXmlDocument()
    private val xPath = XPathFactory.newInstance().newXPath()
    private val documentElement = xmlDocument.documentElement

    private fun getArgs(): String = documentElement.getAttribute("args")

    private fun getRunStats(): RunStats {
        val hostsNode = xPath.compile("/nmaprun/runstats/hosts")
            .evaluate(documentElement, XPathConstants.NODESET)
            .let {
                (it as NodeList).toList().first()
            }
        val finishedNode = xPath.compile("/nmaprun/runstats/finished")
            .evaluate(documentElement, XPathConstants.NODESET)
            .let {
                (it as NodeList).toList().first()
            }

        return RunStats(
            hosts = HostsStats(
                up = hostsNode.getAttribute("up").toInt(),
                down = hostsNode.getAttribute("down").toInt(),
                total = hostsNode.getAttribute("total").toInt(),
            ),
            finishedStats = FinishedStats(
                elapsed = finishedNode.getAttribute("elapsed").toFloat(),
                time = finishedNode.getAttribute("time").toLong(),
                exit = finishedNode.getAttribute("exit"),
            )
        )
    }

    private fun getScanInfo(): List<ScanInfo> {
        val scanInfoNodeList = xPath.compile("/nmaprun/scaninfo")
            .evaluate(documentElement, XPathConstants.NODESET)
            .let {
                (it as NodeList).toList()
            }

        return scanInfoNodeList.map {
            ScanInfo(
                protocol = it.getAttribute("protocol"),
                type = it.getAttribute("type"),
                services = Services(
                    services = it.getAttribute("services"),
                    numServices = it.getAttribute("numservices").toInt(),
                )
            )
        }
    }

    /**
     * Create function to get all hosts with their respective ports. Nmap run xml looks like this:
     *
     *         <host starttime="1267974521" endtime="1267974522">
     *            <status state="up" reason="user-set"/>
     *            <address addr="192.168.1.1" addrtype="ipv4" />
     *            <hostnames><hostname name="neufbox" type="PTR" /></hostnames>
     *            <ports>
     *              <port protocol="tcp" portid="22">
     *                <state state="filtered" reason="no-response" reason_ttl="0"/>
     *                <service name="ssh" method="table" conf="3" />
     *              </port>
     *              <port protocol="tcp" portid="25">
     *                <state state="filtered" reason="no-response" reason_ttl="0"/>
     *                <service name="smtp" method="table" conf="3" />
     *              </port>
     *            </ports>
     *            <hostscript>
     *             <script id="nbstat" output="NetBIOS name: GROSTRUC, NetBIOS user: &lt;unknown&gt;, NetBIOS MAC: &lt;unknown&gt;&#xa;" />  # NOQA: E501
     *             <script id="smb-os-discovery" output=" &#xa;  OS: Unix (Samba 3.6.3)&#xa;  Name: WORKGROUP\Unknown&#xa;  System time: 2013-06-23 15:37:40 UTC+2&#xa;" />  # NOQA: E501
     *             <script id="smbv2-enabled" output="Server doesn&apos;t support SMBv2 protocol" />
     *            </hostscript>
     *            <times srtt="-1" rttvar="-1" to="1000000" />
     *          </host>
     *
     *          <port protocol="tcp" portid="25">
     *           <state state="open" reason="syn-ack" reason_ttl="0"/>
     *            <service name="smtp" product="Exim smtpd" version="4.76" hostname="grostruc" method="probed" conf="10">
     *              <cpe>cpe:/a:exim:exim:4.76</cpe>
     *            </service>
     *            <script id="smtp-commands" output="grostruc Hello localhost [127.0.0.1], SIZE 52428800, PIPELINING, HELP, &#xa; Commands supported: AUTH HELO EHLO MAIL RCPT DATA NOOP QUIT RSET HELP "/>  # NOQA: E501
     *          </port>
     *
     * */

    private fun getHostNames(): List<HostName> {
        val hostNamesNodeList = xPath.compile("/nmaprun/host/hostnames")
            .evaluate(documentElement, XPathConstants.NODESET)
            .let {
                (it as NodeList).toList()
            }

        return if (hostNamesNodeList.isNotEmpty()) {
            hostNamesNodeList.map {
                HostName(
                    name = it.getAttribute("name"),
                    type = it.getAttribute("type")
                )
            }
        } else {
            listOf(HostName(name = "", type = ""))
        }
    }

    /*private fun getHostStatus(hostNode: Node): Status {
        val statusNode 
        return Status(

        )
    }*/

    private fun getHosts(): List<Host> {
        val hostNodeList = xPath.compile("/nmaprun/host")
            .evaluate(documentElement, XPathConstants.NODESET)
            .let {
                (it as NodeList).toList()
            }
        return hostNodeList.map {
            Host(
                startTime = it.getAttribute("starttime").toLong(),
                endTime = it.getAttribute("endtime").toLong(),
                status = Status(
                    state = it.getAttribute("state"),
                    reason = it.getAttribute("reason"),
                    reasonTtl = it.getAttribute("reason_ttl").toInt(),
                ),
                //TODO: GET ALL ADDRESS
                hostNames = getHostNames()
            )
        }
    }

    fun getNmapRun() = NmapRun(
        args = getArgs(),
        runStats = getRunStats(),
        scanInfo = getScanInfo(),
        //TODO: ADD HOSTS
        hosts = getHosts()
    )
}


