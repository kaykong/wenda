package top.kongk.wenda.async;

import java.util.List;


/**
 * 事件处理接口, 继承此接口的事件将会被作为事件消费者, 来处理事件
 *
 * @author kk
 */
public interface EventHandler {

    /**
     * 处理事件
     *
     * @param model
     */
    void doHandle(EventModel model);

    /**
     * 可以处理的事件有哪些
     *
     * @return java.util.List<top.kongk.wenda.async.EventType>
     */
    List<EventType> getSupportEventTypes();

}
