package son.network;

public class InetAddressHelper {
    public static boolean compareAddresses(byte[] addr1, byte[] addr2) {
        if(addr1.length != addr2.length) return false;
        for(var i = 0; i < addr1.length; i++) {
            if(addr1[i] != addr2[i]) return false;
        }
        return true;
    }
}
