
package com.nowcoder.controller;

import com.nowcoder.async.EventProducer;
import com.nowcoder.model.User;
import com.nowcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 登录
 */
@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;
    /**
     *@Description
     * 用来跳转到主页面
     */
    @ResponseBody
    @RequestMapping(path = {"/msg/getUserNameByUserId"}, method = {RequestMethod.GET})
    public String getUserNameByUserId(Model model, @RequestParam("id") String id) {

        User user = userService.getUser(Integer.parseInt(id));
        String name = user.getName();
        return  name;

    }



}
