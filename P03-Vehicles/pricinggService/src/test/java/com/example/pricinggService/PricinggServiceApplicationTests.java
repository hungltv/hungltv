package com.example.pricinggService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.pricinggService.model.Price;
import com.example.pricinggService.service.PricingService;

@SpringBootTest
class PricinggServiceApplicationTests {
	@Test
	void contextLoads() {
		Price price = new Price();
		Long pricing = Long.valueOf(2);
		try {
			price = PricingService.getPrice(pricing);
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println(price.getPrice());
	}

}
