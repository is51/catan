package catan.domain.transfer.output.dashboard;

import catan.domain.model.dashboard.HorizontalLinks;
import catan.domain.model.dashboard.Links;
import catan.domain.model.dashboard.MapElement;
import catan.domain.model.dashboard.VerticalLinks;

public class LinkDetails {

    private Integer topLeftId;
    private Integer topId;
    private Integer topRightId;
    private Integer rightId;
    private Integer bottomRightId;
    private Integer bottomId;
    private Integer bottomLeftId;
    private Integer leftId;

    public LinkDetails() {
    }

    public LinkDetails(Links mapElements) {
        this.topLeftId = mapElements.getTopLeft() != null ? ((MapElement) mapElements.getTopLeft()).getId() : null;
        this.topRightId = mapElements.getTopRight() != null ? ((MapElement) mapElements.getTopRight()).getId() : null;
        this.bottomRightId = mapElements.getBottomRight() != null ? ((MapElement) mapElements.getBottomRight()).getId() : null;
        this.bottomLeftId = mapElements.getBottomLeft() != null ? ((MapElement) mapElements.getBottomLeft()).getId() : null;

        if(mapElements instanceof HorizontalLinks){
            HorizontalLinks horizontalElements = (HorizontalLinks) mapElements;

            this.rightId = horizontalElements.getRight() != null ? ((MapElement) horizontalElements.getRight()).getId() : null;
            this.leftId = horizontalElements.getLeft() != null ? ((MapElement) horizontalElements.getLeft()).getId() : null;
        } else if(mapElements instanceof VerticalLinks){
            VerticalLinks verticalElements = (VerticalLinks) mapElements;

            this.topId = verticalElements.getTop() != null ? ((MapElement) verticalElements.getTop()).getId() : null;
            this.bottomId = verticalElements.getBottom() != null ? ((MapElement) verticalElements.getBottom()).getId() : null;
        }
    }

    public Integer getTopLeftId() {
        return topLeftId;
    }

    public void setTopLeftId(Integer topLeftId) {
        this.topLeftId = topLeftId;
    }

    public Integer getTopId() {
        return topId;
    }

    public void setTopId(Integer topId) {
        this.topId = topId;
    }

    public Integer getTopRightId() {
        return topRightId;
    }

    public void setTopRightId(Integer topRightId) {
        this.topRightId = topRightId;
    }

    public Integer getRightId() {
        return rightId;
    }

    public void setRightId(Integer rightId) {
        this.rightId = rightId;
    }

    public Integer getBottomRightId() {
        return bottomRightId;
    }

    public void setBottomRightId(Integer bottomRightId) {
        this.bottomRightId = bottomRightId;
    }

    public Integer getBottomId() {
        return bottomId;
    }

    public void setBottomId(Integer bottomId) {
        this.bottomId = bottomId;
    }

    public Integer getBottomLeftId() {
        return bottomLeftId;
    }

    public void setBottomLeftId(Integer bottomLeftId) {
        this.bottomLeftId = bottomLeftId;
    }

    public Integer getLeftId() {
        return leftId;
    }

    public void setLeftId(Integer leftId) {
        this.leftId = leftId;
    }
}
