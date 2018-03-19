package pay.kakao.coupon;

import java.time.LocalTime;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Jedis;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponRestController {
	@Resource(name = "redisTemplate") 
	private HashOperations<String, String, String> hashOperations;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@GetMapping("/")
	public String index() {
		String key = "*";
		
		if (this.redisTemplate.hasKey(key)) {
			return "Find key";
		}
		
		return "This is kakao pay coupon system!";
	}
	
	@PostMapping(value = "/create")
	public ResponseEntity<String> createCoupon(@RequestParam Map<String, String> body) {
		String email = body.get("email");
		if (email.isEmpty()) {
			return new ResponseEntity("NO Email", HttpStatus.BAD_REQUEST);
		}
		
		System.out.println("Create new coupon for : " + email);
		
		if (!hashOperations.entries("taejun:coupon:email:" + email).isEmpty()) {
			return new ResponseEntity("Already used email", HttpStatus.BAD_REQUEST);
		}
		
		hashOperations.put("taejun:coupon:email:" + email, "coupon", "test");
		hashOperations.put("taejun:coupon:email:" + email, "date", LocalTime.now().toString());
		
		return new ResponseEntity("DONE", HttpStatus.OK);
		
	}
	
}
