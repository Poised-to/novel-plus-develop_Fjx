package com.java2nb.novel.service.impl;

import com.java2nb.novel.entity.OrderPay;
import com.java2nb.novel.mapper.OrderPayDynamicSqlSupport;
import com.java2nb.novel.mapper.OrderPayMapper;
import com.java2nb.novel.service.OrderService;
import com.java2nb.novel.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.select.SelectDSL.select;

/**
 * @author 11797
 */
@Service
@RequiredArgsConstructor //是一个构造器注入的方法之一
public class OrderServiceImpl implements OrderService {

    //这个对象中提供了很多sql语句
    private final OrderPayMapper orderPayMapper;

    private final UserService userService;


    /**
     * 新建支付订单
     * @param payChannel 支付渠道
     * @param payAmount 支付金额
     * @param userId 用户ID
     * @return
     */
    @SneakyThrows
    @Override
    public Long createPayOrder(Byte payChannel, Integer payAmount, Long userId) {
        Date currentDate = new Date();
        Long outTradeNo = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(currentDate)+new Random().nextInt(10));
        OrderPay orderPay = new OrderPay();
        orderPay.setOutTradeNo(outTradeNo);
        orderPay.setPayChannel(payChannel);
        orderPay.setTotalAmount(payAmount);
        orderPay.setUserId(userId);
        orderPay.setCreateTime(currentDate);
        orderPay.setUpdateTime(currentDate);
        orderPayMapper.insertSelective(orderPay);
        return outTradeNo;
    }

    /**
     * 更新支付订单
     * @param outTradeNo 商户订单号
     * @param tradeNo      支付宝/微信 订单号
     * @param tradeStatus 支付状态
     */
//    @Transactional注解在代码执行出错的时候能够进行事务的回滚。
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePayOrder(Long outTradeNo, String tradeNo, String tradeStatus) {
        SelectStatementProvider selectStatement = select(OrderPayDynamicSqlSupport.id,OrderPayDynamicSqlSupport.payStatus,OrderPayDynamicSqlSupport.totalAmount,OrderPayDynamicSqlSupport.userId)
                .from(OrderPayDynamicSqlSupport.orderPay)
                .where(OrderPayDynamicSqlSupport.outTradeNo, isEqualTo(outTradeNo))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        OrderPay orderPay = orderPayMapper.selectMany(selectStatement).get(0);

        if(orderPay.getPayStatus()!=1) {
            //此订单还未处理过

            if (tradeStatus.equals("TRADE_SUCCESS") || tradeStatus.equals("TRADE_FINISHED")) {
                //支付成功
                //1.更新订单状态为成功
                orderPay.setPayStatus((byte) 1);
                orderPay.setUpdateTime(new Date());
                orderPayMapper.updateByPrimaryKeySelective(orderPay);

                //2.增加用户余额
                userService.addAmount(orderPay.getUserId(),orderPay.getTotalAmount()*100);






            }
        }



    }
}
