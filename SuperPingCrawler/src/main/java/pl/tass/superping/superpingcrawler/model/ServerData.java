package pl.tass.superping.superpingcrawler.model;

import java.util.List;

/**
 * 
 *
 * @since 2018-01-15, 00:53:07
 * @author Grzegorz Majchrzak
 */
public class ServerData {
    private String shortname;
    private String name;
    private PingResults ping;
    private List<TracerouteStep> traceroute;

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PingResults getPing() {
        return ping;
    }

    public void setPing(PingResults ping) {
        this.ping = ping;
    }

    public List<TracerouteStep> getTraceroute() {
        return traceroute;
    }

    public void setTraceroute(List<TracerouteStep> traceroute) {
        this.traceroute = traceroute;
    }
    
}