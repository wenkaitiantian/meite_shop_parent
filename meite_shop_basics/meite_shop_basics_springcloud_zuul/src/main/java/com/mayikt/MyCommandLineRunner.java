package com.mayikt;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyCommandLineRunner implements CommandLineRunner {
	@ApolloConfig
	private Config config;
	    @Value("${server.port}")
    private  String _prot;
	@Override
	public void run(String... args) throws Exception {
		config.addChangeListener(new ConfigChangeListener() {

			@Override
			public void onChange(ConfigChangeEvent changeEvent) {
				log.info("####分布式配置中心监听#####"+ _prot  + changeEvent.changedKeys().toString());
			}
		});
	}

}
