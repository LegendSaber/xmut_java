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
import com.xmut.xmut_java.common.BaseController;
import com.xmut.xmut_java.common.Result;
import com.xmut.xmut_java.sys.entity.SysExperience;
import com.xmut.xmut_java.sys.entity.SysFile;
import com.xmut.xmut_java.sys.entity.SysKnowledge;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.mapper.SysExperienceMapper;
import com.xmut.xmut_java.sys.mapper.SysFileMapper;
import com.xmut.xmut_java.sys.mapper.SysKnowledgeMapper;

@RestController
@RequestMapping("/sysUsermanager")
public class SysUsermanagerController extends BaseController{
	@Autowired
	private SysExperienceMapper sysExperienceMapper;
	
	@Autowired
	private SysKnowledgeMapper sysKnowledgeMapper;
	
	@Autowired
	private SysFileMapper sysFileMapper;
	
	@RequestMapping("/getChartData")
	public Result getChartData(HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			String username = currentUser.getUsername();
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
				experienceParams.setAuthor(username);
				knowledgeParams.setCreateTime(date);
				knowledgeParams.setAuthor(username);
				fileParams.setCreateTime(date);
				knowledgeParams.setAuthor(username);
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
