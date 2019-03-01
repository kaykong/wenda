package top.kongk.wenda.model;

import java.io.Serializable;
import java.util.Objects;

public abstract class IdEntity implements Serializable {

    protected Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * 继承 IdEntity 类的子类之间的 equals 默认通过 id 判断
     *
     * @author kongkk
     * @param obj
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        IdEntity idEntity = (IdEntity) obj;
        if (idEntity.id == null || id == null) {
            return false;
        }

        return Objects.equals(id, idEntity.id);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
