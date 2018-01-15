package pl.tass.superping.superpingcrawler;

import com.google.gson.Gson;
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
import pl.tass.superping.superpingcrawler.model.PingResults;
import pl.tass.superping.superpingcrawler.model.ServerData;
import pl.tass.superping.superpingcrawler.model.SiteInfo;

/**
 *
 *
 * @since 2018-01-14, 18:54:07
 * @author Grzegorz Majchrzak
 */
public class DataGrabber {
    private final String USER_AGENT = "Mozilla/5.0";
    private final int PING_COUNT = 4;
    private final int MAX_RETRY = 4;
    private final String[] ACTIONS = {
        "ping",
        "mtr_report"
    };
    private final String BASE_URL = "https://ping.pe/pinger.php";
    private final String[] SERVERS = {
        "CA_BC_1", "CA_BC_2", "US_WA_1", "US_WA_2",
        "US_CA_1", "US_CA_3", "US_TX_1", "US_IL_1",
        "US_GA_1", "US_NY_2", "EU_NL_3", "EU_LU_1",
        "EU_DE_2", "EU_IT_1", "RU_MSK_1", "RU_TSK_1",
        "SG_1", "JP_1", "AU_1", "CN_1",
        "CN_2", "CN_3", "CN_4", "CN_5",
        "CN_6", "CN_7", "CN_8", "CN_9",
        "CN_10", "CN_11", "CN_12"
    };
    private final String[] SERVERS_NAMES = {
        "Canada, BC, Vancouver", "Canada, BC, Vancouver", "USA, WA, Seattle", "USA, WA, Seattle",
        "USA, CA, Fremont", "USA, CA, Los Angeles", "USA, TX, Dallas", "USA, IL, Chicago",
        "USA, GA, Atlanta", "USA, NY, New York", "Netherlands, Nuland", "Luxembourg, Roost",
        "Germany, Bochum", "Italy, Milan", "Russia, Moscow", "Russia, Tomsk",
        "Singapore", "Japan, Tokyo", "Australia, Sydney", "China, Guizhou",
        "China, Henan", "China, Guangzhou", "China, Beijing", "China, Shenzhen",
        "China, Anhui", "China, Jiangsu", "China, Jiangsu", "China, Jiangsu",
        "China, Qingdao", "China, Qingdao", "China, Shanghai"
    };
    private final String[] TESTED_SITES = {
        "wp.pl", "onet.pl", "allegro.pl"
    };

    private String request_id = "Hxm7pPhIcASCNAVc";

    public static void main(String[] args) throws Exception {
        DataGrabber crawler = new DataGrabber();
        String json = crawler.createStats("wp.pl");
        System.out.println(json);
    }

    public String createStats(String domain) throws UnknownHostException {
        SiteInfo siteInfo = new SiteInfo();
        String siteIp;

        InetAddress address = InetAddress.getByName(domain);
        siteIp = address.getHostAddress();
        siteInfo.setAddress(domain);
        siteInfo.setIp(siteIp);
        List<ServerData> servers = new ArrayList<>();
        for (int i = 0; i < SERVERS.length; i++) {
            ServerData sd = new ServerData();
            sd.setName(SERVERS_NAMES[i]);
            sd.setShortname(SERVERS[i]);
            PingResults pr = pingSite(siteIp, SERVERS[i]);
            sd.setPing(pr);
            servers.add(sd);
        }
        siteInfo.setServers(servers);
        Gson gson = new Gson();
        String res = gson.toJson(siteInfo);
        return res;
    }

    private PingResults pingSite(String destIp, String server) {
        List<Double> pings = new ArrayList<>();
        int packageLost = 0;
        try {
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
                    .append(destIp);
            String url = sb.toString();
            int retryCount = 0;
            for (int i = 0; i < PING_COUNT; i++) {
                if (retryCount == MAX_RETRY)
                    break;
                String time = sendGet(url);
                if (time.trim().isEmpty()) { // Zabezpieczenie przed pustymi odpowiedziami
                    i--;
                    retryCount++;
                    Thread.sleep(1000);
                    System.out.println("Empty response: retrying...");
                    continue;
                }
                retryCount = 0;
                if (time.trim().equals("timeout"))
                    packageLost++;
                else
                    pings.add(Double.parseDouble(time.trim()));
            }
        } catch (Exception ex) {
            Logger.getLogger(DataGrabber.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(pings);
        double minTime = Double.MAX_VALUE;
        double maxTime = Double.MIN_VALUE;
        double sum = 0;
        double percentLost = packageLost / pings.size() * 100;
        for (Double d : pings) {
            sum += d;
            if (d < minTime)
                minTime = d;
            if (d > maxTime)
                maxTime = d;
        }
        double avgTime = sum / (pings.size() - packageLost);
        return new PingResults(percentLost, minTime, maxTime, avgTime);

    }

    private String sendGet(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        StringBuffer response = new StringBuffer();
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        String inputLine;
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
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
