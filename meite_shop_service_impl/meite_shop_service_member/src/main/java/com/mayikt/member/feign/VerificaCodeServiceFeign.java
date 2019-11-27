package com.mayikt.member.feign;


import com.mayikt.weixin.service.VerificaCodeService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "app-mayikt-weixin")
public interface VerificaCodeServiceFeign extends VerificaCodeService {
}
