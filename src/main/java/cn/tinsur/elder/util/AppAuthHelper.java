package cn.tinsur.elder.util;

import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.service.IFamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 前台手机端（app）鉴权辅助类：
 * 解析当前登录用户身份，并校验老人数据的归属
 * （老人只能访问自己的数据，家属只能访问其绑定老人的数据）
 */
@Component
public class AppAuthHelper {

    @Autowired
    private IFamilyService familyService;

    /**
     * 解析token，返回claims（含 id、name、userType）
     *
     * @param token 前端传来的 JWT（Authorization 请求头）
     * @return claims
     */
    public Map<String, Object> parseToken(String token) {
        return JwtUtil.parseToken(token);
    }

    /**
     * 校验当前登录用户是否有权访问指定老人的数据
     * userType=elder：只能访问自己；userType=family：只能访问绑定的老人
     *
     * @param token   当前登录用户的 JWT
     * @param elderId 要访问的老人id
     * @return 校验通过返回 Result.ok()，不通过返回 Result.error("无权访问该老人的数据")
     */
    public Result checkElderPermission(String token, Long elderId) {
        Map<String, Object> claims = JwtUtil.parseToken(token);
        String userType = (String) claims.get("userType");
        Long currentId = ((Number) claims.get("id")).longValue();
        if ("elder".equals(userType)) {
            //老人只能访问自己的数据
            if (!currentId.equals(elderId)) {
                return Result.error("无权访问该老人的数据");
            }
            return Result.ok();
        }
        if ("family".equals(userType)) {
            //家属只能访问其绑定老人的数据
            List<Elder> elders = (List<Elder>) familyService.getEldersById(currentId).getData();
            boolean bound = elders != null && elders.stream().anyMatch(elder -> elder.getId().equals(elderId));
            if (!bound) {
                return Result.error("无权访问该老人的数据");
            }
            return Result.ok();
        }
        return Result.error("无权访问该老人的数据");
    }
}