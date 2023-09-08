package com.ewallet.springbootewallet.controller;

import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.ewallet.springbootewallet.service.serviceImpl.AliPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alipay/")
@Slf4j
public class AliPayController {


    private AliPayService aliPayService;

    @Autowired
    public AliPayController(AliPayService aliPayService){
        this.aliPayService = aliPayService;
    }

    @RequestMapping("createWebTrade")
    public void createWebTrade(HttpServletResponse response, @RequestParam String tradeNo, @RequestParam String subject, @RequestParam String totalAmount ) throws Exception{
        log.info("1");
//        String tradeNo = "20150320091541010121";
//        String subject = "iphone11 128G";
//        String totalAmount = "0.1";
        String returnUrl =  "http://localhost/api/alipay/webReturnUrl";
        AlipayTradePagePayResponse pagePayResponse;
        log.info("2");
        try {
            pagePayResponse = aliPayService.createWebTradeForm(subject, tradeNo, totalAmount, returnUrl);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(pagePayResponse.getBody());// 直接将完整的表单html输出到页面
            response.getWriter().flush();
            response.getWriter().close();
        } catch(Exception e) {
            log.info(e.getMessage());
            System.out.println("new");
        }

    }

    @RequestMapping(value = "webReturnUrl")
    public Object webTradeReturnUrl(HttpServletRequest request){
        System.out.println(request.getParameterMap());
        return request.getParameterMap();
    }
}