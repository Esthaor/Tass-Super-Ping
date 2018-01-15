package pl.tass.superping.superpingcrawler.model;

import java.util.List;

/**
 *
 *
 * @since 2018-01-15, 00:01:04
 * @author Grzegorz Majchrzak
 */
public class SiteInfo {
    private String address;
    private String ip;
    private List<ServerData> servers;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<ServerData> getServers() {
        return servers;
    }

    public void setServers(List<ServerData> servers) {
        this.servers = servers;
    }

}
