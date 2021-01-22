package it.polito.ai.models.store;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Document(collection="invoices")
public class Invoice {
    @Id
    private String id;
    private String username;
    private double amount;
    private List<String> items;
    private Boolean isPaid;

    @CreatedDate
    private LocalDateTime createdAt;

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<String> getItems() { return items; }
    public void setItems(List<String> items) {
        this.items = items;
    }

    public Boolean getPaid() {
        return isPaid;
    }
    public void setPaid(Boolean paid) {
        isPaid = paid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }
}
