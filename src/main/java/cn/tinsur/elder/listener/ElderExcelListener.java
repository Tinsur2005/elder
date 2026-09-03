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
package cn.tinsur.elder.listener;

import cn.tinsur.elder.mapper.ElderMapper;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.vo.ElderExcelVO;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.springframework.beans.BeanUtils;

public class ElderExcelListener extends AnalysisEventListener<ElderExcelVO> {

    private ElderMapper elderMapper;

    public ElderExcelListener(ElderMapper elderMapper) {
        this.elderMapper = elderMapper;
    }

    @Override
    public void invoke(ElderExcelVO elderExcelVO, AnalysisContext context) {
        Elder elder = new Elder();
        BeanUtils.copyProperties(elderExcelVO, elder);
        elder.setId(null);
        elderMapper.insert(elder);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
