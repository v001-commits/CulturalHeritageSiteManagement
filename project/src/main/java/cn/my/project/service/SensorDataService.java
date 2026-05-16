package cn.my.project.service;

import cn.my.project.entity.SensorData;
import cn.my.project.mapper.SensorDataMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SensorDataService extends ServiceImpl<SensorDataMapper, SensorData> {
}
