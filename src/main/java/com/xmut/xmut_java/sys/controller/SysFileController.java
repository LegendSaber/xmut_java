package com.xmut.xmut_java.sys.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.xmut.xmut_java.sys.entity.SysFavorFile;
import com.xmut.xmut_java.sys.entity.SysFile;
import com.xmut.xmut_java.sys.entity.SysKnowledgePicture;
import com.xmut.xmut_java.sys.entity.SysPicture;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.entity.SysUserFile;
import com.xmut.xmut_java.sys.mapper.SysFavorFileMapper;
import com.xmut.xmut_java.sys.mapper.SysFileMapper;
import com.xmut.xmut_java.sys.mapper.SysKnowledgePictureMapper;
import com.xmut.xmut_java.sys.mapper.SysPictureMapper;
import com.xmut.xmut_java.sys.mapper.SysUserFileMapper;
import com.xmut.xmut_java.sys.mapper.SysUserMapper;
import com.xmut.xmut_java.sys.service.SysFileService;

@RestController
@RequestMapping("/sysFile")
public class SysFileController extends BaseController{
	@Autowired
	private SysFileMapper sysFileMapper;
	
	@Autowired
	private SysFileService sysFileService;
	
	@Autowired
	private SysUserFileMapper sysUserFileMapper;
	
	@Autowired
	private SysFavorFileMapper sysFavorFileMapper;
	
	@Autowired
	private SysKnowledgePictureMapper sysKnowledgePictureMapper;
	
	@Autowired
	private SysPictureMapper sysPictureMapper;
	
	@Autowired
	private SysUserMapper sysUserMapper;
	
	@RequestMapping("/upload")
	public Result upload(HttpServletRequest request) {
		Result result = new Result();
		SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
		List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
		Long userId = currentUser.getId();
		SysUser userParams = new SysUser();
		userParams.setId(userId);
		SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(userParams));
		
		int len = files.size();
		try {
			for (int i = 0; i < len; i++) {
				MultipartFile file = files.get(i);
				String fileName = file.getOriginalFilename();
				int index = fileName.lastIndexOf('.');
				String suffix = fileName.substring(index);
				
				if (!suffix.equals(".zip")) {
					result.fail("只能上传.zip文件");
				}
				else if (user.getScore() < 5) {
					result.fail("积分不足，文件上传失败!");
				} else {
					user.setScore(user.getScore() - 5);
					sysUserMapper.update(user, new UpdateWrapper<SysUser>().eq("id", user.getId()));
					sysFileService.saveFile(fileName, file.getBytes(), currentUser);	
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getAll")
	public Result getAll(int currentPage, int pageSize, int flag) {
		Result result = new Result();
		try {
			Page<SysFile> page = new Page<SysFile>(currentPage, pageSize);
			SysFile params = new SysFile();
			QueryWrapper<SysFile> wrapper = new QueryWrapper<SysFile>(params);
			
			if (flag == 1) wrapper.orderByDesc("create_time");
			else wrapper.orderByDesc("favor_num");
			IPage p = sysFileMapper.selectPage(page, wrapper);
			if (p != null) {
				List<SysFile> last = new ArrayList<SysFile>();
				List<SysFile> files = p.getRecords();
				for (SysFile file2 : files) {
					SysUser userParams = new SysUser();
					userParams.setUsername(file2.getAuthor());
					SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(userParams));
					Long avatarId = user.getPicNo();
					if (avatarId != -1) {
						byte[] data = sysFileService.getAvatar(avatarId);
						String img = "data:image/jpeg;base64," + Base64.encodeBase64String(data);
						file2.setImg(img);
					}
					last.add(file2);
				}
				p.setRecords(last);
				result.setData(p);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getSearchFile")
	public Result getSearchFile(String value) {
		Result result = new Result();
		
		try {
			List<SysFile> list = new ArrayList<SysFile>();
			List<SysFile> fileList = sysFileMapper.selectList(new QueryWrapper<SysFile>().like("file_name", value));
		
			if (fileList != null && !fileList.isEmpty()) {
				for (SysFile file : fileList) {
					SysUser userParams = new SysUser();
					userParams.setUsername(file.getAuthor());
					SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(userParams));
					Long avatarId = user.getPicNo();
					if (avatarId != -1) {
						byte[] data = sysFileService.getAvatar(avatarId);
						String img = "data:image/jpeg;base64," + Base64.encodeBase64String(data);
						file.setImg(img);
					}
					list.add(file);
				}
			}		
			
			result.setData(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getMyCollect")
	public Result getMyCollect(int currentPage, int pageSize, String query, HttpServletRequest request) {
		Result result = new Result();
		try {
			Page<SysFile> page = new Page<SysFile>(currentPage, pageSize);
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			SysFile params = new SysFile();
			QueryWrapper<SysFile> wrapper = new QueryWrapper<SysFile>(params);
			
			Set<Long> fileSet = new HashSet<Long>();
			SysFavorFile queryParams = new SysFavorFile();
			queryParams.setUserId(currentUser.getId());
			
			List<SysFavorFile> fileList = sysFavorFileMapper.selectList(new QueryWrapper<SysFavorFile>(queryParams));
			
			if (fileList != null && !fileList.isEmpty()) {
				for (SysFavorFile file : fileList) {
					fileSet.add(file.getFileId());
				}
			}
			
			wrapper.in("id", fileSet);
			wrapper.orderByDesc("create_time");
			if (!query.equals("")) wrapper.like("file_name", query);
			result.setData(sysFileMapper.selectPage(page, wrapper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getMyFile")
	public Result getMyFile(int currentPage, int pageSize, String query, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			Page<SysFile> page = new Page<SysFile>(currentPage, pageSize);
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			SysFile params = new SysFile();
			QueryWrapper<SysFile> wrapper = new QueryWrapper<SysFile>(params);
			Set<Long> fileSet = new HashSet<Long>();
			SysUserFile queryFile = new SysUserFile();
			
			queryFile.setUserId(currentUser.getId());
			List<SysUserFile> fileList = sysUserFileMapper.selectList(new QueryWrapper<SysUserFile>(queryFile));
		
			if (fileList != null && !fileList.isEmpty()) {
				for (SysUserFile myFile : fileList) {
					fileSet.add(myFile.getFileId());
				}
			}
			
			wrapper.in("id", fileSet);
			wrapper.orderByDesc("create_time");
			if (!query.equals("")) wrapper.like("file_name", query);
			result.setData(sysFileMapper.selectPage(page, wrapper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/collect")
	public Result collect(Long id, HttpServletRequest request) {
		Result result = new Result();
		SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
		SysFavorFile params = new SysFavorFile();
		
		params.setUserId(currentUser.getId());
		params.setFileId(id);
		SysFavorFile hasFavor = sysFavorFileMapper.selectOne(new QueryWrapper<SysFavorFile>(params));
		if (hasFavor != null) {
			result.fail("您已收藏!");
		} else {
			SysFile fileParams = new SysFile();
			fileParams.setId(id);
			SysFile file = sysFileMapper.selectOne(new QueryWrapper<SysFile>(fileParams));
			file.setFavorNum(file.getFavorNum() + 1);
			sysFileMapper.update(file, new UpdateWrapper<SysFile>().eq("id", id));
			sysFavorFileMapper.insert(params);
			
			SysUser userParams = new SysUser();
			userParams.setUsername(file.getAuthor());
			SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(userParams));
			user.setScore(user.getScore() + 3);
			sysUserMapper.update(user, new UpdateWrapper<SysUser>().eq("id", user.getId()));
			
			result.success("添加收藏成功!");
		}
		
		return result;
	}
	
	@RequestMapping("/cancelCollect")
	public Result cancelCollect(Long id, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			Map<String, Object> columnMap = new HashMap<String, Object>();
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			
			columnMap.put("user_id", currentUser.getId());
			columnMap.put("file_id", id);
			sysFavorFileMapper.deleteByMap(columnMap);
			
			SysFile fileParams = new SysFile();
			fileParams.setId(id);
			SysFile file = sysFileMapper.selectOne(new QueryWrapper<SysFile>(fileParams));
			file.setFavorNum(file.getFavorNum() - 1);
			sysFileMapper.update(file, new UpdateWrapper<SysFile>().eq("id", id));
			
			SysUser userParams = new SysUser();
			userParams.setUsername(file.getAuthor());
			SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(userParams));
			user.setScore(user.getScore() - 3);
			sysUserMapper.update(user, new UpdateWrapper<SysUser>().eq("id", user.getId()));
			result.success("已成功移除收藏夹!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/delete")
	public Result delete(Long id, HttpServletRequest request) {
		Result result = new Result();
		try {
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			Map<String, Object> columnMap = new HashMap<String, Object>();
			Map<String, Object> columnMapFile = new HashMap<String, Object>();
			List<Long> idList = new ArrayList<Long>();
			SysFavorFile params = new SysFavorFile();
			params.setFileId(id);
			List<SysFavorFile> favorList = sysFavorFileMapper.selectList(new QueryWrapper<SysFavorFile>());
			
			if (favorList != null && !favorList.isEmpty()) {
				for (SysFavorFile favor : favorList) {
					idList.add(favor.getId());
				}
				sysFavorFileMapper.deleteBatchIds(idList);
			}	
			
			columnMap.put("user_id", currentUser.getId());
			columnMap.put("file_id", id);
			sysUserFileMapper.deleteByMap(columnMap);		
			
			columnMapFile.put("id", id);
			sysFileMapper.deleteByMap(columnMapFile);
			result.success("删除文件成功，点击确定前往查看!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/download")
	public Result download(Long id, HttpServletResponse response) {
		Result result = new Result();
		
		try {
			SysFile file = sysFileService.getFile(id);
			if (file != null) {
				response.reset();
				// 配置文件下载
                response.setHeader("content-type", "application/octet-stream");
                response.setContentType("application/octet-stream");
                // 下载文件能正常显示中文
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getFileName(), "UTF-8"));
                
				OutputStream out = response.getOutputStream();
                out.write(file.getFileContent());
                out.flush();
                out.close();
				
				result.success("文件下载完成!");
			}else {
				result.fail("文件错误!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@RequestMapping("/loadPicture")
	public Result loadPicture(Long id, HttpServletResponse response) {
		Result result = new Result();
		
		try {
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			SysKnowledgePicture pictureParams = new SysKnowledgePicture();
			
			pictureParams.setKnowledgeId(id);
			List<SysKnowledgePicture> pictureList = sysKnowledgePictureMapper.selectList(new QueryWrapper<SysKnowledgePicture>(pictureParams));
			
			if (pictureList != null && !pictureList.isEmpty()) {
				for (SysKnowledgePicture getPicture : pictureList) {
					Map<String, String> map = new HashMap<String, String>();
					SysPicture picture = sysFileService.getPicture(getPicture.getPictureId());
					byte[] data = picture.getPictureContent();
					String img = "data:image/jpeg;base64," + Base64.encodeBase64String(data);
					
					map.put("id", "" + picture.getId());
					map.put("name", picture.getPictureName());
					map.put("img", img);
					list.add(map);
				}
			}
			result.setData(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/deletePicture")
	public Result deletePicture(Long knowledge_id, Long picture_id) {
		Result result = new Result();
		
		try {
			Map<String, Object> pictureMap = new HashMap<String, Object>();
			Map<String, Object> map = new HashMap<String, Object>();
			
			pictureMap.put("id", picture_id);
			sysPictureMapper.deleteByMap(pictureMap);
			
			map.put("knowledge_id", knowledge_id);
			map.put("picture_id", picture_id);
			
			sysKnowledgePictureMapper.deleteByMap(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getForAdmin")
	public Result getForAdmin(int currentPage, int pageSize, String query, String value) {
		Result result = new Result();
		
		try {
			Page<SysFile> page = new Page<SysFile>(currentPage, pageSize);
			SysFile params = new SysFile();
			QueryWrapper<SysFile> wrapper = new QueryWrapper<SysFile>(params);
			
			if (!query.equals("")) {
				if (value.equals("作者")) wrapper.like("author", query);
				else wrapper.like("file_name", query);
			}
			
			wrapper.orderByDesc("create_time");
			result.setData(sysFileMapper.selectPage(page, wrapper));
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
			Map<String, Object> columnMap = new HashMap<String, Object>();
			Map<String, Object> columnMapFile = new HashMap<String, Object>();
			List<Long> idList = new ArrayList<Long>();
			SysFavorFile params = new SysFavorFile();
			params.setFileId(id);
			List<SysFavorFile> favorList = sysFavorFileMapper.selectList(new QueryWrapper<SysFavorFile>());
			
			if (favorList != null && !favorList.isEmpty()) {
				for (SysFavorFile favor : favorList) {
					idList.add(favor.getId());
				}
				sysFavorFileMapper.deleteBatchIds(idList);
			}	
			
			columnMap.put("user_id", currentUser.getId());
			columnMap.put("file_id", id);
			sysUserFileMapper.deleteByMap(columnMap);		
			
			columnMapFile.put("id", id);
			sysFileMapper.deleteByMap(columnMapFile);
			result.success("删除文件成功，点击确定前往查看!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
