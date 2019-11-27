package com.mayikt.weixin.feign;


import com.mayikt.member.MemberService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("app-mayikt-member")
public interface MemberServiceFiegn extends MemberService {
}
