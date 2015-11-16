package catan.domain.transfer.output.game;

public class BuyCardDetails {
    private String card;

    public BuyCardDetails() {
    }

    public BuyCardDetails(String card) {
        this.card = card;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
