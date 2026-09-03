/*
 * ============================================================
 *
 *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *      ╚═╝   ╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝
 *
 *  项目名称 : 智慧社区养老系统
 *  源码作者 : Tinsur (tinsur.cn)
 *  作者主页 : https://www.tinsur.cn
 *  联系方式 : me@tinsur.cn
 *  开源协议 : GPL 3.0
 *
 * ============================================================
 */
package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.ExamAppointment;
import cn.tinsur.elder.pojo.entity.ExamAppointmentItem;
import cn.tinsur.elder.pojo.query.ExamAppointmentQuery;
import cn.tinsur.elder.pojo.vo.ExamAppointmentItemVO;
import cn.tinsur.elder.pojo.vo.ExamAppointmentVO;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 老人预约/体检记录表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-02
 */
public interface IExamAppointmentService extends IService<ExamAppointment> {

    /**
     * 分页查询体检预约列表，返回带老人姓名和套餐名称的 ExamAppointmentVO
     * @param examAppointmentQuery 查询条件
     * @return 分页结果
     */
    IPage<ExamAppointmentVO> list(ExamAppointmentQuery examAppointmentQuery);

    /**
     * 新增体检预约
     * 注意：在保存预约时快照套餐价格，并把套餐下的体检项目复制为体检记录明细
     * @param examAppointment
     * @return
     */
    Result addAppointment(ExamAppointment examAppointment);

    /**
     * 修改体检预约：可修改预约日期/时间/备注，换套餐时重新快照价格并重建明细
     *  仅限待体检状态
     * @param id
     * @param examAppointment
     * @return
     */
    Result updateAppointment(Long id, ExamAppointment examAppointment);

    /**
     * 开始体检：将状态从待体检变为体检中
     * @param id
     * @return
     */
    Result startAppointment(Long id);

    /**
     * 取消预约：把状态从待体检或者体检中变为已取消
     * @param id
     * @return
     */
    Result cancelAppointment(Long id);

    /**
     * 获取指定体检记录包含的所有明细
     * @param id
     * @return
     */
    Result<List<ExamAppointmentItemVO>> getAppointmentItemsById(Long id);

    /**
     * 暂存体检结果：先删除该记录所有明细，再插入新数据
     * （仅限体检中状态）
     * @param id
     * @param examAppointmentItems
     * @return
     */
    Result updateAppointmentItems(Long id, List<ExamAppointmentItem> examAppointmentItems);

    /**
     * 完成体检：保存全部明细结果并流转为已完成（数值型结果自动与参考范围比对判定是否异常）
     * @param id
     * @param examAppointmentItems
     * @return
     */
    Result completeAppointment(Long id, List<ExamAppointmentItem> examAppointmentItems);

    /**
     * 根据id删除体检预约，级联删除该记录的全部明细
     * @param id 预约id
     */
    void deleteAppointmentById(Long id);

    /**
     * 批量删除体检预约，同样级联删除各记录的全部明细
     * @param ids 预约id集合
     */
    void deleteAppointmentBatch(List<Long> ids);
}