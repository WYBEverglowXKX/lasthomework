package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.TopicService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wang on 2019/3/27.
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

    @RequestMapping(value = "/topicDetail", method = {RequestMethod.GET})
    public String topicShow(Model model) {
        //先取出当前用户关注的话题，再根据话题去查具体问题来显示
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;

        //List<Question> questionList = questionService.getByTopic();
        List<ViewObject> topicList = new ArrayList<>();
        List<Topic> topic = topicService.getTopic();
        for (Topic topic1 : topic) {
            ViewObject vo = new ViewObject();
            vo.set("id", topic1.getId());
            vo.set("topicName", topic1.getTopicName());
            vo.set("topicImg", topic1.getTopicImg());
            vo.set("user", hostHolder.getUser());
            vo.set("followed", false);
            topicList.add(vo);
        }
        model.addAttribute("topicList", topicList);

        return "topic";
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
    public String unFollowTopic(@RequestParam("topicId") int topicId){
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }
        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_TOPIC, topicId);
        Map<String, Object> info = new HashMap<>();
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_TOPIC, topicId));
        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }


}
