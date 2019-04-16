package top.kongk.wenda.async.handler;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.kongk.wenda.async.EventHandler;
import top.kongk.wenda.async.EventModel;
import top.kongk.wenda.async.EventType;
import top.kongk.wenda.model.EntityType;
import top.kongk.wenda.model.Message;
import top.kongk.wenda.model.User;
import top.kongk.wenda.service.MessageService;
import top.kongk.wenda.service.UserService;
import top.kongk.wenda.util.WendaUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 处理关注事件
 *
 * @author kk
 */
@Component
public class FollowHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {

        Message message = new Message();

        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        message.setHasRead(0);

        User user = userService.getUser(model.getActorId());

        if (model.getEntityType() == EntityType.ENTITY_QUESTION) {
            message.setContent("用户" + user.getName()
                    + "关注了您的问题, <a href=\"/question/" + model.getEntityId() + "\">您的问题链接</a>");
        } else if (model.getEntityType() == EntityType.ENTITY_USER) {
            message.setContent("用户" + user.getName()
                    + "关注了您 , <a href=\"/user/" + model.getActorId() + "\">他的个人主页</a>");
        }

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
