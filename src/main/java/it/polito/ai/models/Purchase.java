package it.polito.ai.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection="purchases")
public class Purchase {

    @Id
    private String id;

    private String userId;

    @JsonIgnore
    private int positionsNumber;
    private List<PositionEntry> positions;
    private double amount;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("positionsNumber")
    public int getPositionsNumber() {
        return positionsNumber;
    }

    @JsonIgnore
    public void setPositionsNumber(int positionsNumber) {
        this.positionsNumber = positionsNumber;
    }

    public List<PositionEntry> getPositions() {
        return positions;
    }

    public void setPositions(List<PositionEntry> positions) {
        this.positions = positions;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}
