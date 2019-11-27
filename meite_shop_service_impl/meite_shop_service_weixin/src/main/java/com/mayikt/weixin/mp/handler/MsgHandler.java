package com.mayikt.weixin.mp.handler;


import com.mayikt.base.BaseResponse;
import com.mayikt.constans.Constants;
import com.mayikt.core.utils.RedisUtil;
import com.mayikt.core.utils.RegexUtils;
import com.mayikt.member.output.dto.UserOutDTO;
import com.mayikt.weixin.feign.MemberServiceFiegn;
import com.mayikt.weixin.mp.builder.TextBuilder;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Slf4j
@Component
public class MsgHandler extends AbstractHandler {
    /**
     * 发送验证码消息
     */
    @Value("${mayikt.weixin.registration.code.message}")
    private String registrationCodeMessage;
    /**
     * 默认回复消息
     */
    @Value("${mayikt.weixin.default.registration.code.message}")
    private String defaultRegistrationCodeMessage;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MemberServiceFiegn memberServiceFiegn;
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {

        if (!wxMessage.getMsgType().equals(XmlMsgType.EVENT)) {
            //TODO 可以选择将消息保存到本地
        }

        //当用户输入关键词如“你好”，“客服”等，并且有客服在线时，把消息转发给在线客服
        try {
            if (StringUtils.startsWithAny(wxMessage.getContent(), "你好", "客服")
                && weixinService.getKefuService().kfOnlineList()
                .getKfOnlineList().size() > 0) {
                return WxMpXmlOutMessage.TRANSFER_CUSTOMER_SERVICE()
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser()).build();
            }
        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        //TODO 组装回复消息
//        1.获取微信客户端发送的消息
        String fromcontent= wxMessage.getContent();
//        2.使用正则表达式验证消息是否为手机号码格式
        if (RegexUtils.checkMobile(fromcontent)){
//          3、根据手机号调用会员服务接口，查询手机号是否存在
            BaseResponse<UserOutDTO> reusltUserInfo=memberServiceFiegn.existMobile(fromcontent);

            if (reusltUserInfo.getRtnCode().equals(Constants.HTTP_RES_CODE_200)){
                return new TextBuilder().build("手机存在"+fromcontent, wxMessage, weixinService);
            }
            if (!reusltUserInfo.getRtnCode().equals(Constants.HTTP_RES_CODE_EXISTMOBILE_203)){
                return new TextBuilder().build(reusltUserInfo.getMsg(), wxMessage, weixinService);
            }


//        4.如果是手机号的格式，随机产生4位数字注册码
            int registCode=registCode();
            redisUtil.setString(Constants.WEIXINCODE_KEY+fromcontent,registCode+"", Constants.WEIXINCODE_TIMEOUT);
             String content= registrationCodeMessage.format(registrationCodeMessage, registCode);
            return new TextBuilder().build(content, wxMessage, weixinService);
        }
        return new TextBuilder().build(defaultRegistrationCodeMessage, wxMessage, weixinService);

    }
// 生成四位随机数
    private int registCode() {
        int registCode = (int) (Math.random() * 9000 + 1000);
        return registCode;
    }


}
