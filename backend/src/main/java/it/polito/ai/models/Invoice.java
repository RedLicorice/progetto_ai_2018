package it.polito.ai.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection="invoices")
public class Invoice {
    @Id
    private String id;
    private String username;
    private double amount;
    private List<String> archives;
    private Boolean isPaid;
    @CreatedDate
    private Long createdAt;

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public List<String> getArchives() {
        return archives;
    }
    public void setArchives(List<String> archives) {
        this.archives = archives;
    }

    public Boolean getPaid() {
        return isPaid;
    }
    public void setPaid(Boolean paid) {
        isPaid = paid;
    }
    public Long getCreatedAt() {
        return createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
