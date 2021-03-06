# Coupon 발급 시스템

사용자로부터 이메일 주소를 입력으로 받아서 16자리의 알파벳과 숫자로 이루어진 **중복없는**
쿠폰 번호를 발급하고 발급된 쿠폰 정보를 같은 페이지에 리스팅하는 웹어플리케이션 개발


# 요구사항

* 웹어플리케이션 개발언어는 Java 또는 Scala, 프레임워크는 Spring-boot/PlayFramework/Akka-HTTP 활용
* 웹어플리케이션은 SPA (Single Page Application)로 개발되어야 함
* 쿠폰번호는 [0-9a- zA-Z]으로 구성
* 중복된 이메일 입력에 따른 쿠폰 발행은 불가
* 쿠폰번호 생성은 라이브러리 사용없이 직접 구현
* 데이타베이스는 사용에 제약 없음 (가능하면 In-memory db 사용)
* 서버에 REST API 구현
* 프론트엔드 구현에는 Twitter Bootstrap등 사용 가능
* 쿠폰번호 리스팅은 Pagination 가능하도록 구현
* 서버와 클라이언트는 JSON Object 통신으로 구현
* 클라이언트 구현을 위한 Javascript framework 제약은 없음

## Framework

 * 개발 언어 : Java
	 * 빌드 : G	
 * 프레임워크 : Spring-boot
	 * 테스트 : spring-boot-starter-test
 * 데이터베이스 : [Redis](https://redis.io/)
 * 프론트엔드 : Bootstrap

# 해결 전략

## 쿠폰 
* 쿠폰 번호의 각 값에 대해서는 제약이 있음 [0-9a- zA-Z]
	* 단, 쿠폰의 길이에 대한 제약이 없으나 16자리로 가정 (xxxx-xxxx-xxxx-xxxx)
* Unique 를 보장하기 위한 방안으로, Date 를 이용할 계획
	* 참고) 1520829216185 : 1/100 초 단위로 uniq 한 값을 생성할 수 있음
	* 단, 이 값을 그대로 이용하기 보다는 현재 쿠폰의 포멧을 최대한 이용 [0-9a- zA-Z]
		* 방안은 Date 값을 62진수로 변경하여, 쿠폰에 값을 생성
	* 문제는 1/100 초 단위로 같은 값을 생성할 수 있음
		* 이 중복을 해결하기 위해서, DB 에 insert 할 때, ID 를 이용함으로써 중복을 피할 수 있음
	* 즉, Date + ID 의 뒤 4자리 → 62진수로 변경 이 값을 쿠폰의 값으로 사용
* Email 값을 base64 encoding 하여 나온 값으로, Date 로 채워진 쿠폰의 값 이후에, 12번째 값 까지 사용
* 검증을 위해 마지막 4자리를 이용할 계획
	* 1~12까지 숫자 중 렌덤으로 하나를 선택, 검증 자리의 위치로 가정
		* 검증 자리의 위치를 13번째 쿠폰의 값으로 사용 (12진수로 간주)
		* 검증 자릐의 값을 14번째 쿠폰의 값으로 사용
	* 앞의 12자리 값을 base64 인코딩을 하고 마지막 2자리를 쿠폰의 15, 16번째 값으로 사용
## API
* [GET] /api/v1/coupon
	* Parameter : 
		* "offset" : integer (query)
		* "limit" : integer (query)
	* Response Code (200)
	* Response Body
		* { 
		"code": "success",
		"message": null,
		"data" : "coupons" : [
			   { 
			     "id" : long,
			     "email" : string,
			     "coupon" : string (16자리 문자)
			     "datetime" : long
			     }, ...
			     ]}
* [POST] /api/v1/create
	* Parameter 
		* "email" : string (body)
	* Response
		* 201 : Created
## DB
* Key : Email
* Value : Hash (id, coupon, date)
