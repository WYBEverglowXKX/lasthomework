package com.nowcoder.service;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * 问题服务
 */
@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;
    /**
     *@Description
     * setvice层从dao获取问题列表
     */
    public List<Question> selectLatestQuestions(int userId,int offset,int limit){
        return  questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    /**
     *@Description
     * 处理用户发布新问题逻辑
     */
    public int addQuestion(Question question) {
        //敏感词过滤
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setSelectType(question.getSelectType());
        question.setTopic(question.getTopic());
        return questionDAO.addQuestion(question)>0?question.getId():0;
    }

    public Question getById(int id){
        return questionDAO.getById(id);
    }

    public int updateCommentCount(int id, int count) {
        return questionDAO.updateCommentCount(id, count);
    }

    public List<Question> getByTopic(List<Integer> topicList) {
        return questionDAO.getByTopic(topicList);
    }

    public List<Question> getBySoleTopic(int topicId) {
        return questionDAO.getBySoleTopic(topicId);
    }
}
