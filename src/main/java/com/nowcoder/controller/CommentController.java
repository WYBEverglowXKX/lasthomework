package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * 评论服务
 */
@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    EventProducer eventProducer;


    /*
    添加问题评论
     */
    @RequestMapping(path = {"/addComment"},method = {RequestMethod.POST,RequestMethod.GET})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){
        Comment comment = new Comment();
        try{
            comment.setContent(content);
            comment.setCreatedDate(new Date());
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setEntityId(questionId);
            if(hostHolder.getUser() != null){
                comment.setUserId(hostHolder.getUser().getId());
            }else{
                return "redirect:/reglogin";
            }
            commentService.addComment(comment);
        }catch (Exception e){
            logger.error("评论失败"+e.getMessage());
        }
        questionService.updateCommentCount(comment.getEntityId(),
                commentService.getCommentCount(comment.getEntityId(),comment.getEntityType()));//更新问题对应的总评论数
        eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(comment.getUserId())
                .setEntityId(questionId));
        return "redirect:/question/" + questionId;

    }

    @RequestMapping(path = {"/addCommentForComment"},method = {RequestMethod.POST,RequestMethod.GET})
    public String addCommentForComment(@RequestParam("commentId") int commentId,
                                       @RequestParam("cContent") String cContent,
                                       @RequestParam("qId") int questionId){
        Comment comment = new Comment();
        try{
            comment.setContent(cContent);
            comment.setCreatedDate(new Date());
            comment.setEntityType(EntityType.ENTITY_COMMENT);
            comment.setEntityId(commentId);
            if(hostHolder.getUser() != null){
                comment.setUserId(hostHolder.getUser().getId());
            }else{
                return "redirect:/reglogin";
            }
            commentService.addComment(comment);
        }catch (Exception e){
            logger.error("评论失败"+e.getMessage());
        }
        questionService.updateCommentCount(comment.getEntityId(),
                commentService.getCommentCount(comment.getEntityId(),comment.getEntityType()));//更新问题对应的总评论数
        eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(comment.getUserId())
                .setEntityId(commentId));
        return "redirect:/question/" + questionId;

    }
}
