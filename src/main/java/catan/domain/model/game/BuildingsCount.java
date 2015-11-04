package catan.domain.model.game;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

@Embeddable
public class BuildingsCount {

    @Column(name = "COUNT_SETTLEMENTS", nullable = false)
    private int settlements;

    @Column(name = "COUNT_CITIES", nullable = false)
    private int cities;

    public BuildingsCount() {
    }

    public BuildingsCount(int settlements, int cities) {
        this.settlements = settlements;
        this.cities = cities;
    }

    public int getSettlements() {
        return settlements;
    }

    public void setSettlements(int settlements) {
        this.settlements = settlements;
    }

    public int getCities() {
        return cities;
    }

    public void setCities(int cities) {
        this.cities = cities;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
