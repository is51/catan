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
    @Column(name = "COORDINATES_ID", unique = true, nullable = false)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoordinatesBean that = (CoordinatesBean) o;

        if (xCoordinate != that.xCoordinate) return false;
        if (yCoordinate != that.yCoordinate) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = xCoordinate;
        result = 31 * result + yCoordinate;
        return result;
    }
}
