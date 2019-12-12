package com.mayikt.member.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mayikt.base.BaseApiService;
import com.mayikt.base.BaseResponse;
import com.mayikt.constans.Constants;
import com.mayikt.core.utils.MD5Util;
import com.mayikt.core.utils.MiteBeanUtils;
import com.mayikt.core.utils.RedisUtil;
import com.mayikt.member.MemberRegisterService;
import com.mayikt.member.feign.VerificaCodeServiceFeign;
import com.mayikt.member.input.dto.UserInpDTO;
import com.mayikt.member.mapper.UserMapper;
import com.mayikt.member.mapper.entity.UserDo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class MemberRegisterServiceImpl  extends BaseApiService<JSONObject> implements MemberRegisterService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private VerificaCodeServiceFeign verificaCodeServiceFeign ;
//    事务
    @Transactional
    public BaseResponse<JSONObject> register(@RequestBody UserInpDTO userInpDTO, String registCode) {
//        1、参数验证
//        String userName=userInpDTO.getUserName();
//        if (StringUtils.isEmpty(userName)){
//            return setResultError("用户名称不能为空");
//        }
        String userMobile=userInpDTO.getMobile();
        if (StringUtils.isEmpty(userMobile)){
            return setResultError("用户手机号不能为空");
        }
        String userPassword=userInpDTO.getPassword();
        if (StringUtils.isEmpty(userPassword)){
            return setResultError("用户密码不能为空");
        }
//        2、验证码注册码是否正确
        BaseResponse<JSONObject> verificaWeixinCode  = verificaCodeServiceFeign.verificaWeixinCode(userInpDTO.getMobile(), registCode);
        if (!verificaWeixinCode.getRtnCode().equals(Constants.HTTP_RES_CODE_200)){
            return setResultError(verificaWeixinCode.getMsg());
        }
//        3、对用户的密码进行加密
        String newPassword= MD5Util.MD5(userPassword);
        userInpDTO.setPassword(newPassword);
//        4、调用数据库插入数据

        return userMapper.register(MiteBeanUtils.dtoToDo(userInpDTO, UserDo.class)) >0?setResultSuccess("注册成功"):setResultError("注册失败");
    }
}
