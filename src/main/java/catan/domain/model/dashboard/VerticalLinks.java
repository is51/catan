package catan.domain.model.dashboard;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
}
