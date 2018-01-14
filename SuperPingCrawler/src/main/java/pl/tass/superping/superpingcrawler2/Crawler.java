package pl.tass.superping.superpingcrawler2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import static javafx.css.StyleOrigin.USER_AGENT;

/**
 *
 *
 * @since 2018-01-14, 18:54:07
 * @author Grzegorz Majchrzak
 */
public class Crawler {
    private final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args) throws Exception {
        Crawler crawler = new Crawler();
        crawler.sendGet();
    }

    private void sendGet() throws Exception {

        String url = "https://ping.pe/pinger.php?action=mtr_report&pinger=US_WA_2&request_id=lPbNs7EIpKIcg4Uc&ip=212.77.98.9";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        BufferedReader in = null;
        StringBuffer response = new StringBuffer();
        String line = "";
        while (line != null && line.isEmpty()) {
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            Thread.sleep(1000);
            line = in.readLine();
            in.close();
        }
        //print result
        System.out.println(response.toString());
    }
}
