package com.goldenowl.springboottemplate;

import com.goldenowl.springboottemplate.app.constant.ProfileConstant;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(ProfileConstant.TEST)
class SpringBootTemplateApplicationTests {

	@Test
	void contextLoads() {
	}

}
