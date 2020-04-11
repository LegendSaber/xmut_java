package com.xmut.xmut_java.sys.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmut.xmut_java.common.BaseController;
import com.xmut.xmut_java.common.Result;
import com.xmut.xmut_java.sys.entity.SysCommentRelation;
import com.xmut.xmut_java.sys.entity.SysFavorComment;
import com.xmut.xmut_java.sys.entity.SysFavorKnowledge;
import com.xmut.xmut_java.sys.entity.SysKnowledge;
import com.xmut.xmut_java.sys.entity.SysKnowledgeComment;
import com.xmut.xmut_java.sys.entity.SysKnowledgePicture;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.entity.SysUserKnowledge;
import com.xmut.xmut_java.sys.mapper.SysCommentMapper;
import com.xmut.xmut_java.sys.mapper.SysCommentRelationMapper;
import com.xmut.xmut_java.sys.mapper.SysFavorCommentMapper;
import com.xmut.xmut_java.sys.mapper.SysFavorKnowledgeMapper;
import com.xmut.xmut_java.sys.mapper.SysKnowledgeCommentMapper;
import com.xmut.xmut_java.sys.mapper.SysKnowledgeMapper;
import com.xmut.xmut_java.sys.mapper.SysKnowledgePictureMapper;
import com.xmut.xmut_java.sys.mapper.SysPictureMapper;
import com.xmut.xmut_java.sys.mapper.SysSonCommentMapper;
import com.xmut.xmut_java.sys.mapper.SysUserKnowledgeMapper;
import com.xmut.xmut_java.sys.mapper.SysUserMapper;
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
	private SysKnowledgeCommentMapper sysKnowledgeCommentMapper;
	
	@Autowired
	private SysCommentMapper sysCommentMapper;
	
	@Autowired
	private SysSonCommentMapper sysSonCommentMapper;
	
	@Autowired
	private SysCommentRelationMapper sysCommentRelationMapper;
	
	@Autowired
	private SysFavorCommentMapper sysFavorCommentMapper;
	
	@Autowired
	private SysKnowledgePictureMapper sysKnowledgePictureMapper;
	
	@Autowired
	private SysPictureMapper sysPictureMapper;
	
	@Autowired
	private SysUserMapper sysUserMapper;
	
	@RequestMapping("/insert")
	public Result insert(String title, String content, String category, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser) request.getSession().getAttribute("user");
			Long userId = currentUser.getId();
			SysUser userParams = new SysUser();
			userParams.setId(userId);
			SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(userParams));
			
			if (user.getScore() < 5) {
				result.fail("积分不足，提交失败!");
			} else {
				user.setScore(user.getScore() - 5);
				sysUserMapper.update(user, new UpdateWrapper<SysUser>().eq("id", userId));
				Date currentDate = new Date();
				
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getAll")
	public Result getAll(int currentPage, int pageSize, String category, int flag, HttpServletRequest request) {
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
			wrapper.eq("category", category);
			
			IPage p = sysKnowledgeMapper.selectPage(page, wrapper);
			if (p != null) {
				List<SysKnowledge> last = new ArrayList<SysKnowledge>();
				List<SysKnowledge> knowledges = p.getRecords();
				for (SysKnowledge knowledge2 : knowledges) {
					SysUser userParams = new SysUser();
					userParams.setUsername(knowledge2.getAuthor());
					SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(userParams));
					Long avatarId = user.getPicNo();
					if (avatarId != -1) {
						byte[] data = sysFileService.getAvatar(avatarId);
						String img = "data:image/jpeg;base64," + Base64.encodeBase64String(data);
						knowledge2.setImg(img);
					}
					last.add(knowledge2);
				}
				p.setRecords(last);
				result.setData(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getSearchKnowledge")
	public Result getSearchKnowledge(String value) {
		Result result = new Result();
		
		try {
			List<SysKnowledge> list = new ArrayList<SysKnowledge>();
			List<SysKnowledge> knowledgeList = sysKnowledgeMapper.selectList(new QueryWrapper<SysKnowledge>().like("title", value));
		
			if (knowledgeList != null && !knowledgeList.isEmpty()) {
				for (SysKnowledge knowledge : knowledgeList) {
					SysUser userParams = new SysUser();
					userParams.setUsername(knowledge.getAuthor());
					SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(userParams));
					Long avatarId = user.getPicNo();
					if (avatarId != -1) {
						byte[] data = sysFileService.getAvatar(avatarId);
						String img = "data:image/jpeg;base64," + Base64.encodeBase64String(data);
						knowledge.setImg(img);
					}
					list.add(knowledge);
				}
			}		
			
			result.setData(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getFavorKnowledge")
	public Result getFavorKnowledge(int currentPage, int pageSize, String query, HttpServletRequest request) {
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
			if (!query.equals("")) wrapper.like("title", query);
			wrapper.orderByDesc("modify_time");
			
			result.setData(sysKnowledgeMapper.selectPage(page, wrapper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getMyKnowledge")
	public Result getMyKnowledge(int currentPage, int pageSize, String query, HttpServletRequest request ) {
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
			if (!query.equals("")) wrapper.like("title", query);
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
		String token = request.getParameter("token");
		String rightToken = (String)request.getSession().getAttribute("token");
		
		if (rightToken.equals(token)) {
			String[] canUpload = {".jpg", ".png", ".jpeg"};
			SysKnowledge knowledge = (SysKnowledge)request.getSession().getAttribute("knowledge");
			List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
			int len = files.size();
		
			for (int i = 0; i < len; i++) {		
				MultipartFile file = files.get(i);
				
				if (file != null){
					try {
						String fileName = file.getOriginalFilename();
						int index = fileName.lastIndexOf('.');
						String suffix = fileName.substring(index);
						boolean isOk = false;
						
						for (String temp : canUpload) {
							if (temp.equalsIgnoreCase(suffix)) {
								isOk = true;
							}
						}
						
						if (isOk) sysFileService.savePicture(fileName, file.getBytes(), knowledge.getId());		
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			request.getSession().removeAttribute("knowledge");
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
		
			SysUser userParams = new SysUser();
			userParams.setUsername(knowledge.getAuthor());
			SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(userParams));
			user.setScore(user.getScore() + 3);
			sysUserMapper.update(user, new UpdateWrapper<SysUser>().eq("id", user.getId()));
			result.setMessage("收藏成功!");
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
			knowledge.setFavorNum(knowledge.getFavorNum() - 1);
			sysKnowledgeMapper.update(knowledge, new UpdateWrapper<SysKnowledge>().eq("id", id));
		
			SysUser userParams = new SysUser();
			userParams.setUsername(knowledge.getAuthor());
			SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(userParams));
			user.setScore(user.getScore() - 3);
			sysUserMapper.update(user, new UpdateWrapper<SysUser>().eq("id", user.getId()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/delete")
	public Result delete(Long id, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser) request.getSession().getAttribute("user");
			Long userId = currentUser.getId();
			Map<String, Object> columnMap = new HashMap<String, Object>();
			Map<String, Object> knColumnMap = new HashMap<String, Object>();
			List<Long> idList = new ArrayList<Long>();
			SysFavorKnowledge favorParams = new SysFavorKnowledge();
			
			favorParams.setKnowledgeId(id);
			List<SysFavorKnowledge> favorList = sysFavorKnowledgeMapper.selectList(new QueryWrapper<SysFavorKnowledge>(favorParams));
			
			if (favorList != null && !favorList.isEmpty()) {
				for (SysFavorKnowledge favor : favorList) {
					idList.add(favor.getId());
				}
				sysFavorKnowledgeMapper.deleteBatchIds(idList);
			}
			
			columnMap.put("user_id", userId);
			columnMap.put("knowledge_id", id);
			
			sysUserKnowledgeMapper.deleteByMap(columnMap);
			
			knColumnMap.put("id", id);
			
			sysKnowledgeMapper.deleteByMap(knColumnMap);
			
			List<Long> commentIdList = new ArrayList<Long>();
			List<Long> sonCommentIdList = new ArrayList<Long>();
			SysKnowledgeComment commentParams = new SysKnowledgeComment();
			
			commentParams.setKnowledgeId(id);
			List<SysKnowledgeComment> comments = sysKnowledgeCommentMapper.selectList(new QueryWrapper<SysKnowledgeComment>(commentParams));
			
			if (comments != null && !comments.isEmpty()) {
				for (SysKnowledgeComment comment : comments) {
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
				sysKnowledgeCommentMapper.deleteBatchIds(commentIdList);
			}
			
			List<Long> pictureIdList = new ArrayList<Long>();
			SysKnowledgePicture knowledgeParams = new SysKnowledgePicture();
			
			knowledgeParams.setKnowledgeId(id);
			List<SysKnowledgePicture> pictureList = sysKnowledgePictureMapper.selectList(new QueryWrapper<SysKnowledgePicture>(knowledgeParams));
			
			if (pictureList != null && !pictureList.isEmpty()) {
				for (SysKnowledgePicture picture : pictureList) {
					pictureIdList.add(picture.getId());
					Long pictureId = picture.getPictureId();
					
					Map<String, Object> pictureMap = new HashMap<String, Object>();
					
					pictureMap.put("id", pictureId);
					
					sysPictureMapper.deleteByMap(pictureMap);
				}
				sysKnowledgePictureMapper.deleteBatchIds(pictureIdList);
			}
			result.success("删除成功，点击确定前往查看!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/updata")
	public Result updata(Long id, String title, String content, String category, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysKnowledge params = new SysKnowledge();
			params.setId(id);
			SysKnowledge knowledge = sysKnowledgeMapper.selectOne(new QueryWrapper<SysKnowledge>(params));
			
			knowledge.setTitle(title);
			knowledge.setCategory(category);
			knowledge.setContent(content);
			knowledge.setModifyTime(new Date());
			
			sysKnowledgeMapper.update(knowledge, new UpdateWrapper<SysKnowledge>().eq("id", id));
			request.getSession().setAttribute("knowledge", knowledge);
			result.success("修改文章成功，点击确定前往查看!");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getForAdmin")
	public Result getForAdmin(int currentPage, int pageSize, String query, String value) {
		Result result = new Result();
		
		try {
			Page<SysKnowledge> page = new Page<SysKnowledge>(currentPage, pageSize);
			SysKnowledge params = new SysKnowledge();
			QueryWrapper<SysKnowledge> wrapper = new QueryWrapper<SysKnowledge>(params);
		
			if (!query.equals("")) {
				if (value.equals("标题")) wrapper.like("title", query);
				else wrapper.like("author", query);
			}
			wrapper.orderByDesc("modify_time");
			result.setData(sysKnowledgeMapper.selectPage(page, wrapper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getKnowledgeById")
	public Result getKnowledgeById(Long id) {
		Result result = new Result();
		
		try {
			SysKnowledge params = new SysKnowledge();
			
			params.setId(id);
			SysKnowledge knowledge = sysKnowledgeMapper.selectOne(new QueryWrapper<SysKnowledge>(params));
			
			if (knowledge != null) {
				result.setData(knowledge);
			}else {
				result.fail("这篇文章已经被删除!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/deleteByAdmin")
	public Result deleteByAdmin(Long id, String author) {
		Result result = new Result();
		
		try {
			SysUser userParams = new SysUser();
			userParams.setUsername(author);
			SysUser currentUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>(userParams));
			Long userId = currentUser.getId();
			Map<String, Object> columnMap = new HashMap<String, Object>();
			Map<String, Object> knColumnMap = new HashMap<String, Object>();
			List<Long> idList = new ArrayList<Long>();
			SysFavorKnowledge favorParams = new SysFavorKnowledge();
			
			favorParams.setKnowledgeId(id);
			List<SysFavorKnowledge> favorList = sysFavorKnowledgeMapper.selectList(new QueryWrapper<SysFavorKnowledge>(favorParams));
			
			if (favorList != null && !favorList.isEmpty()) {
				for (SysFavorKnowledge favor : favorList) {
					idList.add(favor.getId());
				}
				sysFavorKnowledgeMapper.deleteBatchIds(idList);
			}
			
			columnMap.put("user_id", userId);
			columnMap.put("knowledge_id", id);
			
			sysUserKnowledgeMapper.deleteByMap(columnMap);
			
			knColumnMap.put("id", id);
			
			sysKnowledgeMapper.deleteByMap(knColumnMap);
			
			List<Long> commentIdList = new ArrayList<Long>();
			List<Long> sonCommentIdList = new ArrayList<Long>();
			SysKnowledgeComment commentParams = new SysKnowledgeComment();
			
			commentParams.setKnowledgeId(id);
			List<SysKnowledgeComment> comments = sysKnowledgeCommentMapper.selectList(new QueryWrapper<SysKnowledgeComment>(commentParams));
			
			if (comments != null && !comments.isEmpty()) {
				for (SysKnowledgeComment comment : comments) {
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
				sysKnowledgeCommentMapper.deleteBatchIds(commentIdList);
			}
			
			List<Long> pictureIdList = new ArrayList<Long>();
			SysKnowledgePicture knowledgeParams = new SysKnowledgePicture();
			
			knowledgeParams.setKnowledgeId(id);
			List<SysKnowledgePicture> pictureList = sysKnowledgePictureMapper.selectList(new QueryWrapper<SysKnowledgePicture>(knowledgeParams));
			
			if (pictureList != null && !pictureList.isEmpty()) {
				for (SysKnowledgePicture picture : pictureList) {
					pictureIdList.add(picture.getId());
					Long pictureId = picture.getPictureId();
					
					Map<String, Object> pictureMap = new HashMap<String, Object>();
					
					pictureMap.put("id", pictureId);
					
					sysPictureMapper.deleteByMap(pictureMap);
				}
				sysKnowledgePictureMapper.deleteBatchIds(pictureIdList);
			}
			result.success("删除成功，点击确定前往查看!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
