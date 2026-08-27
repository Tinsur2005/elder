package cn.tinsur.elder.service.impl;

import cn.tinsur.elder.mapper.ElderMapper;
import cn.tinsur.elder.mapper.ElderTagMapper;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.vo.ElderTag;
import cn.tinsur.elder.pojo.entity.Tag;
import cn.tinsur.elder.pojo.query.ElderQuery;
import cn.tinsur.elder.listener.ElderExcelListener;
import cn.tinsur.elder.pojo.vo.ElderExcelVO;
import cn.tinsur.elder.service.IElderService;
import cn.tinsur.elder.util.ExcelUtil;
import cn.tinsur.elder.util.Result;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
    @Autowired
    private ElderTagMapper elderTagMapper;

    /**
     * 获取老人列表（分页）
     * @param elderQuery
     * @return
     */
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

    /**
     * 根据id获取标签列表
     * @param id
     * @return
     */
    @Override
    public Result<List<Tag>> getTagsById(Long id) {
        LambdaQueryWrapper<ElderTag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ElderTag :: getElderId, id);
        List<Long> list =
                elderTagMapper.selectList(lambdaQueryWrapper)
                        .stream()
                        .map(ElderTag :: getTagId)
                        .toList();
        return Result.ok(list);
    }

    /**
     * 根据老人id删除这个老人的所有标签
     * @param id
     */
    @Override
    public void deleteAllTagsById(Long id) {
        LambdaQueryWrapper<ElderTag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ElderTag :: getElderId, id);
        elderTagMapper.delete(lambdaQueryWrapper);
    }

    /**
     * 根据老人id添加标签，传入的第二个参数应该是标签ID组成的Long数组
     * @param id
     * @param tagId
     */
    @Override
    public void addTagById(Long id, Long[] tagId) {
        for (Long tag : tagId) {
            ElderTag elderTag = new ElderTag();
            elderTag.setElderId(id);
            elderTag.setTagId(tag);
            elderTagMapper.insert(elderTag);
        }
    }

    /**
     * 根据老人id更新标签，传入的第二个参数应该是标签ID组成的Long数组
     * 这个方法的实现方法是，先根据id删除elder-tag中间表中有关这个老人的所有数据，再根据id和tagId数组插入新的数据
     * @param id
     * @param tags
     * @return
     */
    @Override
    public Result updateTags(Long id, Long[] tags) {
        deleteAllTagsById(id);
        addTagById(id, tags);
        return Result.ok("更新成功");
    }

    @Override
    public void exportExcel(HttpServletResponse response) {
        List<Elder> list = elderMapper.selectList(null); //写null则查出所有老人
        List<ElderExcelVO> elderExcelVOList = list.stream().map(elder -> {
            ElderExcelVO elderExcelVO = new ElderExcelVO();
            BeanUtils.copyProperties(elder, elderExcelVO);
            return elderExcelVO;
        }).toList();
        ExcelUtil.exportExcel(response, elderExcelVOList, ElderExcelVO.class, "老人信息表");
    }

    @Override
    public void importExcel(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), ElderExcelVO.class, new ElderExcelListener(elderMapper)).sheet().doRead();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
