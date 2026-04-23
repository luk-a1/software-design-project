package medifly.location;

public class DeliveryCostCalculator {
    private static final double[][] COST_MATRIX = {
        {0, 25, 18, 38, 44},
        {25, 0, 15, 28, 35},
        {18, 15, 0, 34, 40},
        {38, 28, 34, 0, 30},
        {44, 35, 40, 30, 0}
    };

    public double getTravelCost(HKLocation from, HKLocation to) {
        return COST_MATRIX[from.ordinal()][to.ordinal()];
    }
}
