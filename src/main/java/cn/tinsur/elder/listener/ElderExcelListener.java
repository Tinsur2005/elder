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