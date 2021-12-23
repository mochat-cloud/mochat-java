package com.mochat.mochat.config.web;

import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 跨域配置
 *
 * @author: Huayu
 * @time: 2020/11/22 17:48
 */
@Configuration
@WebFilter(filterName = "CorsFilter", urlPatterns = {"/*"})
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 这里最好明确的写允许的域名
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Allow-Headers", "Authorization,Accept,Content-Type,Origin,User-Agent,X-Requested-With,MoChat-Corp-Id,MoChat-Source-Type");
        filterChain.doFilter(servletRequest, servletResponse);
    }

}