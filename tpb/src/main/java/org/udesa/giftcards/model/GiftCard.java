package org.udesa.giftcards.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class GiftCard extends ModelEntity {
    public static final String CargoImposible = "CargoImposible";
    public static final String InvalidCard = "InvalidCard";

    @Column(unique = true)
    private String cardId;
    @Column
    private int balance;
    @Column
    private String owner;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "giftcard_charges", joinColumns = @JoinColumn(name = "giftcard_id"))
    @Column(name = "description")
    private List<String> charges = new ArrayList<>();

    public GiftCard() {
    }

    public GiftCard(String cardId, int initialBalance) {
        this.cardId = cardId;
        this.balance = initialBalance;
    }

    public GiftCard charge(int anAmount, String description) {
        if (!owned() || (balance - anAmount < 0))
            throw new RuntimeException(CargoImposible);

        balance = balance - anAmount;
        charges.add(description);
        return this;
    }

    public GiftCard redeem(String newOwner) {
        if (owned())
            throw new RuntimeException(InvalidCard);

        owner = newOwner;
        return this;
    }

    // projectors
    public boolean owned() {
        return owner != null;
    }

    public boolean isOwnedBy(String aPossibleOwner) {
        return owner != null && owner.equals(aPossibleOwner);
    }

    // accessors
    public String id() {
        return cardId;
    }

    public int balance() {
        return balance;
    }

    public List<String> charges() {
        return charges;
    }
}
