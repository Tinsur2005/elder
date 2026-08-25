package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.pojo.query.ElderQuery;
import cn.tinsur.elder.pojo.query.UserQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-24
 */
public interface IElderService extends IService<Elder> {

    IPage<Elder> list(ElderQuery elderrQuery);
}
