package com.xmut.xmut_java.sys.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmut.xmut_java.common.BaseController;
import com.xmut.xmut_java.common.Result;
import com.xmut.xmut_java.sys.entity.SysComment;
import com.xmut.xmut_java.sys.entity.SysFavorKnowledge;
import com.xmut.xmut_java.sys.entity.SysKnowledge;
import com.xmut.xmut_java.sys.entity.SysKnowledgePicture;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.entity.SysUserKnowledge;
import com.xmut.xmut_java.sys.mapper.SysCommentMapper;
import com.xmut.xmut_java.sys.mapper.SysFavorKnowledgeMapper;
import com.xmut.xmut_java.sys.mapper.SysKnowledgeMapper;
import com.xmut.xmut_java.sys.mapper.SysKnowledgePictureMapper;
import com.xmut.xmut_java.sys.mapper.SysPictureMapper;
import com.xmut.xmut_java.sys.mapper.SysUserKnowledgeMapper;
import com.xmut.xmut_java.sys.service.SysFileService;

@RestController
@RequestMapping("/sysKnowledge")
public class SysKnowledgeController extends BaseController{
	@Autowired
	private SysKnowledgeMapper sysKnowledgeMapper;
	
	@Autowired
	private SysUserKnowledgeMapper sysUserKnowledgeMapper;
	
	@Autowired
	private SysFavorKnowledgeMapper sysFavorKnowledgeMapper;
	
	@Autowired
	private SysFileService sysFileService;
	
	@Autowired
	private SysCommentMapper sysCommentMapper;
	
	@Autowired
	private SysKnowledgePictureMapper sysKnowledgePictureMapper;
	
	@Autowired
	private SysPictureMapper sysPictureMapper;
	
	
	@RequestMapping("/insert")
	public Result insert(String title, String content, String category, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			Date currentDate = new Date();
			SysUser currentUser = (SysUser) request.getSession().getAttribute("user");
			SysKnowledge knowledgeParams = new SysKnowledge();
			SysUserKnowledge userKnowlegeParams = new SysUserKnowledge();
			
			knowledgeParams.setAuthor(currentUser.getUsername());
			knowledgeParams.setTitle(title);
			knowledgeParams.setContent(content);
			knowledgeParams.setCategory(category);
			knowledgeParams.setFavorNum((long)0);
			knowledgeParams.setCreateTime(currentDate);
			knowledgeParams.setModifyTime(currentDate);
			sysKnowledgeMapper.insert(knowledgeParams);
			request.getSession().setAttribute("knowledge", knowledgeParams);
			
			userKnowlegeParams.setUserId(currentUser.getId());
			userKnowlegeParams.setKnowledgeId(knowledgeParams.getId());
			sysUserKnowledgeMapper.insert(userKnowlegeParams);
			
			result.setMessage("分享知识成功，点击确定查看");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getAll")
	public Result getAll(int currentPage, int pageSize, String category, int flag) {
		Result result = new Result();
		
		try {
			Page<SysKnowledge> page = new Page<SysKnowledge>(currentPage, pageSize);
			SysKnowledge params = new SysKnowledge();
			QueryWrapper<SysKnowledge> wrapper = new QueryWrapper<SysKnowledge>(params);
			Set<Long> idList = new HashSet<Long>();
			SysKnowledge knowledgeParams = new SysKnowledge();
			
			knowledgeParams.setCategory(category);
			List<SysKnowledge> knowledgeList = sysKnowledgeMapper.selectList(new QueryWrapper<SysKnowledge>(knowledgeParams));
			
			if (knowledgeList != null && !knowledgeList.isEmpty()) {
				for (SysKnowledge knowledge : knowledgeList) {
					idList.add(knowledge.getId());
				}
			}
			
			wrapper.in("id", idList);
			if (flag == 1) wrapper.orderByDesc("modify_time");
			else wrapper.orderByDesc("favor_num");
			
			result.setData(sysKnowledgeMapper.selectPage(page, wrapper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getFavorKnowledge")
	public Result getFavorKnowledge(int currentPage, int pageSize, String category, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			Page<SysKnowledge> page = new Page<SysKnowledge>(currentPage, pageSize);
			SysKnowledge params = new SysKnowledge();
			QueryWrapper<SysKnowledge> wrapper = new QueryWrapper<SysKnowledge>(params); 
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			SysFavorKnowledge favorKnowledgeParams = new SysFavorKnowledge();
			Set<Long> idList = new HashSet<Long>();
			favorKnowledgeParams.setUserId(currentUser.getId());
			List<SysFavorKnowledge> favorList = sysFavorKnowledgeMapper.selectList(new QueryWrapper<SysFavorKnowledge>(favorKnowledgeParams));
			
			if (favorList != null && !favorList.isEmpty()) {
				for (SysFavorKnowledge favor : favorList) {
					idList.add(favor.getKnowledgeId());
				}
			}
			wrapper.in("id", idList);
			wrapper.eq("category", category);
			wrapper.orderByDesc("modify_time");
			
			result.setData(sysKnowledgeMapper.selectPage(page, wrapper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getMyKnowledge")
	public Result getMyKnowledge(int currentPage, int pageSize, String category, HttpServletRequest request ) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			Page<SysKnowledge> page = new Page<SysKnowledge>(currentPage, pageSize);
			SysKnowledge params = new SysKnowledge();
			QueryWrapper<SysKnowledge> wrapper = new QueryWrapper<SysKnowledge>(params);
			Set<Long> idList = new HashSet<Long>();
			SysUserKnowledge userKnowledgeParams = new SysUserKnowledge();
			userKnowledgeParams.setUserId(currentUser.getId());
			List<SysUserKnowledge> userKnowledgeList = sysUserKnowledgeMapper.selectList(new QueryWrapper<SysUserKnowledge>(userKnowledgeParams));
			
			if (userKnowledgeList != null && !userKnowledgeList.isEmpty()) {
				for (SysUserKnowledge userKnowledge : userKnowledgeList) {
					idList.add(userKnowledge.getKnowledgeId());
				}
			}
			
			wrapper.in("id", idList);
			wrapper.eq("category", category);
			wrapper.orderByDesc("modify_time");
			result.setData(sysKnowledgeMapper.selectPage(page, wrapper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/upload")
	public Result upload(HttpServletRequest request) {
		Result result = new Result();
		SysKnowledge knowledge = (SysKnowledge)request.getSession().getAttribute("knowledge");
		List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
		int len = files.size();
	
		for (int i = 0; i < len; i++) {		
			MultipartFile file = files.get(i);
			if (file != null){
				try {
					sysFileService.savePicture(file.getOriginalFilename(), file.getBytes(), knowledge.getId());		
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	
		return result;
	}
	
	@RequestMapping("/isCollect")
	public Result isCollect(Long id, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			SysFavorKnowledge favorParams = new SysFavorKnowledge();
			
			favorParams.setUserId(currentUser.getId());
			favorParams.setKnowledgeId(id);
			
			SysFavorKnowledge favor = sysFavorKnowledgeMapper.selectOne(new QueryWrapper<SysFavorKnowledge>(favorParams));
		
			if (favor == null) {
				result.fail("未收藏");
			} else {
				result.success("已收藏");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/isMyKnowledge")
	public Result isMyKnowledge(Long id, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			SysUserKnowledge myKnowledgeParams = new SysUserKnowledge();
			
			myKnowledgeParams.setUserId(currentUser.getId());
			myKnowledgeParams.setKnowledgeId(id);
			
			SysUserKnowledge my = sysUserKnowledgeMapper.selectOne(new QueryWrapper<SysUserKnowledge>(myKnowledgeParams));
			
			if (my == null) {
				result.fail("不是我的");
			} else {
				result.success("是我的");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/collect")
	public Result collect(Long id, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysKnowledge knowledge = null;
			SysKnowledge knowledeParams = new SysKnowledge();
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			SysFavorKnowledge favorParams = new SysFavorKnowledge();
			
			favorParams.setUserId(currentUser.getId());
			favorParams.setKnowledgeId(id);
			
			sysFavorKnowledgeMapper.insert(favorParams);
			
			knowledeParams.setId(id);
			knowledge = sysKnowledgeMapper.selectOne(new QueryWrapper<SysKnowledge>(knowledeParams));
			knowledge.setFavorNum(knowledge.getFavorNum() + 1);
			sysKnowledgeMapper.update(knowledge, new UpdateWrapper<SysKnowledge>().eq("id", id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/cancelCollect")
	public Result cancelCollect(Long id, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysKnowledge knowledge = null;
			SysKnowledge knowledeParams = new SysKnowledge();
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			Map<String, Object> map = new HashMap<String, Object>();
			
			map.put("user_id", currentUser.getId());
			map.put("knowledge_id", id);
			
			sysFavorKnowledgeMapper.deleteByMap(map);
			knowledeParams.setId(id);
			knowledge = sysKnowledgeMapper.selectOne(new QueryWrapper<SysKnowledge>(knowledeParams));
			knowledge.setFavorNum(knowledge.getFavorNum() + 1);
			sysKnowledgeMapper.update(knowledge, new UpdateWrapper<SysKnowledge>().eq("id", id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/delete")
	public Result delete(Long id, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
