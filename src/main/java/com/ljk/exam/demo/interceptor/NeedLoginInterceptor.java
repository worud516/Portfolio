package com.ljk.exam.demo.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.ljk.exam.demo.util.Ut;
import com.ljk.exam.demo.vo.Rq;

@Component
public class NeedLoginInterceptor implements HandlerInterceptor {
	private Rq rq;

	public NeedLoginInterceptor(Rq rq) {
		this.rq = rq;
	}

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/html; utf-8");
		
		if (!rq.isLogined()) {
			String afterLoginUri = rq.getEncodedCurrentUri();
            resp.getWriter().append(Ut.jsReplace("로그인 후 이용해주세요.", "../member/login?afterLoginUri=" + afterLoginUri));
			return false;
		}

		return HandlerInterceptor.super.preHandle(req, resp, handler);
	}
}