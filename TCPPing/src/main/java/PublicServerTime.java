import java.io.IOException;
import java.net.InetAddress;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public final class PublicServerTime {

    public static long getNTPOffset() {

        NTPUDPClient client = new NTPUDPClient(); 
        // We want to timeout if a response takes longer than 5 seconds
        client.setDefaultTimeout(5000);

            try {
                InetAddress hostAddr = InetAddress.getByName("ntp02.oal.ul.pt");
                TimeInfo info = client.getTime(hostAddr);
                info.computeDetails();
                return info.getOffset();

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        

        client.close();

        return 0;

    }
}