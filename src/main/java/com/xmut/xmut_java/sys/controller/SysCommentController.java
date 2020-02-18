package com.xmut.xmut_java.sys.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmut.xmut_java.common.BaseController;
import com.xmut.xmut_java.common.Result;
import com.xmut.xmut_java.sys.entity.SysComment;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.entity.SysExperienceComment;
import com.xmut.xmut_java.sys.mapper.SysCommentMapper;
import com.xmut.xmut_java.sys.mapper.SysExperienceCommentMapper;

@RestController
@RequestMapping("/sysComment")
public class SysCommentController extends BaseController{
	@Autowired
	private SysCommentMapper sysCommentMapper;
	
	@Autowired
	private SysExperienceCommentMapper sysExperienceCommentMapper;
	
	@RequestMapping("/getAll")
	public Result getAll(int currentPage, int pageSize, Long id, int flag) {
		Result result = new Result();
		
		try {
			Page<SysComment> page = new Page<SysComment>(currentPage, pageSize);
			SysComment params = new SysComment();
			QueryWrapper<SysComment> warpper = new QueryWrapper<SysComment>(params);
			Set<Long> commentidSet = new HashSet<Long>();
			SysExperienceComment queryComment = new SysExperienceComment();
			
			queryComment.setExperienceId(id);
			List<SysExperienceComment> commentList = sysExperienceCommentMapper.selectList(new QueryWrapper<SysExperienceComment>(queryComment));
			
			if (commentList != null && !commentList.isEmpty()) {
				for (SysExperienceComment tmp : commentList) {
					commentidSet.add(tmp.getCommentId());
				}
			}
			
			warpper.in("id", commentidSet);
			if (flag == 1) warpper.orderByDesc("create_time");
			else warpper.orderByDesc("favor_num");
			result.setData(sysCommentMapper.selectPage(page, warpper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/insert")
	public Result insert(Long id, String content, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			SysComment params = new SysComment();
			
			params.setAuthor(currentUser.getUsername());
			params.setContent(content);
			params.setCreateTime(new Date());
			params.setFavorNum((long) 0);
			
			sysCommentMapper.insert(params);
			SysComment comment = sysCommentMapper.selectOne(new QueryWrapper<SysComment>(params));
			
			if (comment != null) {
				Long commentId = comment.getId();
				SysExperienceComment params2 = new SysExperienceComment();
				
				params2.setExperienceId(id);
				params2.setCommentId(commentId);
				sysExperienceCommentMapper.insert(params2);
				result.setMessage("评论成功，点击确定前往查看!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
