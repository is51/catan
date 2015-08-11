package catan.domain.model.dashboard;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "COORDINATES")
public class CoordinatesBean {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "COORDINATE_ID", unique = true, nullable = false)
    private int id;

    @Column(name = "X_COORDINATE", unique = false, nullable = false)
    private int xCoordinate;

    @Column(name = "Y_COORDINATE", unique = false, nullable = false)
    private int yCoordinate;

    public CoordinatesBean() {
    }

    public CoordinatesBean(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
}
