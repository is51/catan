package catan.domain.model.dashboard;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;

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

    @Override
    public List<T> listAllNotNullItems() {
        List<T> allElements = super.listAllNotNullItems();
        if(right != null) allElements.add(right);
        if(left != null) allElements.add(left);

        return allElements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HorizontalLinks)) return false;
        if (!super.equals(o)) return false;

        HorizontalLinks that = (HorizontalLinks) o;

        if (left != null ? !left.equals(that.left) : that.left != null) return false;
        if (right != null ? !right.equals(that.right) : that.right != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (right != null ? right.hashCode() : 0);
        result = 31 * result + (left != null ? left.hashCode() : 0);

        return result;
    }
}
