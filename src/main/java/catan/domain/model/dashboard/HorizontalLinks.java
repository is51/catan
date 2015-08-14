package catan.domain.model.dashboard;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class HorizontalLinks<T> extends Links<T>{

    @ManyToOne
    @JoinColumn(name = "RIGHT_NEIGHBOUR")
    private T right;

    @ManyToOne
    @JoinColumn(name = "LEFT_NEIGHBOUR")
    private T left;

    public T getRight() {
        return right;
    }

    public void setRight(T right) {
        this.right = right;
    }

    public T getLeft() {
        return left;
    }

    public void setLeft(T left) {
        this.left = left;
    }
}
