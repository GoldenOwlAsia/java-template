package com.goldenowl.ticketbooking;

import com.goldenowl.ticketbooking.constant.ProfileConstant;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(ProfileConstant.TEST)
class TicketBookingApplicationTests {

	@Test
	void contextLoads() {
	}

}
