package cn.my.project.service;

import cn.my.project.entity.EnvironmentData;
import cn.my.project.mapper.EnvironmentDataMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentDataService extends ServiceImpl<EnvironmentDataMapper, EnvironmentData> {
}
