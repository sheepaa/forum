package com.newcoder.community.dao;

import com.newcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    // offset：当前页起始行的行号， limit：当前页有多少行
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // 要使用动态sql,最好用@Param注解，给参数取别名
    int selectDiscussPostRows(@Param("userId")int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);
}
