package top.kongk.wenda.async.handler;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.kongk.wenda.async.EventHandler;
import top.kongk.wenda.async.EventModel;
import top.kongk.wenda.async.EventType;
import top.kongk.wenda.model.EntityType;
import top.kongk.wenda.model.Message;
import top.kongk.wenda.model.User;
import top.kongk.wenda.service.FollowService;
import top.kongk.wenda.service.MessageService;
import top.kongk.wenda.service.UserService;
import top.kongk.wenda.util.WendaUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 回答问题事件
 *
 * @author kk
 */
@Component
public class AnswerHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    FollowService followService;

    @Override
    public void doHandle(EventModel model) {

        /*
         * 回答问题, 要让所有关注此问题的人, 以及问题的创建人 都收到信息
         */

        //获取toId
        int questionId = model.getEntityId();
        int answerId = Integer.parseInt(model.getExt("answerId"));

        //获取问题的所有关注者
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_QUESTION, questionId, -1);
        int ownerId = model.getEntityOwnerId();

        if (!followers.contains(ownerId)) {
            followers.add(ownerId);
        }

        List<Message> messageList = new ArrayList<>(followers.size());

        for (Integer id : followers) {

            Message message = new Message();
            message.setFromId(WendaUtil.SYSTEM_USERID);
            message.setToId(id);
            message.setCreatedDate(new Date());
            message.setHasRead(0);

            User user = userService.getUser(model.getActorId());

            if (id == ownerId) {
                message.setContent("用户[" + user.getName()
                        + "]回答了您提出的问题, <a href=\"/question/" + questionId + "/answer?aid=" +
                        answerId + "\">回答链接</a>");
            } else {
                message.setContent("用户[" + user.getName()
                        + "]回答了您关注的问题, <a href=\"/question/" + questionId + "/answer?aid=" +
                        answerId + "\">回答链接</a>");
            }

            messageList.add(message);
        }

        messageService.addMessageList(messageList);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.Answer);
    }
}
