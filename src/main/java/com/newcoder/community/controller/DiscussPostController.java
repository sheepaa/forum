package com.newcoder.community.controller;


import com.newcoder.community.entity.Comment;
import com.newcoder.community.entity.DiscussPost;
import com.newcoder.community.entity.Page;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.CommentService;
import com.newcoder.community.service.DiscussPostService;
import com.newcoder.community.service.LikeService;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommonUtil;
import com.newcoder.community.util.CommunityConstant;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @PostMapping(path = "/add")
    @ResponseBody
    public String addDiscussPost(String title, String content){
        //判断登录状态(不过拦截器不是会判断登录状态的嘛，加一个requiredLogin注解)
        User user = hostHolder.getUsers();
        if(user == null){
            return CommonUtil.getJSONString(403, "你还没有登录噢");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // 报错的情况,将来统一处理.
        return CommonUtil.getJSONString(0, "发布成功!");
    }

    @GetMapping(path = "/detail/{discussId}")
    public String getDiscussPost(@PathVariable("discussId") int discussId, Model model, Page page){
        //帖子的详情、作者、点赞信息
        DiscussPost discussPost = discussPostService.findDiscussPost(discussId);
        model.addAttribute("post", discussPost);
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user", user);
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussId);
        //当用户没有登录时，也可以查看到点赞信息，但是点赞状态就肯定为0
        int likeStatus = hostHolder.getUsers() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUsers().getId(), ENTITY_TYPE_POST, discussId);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("likeStatus", likeStatus);

        //设置分页信息 每次访问下一个评论时的path还是此方法
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussId);
        page.setRows(discussPost.getCommentCount());

        // comment:给帖子的评论
        // reply：给评论的评论
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, discussPost.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();

        for (Comment comment : commentList) {
            Map<String, Object> commentVo = new HashMap<>();
            //评论
            commentVo.put("comment", comment);
            //作者
            commentVo.put("user", userService.findUserById(comment.getUserId()));
            //点赞
            long commentLikeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
            int commentLikeStatus = hostHolder.getUsers() == null ? 0 :
                    likeService.findEntityLikeStatus(hostHolder.getUsers().getId(), ENTITY_TYPE_COMMENT, comment.getId());
            commentVo.put("likeCount", commentLikeCount);
            commentVo.put("likeStatus", commentLikeStatus);
            //评论的评论（回复）
            List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
            List<Map<String, Object>> replyVoList = new ArrayList<>();
            for (Comment reply : replyList) {
                //评论信息、发布评论的user、点赞信息
                Map<String, Object> replyVo = new HashMap<>();
                replyVo.put("reply", reply);
                replyVo.put("user", userService.findUserById(reply.getUserId()));
                long replyLikeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                int replyLikeStatus = hostHolder.getUsers() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUsers().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                replyVo.put("likeCount", replyLikeCount);
                replyVo.put("likeStatus", replyLikeStatus);

                //看有没有回复目标
                User target = reply.getTargetId()==0 ? null : userService.findUserById(reply.getTargetId());
                replyVo.put("target", target);
                replyVoList.add(replyVo);
            }
            commentVo.put("replys", replyVoList);

            //回复数量(要在前端界面上展示)
            int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
            commentVo.put("replyCount", replyCount);
            commentVoList.add(commentVo);
        }
        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }
}
