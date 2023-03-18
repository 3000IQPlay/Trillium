package dev._3000IQPlay.trillium.protect.antivm;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MacUtil {
    public static String getAddress() {
        String address = "";
        InetAddress lanIp = null;
        try {
            String ipAddress = null;
            Enumeration<NetworkInterface> net = null;
            net = NetworkInterface.getNetworkInterfaces();
            while (net.hasMoreElements()) {
                NetworkInterface element = net.nextElement();
                Enumeration<InetAddress> addresses = element.getInetAddresses();
                while (addresses.hasMoreElements() && !MacUtil.isVMMac(element.getHardwareAddress())) {
                    InetAddress ip = addresses.nextElement();
                    if (!(ip instanceof Inet4Address) || !ip.isSiteLocalAddress()) continue;
                    ipAddress = ip.getHostAddress();
                    lanIp = InetAddress.getByName(ipAddress);
                }
            }
            if (lanIp == null) {
                return null;
            }
            address = MacUtil.getMacAddress(lanIp);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return address;
    }

    private static String getMacAddress(InetAddress ip) {
        String address = null;
        try {
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; ++i) {
                sb.append(String.format("%02X%s", mac[i], i < mac.length - 1 ? "-" : ""));
            }
            address = sb.toString();
        }
        catch (SocketException ex) {
            ex.printStackTrace();
        }
        return address;
    }

    public static boolean isVMMac(byte[] mac) {
        byte[][] invalidMacs;
        if (null == mac) {
            return false;
        }
        for (byte[] invalid : invalidMacs = new byte[][]{{0, 5, 105}, {0, 28, 20}, {0, 12, 41}, {0, 80, 86}, {8, 0, 39}, {10, 0, 39}, {0, 3, -1}, {0, 21, 93}}) {
            if (invalid[0] != mac[0] || invalid[1] != mac[1] || invalid[2] != mac[2]) continue;
            return true;
        }
        return false;
    }
}