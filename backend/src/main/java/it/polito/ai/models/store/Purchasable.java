package it.polito.ai.models.store;

/*
* Any item which caan be sold or purchased must implement this interface.
* It foces the item to expose necessary methods for the store operations.
* */
public interface Purchasable {
    public Integer getPrice();
    public String getId();
}
