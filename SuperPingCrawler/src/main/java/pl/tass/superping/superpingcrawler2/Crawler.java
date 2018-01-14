package pl.tass.superping.superpingcrawler2;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 *
 * @since 2018-01-14, 18:54:07
 * @author Grzegorz Majchrzak
 */
public class Crawler {
    private final String USER_AGENT = "Mozilla/5.0";
    private final int PING_COUNT = 4;
    private final String[] ACTIONS = {
        "ping",
        "mtr_report"
    };
    private final String BASE_URL = "https://ping.pe/pinger.php";
    private String request_id = "Hxm7pPhIcASCNAVc";
    private final String[] SERVERS = {
        "CA_BC_1", "CA_BC_2", "US_WA_1", "US_WA_2",
        "US_CA_1", "US_CA_3", "US_TX_1", "US_IL_1",
        "US_GA_1", "US_NY_2", "US_NY_2", "EU_NL_3",
        "EU_LU_1", "EU_DE_2", "EU_IT_1", "RU_MSK_1",
        "RU_TSK_1", "SG_1", "JP_1", "AU_1",
        "CN_1", "CN_2", "CN_3", "CN_4",
        "CN_5", "CN_6", "CN_7", "CN_8",
        "CN_9", "CN_10", "CN_11", "CN_12"
    };

    public static void main(String[] args) throws Exception {
        Crawler crawler = new Crawler();
        crawler.pingSite("wp.pl");
    }

    public void pingSite(String domain) {
        String siteIp;
        try {
            InetAddress address = InetAddress.getByName("wp.pl");
            siteIp = address.getHostAddress();
            for (String server : SERVERS) {
                StringBuilder sb = new StringBuilder();
                sb.append(BASE_URL)
                        .append("?")
                        .append("action=")
                        .append(ACTIONS[0])
                        .append("&")
                        .append("pinger=")
                        .append(server)
                        .append("&")
                        .append("request_id=")
                        .append(request_id)
                        .append("&")
                        .append("ip=")
                        .append(siteIp);
                String url = sb.toString();
                int packageLost = 0;
                List<Double> pingTimes = new ArrayList<>();
                for (int i = 0; i < PING_COUNT; i++) {
                    String time = sendGet(url);
                    if (time.trim().isEmpty()) { // Zabezpieczenie przed pustymi odpowiedziami
                        i--;
                        Thread.sleep(1000);
                        System.out.println("Empty response: retrying...");
                        continue;
                    }
                    if (time.trim().equals("timeout"))
                        packageLost++;
                    else
                        pingTimes.add(Double.parseDouble(time.trim()));
                }
                System.out.println("Ping result: " + pingTimes);
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String sendGet(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        StringBuffer response = new StringBuffer();
        String line = "";
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        String inputLine;
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        line = response.toString();
        in.close();
        return response.toString();
    }

    private void sendPost() throws Exception {

        String url = "";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();

        //print result
        System.out.println(response.toString());
    }

}
