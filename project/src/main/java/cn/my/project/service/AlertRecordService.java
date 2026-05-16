package cn.my.project.service;

import cn.my.project.entity.AlertRecord;
import cn.my.project.mapper.AlertRecordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AlertRecordService extends ServiceImpl<AlertRecordMapper, AlertRecord> {
}
