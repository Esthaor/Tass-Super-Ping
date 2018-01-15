package pl.tass.superping.superpingcrawler.model;

/**
 *
 *
 * @since 2018-01-14, 23:25:14
 * @author Grzegorz Majchrzak
 */
public class PingResults {
    private final Double percent;
    private final Double min;
    private final Double max;
    private final Double avg;

    public PingResults(Double percent, Double min, Double max, Double avg) {
        this.percent = percent;
        this.min = min;
        this.max = max;
        this.avg = avg;
    }

    public Double getPercent() {
        return percent;
    }

    public Double getMin() {
        return min;
    }

    public Double getMax() {
        return max;
    }

    public Double getAvg() {
        return avg;
    }

}
