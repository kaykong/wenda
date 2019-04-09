package top.kongk.wenda.controller;


import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.kongk.wenda.model.HostHolder;
import top.kongk.wenda.model.Message;
import top.kongk.wenda.model.User;
import top.kongk.wenda.model.ViewObject;
import top.kongk.wenda.service.MessageService;
import top.kongk.wenda.service.SensitiveService;
import top.kongk.wenda.service.UserService;
import top.kongk.wenda.util.WendaUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author kk
 */
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    SensitiveService sensitiveService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model) {
        try {
            int localUserId = hostHolder.getCurrentUser().getId();
            List<ViewObject> conversations = new ArrayList<>();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("user", user);
                vo.set("unread", messageService.getConvesationUnreadCount(localUserId, msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model, @Param("conversationId") String conversationId) {
        try {
            User currentUser = hostHolder.getCurrentUser();
            if (StringUtils.isBlank(conversationId)) {
                return "letterDetail";
            }

            String[] userIds = conversationId.split("_");
            if (userIds.length != 2) {
                return "letterDetail";
            }

            Boolean check = false;
            for (String userId : userIds) {
                if (userId.equals(currentUser.getId().toString())) {
                    check = true;
                    break;
                }
            }

            if (!check) {
                return "redirect:/";
            }

            List<Message> conversationList = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                User user = userService.getUser(msg.getFromId());
                if (user == null) {
                    continue;
                }
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());
                messages.add(vo);
            }
            model.addAttribute("messages", messages);

            /*
             * 把 conversationId 中 toId 为当前用户id的 message 的修改为已读
             */
            messageService.updateMessageStatusByConversationId(conversationId, currentUser.getId());

        } catch (Exception e) {
            logger.error("获取详情消息失败" + e.getMessage());
        }



        return "letterDetail";
    }


    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content) {
        try {
            if (hostHolder.getCurrentUser() == null) {
                return WendaUtil.getJSONString(999, "未登录");
            }
            User user = userService.getUserByName(toName);
            if (user == null) {
                return WendaUtil.getJSONString(1, "用户不存在");
            }

            Message msg = new Message();
            msg.setContent(sensitiveService.filter(content));
            msg.setFromId(hostHolder.getCurrentUser().getId());
            msg.setToId(user.getId());
            msg.setCreatedDate(new Date());
            msg.setHasRead(0);
            messageService.addMessage(msg);
            return WendaUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("增加站内信失败" + e.getMessage());
            return WendaUtil.getJSONString(1, "插入站内信失败");
        }
    }


    /*@RequestMapping(path = {"/msg/jsonAddMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content) {
        try {
            Message msg = new Message();
            msg.setContent(content);
            msg.setFromId(fromId);
            msg.setToId(toId);
            msg.setCreatedDate(new Date());
            //msg.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));
            messageService.addMessage(msg);
            return WendaUtil.getJSONString(msg.getId());
        } catch (Exception e) {
            logger.error("增加评论失败" + e.getMessage());
            return WendaUtil.getJSONString(1, "插入评论失败");
        }
    }*/
}
