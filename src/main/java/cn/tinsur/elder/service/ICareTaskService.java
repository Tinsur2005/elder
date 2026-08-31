package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.CareTask;
import cn.tinsur.elder.pojo.query.CareTaskQuery;
import cn.tinsur.elder.pojo.vo.CareTaskVO;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 护理任务与打卡记录表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
public interface ICareTaskService extends IService<CareTask> {

    /**
     * 分页查询护理任务列表，返回带老人姓名、执行护理员姓名的 CareTaskVO
     *
     * @param careTaskQuery 查询条件（老人、状态、计划执行日期范围、分页）
     * @return 分页结果
     */
    IPage<CareTaskVO> list(CareTaskQuery careTaskQuery);

    /**
     * 查询任务详情，返回带老人姓名、执行护理员姓名的 CareTaskVO
     *
     * @param id 任务id
     * @return 带姓名的详情，查不到返回 null
     */
    CareTaskVO getDetail(Long id);

    /**
     * 完成任务：状态置为已完成(1)，记录实际完成时间，并写入执行结果、打卡照片、备注、执行人
     *
     * @param id       任务id
     * @param careTask 需要更新的执行信息（执行结果、照片、备注、执行人）
     * @return 处理结果
     */
    Result complete(Long id, CareTask careTask);

    /**
     * 跳过/取消任务：状态置为已跳过(2)
     *
     * @param id 任务id
     * @return 处理结果
     */
    Result skip(Long id);
}