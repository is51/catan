package catan.domain.transfer.output.game;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiceDetails {
    private boolean thrown;
    private Integer value;
    private Integer first;
    private Integer second;

    public DiceDetails() {
    }

    public DiceDetails(boolean thrown, Integer value, Integer first, Integer second) {
        this.thrown = thrown;
        if (thrown) {
            this.value = value;
            this.first = first;
            this.second = second;
        }
    }

    public boolean getThrown() {
        return thrown;
    }

    public void setThrown(boolean thrown) {
        this.thrown = thrown;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getFirst() {
        return first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }

}
