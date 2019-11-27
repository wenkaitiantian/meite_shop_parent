package com.mayikt.weixin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mayikt.base.BaseApiService;
import com.mayikt.base.BaseResponse;
import com.mayikt.constans.Constants;
import com.mayikt.core.utils.RedisUtil;
import com.mayikt.weixin.service.VerificaCodeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class VerificaCodeServiceImpl extends BaseApiService<JSONObject> implements VerificaCodeService {
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public BaseResponse<JSONObject> verificaWeixinCode(String phone, String weixinCode) {
//      1、  验证参数是否为空
        if (StringUtils.isEmpty(phone)){
            return setResultError("手机号码不能为空");
        }

        if (StringUtils.isEmpty(weixinCode)) {
            return setResultError("注册码不能为空!");
        }
//      2、 根据手机号码查询redis返回对应code
        String weixinCodeKey = Constants.WEIXINCODE_KEY + phone;
        String redisCode = redisUtil.getString(weixinCodeKey);
        if (StringUtils.isEmpty(redisCode)) {
            return setResultError("注册码可能已经过期!!");
        }
//      3、  redis中的注册码与传递参数的微信code对比
        if (!redisCode.equals(weixinCode)){
            return setResultError("请输入正确的注册码");
        }
        // 移出对应验证码
        redisUtil.delKey(weixinCodeKey);
        return setResultSuccess("验证码比对正确");

    }
}
