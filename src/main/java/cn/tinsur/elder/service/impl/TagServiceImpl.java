package cn.tinsur.elder.service.impl;

import cn.tinsur.elder.pojo.entity.Tag;
import cn.tinsur.elder.mapper.TagMapper;
import cn.tinsur.elder.service.ITagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 标签表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-26
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {

}
