package interceptor;

import cn.tinsur.elder.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取Token
        String token = request.getHeader("Authorization");
        if (ObjectUtils.isEmpty(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        try {
            //解析token
            Map<String, Object> claims = JwtUtil.parseToken(token);
            //前后台token隔离：老人/家属的token（带userType）不能访问后台管理接口，后台用户的token不能访问前台接口
            String userType = (String) claims.get("userType");
            String uri = request.getRequestURI();
            if (uri.startsWith("/admin") && userType != null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            if (uri.startsWith("/app") && userType == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            return true;//放行
        }catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}
