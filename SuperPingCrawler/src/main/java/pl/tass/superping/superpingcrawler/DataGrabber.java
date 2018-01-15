package pl.tass.superping.superpingcrawler;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import pl.tass.superping.superpingcrawler.model.PingResults;
import pl.tass.superping.superpingcrawler.model.ServerData;
import pl.tass.superping.superpingcrawler.model.SiteInfo;
import pl.tass.superping.superpingcrawler.model.TracerouteStep;

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
    private final int SLEEPTIME = 1000;
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
    public static final String[] TESTED_SITES = {
        "wp.pl", "onet.pl", "allegro.pl"
    };

    private String request_id;

    public DataGrabber() {
        establishConnection();
    }

    public static void main(String[] args) throws Exception {
        DataGrabber crawler = new DataGrabber();
        String site = DataGrabber.TESTED_SITES[0];
        String json = crawler.createStats(site);
        new File("data").mkdirs();
        File file = new File("data/" + site.replace(".", "") + ".json");
        file.createNewFile();
        Files.write(Paths.get(file.getAbsolutePath()), json.getBytes());
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
//            PingResults pr = pingSite(siteIp, SERVERS[i]);
//            sd.setPing(pr);
//            List<TracerouteStep> tsl = tracerouteSite(siteIp, SERVERS[i]);
//            sd.setTraceroute(tsl);
            servers.add(sd);
        }
        siteInfo.setServers(servers);
        Gson gson = new Gson();
        String res = gson.toJson(siteInfo);
        return res;
    }

    private void establishConnection() {
        try {
            String res = sendGet("http://ping.pe/google.pl");
            Document html = Jsoup.parse(res);
            Element script = html.select("#megatable tr td script").first();
            String scriptText = script.dataNodes().get(0).getWholeData();
            String preparsed = scriptText.substring(scriptText.indexOf("doSuperCrazyShit"));
            Pattern pattern = Pattern.compile("\\(\\s'.+',\\s'.+',\\s'(.+)',\\s'.+'\\s\\)");
            Matcher matcher = pattern.matcher(preparsed);
            if (matcher.find())
                request_id = matcher.group(1);
            else
                throw new Exception("Cannot establish connection!");
        } catch (Exception ex) {
            Logger.getLogger(DataGrabber.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                    System.out.println("Empty response: retrying...");
                    Thread.sleep(1000);
                    continue;
                }
                retryCount = 0;
                if (time.trim().equals("timeout"))
                    packageLost++;
                else
                    pings.add(Double.parseDouble(time.trim()));
                Thread.sleep(SLEEPTIME);
            }
        } catch (Exception ex) {
            Logger.getLogger(DataGrabber.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(pings);
        double minTime = Double.MAX_VALUE;
        double maxTime = Double.MIN_VALUE;
        double sum = 0;
        double avgTime;
        double percentLost = packageLost / PING_COUNT * 100;
        if (!pings.isEmpty()) {
            for (Double d : pings) {
                sum += d;
                if (d < minTime)
                    minTime = d;
                if (d > maxTime)
                    maxTime = d;
            }
            avgTime = sum / pings.size();
        } else {
            minTime = -1;
            maxTime = -1;
            avgTime = -1;
        }
        return new PingResults(percentLost, minTime, maxTime, avgTime);
    }

    private List<TracerouteStep> tracerouteSite(String destIp, String server) {
        List<TracerouteStep> res = new ArrayList<>();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(BASE_URL)
                    .append("?")
                    .append("action=")
                    .append(ACTIONS[1])
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
            String traceroute = "";
            while (traceroute.isEmpty()) {
                if (retryCount == MAX_RETRY)
                    break;
                String route = sendGet(url);
                if (route.trim().isEmpty()) { // Zabezpieczenie przed pustymi odpowiedziami
                    retryCount++;
                    System.out.println("Empty response: retrying...");
                    Thread.sleep(30000);    // Traceroute trwa u nich długo, dlatego dlugi sleep gdy brak danych
                    continue;
                }
                retryCount = 0;
                System.out.println(route);
//                res.add(Double.parseDouble(time.trim()));
                Thread.sleep(SLEEPTIME);
            }
        } catch (Exception ex) {
            Logger.getLogger(DataGrabber.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(res);
        return res;
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

}
