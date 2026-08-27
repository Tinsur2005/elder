package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.pojo.query.UserQuery;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-24
 */
public interface IUserService extends IService<User> {

    IPage<User> list(UserQuery userQuery);

    void exportExcel(HttpServletResponse response);

    void importExcel(MultipartFile file);

    Result add(User user);
}
