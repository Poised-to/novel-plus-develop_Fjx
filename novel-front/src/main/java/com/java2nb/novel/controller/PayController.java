package com.java2nb.novel.controller;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.java2nb.novel.core.bean.UserDetails;
import com.java2nb.novel.core.config.AlipayProperties;
import com.java2nb.novel.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Poised-to
 */
@Controller
@RequestMapping("pay")
@RequiredArgsConstructor
@Slf4j
//BaseController：这里面写的方法有关于jwt令牌
public class PayController extends BaseController {

    private final AlipayProperties alipayConfig;

    private final OrderService orderService;


    /**
     * 支付宝支付
     */
//    @SneakyThrows注解是由lombok为我们封装的，它可以为我们的代码生成一个try...catch块，并把异常向上抛出来
    //消除try...catch这一串代码，使代码更简洁
    @SneakyThrows
    @PostMapping("aliPay")
    public void aliPay(Integer payAmount,HttpServletRequest request,HttpServletResponse httpResponse) {
        //通过getUserDetails方法，获取到request中传来的数据，并保存起来，然后判断是否为空
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            //未登录，跳转到登陆页面
            //实现重定向
            //这里把支付总额加上可能是因为将现在的数据传过去，省的重新选择
            httpResponse.sendRedirect("/user/login.html?originUrl=/pay/aliPay?payAmount="+payAmount);
        }else {
            //创建充值订单
            Long outTradeNo = orderService.createPayOrder((byte)1,payAmount,userDetails.getId());

            //TODO 获得初始化的AlipayClient,主要封装支付请求的公共参数，如请求网关，签名等信息
            //AlipayClient由导包(外部得来)
            AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.getGatewayUrl(), alipayConfig.getAppId(), alipayConfig.getMerchantPrivateKey(), "json", alipayConfig.getCharset(), alipayConfig.getPublicKey(), alipayConfig.getSignType());
            //TODO 创建API对应的request,调用支付宝接口
            //AlipayTradePagePayRequest此参数也是由外部导包得来
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            //在公共参数中设置回跳和通知地址
            //TODO 设置同步通知的地址
            alipayRequest.setReturnUrl(alipayConfig.getReturnUrl());
            //TODO 设置同步通知的地址
            alipayRequest.setNotifyUrl(alipayConfig.getNotifyUrl());
            //填充业务参数
            //TODO 设置请求业务中的各个参数
            alipayRequest.setBizContent("{" +
                    "    \"out_trade_no\":\"" + outTradeNo + "\"," +
                    "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                    "    \"total_amount\":" + payAmount + "," +
                    "    \"subject\":\"小说精品屋-plus\"" +
                    "  }");
            //调用SDK生成表单
            //TODO 网页支付请求
            String form = alipayClient.pageExecute(alipayRequest).getBody();

            //TODO 此方法在getWrite之前调用，将会把响应的编码按照给定的字符设置
            httpResponse.setContentType("text/html;charset=utf-8");
            //直接将完整的表单html输出到页面
            httpResponse.getWriter().write(form);
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        }

    }

    /**
     * 支付宝支付通知
     * */
    @SneakyThrows
    @RequestMapping("aliPay/notify")
    public void aliPayNotify(HttpServletRequest request,HttpServletResponse httpResponse){

        //TODO .PrintWriter可以直接调用write()或print()方法，把字符串作为参数写入，这样就可以写入json格式的数据了,通过这种方式，客户端就可以接受到数据了
        PrintWriter out = httpResponse.getWriter();

        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<>();
        /* TODO
        request.getParameterMap()返回的是一个Map类型的值，
        该返回值记录着前端（如jsp页面）所提交请求中的请求参数和请求参数值的映射关系。
        这个返回值有个特别之处——只能读。不像普通的Map类型数据一样可以修改。
        这是因为服务器为了实现一定的安全规范，所作的限制
         */
        Map<String,String[]> requestParams = request.getParameterMap();
        // keySet():取出requestParams的所有的key值
        for (String name : requestParams.keySet()) {
            //取出所有key对应的value
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        //调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.getPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType());

        //——请在这里编写您的程序（以下代码仅作参考）——

	/* 实际验证过程建议商户务必添加以下校验：
	1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
	2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
	3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
	4、验证app_id是否为该商户本身。
	*/
        if(signVerified) {
            //验证成功
            //商户订单号
            String outTradeNo = new String(request.getParameter("out_trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

            //支付宝交易号
            String tradeNo = new String(request.getParameter("trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

            //交易状态
            String tradeStatus = new String(request.getParameter("trade_status").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

            //更新订单状态
            orderService.updatePayOrder(Long.parseLong(outTradeNo), tradeNo, tradeStatus);

            out.println("success");

        }else {//验证失败
            out.println("fail");

            //调试用，写文本函数记录程序运行情况是否正常
            //String sWord = AlipaySignature.getSignCheckContentV1(params);
            //AlipayConfig.logResult(sWord);
        }
    }
}
