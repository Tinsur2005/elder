package cn.tinsur.elder.listener;

import cn.tinsur.elder.mapper.UserMapper;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.pojo.vo.UserExcelVO;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.springframework.beans.BeanUtils;

public class UserExcelListener extends AnalysisEventListener<UserExcelVO> {

    private UserMapper userMapper;

    public UserExcelListener(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void invoke(UserExcelVO userExcelVO, AnalysisContext context) {
        User user = new User();
        BeanUtils.copyProperties(userExcelVO, user);
        user.setId(null);
        userMapper.insert(user);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
