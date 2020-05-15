package it.polito.ai.models;

import java.util.List;

public class PurchaseResponse {

    private double amount;
    private int positionsNumber;
    private List<PositionEntry> positions;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getPositionsNumber() {
        return positionsNumber;
    }

    public void setPositionsNumber(int positionsNumber) {
        this.positionsNumber = positionsNumber;
    }

    public List<PositionEntry> getPositions() {
        return positions;
    }

    public void setPositions(List<PositionEntry> positions) {
        this.positions = positions;
    }

}
