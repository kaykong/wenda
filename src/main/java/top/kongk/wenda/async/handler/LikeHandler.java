package top.kongk.wenda.async.handler;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.kongk.wenda.async.EventHandler;
import top.kongk.wenda.async.EventModel;
import top.kongk.wenda.async.EventType;
import top.kongk.wenda.model.Message;
import top.kongk.wenda.model.User;
import top.kongk.wenda.service.MessageService;
import top.kongk.wenda.service.UserService;
import top.kongk.wenda.util.MyMailSender;
import top.kongk.wenda.util.WendaUtil;

import java.util.*;


/**
 * 处理点赞事件的
 *
 * @author kk
 */
@Component
public class LikeHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    MyMailSender mailSender;

    @Override
    public void doHandle(EventModel model) {
        //处理点赞事件, 谁给哪个回答点了赞, 使用系统用户通知回答者
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setHasRead(0);
        message.setCreatedDate(new Date());
        User actorUser = userService.getUser(model.getActorId());
        User entityOwnerUser = userService.getUser(model.getEntityOwnerId());
        message.setContent("用户 [" + actorUser.getName()
                + "] 赞了您的回答 , <a href=\"/question/"+ model.getExt("questionId") + "/answer?aid=" + model.getEntityId() + "\">您的回答链接</a>");

        messageService.addMessage(message);

        //发送邮件提醒
        /*if (StringUtils.isNotBlank(entityOwnerUser.getEmail())) {
            //可以判断用户是否想要接收点赞通知
            Map<String, Object> map = new HashMap<>(2);
            map.put("username", entityOwnerUser.getName());
            map.put("actorUserName", actorUser.getName());
            map.put("message", message.getContent());
            mailSender.sendWithHTMLTemplate(entityOwnerUser.getEmail(), "有人给你点赞了", "mails/like_comment.html", map);
        }*/
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
