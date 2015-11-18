package catan.domain.transfer.output.game;

public class BoughtCardDetails {
    private String card;

    public BoughtCardDetails() {
    }

    public BoughtCardDetails(String card) {
        this.card = card;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
