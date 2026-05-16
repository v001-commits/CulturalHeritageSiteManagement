package cn.my.project.service;

import cn.my.project.entity.OperationLog;
import cn.my.project.mapper.OperationLogMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OperationLogService extends ServiceImpl<OperationLogMapper, OperationLog> {
}
