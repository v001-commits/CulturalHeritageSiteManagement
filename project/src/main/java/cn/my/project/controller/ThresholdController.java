package cn.my.project.controller;

import cn.my.project.common.Result;
import cn.my.project.entity.AlertRule;
import cn.my.project.entity.ThresholdTemplate;
import cn.my.project.service.AlertRuleService;
import cn.my.project.service.ThresholdTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/threshold")
public class ThresholdController {

    @Autowired
    private AlertRuleService alertRuleService;

    @Autowired
    private ThresholdTemplateService templateService;

    @GetMapping("/rules/{areaId}")
    public Result<List<AlertRule>> getRules(@PathVariable Long areaId) {
        return Result.success(alertRuleService.list(new LambdaQueryWrapper<AlertRule>()
                .eq(AlertRule::getAreaId, areaId)));
    }

    @PostMapping("/rule")
    public Result<String> saveRule(@RequestBody AlertRule rule) {
        alertRuleService.saveOrUpdate(rule);
        return Result.success("保存成功");
    }

    @DeleteMapping("/rule/{id}")
    public Result<String> deleteRule(@PathVariable Long id) {
        alertRuleService.removeById(id);
        return Result.success("删除成功");
    }

    @GetMapping("/templates")
    public Result<List<ThresholdTemplate>> getTemplates() {
        return Result.success(templateService.list());
    }
}
