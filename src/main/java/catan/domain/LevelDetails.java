package catan.domain;


import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class LevelDetails {
    private Integer id;
    private Integer size;
    private List<HexBean> hexList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<HexBean> getHexList() {
        return hexList;
    }

    public void setHexList(List<HexBean> hexList) {
        this.hexList = hexList;
    }
}
