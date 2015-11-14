package catan.domain.model.dashboard;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public class Links<T> {
    @ManyToOne
    @JoinColumn(name = "TOP_LEFT")
    private T topLeft;

    @ManyToOne
    @JoinColumn(name = "TOP_RIGHT")
    private T topRight;

    @ManyToOne
    @JoinColumn(name = "BOTTOM_RIGHT")
    private T bottomRight;

    @ManyToOne
    @JoinColumn(name = "BOTTOM_LEFT")
    private T bottomLeft;

    public T getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(T topLeft) {
        this.topLeft = topLeft;
    }

    public T getTopRight() {
        return topRight;
    }

    public void setTopRight(T topRight) {
        this.topRight = topRight;
    }

    public T getBottomRight() {
        return bottomRight;
    }

    public void setBottomRight(T bottomRight) {
        this.bottomRight = bottomRight;
    }

    public T getBottomLeft() {
        return bottomLeft;
    }

    public void setBottomLeft(T bottomLeft) {
        this.bottomLeft = bottomLeft;
    }

    public List<T> listAllNotNullItems() {
        List<T> allElements = new ArrayList<T>();
        if(topLeft != null) allElements.add(topLeft);
        if(topRight != null) allElements.add(topRight);
        if(bottomRight != null) allElements.add(bottomRight);
        if(bottomLeft != null) allElements.add(bottomLeft);

        return allElements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Links)) return false;

        Links links = (Links) o;

        if (bottomLeft != null ? !bottomLeft.equals(links.bottomLeft) : links.bottomLeft != null) return false;
        if (bottomRight != null ? !bottomRight.equals(links.bottomRight) : links.bottomRight != null) return false;
        if (topLeft != null ? !topLeft.equals(links.topLeft) : links.topLeft != null) return false;
        if (topRight != null ? !topRight.equals(links.topRight) : links.topRight != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = topLeft != null ? topLeft.hashCode() : 0;
        result = 31 * result + (topRight != null ? topRight.hashCode() : 0);
        result = 31 * result + (bottomRight != null ? bottomRight.hashCode() : 0);
        result = 31 * result + (bottomLeft != null ? bottomLeft.hashCode() : 0);

        return result;
    }
}
