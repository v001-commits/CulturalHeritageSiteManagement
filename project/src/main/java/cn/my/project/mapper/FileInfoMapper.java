package cn.my.project.mapper;

import cn.my.project.entity.FileInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {

    /**
     * 根据模块查询文件列表
     * @param module 模块标识
     * @return 文件信息列表
     */
    @Select("SELECT * FROM file_info WHERE module = #{module} ORDER BY create_time DESC")
    List<FileInfo> selectByModule(String module);

    /**
     * 根据用户ID查询文件列表
     * @param userId 用户ID
     * @return 文件信息列表
     */
    @Select("SELECT * FROM file_info WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<FileInfo> selectByUserId(Long userId);
}