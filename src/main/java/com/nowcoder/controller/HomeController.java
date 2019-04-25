package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 主页面controller
 */
@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    public  static int QUESTION_LIMIT = 10;
    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FollowService followService;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    TopicService topicService;

    /**
     *@Description
     * 跳转到用户个人页面
     */
    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos", getQuestions(userId, 0, 10));

        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("currentUser", hostHolder.getUser());
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        if (hostHolder.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
        return "profile";
    }

    /**
     *@Description
     * 跳转到主页面，显示所有用户的问题
     */
    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET})
    public String index(Model model,HttpSession session){
        if(session.getAttribute("limit")==null){
            model.addAttribute("vos",getQuestions(0,0,10));
        }
        else{
            model.addAttribute("vos",getQuestions(0,0,(int)session.getAttribute("limit")));
        }
        return "index";
    }
    @RequestMapping(path = {"/"}, method = {RequestMethod.POST})
    public String FindMoreQuestions(Model model,HttpSession session){
        if(QUESTION_LIMIT==10){
            QUESTION_LIMIT = QUESTION_LIMIT+10;
            session.setAttribute("limit",QUESTION_LIMIT);
            model.addAttribute("vos",getQuestions(0,0,(int)session.getAttribute("limit")));
        }
        else{
            if((getQuestions(0,0,(int)session.getAttribute("limit")).size()%10)==0)
            {
                QUESTION_LIMIT = QUESTION_LIMIT+10;
                session.setAttribute("limit",QUESTION_LIMIT);
                model.addAttribute("vos",getQuestions(0,0,(int)session.getAttribute("limit")));
            }
            else
                model.addAttribute("vos",getQuestions(0,0,(int)session.getAttribute("limit")));
        }
        System.out.println(session.getAttribute("limit"));
        return "index";
    }
    /**
     *@Description
     * ViewObject作用：比如你一个模块要显示的不仅仅有问题的实体，还有用户的照片名称等，如果仅仅通过一个model传传不过去
     * 这里创建一个ViewObject实体，用来存储其它实体例如问题名称等，然后再把这些vo实体加入到一个ArrayList里
     * 在前台通过 vos.question 这样的访问
     */
    public List<ViewObject> getQuestions(int userId,int offset,int limit){
        List<Question> questionList = questionService.selectLatestQuestions(userId,offset,limit);

        ArrayList<ViewObject> vos = new ArrayList<>();
        for (Question question : questionList){
            ViewObject vo = new ViewObject();
            vo.set("question",question);
            vo.set("user",userService.getUser(question.getUserId()));
            vo.set("followCount",followService.getFollowerCount(EntityType.ENTITY_QUESTION,question.getId()));
            if (question.getTopic() != null) {
                vo.set("topic", topicService.getTopicById(question.getTopic()).getTopicName());
                vo.set("topicId", topicService.getTopicById(question.getTopic()).getId());
            }
            vos.add(vo);
        }
        return  vos;
    }
}
