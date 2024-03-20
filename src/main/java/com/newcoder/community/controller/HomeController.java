package com.newcoder.community.controller;

import com.newcoder.community.entity.Page;
import com.newcoder.community.service.LikeService;
import com.newcoder.community.util.CommunityConstant;
import org.springframework.ui.Model;
import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.entity.DiscussPost;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.DiscussPostService;
import com.newcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping(path = "/index") //获取首页信息
    public String getIndexPage(Model model, Page page){
        // 方法调用钱,SpringMVC会自动实例化Model和Page,并将Page注入Model.
        // 所以,在thymeleaf中可以直接访问Page对象中的数据.
        page.setPath("/index");
        page.setRows(discussPostService.findDiscussPostRows(0));
        List<Map<String, Object>> result = new ArrayList<>();
        List<DiscussPost> discussPosts = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        //根据十条discussPost分别找到他们的User
        for (DiscussPost discussPost : discussPosts) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(discussPost.getUserId());
            map.put("post", discussPost);
            map.put("user", user);

            long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
            map.put("likeCount", likeCount);
            result.add(map);
        }
        model.addAttribute("discussPosts", result);
        return "/index";
    }


}
