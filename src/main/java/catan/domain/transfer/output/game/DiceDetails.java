package catan.domain.transfer.output.game;

public class DiceDetails {
    private Integer value;
    private Integer firstValue;
    private Integer secondValue;

    public DiceDetails() {
    }

    public DiceDetails(Integer value, Integer firstValue, Integer secondValue) {
        this.value = value;
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getFirstValue() {
        return firstValue;
    }

    public void setFirstValue(Integer firstValue) {
        this.firstValue = firstValue;
    }

    public Integer getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(Integer secondValue) {
        this.secondValue = secondValue;
    }

}
