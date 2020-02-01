package com.xmut.xmut_java.common;

import java.io.Serializable;

import com.xmut.xmut_java.common.constants.ResultType;


public class Result<T> implements Serializable {
	private String statusCode = ResultType.SUCCESS.getCode();
	private String message = ResultType.SUCCESS.getName();
	private T data = null;
	private Boolean success = true;
	
	public String getStatusCode() {
		return statusCode;
	}
	
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void  setMessage(String message) {
		this.message = message;
	}
	
	public T getData() {
		return this.data;
	}
	
	public void setData(T data) {
		this.data = data;
	}
	
	public Boolean isSuccess() {
		return this.success;
	}
	
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	
	public void addError() {
		this.addError("");
	}
	
	public void addError(String message) {
		this.success = false;
		this.message = message;
		this.statusCode = ResultType.INTERNAL_SERVER_ERRPR.getCode();
		if (this.message == null || "".equals(this.message)) {
			this.message = ResultType.INTERNAL_SERVER_ERRPR.getName();
		}
	}
	
	public void success() {
		this.success("");
	}
	
	public void success(String message) {
		this.success = true;
		this.message = message;
		this.statusCode = ResultType.SUCCESS.getCode();
		if (this.message == null || "".equals(this.message)) {
			this.message = ResultType.SUCCESS.getName();
		}
	}
	
	public void fail() {
		this.fail("");
	}
	
	public void fail(String message) {
		this.success = false;
		this.message = message;
		this.statusCode = ResultType.FAIL.getCode();
		if (this.message == null || "".equals(this.message)) {
			this.message = ResultType.FAIL.getName();
		}
	}
	
	public void unauthorized() {
		this.unauthorized("");
	}
	
	public void unauthorized(String message) {
		this.success = false;
		this.message = message;
		this.statusCode = ResultType.UNAUTHORIZED.getCode();
		
		if (this.message == null || "".equals(this.message)) {
			this.message = ResultType.UNAUTHORIZED.getName();
		}
	}
	
	public void notfound() {
		this.notfound("");
	}
	
	public void notfound(String message) {
		this.success = false;
		this.message = message;
		this.statusCode = ResultType.NOT_FOUND.getCode();
		
		if (this.message == null || "".equals(this.message)){
			this.message = ResultType.NOT_FOUND.getName();
		}
	}
	
	public void againLogin() {
		this.againLogin("");
	}
	
	public void againLogin(String message) {
		this.success = false;
		this.message = message;
		this.statusCode = ResultType.AGAIN_LOGIN.getCode();
		
		if (this.message == null || "".equals(this.message)) {
			this.message = ResultType.AGAIN_LOGIN.getName();
		}
	}
}