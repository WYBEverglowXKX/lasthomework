package com.nowcoder.service;

import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * 消息服务
 */
@Service
public class MessageService {
    @Autowired
    MessageDAO messageDAO;

    @Autowired
    SensitiveService sensitiveService;

    /**
     *@Description
     * 发送消息Service
     */
    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveService.filter(message.getContent()));
        return messageDAO.addMessage(message)>0?message.getId():0;
    }

    public List<Message> getConversationDetail(String conversationId, int offset, int limit){
        return messageDAO.getConversationDetail(conversationId, offset, limit);
    }

    public List<Message> getConversationList(int userId, int offset, int limit){
        return messageDAO.getConversationList(userId, offset, limit);
    }

    public int getConversationUnreadCount(int userId,String coversationId){
        return messageDAO.getConversationUnreadCount(userId, coversationId);
    }

    public int deleteMessage(String conversationId){
        int isDeleted = 1;
        try{
            messageDAO.deleteMessage(conversationId);
        }catch (Exception e){
            isDeleted = -1;
        }
        return isDeleted;
    }

    public int deleteSingleMessage(int id){
        int isDeleted = 1;
        try{
            messageDAO.deleteSingleMessage(id);
        }catch (Exception e){
            isDeleted = -1;
        }
        return isDeleted;
    }
}
