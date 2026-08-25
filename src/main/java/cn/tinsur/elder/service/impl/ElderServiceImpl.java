package cn.tinsur.elder.service.impl;

import cn.tinsur.elder.mapper.ElderMapper;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.query.ElderQuery;
import cn.tinsur.elder.service.IElderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-24
 */
@Service
public class ElderServiceImpl extends ServiceImpl<ElderMapper, Elder> implements IElderService {
    @Autowired
    private ElderMapper elderMapper;

    @Override
    public IPage<Elder> list(ElderQuery elderQuery) {
        IPage<Elder> page = new Page<>(elderQuery.getPage(), elderQuery.getLimit());
        LambdaQueryWrapper<Elder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!ObjectUtils.isEmpty(elderQuery.getName()),Elder::getName,elderQuery.getName())
                .between(!ObjectUtils.isEmpty(elderQuery.getBeginCreateTime())
                        && !ObjectUtils.isEmpty(elderQuery.getEndCreateTime()),
                        Elder::getCreateTime, elderQuery.getBeginCreateTime(),
                        elderQuery.getEndCreateTime())
                .orderByDesc(Elder::getCreateTime);
        return elderMapper.selectPage(page, lambdaQueryWrapper);
    }
}
