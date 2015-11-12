package catan.domain.transfer.output.game;

public class ObtainedDevCardDetails {
    private String card;

    public ObtainedDevCardDetails() {
    }

    public ObtainedDevCardDetails(String card) {
        this.card = card;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
