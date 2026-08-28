package cn.tinsur.elder.service.impl;

import cn.tinsur.elder.pojo.query.Contract;
import cn.tinsur.elder.mapper.ContractMapper;
import cn.tinsur.elder.service.IContractService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 合同表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-28
 */
@Service
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements IContractService {

}
