package com.xmut.xmut_java.sys.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmut.xmut_java.common.BaseController;
import com.xmut.xmut_java.common.Result;
import com.xmut.xmut_java.sys.entity.SysAdmin;
import com.xmut.xmut_java.sys.entity.SysExperience;
import com.xmut.xmut_java.sys.entity.SysFile;
import com.xmut.xmut_java.sys.entity.SysKnowledge;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.mapper.SysAdminMapper;
import com.xmut.xmut_java.sys.mapper.SysExperienceMapper;
import com.xmut.xmut_java.sys.mapper.SysFileMapper;
import com.xmut.xmut_java.sys.mapper.SysKnowledgeMapper;
import com.xmut.xmut_java.sys.mapper.SysUserMapper;

@RestController
@RequestMapping("/sysAdmin")
public class SysAdminComtroller extends BaseController{
	@Autowired
	private SysAdminMapper sysAdminMapper;
	
	@Autowired
	private SysUserMapper sysUserMapper;
	
	@Autowired
	private SysExperienceMapper sysExperienceMapper;
	
	@Autowired
	private SysKnowledgeMapper sysKnowledgeMapper;
	
	@Autowired
	private SysFileMapper sysFileMapper;
	
	@RequestMapping("/login")
	public Result login(String name, String password, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysAdmin params = new SysAdmin();
			
			params.setName(name);
			params.setPassword(password);
			
			SysAdmin admin = sysAdminMapper.selectOne(new QueryWrapper<SysAdmin>(params));
			
			if (admin == null) {
				result.fail("用户名或密码错误");
			} else if (admin.getIsUsed() == 0) {
				result.fail("账户已经停用，请联系超级管理员");
			} else {
				request.getSession().setAttribute("admin", admin);
				result.setData(admin);
				result.success("登录成功!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/logout")
	public Result logout(HttpServletRequest request) {
		Result result = new Result();
		
		try {
			if (request.getSession().getAttribute("admin") != null) {
				request.getSession().removeAttribute("admin");
			}
			result.success("退出登录成功!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getForAdmin")
	public Result getForAdmin(int currentPage, int pageSize, String query) {
		Result result = new Result();
		
		try {
			Page<SysAdmin> page = new Page<SysAdmin>(currentPage, pageSize);
			SysAdmin params = new SysAdmin();
			QueryWrapper<SysAdmin> wrapper = new QueryWrapper<SysAdmin>(params);
			
			if (!query.equals("")) wrapper.like("name", query);
			
			result.setData(sysAdminMapper.selectPage(page, wrapper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/register")
	public Result register(String name, String password, String roleName) {
		Result result = new Result();
		
		try {
			SysAdmin params = new SysAdmin();
			params.setName(name);
			
			SysAdmin admin = sysAdminMapper.selectOne(new QueryWrapper<SysAdmin>(params));
			
			if (admin != null) {
				result.fail("账户已经存在!");
			} else {
				params.setPassword(password);
				params.setRoleName(roleName);
				params.setIsUsed((long)1);
				sysAdminMapper.insert(params);
				result.success("添加成功，点击确定前往查看!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/status")
	public Result status(Long id, Long isUsed) {
		Result result = new Result();
		
		
		try {
			SysAdmin params = new SysAdmin();
			params.setId(id);
			
			SysAdmin admin = sysAdminMapper.selectOne(new QueryWrapper<SysAdmin>(params));
			
			if (admin != null) {
				admin.setIsUsed(isUsed);
				sysAdminMapper.update(admin, new UpdateWrapper<SysAdmin>().eq("id", id));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/deleteAdmin")
	public Result deleteAdmin(Long id) {
		Result result = new Result();
		
		try {
			sysAdminMapper.deleteById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getUserData")
	public Result getUserData() {
		Result result = new Result();
		
		try {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			
			SysUser userParams = new SysUser();
			
			Map<String, Object> map = new HashMap<String, Object>();
			userParams.setRoleName("研友");
			int number = sysUserMapper.selectCount(new QueryWrapper<SysUser>(userParams));
			
			map.put("角色", "研友");
			map.put("访问人数", number);
			
			list.add(map);
			
			Map<String, Object> map2 = new HashMap<String, Object>();
			userParams.setRoleName("路人");
			int number2 = sysUserMapper.selectCount(new QueryWrapper<SysUser>(userParams));
			
			map2.put("角色", "路人");
			map2.put("访问人数", number2);
			
			list.add(map2);
			
			result.setData(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("getChartData")
	public Result getChartData() {
		Result result = new Result();
		
		try {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			Calendar calendar = Calendar.getInstance();
			Date date = new Date();
			calendar.add(Calendar.DATE, -7);
			for (int i = 0; i < 7; i++) {
				SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");	

				calendar.add(Calendar.DATE, +1);
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH) + 1;
				int day = calendar.get(Calendar.DAY_OF_MONTH);
					
				String tmpDate = year + "-" + month + "-" + day;
				date = ft.parse(tmpDate);
				SysExperience experienceParams = new SysExperience();
				SysKnowledge knowledgeParams = new SysKnowledge();
				SysFile fileParams = new SysFile();
				
				experienceParams.setCreateTime(date);
				knowledgeParams.setCreateTime(date);
				fileParams.setCreateTime(date);
				int experienceNumber = sysExperienceMapper.selectCount(new QueryWrapper<SysExperience>(experienceParams));
				int knowledgeNumber = sysKnowledgeMapper.selectCount(new QueryWrapper<SysKnowledge>(knowledgeParams));
				int fileNumber = sysFileMapper.selectCount(new QueryWrapper<SysFile>(fileParams));
				
				Map<String, Object> map = new HashMap<String, Object>();
				
				map.put("日期", tmpDate);
				map.put("新增经验贴", experienceNumber);
				map.put("新增知识贴", knowledgeNumber);
				map.put("新增文件数", fileNumber);
				list.add(map);
			}
			result.setData(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
