package pl.tass.superping.superpingcrawler.model;

/**
 * 
 *
 * @since 2018-01-15, 00:00:54
 * @author Grzegorz Majchrzak
 */
public class TracerouteStep {
    private String country;
    private String address;
    private String domain;
    private String delay;
    private String name;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}