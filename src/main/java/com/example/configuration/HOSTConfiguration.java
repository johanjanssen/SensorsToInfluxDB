package com.example.configuration;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.*;
import java.util.Enumeration;

@Component
public class HOSTConfiguration {
    private String ipAddress;

    @PostConstruct
    private void initializeIPAddress() {
        retrieveIPAddress();
    }

    /**
     * Retrieves the IPV4 address of the wlan0 interface of the host.
     * Address is used to uniquely identify where the sensordata is coming from.
     * Feel free to change to something else.
     */
    private void retrieveIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();

                    if (inetAddress instanceof Inet4Address && "wlan0".equals(iface.getName())) {
                        ipAddress = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public String retrieveHOSTIPAddress() {
        return ipAddress;
    }
}
