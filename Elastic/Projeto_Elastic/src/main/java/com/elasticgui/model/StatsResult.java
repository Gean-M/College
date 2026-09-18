package com.elasticgui.model;

/**
 * Resultado da agregação "stats" sobre o campo reading_time
 * (count, min, max, avg, sum), como visto na aula de agregações.
 */
public class StatsResult {

    private final long count;
    private final Double min;
    private final Double max;
    private final Double avg;
    private final Double sum;

    public StatsResult(long count, Double min, Double max, Double avg, Double sum) {
        this.count = count;
        this.min = min;
        this.max = max;
        this.avg = avg;
        this.sum = sum;
    }

    public long getCount() {
        return count;
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

    public Double getSum() {
        return sum;
    }
}
