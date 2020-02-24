package com.xmut.xmut_java.sys.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmut.xmut_java.common.BaseController;
import com.xmut.xmut_java.common.Result;
import com.xmut.xmut_java.sys.entity.SysFile;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.mapper.SysFileMapper;
import com.xmut.xmut_java.sys.service.SysFileService;

@RestController
@RequestMapping("/sysFile")
public class SysFileController extends BaseController{
	@Autowired
	private SysFileMapper sysFileMapper;
	
	@Autowired
	private SysFileService sysFileService;
	
	@RequestMapping("/upload")
	public Result upload(HttpServletRequest request) {
		Result result = new Result();
		SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
		List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
		int len = files.size();
	
		for (int i = 0; i < len; i++) {
			try {
				MultipartFile file = files.get(i);
				sysFileService.saveFile(file.getOriginalFilename(), file.getBytes(), currentUser);		
			} catch (IOException e) {
				e.printStackTrace();
			}
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
			result.setData(sysFileMapper.selectPage(page, wrapper));
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getMyFile")
	public Result getMyFile(int currentPage, int pageSize) {
		Result result = new Result();
		
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
