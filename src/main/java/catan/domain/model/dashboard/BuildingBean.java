package catan.domain.model.dashboard;

import catan.domain.model.dashboard.types.EdgeBuiltType;
import catan.domain.model.game.GameUserBean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "BUILDING")
public class BuildingBean {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "BUILDING_ID", unique = true, nullable = false)
    private int id;

    @Column(name = "BUILT", unique = false, nullable = false)
    private EdgeBuiltType built;

    @ManyToOne
    @JoinColumn(name = "BUILDING_OWNER_ID")
    private GameUserBean buildingOwner;

    public BuildingBean() {
    }

    public BuildingBean(EdgeBuiltType built, GameUserBean buildingOwner) {
        this.built = built;
        this.buildingOwner = buildingOwner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EdgeBuiltType getBuilt() {
        return built;
    }

    public void setBuilt(EdgeBuiltType built) {
        this.built = built;
    }

    public GameUserBean getBuildingOwner() {
        return buildingOwner;
    }

    public void setBuildingOwner(GameUserBean buildingOwner) {
        this.buildingOwner = buildingOwner;
    }
}
