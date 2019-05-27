package com.ivay.ivay_app.filter;

import com.ivay.ivay_app.dto.XLoginUser;
import com.ivay.ivay_app.service.XTokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Token过滤器
 * 
 * @author xx
 *
 *         2017年10月14日
 */
@Component
public class TokenFilter extends OncePerRequestFilter {

	public static final String TOKEN_KEY = "token";

	@Autowired
	private XTokenService tokenService;
	@Autowired
	private UserDetailsService userDetailsService;
	private static final Long MINUTES_10 = 10 * 60 * 1000L;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = getToken(request);
		if (StringUtils.isNotBlank(token)) {
			XLoginUser xLoginUser=tokenService.getLoginUser(token);

			if (xLoginUser != null) {
				xLoginUser = checkLoginTime(xLoginUser);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(xLoginUser,
						null, xLoginUser.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}

		filterChain.doFilter(request, response);
	}

	/**
	 * 校验时间<br>
	 * 过期时间与当前时间对比，临近过期10分钟内的话，自动刷新缓存
	 * 
	 * @param xloginUser
	 * @return
	 */
	private XLoginUser checkLoginTime(XLoginUser xloginUser) {
		long expireTime = xloginUser.getExpireTime();
		long currentTime = System.currentTimeMillis();
		if (expireTime - currentTime <= MINUTES_10) {
			String token = xloginUser.getToken();
			xloginUser = (XLoginUser) userDetailsService.loadUserByUsername(xloginUser.getUsername());
			xloginUser.setToken(token);
			tokenService.refresh(xloginUser);
		}
		return xloginUser;
	}

	/**
	 * 根据参数或者header获取token
	 * 
	 * @param request
	 * @return
	 */
	public static String getToken(HttpServletRequest request) {
		String token = request.getParameter(TOKEN_KEY);
		if (StringUtils.isBlank(token)) {
			token = request.getHeader(TOKEN_KEY);
		}

		return token;
	}

}
