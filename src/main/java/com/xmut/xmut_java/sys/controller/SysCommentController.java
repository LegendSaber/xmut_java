package com.xmut.xmut_java.sys.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmut.xmut_java.common.BaseController;
import com.xmut.xmut_java.common.Result;
import com.xmut.xmut_java.sys.entity.SysComment;
import com.xmut.xmut_java.sys.entity.SysCommentRelation;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.entity.SysExperienceComment;
import com.xmut.xmut_java.sys.entity.SysFavorComment;
import com.xmut.xmut_java.sys.entity.SysKnowledgeComment;
import com.xmut.xmut_java.sys.entity.SysSonComment;
import com.xmut.xmut_java.sys.mapper.SysCommentMapper;
import com.xmut.xmut_java.sys.mapper.SysCommentRelationMapper;
import com.xmut.xmut_java.sys.mapper.SysExperienceCommentMapper;
import com.xmut.xmut_java.sys.mapper.SysFavorCommentMapper;
import com.xmut.xmut_java.sys.mapper.SysKnowledgeCommentMapper;
import com.xmut.xmut_java.sys.mapper.SysSonCommentMapper;
import com.xmut.xmut_java.sys.mapper.SysUserMapper;

@RestController
@RequestMapping("/sysComment")
public class SysCommentController extends BaseController{
	@Autowired
	private SysCommentMapper sysCommentMapper;
	
	@Autowired
	private SysExperienceCommentMapper sysExperienceCommentMapper;
	
	@Autowired
	private SysFavorCommentMapper sysFavorCommentMapper;
	
	@Autowired
	private SysSonCommentMapper sysSonCommentMapper;
	
	@Autowired
	private SysCommentRelationMapper sysCommentRelationMapper;
	
	@Autowired
	private SysKnowledgeCommentMapper sysKnowledgeCommentMapper;
	
	@Autowired
	private SysUserMapper sysUserMapper;
	
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
			IPage p = sysCommentMapper.selectPage(page, warpper);
			
			if (p != null) {
				List<SysComment> last = new ArrayList<SysComment>();
				List<SysComment> comment = p.getRecords();
				for (SysComment c : comment) {
					Long fatherId = c.getId();
					SysCommentRelation queryRelation = new SysCommentRelation();
					queryRelation.setFatherId(fatherId);
					List<SysCommentRelation> relationList = sysCommentRelationMapper.selectList(new QueryWrapper<SysCommentRelation>(queryRelation));
					List<SysSonComment> sonCommentList = new ArrayList<SysSonComment>();
					for (SysCommentRelation r : relationList) {
						SysSonComment sonParams = new SysSonComment();
						
						sonParams.setId(r.getSonId());
						SysSonComment addSon = sysSonCommentMapper.selectOne(new QueryWrapper<SysSonComment>(sonParams));
						sonCommentList.add(addSon);
					}
					c.setSonComment(sonCommentList);
					last.add(c);
				}
				p.setRecords(last);
				result.setData(p);
			}
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
			Long userId = currentUser.getId();
			SysUser userParams = new SysUser();
			userParams.setId(userId);
			SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(userParams));
			
			if (user.getScore() < 2) {
				result.fail("积分不足，提交失败");
			} else {
				user.setScore(user.getScore() - 1);
				sysUserMapper.update(user, new UpdateWrapper<SysUser>().eq("id", userId));
				SysComment params = new SysComment();
				
				params.setAuthor(currentUser.getUsername());
				params.setContent(content);
				params.setCreateTime(new Date());
				params.setFavorNum((long) 0);
				params.setSonComment(null);
				
				sysCommentMapper.insert(params);
				Long commentId = params.getId();
				SysExperienceComment params2 = new SysExperienceComment();
					
				params2.setExperienceId(id);
				params2.setCommentId(commentId);
				sysExperienceCommentMapper.insert(params2);
				result.success("评论成功，点击确定前往查看!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/recommend")
	public Result recommend(Long id, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			Long userId = currentUser.getId();
			SysFavorComment params = new SysFavorComment();
			params.setUserId(userId);
			params.setCommentId(id);
			
			SysFavorComment hascomment = sysFavorCommentMapper.selectOne(new QueryWrapper<SysFavorComment>(params));
			
			if (hascomment == null) {
				SysComment commentParams = new SysComment();
				commentParams.setId(id);
				SysComment comment = sysCommentMapper.selectOne(new QueryWrapper<SysComment>(commentParams));
				
				comment.setFavorNum(comment.getFavorNum() + 1);
				sysCommentMapper.update(comment, new UpdateWrapper<SysComment>().eq("id", id));
				
				SysUser userParams = new SysUser();
				
				userParams.setUsername(comment.getAuthor());
				
				SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(userParams));
				user.setScore(user.getScore() + 1);
				sysUserMapper.update(user, new UpdateWrapper<SysUser>().eq("id", user.getId()));
				
				SysFavorComment favor = new SysFavorComment();
				favor.setCommentId(id);
				favor.setUserId(userId);
				sysFavorCommentMapper.insert(favor);
				result.success("推荐成功!");
			} else {
				result.fail("已经推荐过了");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/insertSon")
	public Result insertSon(Long id, String content, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			SysCommentRelation relationParams = new SysCommentRelation();
			SysSonComment params = new SysSonComment();
			params.setAuthor(currentUser.getUsername());
			params.setContent(content);
			params.setCreateTime(new Date());
			
			sysSonCommentMapper.insert(params);
			relationParams.setFatherId(id);
			relationParams.setSonId(params.getId());
			sysCommentRelationMapper.insert(relationParams);
			result.setMessage("回复成功，点击确定查看!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/insertKnowledgeComment")
	public Result insertKnowledgeComment(Long id, String content, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			Long userId = currentUser.getId();
			SysUser userParams = new SysUser();
			userParams.setId(userId);
			SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(userParams));
			if (user.getScore() < 2) {
				result.fail("积分不足，评论失败");
			} else {
				user.setScore(user.getScore() - 2);
				SysComment commentParams = new SysComment();
				SysKnowledgeComment params = new SysKnowledgeComment();
				
				commentParams.setAuthor(currentUser.getUsername());
				commentParams.setContent(content);
				commentParams.setCreateTime(new Date());
				commentParams.setFavorNum((long)0);
				
				sysCommentMapper.insert(commentParams);
				
				params.setKnowledgeId(id);
				params.setCommentId(commentParams.getId());
				sysKnowledgeCommentMapper.insert(params);
				result.success("评论成功,点击确定前往查看!");
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getKnowledgeComment")
	public Result getKnowledgeComment(int currentPage, int pageSize, Long id, int flag) {
		Result result = new Result();
		
		try {
			Page<SysComment> page = new Page<SysComment>(currentPage, pageSize);
			SysComment params = new SysComment();
			QueryWrapper<SysComment> wrapper = new QueryWrapper<SysComment>(params);
			Set<Long> commentSet = new HashSet<Long>();
			SysKnowledgeComment commentParams = new SysKnowledgeComment();
			
			commentParams.setKnowledgeId(id);
			List<SysKnowledgeComment> commentList = sysKnowledgeCommentMapper.selectList(new QueryWrapper<SysKnowledgeComment>(commentParams));
		
			if (commentList != null && !commentList.isEmpty()) {
				for (SysKnowledgeComment comment : commentList) {
					commentSet.add(comment.getCommentId());
				}
			}
			
			wrapper.in("id", commentSet);
			if (flag == 1) wrapper.orderByDesc("create_time");
			else wrapper.orderByDesc("favor_num");
			
			IPage p = sysCommentMapper.selectPage(page, wrapper);
			
			if (p != null) {
				List<SysComment> last = new ArrayList<SysComment>();
				List<SysComment> comment = p.getRecords();
				for (SysComment c : comment) {
					Long fatherId = c.getId();
					SysCommentRelation queryRelation = new SysCommentRelation();
					queryRelation.setFatherId(fatherId);
					List<SysCommentRelation> relationList = sysCommentRelationMapper.selectList(new QueryWrapper<SysCommentRelation>(queryRelation));
					List<SysSonComment> sonCommentList = new ArrayList<SysSonComment>();
					for (SysCommentRelation r : relationList) {
						SysSonComment sonParams = new SysSonComment();
						
						sonParams.setId(r.getSonId());
						SysSonComment addSon = sysSonCommentMapper.selectOne(new QueryWrapper<SysSonComment>(sonParams));
						sonCommentList.add(addSon);
					}
					c.setSonComment(sonCommentList);
					last.add(c);
				}
				p.setRecords(last);
				result.setData(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
}
