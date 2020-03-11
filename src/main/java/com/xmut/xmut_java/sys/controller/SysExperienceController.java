package com.xmut.xmut_java.sys.controller;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmut.xmut_java.common.BaseController;
import com.xmut.xmut_java.common.Result;
import com.xmut.xmut_java.sys.entity.SysCommentRelation;
import com.xmut.xmut_java.sys.entity.SysExperience;
import com.xmut.xmut_java.sys.entity.SysExperienceComment;
import com.xmut.xmut_java.sys.entity.SysFavorComment;
import com.xmut.xmut_java.sys.entity.SysFavorExperience;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.entity.SysUserExperience;
import com.xmut.xmut_java.sys.mapper.SysCommentMapper;
import com.xmut.xmut_java.sys.mapper.SysCommentRelationMapper;
import com.xmut.xmut_java.sys.mapper.SysExperienceCommentMapper;
import com.xmut.xmut_java.sys.mapper.SysExperienceMapper;
import com.xmut.xmut_java.sys.mapper.SysFavorCommentMapper;
import com.xmut.xmut_java.sys.mapper.SysFavorExperienceMapper;
import com.xmut.xmut_java.sys.mapper.SysSonCommentMapper;
import com.xmut.xmut_java.sys.mapper.SysUserExperienceMapper;
import com.xmut.xmut_java.sys.mapper.SysUserMapper;
import com.xmut.xmut_java.sys.service.SysFileService;

@RestController
@RequestMapping("/sysExperience")
public class SysExperienceController extends BaseController{
	@Autowired
	private SysExperienceMapper sysExperienceMapper;
	
	@Autowired
	private SysUserExperienceMapper sysUserExperienceMapper;
	
	@Autowired
	private SysFavorExperienceMapper sysFavorExperienceMapper;
	
	@Autowired
	private SysExperienceCommentMapper sysExperienceCommentMapper;
	
	@Autowired
	private SysCommentRelationMapper sysCommentRelationMapper;
	
	@Autowired
	private SysSonCommentMapper sysSonCommentMapper;
	
	@Autowired
	private SysCommentMapper sysCommentMapper;
	
	@Autowired
	private SysFavorCommentMapper sysFavorCommentMapper;
	
	@Autowired
	private SysUserMapper sysUserMapper;
	
	@Autowired
	private SysFileService sysFileService;
	
	@RequestMapping("/getAll")
	public Result getAll(int currentPage, int pageSize, int flag) {
		Result result = new Result();
		try {
			Page<SysExperience> page = new Page<SysExperience>(currentPage, pageSize);
			SysExperience parms = new SysExperience();
			QueryWrapper<SysExperience> warpper = new QueryWrapper<SysExperience>(parms);
			
			if (flag == 1) warpper.orderByDesc("modify_time");
			else if (flag == 2) warpper.orderByDesc("favor_num"); 
			IPage p = sysExperienceMapper.selectPage(page, warpper);
			if (p != null) {
				List<SysExperience> last = new ArrayList<SysExperience>();
				List<SysExperience> experiences = p.getRecords();
				for (SysExperience experiences2 : experiences) {
					SysUser userParams = new SysUser();
					userParams.setUsername(experiences2.getAuthor());
					SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(userParams));
					Long avatarId = user.getPicNo();
					if (avatarId != -1) {
						byte[] data = sysFileService.getAvatar(avatarId);
						String img = "data:image/jpeg;base64," + Base64.encodeBase64String(data);
						experiences2.setImg(img);
					}
					last.add(experiences2);
				}
				p.setRecords(last);
				result.setData(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getMyExperience")
	public Result getMyExperience(int currentPage, int pageSize, String query, HttpServletRequest request) {
		Result result = new Result();
		try {
			Page<SysExperience> page = new Page<SysExperience>(currentPage, pageSize);
			SysExperience parms = new SysExperience();
			QueryWrapper<SysExperience> warpper = new QueryWrapper<SysExperience>(parms);
			
			Set<Long> experienceidSet = new HashSet<Long>();
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			SysUserExperience queryExperience = new SysUserExperience();
			queryExperience.setUserId(currentUser.getId());
			List<SysUserExperience> experienceList = sysUserExperienceMapper.selectList(new QueryWrapper<SysUserExperience>(queryExperience));
			
			if (experienceList != null && !experienceList.isEmpty()) {
				for (SysUserExperience userandexperience : experienceList) {
					experienceidSet.add(userandexperience.getExperienceId());
				}
			}
			warpper.in("id", experienceidSet);
			warpper.orderByDesc("modify_time");
			if (!query.equals("")) warpper.like("title", query);
			result.setData(sysExperienceMapper.selectPage(page, warpper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getFavorExperience")
	public Result getFavorExperience(int currentPage, int pageSize, String query, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			Page<SysExperience> page = new Page<SysExperience>(currentPage, pageSize);
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			SysExperience parms = new SysExperience();
			QueryWrapper<SysExperience> warpper = new QueryWrapper<SysExperience>(parms);
			Set<Long> experienceidSet = new HashSet<Long>();
			SysFavorExperience queryExperience = new SysFavorExperience();
			
			queryExperience.setUserId(currentUser.getId());
			List<SysFavorExperience> experienceList = sysFavorExperienceMapper.selectList(new QueryWrapper<SysFavorExperience>(queryExperience));
			
			
			if (experienceList != null && !experienceList.isEmpty()) {
				for (SysFavorExperience userandexperience : experienceList) {
					experienceidSet.add(userandexperience.getExperienceId());
				}
			}
			
			warpper.in("id", experienceidSet);
			warpper.orderByDesc("modify_time");
			if (!query.equals("")) warpper.like("title", query);
			result.setData(sysExperienceMapper.selectPage(page, warpper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("insert")
	public Result insert(String title, String content, HttpServletRequest request) {
		Result result = new Result();
		Date currentDate = new Date();
		SysUser currentUser = (SysUser) request.getSession().getAttribute("user");
		Long userId = currentUser.getId();
		SysExperience experienceParams = new SysExperience();
		SysUserExperience params = new SysUserExperience();
		
		try {
			SysUser userParams = new SysUser();
			userParams.setId(userId);
			SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(userParams));
			if (user.getScore() < 3) {
				result.fail("积分不足，提交失败");
				return result;
			}
			else {
				user.setScore(user.getScore() - 3);
				sysUserMapper.update(user, new UpdateWrapper<SysUser>().eq("id", userId));
				experienceParams.setAuthor(currentUser.getUsername());
				experienceParams.setTitle(title);
				experienceParams.setContent(content);
				experienceParams.setFavorNum((long)0);
				experienceParams.setCreateTime(currentDate);
				experienceParams.setModifyTime(currentDate);
				sysExperienceMapper.insert(experienceParams);
					
				params.setUserId(userId);
				params.setExperienceId(experienceParams.getId());
				sysUserExperienceMapper.insert(params);
				result.setMessage("提交成功,请前往查看");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/deleteExperience")
	public Result deleteExperience(Long id, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			Long userId = currentUser.getId();
			Map<String, Object> columnMap = new HashMap<String, Object>();
			Map<String, Object> exColumnMap = new HashMap<String, Object>();
			SysFavorExperience params = new SysFavorExperience();
			List<Long> idList = new ArrayList<Long>();
			params.setExperienceId(id);
			List<SysFavorExperience> favorList = sysFavorExperienceMapper.selectList(new QueryWrapper<SysFavorExperience>(params));
			
			if (favorList != null && !favorList.isEmpty()) {
				for (SysFavorExperience favor : favorList) {
					idList.add(favor.getId());
				}
				sysFavorExperienceMapper.deleteBatchIds(idList);
			}
			
			columnMap.put("user_id", userId);
			columnMap.put("experience_id", id);
			
			sysUserExperienceMapper.deleteByMap(columnMap);
			
			exColumnMap.put("id", id);
			
			sysExperienceMapper.deleteByMap(exColumnMap);
			
			List<Long> commentIdList = new ArrayList<Long>();
			List<Long> sonCommentIdList = new ArrayList<Long>();
			SysExperienceComment commentParams = new SysExperienceComment();
			commentParams.setExperienceId(id);
			List<SysExperienceComment> comments = sysExperienceCommentMapper.selectList(new QueryWrapper<SysExperienceComment>(commentParams));
			
			if (comments != null && !comments.isEmpty()) {
				for (SysExperienceComment comment : comments) {
					Map<String, Object> commentMap = new HashMap<String, Object>();
					commentMap.put("id", comment.getCommentId());
					sysCommentMapper.deleteByMap(commentMap);
					commentIdList.add(comment.getId());
					SysCommentRelation relationParams = new SysCommentRelation();
					relationParams.setFatherId(comment.getCommentId());
					List<SysCommentRelation> commentsRelation = sysCommentRelationMapper.selectList(new QueryWrapper<SysCommentRelation>(relationParams));
					
					if (commentsRelation != null && !commentsRelation.isEmpty()) {
						for (SysCommentRelation commentRelation : commentsRelation) {
							sonCommentIdList.add(commentRelation.getId());
							Map<String, Object> sonMap = new HashMap<String, Object>();
							sonMap.put("id", commentRelation.getSonId());
							sysSonCommentMapper.deleteByMap(sonMap);
						}
						sysCommentRelationMapper.deleteBatchIds(sonCommentIdList);
					}
					
					SysFavorComment favorCommentParams = new SysFavorComment();
					favorCommentParams.setCommentId(comment.getCommentId());
					List<SysFavorComment> favorComments = sysFavorCommentMapper.selectList(new QueryWrapper<SysFavorComment>(favorCommentParams));
					List<Long> favorCommentList = new ArrayList<Long>();
					
					if (favorComments != null && !favorComments.isEmpty()) {
						for (SysFavorComment favorComment : favorComments) {
							favorCommentList.add(favorComment.getId());
						}
						sysFavorCommentMapper.deleteBatchIds(favorCommentList);
					}
				}
				sysExperienceCommentMapper.deleteBatchIds(commentIdList);
			}
			result.setMessage("删除成功，点击确定前往查看!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/modifyExperience")
	public Result modifyExperience(String title, String content, Long id, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysExperience params = new SysExperience();
			
			params.setId(id);
			SysExperience oldExperience = sysExperienceMapper.selectOne(new QueryWrapper<SysExperience>(params));
			
			if (oldExperience != null) {
				oldExperience.setTitle(title);
				oldExperience.setContent(content);
				oldExperience.setModifyTime(new Date());
				sysExperienceMapper.update(oldExperience, new UpdateWrapper<SysExperience>().eq("id", id));
				result.setMessage("修改成功，点击确定前往查看!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/isMyExperience")
	public Result isMyExperience(Long id, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			Long userId = currentUser.getId();
			SysUserExperience params = new SysUserExperience();
			
			params.setUserId(userId);
			params.setExperienceId(id);
			
			SysUserExperience isMy = sysUserExperienceMapper.selectOne(new QueryWrapper<SysUserExperience>(params));
			
			if (isMy != null) {
				result.success("是我的");
			} else {
				result.fail("不是我的");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getForAdmin")
	public Result getForAdmin(int currentPage, int pageSize, String query, String value) {
		Result result = new Result();
		
		try {
			Page<SysExperience> page = new Page<SysExperience>(currentPage, pageSize);
			SysExperience params = new SysExperience();
			QueryWrapper<SysExperience> wrapper = new QueryWrapper<SysExperience>(params);
		
			if (!query.equals("")) {
				if (value.equals("标题")) wrapper.like("title", query);
				else wrapper.like("author", query);
			}
			wrapper.orderByDesc("modify_time");
			result.setData(sysExperienceMapper.selectPage(page, wrapper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/deleteExperienceByAdmin")
	public Result deleteExperienceByAdmin(Long id, String author) {
		Result result = new Result();
		
		try {
			SysUser userParams = new SysUser();
			userParams.setUsername(author);
			SysUser deleteUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>(userParams));
			Long userId = deleteUser.getId();
			
			Map<String, Object> columnMap = new HashMap<String, Object>();
			Map<String, Object> exColumnMap = new HashMap<String, Object>();
			SysFavorExperience params = new SysFavorExperience();
			List<Long> idList = new ArrayList<Long>();
			params.setExperienceId(id);
			List<SysFavorExperience> favorList = sysFavorExperienceMapper.selectList(new QueryWrapper<SysFavorExperience>(params));
			
			if (favorList != null && !favorList.isEmpty()) {
				for (SysFavorExperience favor : favorList) {
					idList.add(favor.getId());
				}
				sysFavorExperienceMapper.deleteBatchIds(idList);
			}
			
			columnMap.put("user_id", userId);
			columnMap.put("experience_id", id);
			
			sysUserExperienceMapper.deleteByMap(columnMap);
			
			exColumnMap.put("id", id);
			
			sysExperienceMapper.deleteByMap(exColumnMap);
			
			List<Long> commentIdList = new ArrayList<Long>();
			List<Long> sonCommentIdList = new ArrayList<Long>();
			SysExperienceComment commentParams = new SysExperienceComment();
			commentParams.setExperienceId(id);
			List<SysExperienceComment> comments = sysExperienceCommentMapper.selectList(new QueryWrapper<SysExperienceComment>(commentParams));
			
			if (comments != null && !comments.isEmpty()) {
				for (SysExperienceComment comment : comments) {
					Map<String, Object> commentMap = new HashMap<String, Object>();
					commentMap.put("id", comment.getCommentId());
					sysCommentMapper.deleteByMap(commentMap);
					commentIdList.add(comment.getId());
					SysCommentRelation relationParams = new SysCommentRelation();
					relationParams.setFatherId(comment.getCommentId());
					List<SysCommentRelation> commentsRelation = sysCommentRelationMapper.selectList(new QueryWrapper<SysCommentRelation>(relationParams));
					
					if (commentsRelation != null && !commentsRelation.isEmpty()) {
						for (SysCommentRelation commentRelation : commentsRelation) {
							sonCommentIdList.add(commentRelation.getId());
							Map<String, Object> sonMap = new HashMap<String, Object>();
							sonMap.put("id", commentRelation.getSonId());
							sysSonCommentMapper.deleteByMap(sonMap);
						}
						sysCommentRelationMapper.deleteBatchIds(sonCommentIdList);
					}
					
					SysFavorComment favorCommentParams = new SysFavorComment();
					favorCommentParams.setCommentId(comment.getCommentId());
					List<SysFavorComment> favorComments = sysFavorCommentMapper.selectList(new QueryWrapper<SysFavorComment>(favorCommentParams));
					List<Long> favorCommentList = new ArrayList<Long>();
					
					if (favorComments != null && !favorComments.isEmpty()) {
						for (SysFavorComment favorComment : favorComments) {
							favorCommentList.add(favorComment.getId());
						}
						sysFavorCommentMapper.deleteBatchIds(favorCommentList);
					}
				}
				sysExperienceCommentMapper.deleteBatchIds(commentIdList);
			}
			result.setMessage("删除成功，点击确定前往查看!");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getExperienceById")
	public Result getExperienceById(Long id) {
		Result result = new Result();
		
		try {
			SysExperience params = new SysExperience();
			
			params.setId(id);
			
			SysExperience experience = sysExperienceMapper.selectOne(new QueryWrapper<SysExperience>(params));
			
			if (experience != null) result.setData(experience);
			else result.fail("这篇文章已经被删除!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
