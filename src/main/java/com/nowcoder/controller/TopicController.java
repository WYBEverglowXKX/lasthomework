package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.TopicService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 话题模块
 */

@Controller
public class TopicController {
    private static final Logger logger = LoggerFactory.getLogger(TopicController.class);

    @Autowired
    TopicService topicService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/topic", method = {RequestMethod.GET})
    public String topicShow(Model model) {
//        List<Integer> followList = followService.getFollowees(hostHolder.getUser().getId(), EntityType.ENTITY_TOPIC, -1);
//        if (followList == null || followList.size() == 0) {
        List<ViewObject> topicList = new ArrayList<>();
        List<Topic> topic = topicService.getTopic();
        for (Topic topic1 : topic) {
            ViewObject vo = new ViewObject();
            vo.set("id", topic1.getId());
            vo.set("topicName", topic1.getTopicName());
            vo.set("topicImg", topic1.getTopicImg());
            vo.set("user", hostHolder.getUser());
            boolean res = followService.isFollowee(hostHolder.getUser().getId(), EntityType.ENTITY_TOPIC, topic1.getId());
            vo.set("followed", followService.isFollowee(hostHolder.getUser().getId(), EntityType.ENTITY_TOPIC, topic1.getId()));
            topicList.add(vo);
        }
        long count = followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_TOPIC);
        model.addAttribute("topicList", topicList);
        model.addAttribute("followedCount", count);

        return "topic";
//        } else {
//            return topicDetail(model);
//        }

    }

    @RequestMapping(path = {"/followTopics"}, method = {RequestMethod.POST})
    @ResponseBody
    public String followTopic(@RequestParam("topicId") int topicId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }
        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_TOPIC, topicId);
        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_TOPIC, topicId));
        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    @RequestMapping(path = {"/unFollowTopics"}, method = {RequestMethod.POST})
    @ResponseBody
    public String unFollowTopic(@RequestParam("topicId") int topicId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }
        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_TOPIC, topicId);
        Map<String, Object> info = new HashMap<>();
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_TOPIC, topicId));
        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    @RequestMapping(path = {"/topicDetail"}, method = {RequestMethod.GET})
    public String topicDetail(Model model) {
        List<Integer> followList = followService.getFollowees(hostHolder.getUser().getId(), EntityType.ENTITY_TOPIC, -1);
        if (followList.size() != 0 && followList != null) {
            List<Question> questionList = questionService.getByTopic(followList);
            List<ViewObject> questions = new ArrayList<>();
            for (Question question : questionList) {
                ViewObject vo = new ViewObject();
                vo.set("question", question);
                vo.set("user", userService.getUser(question.getUserId()));
                vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
                questions.add(vo);
            }
            model.addAttribute("currentUser", hostHolder.getUser());
            model.addAttribute("questions", questions);
            return "topicDetail";
        } else {
            return topicShow(model);
        }
    }

    @RequestMapping(path = {"/topic/{topicId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String soleTopic(Model model, @PathVariable("topicId") int topicId) {
        Topic topic = topicService.getTopicById(topicId);
        List<Question> questionList = questionService.getBySoleTopic(topicId);
        List<ViewObject> questions = new ArrayList<>();
        for (Question question : questionList) {
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("user", userService.getUser(question.getUserId()));
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            questions.add(vo);
        }
        model.addAttribute("topic", topic);
        model.addAttribute("followed", followService.isFollowee(hostHolder.getUser().getId(), EntityType.ENTITY_TOPIC, topicId));
        model.addAttribute("questions", questions);
        return "soleTopic";
    }


}
