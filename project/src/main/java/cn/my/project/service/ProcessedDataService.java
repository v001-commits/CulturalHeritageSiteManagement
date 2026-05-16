package cn.my.project.service;

import cn.my.project.entity.ProcessedData;
import cn.my.project.mapper.ProcessedDataMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ProcessedDataService extends ServiceImpl<ProcessedDataMapper, ProcessedData> {
}
