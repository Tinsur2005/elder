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
package cn.tinsur.elder.controller.app;


import cn.tinsur.elder.pojo.entity.ExamPackage;
import cn.tinsur.elder.pojo.entity.ExamPackageItem;
import cn.tinsur.elder.pojo.vo.ExamPackageVO;
import cn.tinsur.elder.service.IExamPackageService;
import cn.tinsur.elder.util.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前台手机端体检套餐 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-02
 */
@RestController
@RequestMapping("/app/exam-packages")
public class AppExamPackageController {
    @Autowired
    private IExamPackageService examPackageService;

    /**
     * 获取全部上架状态的体检套餐列表（供前台预约体检选套餐），并填充每个套餐包含的项目数量
     * GET /exam-packages/list
     */
    @GetMapping("/list")
    public Result<List<ExamPackageVO>> list() {
        List<ExamPackageVO> examPackageVOList = examPackageService.listAll().stream()
                .map(examPackage -> {
                    ExamPackageVO vo = new ExamPackageVO();
                    BeanUtils.copyProperties(examPackage, vo);
                    //填充套餐包含的体检项目数量，供前台展示"包含 N 个体检项目"
                    vo.setItemCount(examPackageService.getPackageItemsById(examPackage.getId()).getData().size());
                    return vo;
                })
                .toList();
        return Result.ok(examPackageVOList);
    }

    /**
     * 获取指定套餐包含的体检项目列表（供前台预约时展示套餐项目，项目名称由前端用体检项目列表映射）
     * GET /exam-packages/getPackageItemsById/1
     */
    @GetMapping("/getPackageItemsById/{id}")
    public Result<List<ExamPackageItem>> getPackageItemsById(@PathVariable Long id) {
        return examPackageService.getPackageItemsById(id);
    }
}
