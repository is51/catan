package catan.domain.model.dashboard;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

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

    public Integer getIdOfElement(T element) {
        if (element == null) {
            return null;
        }

        if(element instanceof EdgeBean){
            EdgeBean edge = (EdgeBean)element;
            return edge.getId();
        }

        if(element instanceof NodeBean){
            NodeBean node = (NodeBean)element;
            return node.getId();
        }

        if(element instanceof HexBean){
            HexBean hex = (HexBean)element;
            return hex.getId();
        }

        return null;
    }
}
