package catan.domain.model.dashboard;

import catan.domain.model.game.GameUserBean;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class Building<T extends Enum<?>> {

    @Enumerated(EnumType.STRING)
    @Column(name = "BUILT", unique = false, nullable = true)
    private T built;

    @ManyToOne
    @JoinColumn(name = "BUILDING_OWNER_ID")
    private GameUserBean buildingOwner;

    public Building() {
    }

    public Building(T built, GameUserBean buildingOwner) {
        this.built = built;
        this.buildingOwner = buildingOwner;
    }


    public T getBuilt() {
        return built;
    }

    public void setBuilt(T built) {
        this.built = built;
    }

    public GameUserBean getBuildingOwner() {
        return buildingOwner;
    }

    public void setBuildingOwner(GameUserBean buildingOwner) {
        this.buildingOwner = buildingOwner;
    }
}
