package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.Contract;
import cn.tinsur.elder.pojo.query.ContractQuery;
import cn.tinsur.elder.pojo.vo.ContractVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 合同表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-28
 */
public interface IContractService extends IService<Contract> {

    /**
     * 分页查询合同列表，返回ContractVO，并在每个ContractVO中填充绑定的老人姓名elderName
     * @param contractQuery
     * @return
     */
    IPage<ContractVO> list(ContractQuery contractQuery);
}