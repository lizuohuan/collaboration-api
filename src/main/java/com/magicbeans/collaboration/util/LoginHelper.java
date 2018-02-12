package com.magicbeans.collaboration.util;

import com.magicbeans.collaboration.entity.User;
import com.magicbeans.collaboration.exception.InterfaceCommonException;
import com.magicbeans.collaboration.redis.RedisService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


public class LoginHelper {
	
	public static final String TOKEN = "token";

	public static boolean isLogin=false;

	/**SESSION USER*/
	public static final String SESSION_USER = "admin_user";

	/** key前缀 */
	public static final String KEY_LOGIN = "login_";



	public static User getCurrentUser(RedisService redisService) throws Exception{
		HttpServletRequest req = ((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest();
		String token = req.getHeader(TOKEN);
		if(CommonUtil.isEmpty(token)){
			throw new InterfaceCommonException(StatusConstant.NOTLOGIN,"未登录");
		}
		User user = (User)redisService.get(token);
		if(null == user){
			throw new InterfaceCommonException(StatusConstant.NOTLOGIN,"未登录");
		}
		return user;
	}

}
