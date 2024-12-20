package son.network;

public class InetAddressHelper {
    public static boolean compareAddresses(byte[] addr1, byte[] addr2) {
        if((addr1 == null && addr2 != null) || (addr1 != null && addr1 == null)) return false;
        if(addr1.length != addr2.length) return false;
        for(var i = 0; i < addr1.length; i++) {
            if(addr1[i] != addr2[i]) return false;
        }
        return true;
    }

    public static boolean isLocalAddress(byte[] addr) {
        return  addr != null &&
                addr.length == 4 &&
                ((addr[0] == (byte)192 && addr[1] == (byte)168) ||
                (addr[0] == (byte)10) ||
                (addr[0] == (byte)172));
    }

    public static String toString(byte[] addr) {
        if(addr == null) return null;
        StringBuilder sb = new StringBuilder();
        for (byte b : addr) {
            if(b < 0) sb.append(256+b);
            else sb.append(b);
            sb.append(".");
        }
        return sb.substring(0, sb.length()-1);
    }
}
