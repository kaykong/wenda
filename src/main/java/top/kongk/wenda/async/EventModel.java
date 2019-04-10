package top.kongk.wenda.async;

import java.util.HashMap;
import java.util.Map;


/**
 * 事件模型实体类
 *
 * @author kk
 */
public class EventModel {

    /**
     * 事件类型
     */
    private EventType type;
    /**
     * 事件的发起者
     */
    private int actorId;
    /**
     * 事件相关的实体的类型
     */
    private int entityType;
    /**
     * 事件相关的实体的id
     */
    private int entityId;
    /**
     * 事件的影响者
     */
    private int entityOwnerId;

    /**
     * 额外的需要补充的参数
     */
    private Map<String, String> exts = new HashMap<>();

    public EventModel() {

    }

    public EventModel setExt(String key, String value) {
        exts.put(key, value);
        return this;
    }

    public EventModel(EventType type) {
        this.type = type;
    }

    public String getExt(String key) {
        return exts.get(key);
    }


    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
}
