package dev._3000IQPlay.trillium.protect.antivm;

import dev._3000IQPlay.trillium.protect.antivm.MacUtil;

import java.net.NetworkInterface;
import java.util.Enumeration;

public class VMDetector {
	public static boolean isVM() {
        try {
            Enumeration<NetworkInterface> net = null;
            net = NetworkInterface.getNetworkInterfaces();
            if (net.hasMoreElements()) {
                NetworkInterface element = net.nextElement();
                return MacUtil.isVMMac(element.getHardwareAddress());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}