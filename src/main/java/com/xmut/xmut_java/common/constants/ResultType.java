package com.xmut.xmut_java.common.constants;

public enum ResultType {
	SUCCESS("200", "操作成功"),
	FAIL("400", "操作失败"),
	UNAUTHORIZED("401", "权限不足"),
	NOT_FOUND("404", "接口不存在"),
	INTERNAL_SERVER_ERRPR("500", "服务器内部错误"),
	AGAIN_LOGIN("600", "请重新登录");
	
	private String code;
	private String name;
	
	ResultType(String code, String name){
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public String getName() {
		return this.name;
	}
}