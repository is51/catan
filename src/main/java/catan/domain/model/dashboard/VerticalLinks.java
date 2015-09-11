package catan.domain.model.dashboard;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Arrays;
import java.util.List;

@Embeddable
public class VerticalLinks<T> extends Links<T>{

    @ManyToOne
    @JoinColumn(name = "TOP")
    private T top;

    @ManyToOne
    @JoinColumn(name = "BOTTOM")
    private T bottom;

    public T getTop() {
        return top;
    }

    public void setTop(T top) {
        this.top = top;
    }

    public T getBottom() {
        return bottom;
    }

    public void setBottom(T bottom) {
        this.bottom = bottom;
    }

    @Override
    public List<T> all() {
        List<T> allElements = super.all();
        allElements.add(top);
        allElements.add(bottom);

        return allElements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VerticalLinks)) return false;
        if (!super.equals(o)) return false;

        VerticalLinks that = (VerticalLinks) o;

        if (bottom != null ? !bottom.equals(that.bottom) : that.bottom != null) return false;
        if (top != null ? !top.equals(that.top) : that.top != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (top != null ? top.hashCode() : 0);
        result = 31 * result + (bottom != null ? bottom.hashCode() : 0);
        return result;
    }
}
