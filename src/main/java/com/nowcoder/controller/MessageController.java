package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 消息页面Controller
 */
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    /**
     * 发送消息处理模块，注意要判断发送的目的人是否存在
     */
    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String addComment(@RequestParam("content") String content,
                             @RequestParam("toName") String toName) {
        try {
            User user = userService.selectByName(toName);
            if (user==null){
                return WendaUtil.getJSONString(1,"用户不存在");
            }
            Message message = new Message();
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setToId(user.getId());
            if (hostHolder.getUser()==null){
               return WendaUtil.getJSONString(999,"用户未登录");
            }else {
                message.setFromId(hostHolder.getUser().getId());
            }
            messageService.addMessage(message);
            return  WendaUtil.getJSONString(0);
        }catch (Exception e){
            logger.error("评论失败"+e.getMessage());
            return WendaUtil.getJSONString(1,"发送失败");
        }
    }

    /**
     * 消息列表模块  显示当前用户和其它所有用户的未读信息
     */
    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String getConversationList(Model model) {
        if (hostHolder.getUser()==null){
            return "redirect:/reglogin";
        }
        int localUserId = hostHolder.getUser().getId();
        List<Message> messageList = messageService.getConversationList(localUserId,0,10);
        List<ViewObject> conversations = new ArrayList<ViewObject>();
        for (Message message : messageList){
            ViewObject vo = new ViewObject();
            vo.set("message",message);
            vo.set("user",userService.getUser(message.getFromId()));

            vo.set("unread", messageService.getConversationUnreadCount(localUserId, message.getConversationId()));

            conversations.add(vo);
        }
        model.addAttribute("conversations",conversations);


        return "letter";
    }

    /**
     * 消息具体模块，显示当前用户和其它具体用户聊天的信息
     */
    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String getConversationDetail(Model model, @RequestParam("conversationId") String conversationId) {
        List<Message> messagesList = messageService.getConversationDetail(conversationId,0,10);
        List<ViewObject> res = new ArrayList<ViewObject>();


        for (Message message : messagesList){
            ViewObject vo = new ViewObject();
            vo.set("message",message);
            vo.set("user",userService.getUser(message.getFromId()));
            vo.set("toUser",userService.getUser(message.getToId()));
            res.add(vo);
        }
        model.addAttribute("messages",res);
        return "letterDetail";
    }

    /**
     * 删除整个会话
     */
   // @ResponseBody
    @RequestMapping(path = {"/msg/deleteMessage"}, method = {RequestMethod.GET})
    public int deleteMessage(Model model, @RequestParam("conversationId") String conversationId) {

        int isDeleted = 0;
        isDeleted = messageService.deleteMessage(conversationId);
        return isDeleted;

    }

    /**
     * 删除单个评论
     */
   // @ResponseBody
    @RequestMapping(path = {"/msg/deleteSingleMessage"}, method = {RequestMethod.GET})
    public int deleteSingleMessage(Model model, @RequestParam("id") String id) {

        int isDeleted = 0;
        isDeleted = messageService.deleteSingleMessage(Integer.parseInt(id));
        return isDeleted;

    }



}
