package com.ewallet.springbootewallet.service.serviceImpl;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.payment.app.models.AlipayTradeAppPayResponse;
import com.alipay.easysdk.payment.common.models.*;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.ewallet.springbootewallet.config.AliPayConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class AliPayService {
    private static final Logger logger = LoggerFactory.getLogger(AliPayService.class);

    private Properties aliPayProperties;

    @Autowired
    public AliPayService(Properties aliPayProperties){

        this.aliPayProperties =aliPayProperties;

        //设置Factory 全局参数
        {
            Config config = new Config();
            config.appId = aliPayProperties.getProperty(AliPayConstant.appId);

            if(StringUtils.isNotBlank(aliPayProperties.getProperty(AliPayConstant.alipayPublicKey))) {
                config.alipayPublicKey = aliPayProperties.getProperty(AliPayConstant.alipayPublicKey);
            } else{
                //如果采用非证书模式，则无需赋值下面的三个证书路径，改为赋值如上的支付宝公钥字符串即可
                String merchantCertPath = aliPayProperties.getProperty(AliPayConstant.merchantCertPath);
                String alipayCertPath = aliPayProperties.getProperty(AliPayConstant.alipayCertPath);
                String alipayRootCertPath = aliPayProperties.getProperty(AliPayConstant.alipayRootCertPath);

                if(StringUtils.isNotBlank(merchantCertPath) && StringUtils.isNotBlank(alipayCertPath)
                        && StringUtils.isNotBlank(alipayRootCertPath)){
                    config.merchantCertPath = merchantCertPath;
                    config.alipayCertPath = merchantCertPath;
                    config.alipayRootCertPath = alipayRootCertPath;
                }else {
                    logger.error("merchantCertPath:{},alipayCertPath:{},alipayRootCertPath:{}"
                            ,merchantCertPath,alipayCertPath,alipayRootCertPath);
                }
            }
            config.gatewayHost = aliPayProperties.getProperty(AliPayConstant.gatewayHost);
            config.protocol = aliPayProperties.getProperty(AliPayConstant.protocol);
            config.signType = aliPayProperties.getProperty(AliPayConstant.signType);
            config.merchantPrivateKey = aliPayProperties.getProperty(AliPayConstant.merchantPrivateKey);
            config.notifyUrl = aliPayProperties.getProperty(AliPayConstant.notifyUrl);
            //可设置AES密钥，调用AES加解密相关接口时需要（可选）
//            config.encryptKey = aliPayProperties.getProperty(AliPayConstant.encryptKey);
            Factory.setOptions(config);
        }

    }

    /**
     * 对应 alipay.trade.app.pay 接口
     * 构造交易数据供商户app到支付宝下单
     */
    public AlipayTradeAppPayResponse createAppTradeForm(String subject ,String tradeNo,String totalAmount) throws Exception {

        return Factory.Payment.App()
                //单独设置超时时间 默认15分钟
                .optional("timeout_express",aliPayProperties.getProperty(AliPayConstant.timeoutExpress,"15m"))
                .pay(subject,tradeNo,totalAmount);
    }

    /**
     * 对应alipay.trade.page.pay 接口
     * 构造交易数据供pc端到支付宝下单
     */
    public AlipayTradePagePayResponse createWebTradeForm(String subject ,String tradeNo,String totalAmount,String returnUrl) throws Exception{
        return Factory.Payment.Page()
                //单独设置超时时间 默认15分钟
                .optional("timeout_express",aliPayProperties.getProperty(AliPayConstant.timeoutExpress,"15m"))
                .pay(subject,tradeNo,totalAmount, returnUrl);
    }

    /**
     * 对应alipay.trade.query(统一收单线下交易查询)
     */
    public AlipayTradeQueryResponse queryTrade(String tradeNo) throws Exception {

        return Factory.Payment.Common().query(tradeNo);
    }

    /**
     * alipay.trade.cancel
     */
    public AlipayTradeCancelResponse cancelTrade(String tradeNo) throws Exception{
        return Factory.Payment.Common().cancel(tradeNo);
    }

    /**
     * alipay.trade.close(统一收单交易关闭接口)
     */
    public AlipayTradeCloseResponse closeTrade(String tradeNo) throws Exception{
        return Factory.Payment.Common().close(tradeNo);
    }

    /**
     * alipay.trade.refund(统一收单交易退款接口)
     */
    public AlipayTradeRefundResponse refundTrade(String tradeNo,String refundAmount) throws Exception{
        return Factory.Payment.Common().refund(tradeNo,refundAmount);
    }

    /**
     * alipay.trade.fastpay.refund.query(统一收单交易退款查询)
     */
    public AlipayTradeFastpayRefundQueryResponse refundQuery(String tradeNo,String outRequestNo) throws Exception{
        return Factory.Payment.Common().queryRefund(tradeNo,outRequestNo);
    }


}