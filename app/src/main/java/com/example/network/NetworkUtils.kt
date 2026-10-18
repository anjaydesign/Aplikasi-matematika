package com.example.network

import android.content.Context
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Collections

object NetworkUtils {

    const val DEFAULT_PORT = 8888

    fun getLocalIpAddress(context: Context? = null): String {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                if (intf.isLoopback || !intf.isUp) continue
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        val host = addr.hostAddress ?: ""
                        if (host.isNotEmpty() && !host.startsWith("127.")) {
                            return host
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (context != null) {
            try {
                val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                val ipInt = wm?.connectionInfo?.ipAddress ?: 0
                if (ipInt != 0) {
                    return String.format(
                        "%d.%d.%d.%d",
                        ipInt and 0xff,
                        ipInt shr 8 and 0xff,
                        ipInt shr 16 and 0xff,
                        ipInt shr 24 and 0xff
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return "127.0.0.1"
    }

    fun ipToRoomCode(ip: String): String {
        val parts = ip.split(".")
        return if (parts.size == 4) {
            "${parts[2]}-${parts[3]}"
        } else {
            ip
        }
    }

    fun roomCodeToIp(baseSubnet: String, code: String): String {
        val clean = code.trim()
        if (clean.contains(".")) return clean
        val subParts = baseSubnet.split(".")
        if (subParts.size >= 2 && clean.contains("-")) {
            val codeParts = clean.split("-")
            if (codeParts.size == 2) {
                return "${subParts[0]}.${subParts[1]}.${codeParts[0]}.${codeParts[1]}"
            }
        }
        return clean
    }
}
