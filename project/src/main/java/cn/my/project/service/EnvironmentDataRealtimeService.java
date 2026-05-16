package cn.my.project.service;

import cn.my.project.entity.EnvironmentDataRealtime;
import cn.my.project.mapper.EnvironmentDataRealtimeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvironmentDataRealtimeService extends ServiceImpl<EnvironmentDataRealtimeMapper, EnvironmentDataRealtime> {
    
    /**
     * 获取区域最新环境数据
     */
    public EnvironmentDataRealtime getLatestByAreaId(Long areaId) {
        return this.getOne(new LambdaQueryWrapper<EnvironmentDataRealtime>()
                .eq(EnvironmentDataRealtime::getAreaId, areaId)
                .orderByDesc(EnvironmentDataRealtime::getRecordTime)
                .last("LIMIT 1"));
    }
    
    /**
     * 获取设备最新环境数据
     */
    public EnvironmentDataRealtime getLatestByDeviceId(Long deviceId) {
        return this.getOne(new LambdaQueryWrapper<EnvironmentDataRealtime>()
                .eq(EnvironmentDataRealtime::getDeviceId, deviceId)
                .orderByDesc(EnvironmentDataRealtime::getRecordTime)
                .last("LIMIT 1"));
    }
    
    /**
     * 获取所有区域的最新数据
     */
    public List<EnvironmentDataRealtime> getAllLatestData() {
        return this.baseMapper.selectList(new LambdaQueryWrapper<EnvironmentDataRealtime>()
                .orderByDesc(EnvironmentDataRealtime::getRecordTime));
    }
}
