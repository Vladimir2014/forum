package com.ngtr.forum.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtProvider jwtProvider;
	
	@Autowired
	private UserDetailsService userDetailsService;

	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String jwtToken = getJwtToeknFromRequest(request);
		
		System.out.println("TOKEN : " + jwtToken);
		
		try {
			if (StringUtils.hasText(jwtToken) && jwtProvider.validateToken(jwtToken)) {
				String username = jwtProvider.getUserNameFromToken(jwtToken);
				
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			throw e;
		}
		filterChain.doFilter(request, response);
	}

	private String getJwtToeknFromRequest(HttpServletRequest request) {
		String tokenKeyValue = request.getHeader("Authorization");
		
		return (StringUtils.hasText(tokenKeyValue) && tokenKeyValue.startsWith("Bearer")
				? tokenKeyValue.replace("Bearer", "").trim()
				: tokenKeyValue);
	}

}
