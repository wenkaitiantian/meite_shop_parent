package com.mayikt.member.service.impl;

import com.mayikt.base.BaseApiService;
import com.mayikt.base.BaseResponse;
import com.mayikt.constans.Constants;
import com.mayikt.core.utils.MiteBeanUtils;
import com.mayikt.member.MemberService;
import com.mayikt.member.mapper.UserMapper;
import com.mayikt.member.mapper.entity.UserDo;
import com.mayikt.member.output.dto.UserOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
@RestController
@Slf4j
public class MenberServiceImpl extends BaseApiService<UserOutDTO> implements MemberService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public BaseResponse<UserOutDTO> existMobile(String mobile) {
//        验证参数
        if (StringUtils.isEmpty(mobile)){
            return setResultError("手机号码不能为空!");
        }
//        数据库中查找手机号
        UserDo userDo=userMapper.existMobile(mobile);
//       如果不存在返回203
        if (userDo == null){
            return setResultError(Constants.HTTP_RES_CODE_EXISTMOBILE_203,"用户不存在");
        }
        // 注意需要将敏感数据进行脱敏
        return setResultSuccess(MiteBeanUtils.doToDto(userDo,UserOutDTO.class));

    }
}
