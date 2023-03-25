package com.java2nb.novel.domain;

import java.io.Serializable;


import java.math.BigDecimal;//用来对超过16位有效位的数进行精确的运算

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.java2nb.common.jsonserializer.LongToStringSerializer;


import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;



/**
 * 充值订单
 * 
 * @author xiongxy
 * @email 1179705413@qq.com
 * @date 2020-12-01 03:49:57
 */
/*
这个类是个实体类，继承了Serializable类中的对象才能被实例化，相当于告诉jvm这个对象我不序列化了，你来帮我
 */
public class PayDO implements Serializable {
	/*发现序列化的serialVersionUID值和反序列化的serialVersionUID不一致
	JAVA序列化的机制是通过 判断类的serialVersionUID来验证的版本一致的。
	在进行反序列化时，JVM会把传来的字节流中的serialVersionUID于本地相应实体类的serialVersionUID进行比较。
	如果相同说明是一致的，可以进行反序列化，否则会出现反序列化版本一致的异常，即是InvalidCastException。*/
	private static final long serialVersionUID = 1L;
	
	//主键
		//java中的long能表示的范围比js中number大,也就意味着部分数值在js中存不下(变成不准确的值)
	//所以通过序列化成字符串来解决
	@JsonSerialize(using = LongToStringSerializer.class) //@JsonSerialize:指定序列化方法   using = LongToStringSerializer.class：用于序列化时嵌入我们自定义的代码
			private Long id;
	//保留
		//java中的long能表示的范围比js中number大,也就意味着部分数值在js中存不下(变成不准确的值)
	//所以通过序列化成字符串来解决
	@JsonSerialize(using = LongToStringSerializer.class)
			private Long outTradeNo;
	//订单号
			private String tradeNo;
	//保留  支付频道
			private Integer payChannel;
	//交易香蕉币
			private Integer totalAmount;  //交易总金额
	//支付用户ID
		//java中的long能表示的范围比js中number大,也就意味着部分数值在js中存不下(变成不准确的值)
	//所以通过序列化成字符串来解决
	@JsonSerialize(using = LongToStringSerializer.class)
			private Long userId;
	//支付状态：0：支付失败，1：支付成功，2：待支付
			private Integer payStatus;
	//创建时间
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private Date createTime;
	//更新时间
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private Date updateTime;

	/**
	 * 设置：主键
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：主键
	 */
	public Long getId() {
		return id;
	}
	/**
	 * 设置：保留
	 */
	public void setOutTradeNo(Long outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	/**
	 * 获取：保留
	 */
	public Long getOutTradeNo() {
		return outTradeNo;
	}
	/**
	 * 设置：订单号
	 */
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	/**
	 * 获取：订单号
	 */
	public String getTradeNo() {
		return tradeNo;
	}
	/**
	 * 设置：保留
	 */
	public void setPayChannel(Integer payChannel) {
		this.payChannel = payChannel;
	}
	/**
	 * 获取：保留
	 */
	public Integer getPayChannel() {
		return payChannel;
	}
	/**
	 * 设置：交易香蕉币
	 */
	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}
	/**
	 * 获取：交易香蕉币
	 */
	public Integer getTotalAmount() {
		return totalAmount;
	}
	/**
	 * 设置：支付用户ID
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * 获取：支付用户ID
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * 设置：支付状态：0：支付失败，1：支付成功，2：待支付
	 */
	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}
	/**
	 * 获取：支付状态：0：支付失败，1：支付成功，2：待支付
	 */
	public Integer getPayStatus() {
		return payStatus;
	}
	/**
	 * 设置：创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取：创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * 设置：更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	/**
	 * 获取：更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}
}
