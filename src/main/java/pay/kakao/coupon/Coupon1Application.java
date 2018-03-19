package pay.kakao.coupon;

import javax.annotation.Resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.HashOperations;

@SpringBootApplication
public class Coupon1Application {
	
	@Resource(name = "redisTemplate") 
	private HashOperations<String, String, String> hashOperations;

	public static void main(String[] args) {
		SpringApplication.run(Coupon1Application.class, args);
	}
}
