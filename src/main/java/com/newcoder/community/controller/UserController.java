package com.newcoder.community.controller;

import com.newcoder.community.annotation.LoginRequired;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.FollowService;
import com.newcoder.community.service.LikeService;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommonUtil;
import com.newcoder.community.util.CommunityConstant;
import com.newcoder.community.util.HostHolder;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Controller
@RequestMapping(path = "/user")
public class UserController implements CommunityConstant {

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @GetMapping(path = "/setting")
    public String getSetting(){
        return "/site/setting";
    }

    @PostMapping(path = "/upload")
    public String uploadHeader(MultipartFile headerImg, Model model){
        System.out.println(headerImg);
        //图片为空
        if(headerImg==null) {
            model.addAttribute("error", "您还没有选择图片");
            return "/site/setting";
        }
        String fileName = headerImg.getOriginalFilename();
        //截取图片后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //没有后缀
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error", "图片格式不正确");
            return "/site/setting";
        }
        fileName = CommonUtil.generateUUID()+suffix;
        File dest = new File(uploadPath+fileName);
        try {
            headerImg.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("上传文件失败，服务器发生异常",e);
        }

        //更新当前用户头像的路径（web访问路径）
        User user = hostHolder.getUsers();
        String headerUrl = domain + contextPath + "/user/header/"+fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";

    }

    @GetMapping(path = "/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //根据fileName拼接出对应路径
        fileName = uploadPath + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/"+suffix);
        try (FileInputStream fis = new FileInputStream(fileName)){
            OutputStream os = response.getOutputStream();
            //一次性写1024个byte
            byte[] buffer = new byte[1024];
            int b=0;
            while((b=fis.read(buffer)) != -1){
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            throw new RuntimeException("读取头像失败", e);
        }

    }

    @GetMapping(path = "/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        //查询用户并且判断用户是否存在
        User user = userService.findUserById(userId);
        if(user==null) {
            throw new IllegalArgumentException("用户不存在");
        }
        model.addAttribute("user", user);
        //点赞数量
        long likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);

        //当前登录用户对当前user是否已关注
        boolean hasFollowed = false;
        if(hostHolder.getUsers() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUsers().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }

}
