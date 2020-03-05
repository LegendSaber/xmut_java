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
import com.xmut.xmut_java.common.BaseController;
import com.xmut.xmut_java.common.Result;
import com.xmut.xmut_java.sys.entity.SysSign;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.mapper.SysSignMapper;
import com.xmut.xmut_java.sys.mapper.SysUserMapper;

@RestController
@RequestMapping("/sysSign")
public class SysSignController extends BaseController{
	@Autowired
	private SysUserMapper sysUserMapper;
	
	@Autowired
	private SysSignMapper sysSignMapper;
	
	@RequestMapping("/isSign")
	public Result isSign(HttpServletRequest request) {
		Result result = new Result();
		SysUser loginUser = (SysUser)request.getSession().getAttribute("user");
		SysSign params = new SysSign();
		
		params.setCreateTime(new Date());
		params.setUsername(loginUser.getUsername());
		SysSign sign = sysSignMapper.selectOne(new QueryWrapper<SysSign>(params));
		
		if (sign == null) {
			result.fail("今日未签到");
		} else {
			result.success("今日已签到");
		}
		
		return result;
	}
	
	@RequestMapping("/sign")
	public Result sign(HttpServletRequest request) {
		Result result = new Result();
		SysUser loginUser = (SysUser)request.getSession().getAttribute("user");
		Long id = loginUser.getId();
		SysUser params = new SysUser();
		params.setId(id);
		SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(params));
		
		
		user.setSignDay((long)user.getSignDay() + 1);
		user.setScore((long)user.getScore() + 1);
		
		if (user.getSignDay() >= 5 && user.getSignDay() % 5 == 0) {
			user.setScore((long)user.getScore() + 5);
			result.setMessage("签到成功,积分+1.连续签到5天，积分+5.累计签到天数: " + user.getSignDay());
		}else {
			result.setMessage("签到成功,积分+1.累计签到天数: " + user.getSignDay());
		}
		sysUserMapper.update(loginUser, new UpdateWrapper<SysUser>().eq("id", id));
		SysSign signParams = new SysSign();
		
		signParams.setCreateTime(new Date());
		signParams.setUsername(user.getUsername());
		sysSignMapper.insert(signParams);
		
		return result;
	}
	
	@RequestMapping("/getSignNum")
	public Result getSignNum() {
		Result result = new Result();
		
		try {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			Calendar calendar = Calendar.getInstance();
			Date date = new Date();
			for (int i = 0; i < 7; i++) {
				SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");	

				calendar.add(Calendar.DATE, -1);
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH) + 1;
				int day = calendar.get(Calendar.DAY_OF_MONTH);
					
				String tmpDate = year + "-" + month + "-" + day;
				date = ft.parse(tmpDate);
				
				SysSign params = new SysSign();
				
				params.setCreateTime(date);
				int number = sysSignMapper.selectCount(new QueryWrapper<SysSign>(params));
				
				Map<String, Object> map = new HashMap<String, Object>();
				
				map.put("日期", tmpDate);
				map.put("签到人数", number);
				list.add(map);
			}
			result.setData(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
