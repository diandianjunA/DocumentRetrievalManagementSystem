package com.project.documentretrievalmanagementsystem.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import com.project.documentretrievalmanagementsystem.common.BaseContext;
import com.project.documentretrievalmanagementsystem.common.R;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();
        //定义不需要处理的请求路径
        String[] urls = {"/static/**","/user/login"};
        boolean check = check(urls, requestURI);
        if(check){
            //放行
            filterChain.doFilter(request,response);
            return;
        }
        String token = request.getHeader("Authorization");
        if(!StringUtils.isEmpty(token)){
            //判断用户是否登录
            if(request.getSession().getAttribute(token)!=null){
                //将id存到当前线程中
                Long userId = (Long) request.getSession().getAttribute(token);
                BaseContext.setCurrentId(userId);
                //放行
                filterChain.doFilter(request,response);
                return;
            }
            //如果未登录
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        }
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls , String requestURI){
        for(String url:urls){
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
