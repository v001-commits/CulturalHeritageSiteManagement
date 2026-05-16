# 文化遗产地环境监测管理系统

## 项目简介

文化遗产地环境监测管理系统（Cultural Heritage Environmental Monitoring System）是一个用于文化遗产地环境数据实时监测、预警管理和可视化展示的综合管理平台。系统采用前后端分离架构，支持多角色权限管理，为文化遗产保护提供科学的监测手段和数据分析支持。

## 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.6.13 | 后端框架 |
| MyBatis-Plus | 3.5.3.1 | ORM框架 |
| MySQL | 8.0.33 | 数据库 |
| JWT (jjwt) | 0.11.5 | 身份认证 |
| Lombok | - | 简化代码 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4.21 | 前端框架 |
| Vite | 5.2.0 | 构建工具 |
| Vue Router | 5.0.3 | 路由管理 |
| Axios | 1.13.6 | HTTP请求 |
| Leaflet | 1.9.4 | 二维地图 |
| ECharts | 6.0.0 | 数据可视化 |

## 功能模块

### 1. 用户认证与权限管理

- **登录方式**：支持账号密码登录和手机号+图形验证码登录（仅限监测人员）
- **身份认证**：基于JWT Token的无状态认证机制
- **角色管理**：支持四种用户角色
  - `admin` - 系统管理员：拥有所有功能权限
  - `monitor` - 监测人员：负责数据采集和预警处理
  - `researcher` - 研究人员：负责数据分析和统计
  - `manager` - 管理人员：负责区域管理
- **区域权限**：非管理员用户只能访问被授权的区域数据
- **安全机制**：密码强度验证、登录失败锁定（5次错误锁定30分钟）

### 2. 遗产地区域管理

- 区域信息维护（名称、编码、描述）
- 地理位置标注（经纬度、海拔）
- 边界范围定义
- 遗产类型分类
- 风险等级评估
- 三维模型URL配置

### 3. 监测设备管理

- **设备类型支持**：
  - 温湿度传感器 (temperature)
  - 空气质量传感器 (air)
  - 土壤监测仪 (soil)
  - 水质监测仪 (water)
  - 裂缝监测仪 (crack)
  - 风速风向仪 (wind)
  - 监控摄像头 (camera)
  - 无人机 (drone)
- **设备状态监控**：在线、离线、报警、维护中
- **设备信息管理**：名称、类型、状态、风险等级、坐标、海拔
- **设备照片上传**与预览
- **筛选功能**：按区域、状态、类型筛选，支持名称搜索
- **统计概览**：设备总数、在线设备、离线/维护设备、报警设备

### 4. 环境数据监测

- **实时数据采集**：温度、湿度、PM2.5、风速等环境参数
- **趋势数据分析**：支持24小时/7天等时间窗口的趋势图表
- **历史数据查询**：按时间范围查询历史监测数据
- **数据可视化**：ECharts图表展示数据变化趋势

### 5. 预警管理

- **预警阈值设置**：自定义各环境参数的预警阈值
- **预警规则配置**：阈值模板管理
- **预警记录管理**：查看预警历史记录
- **预警处理流程**：预警确认、处理状态更新
- **权限控制**：仅管理员和监测人员可处理预警

### 6. 三维可视化展示

- **二维地图**：Leaflet地图展示遗产地分布
- **区域标注**：在地图上标注遗产地位置和边界
- **设备点位显示**：展示各监测设备的位置
- **实时数据关联**：点击区域/设备查看实时环境数据
- **风险等级可视化**：不同颜色标识不同风险等级

### 7. 数据统计分析

- 多维度数据统计
- 环境参数变化趋势分析
- 按区域、时段、季节筛选数据
- 统计报表生成

### 8. 操作日志

- 用户操作记录
- 登录日志追踪
- 操作类型、内容、结果记录

### 9. 文件管理

- 文件上传与存储
- 文件信息记录
- 支持设备照片、区域模型等文件管理

## 项目结构

```
CulturalHeritageSiteManagement/
├── docs/                           # 项目文档
│   └── 设备管理功能实现文档.md
├── front/                          # 前端项目
│   ├── public/                     # 公共资源
│   ├── src/
│   │   ├── api/                    # API接口封装
│   │   │   ├── auth.js             # 认证接口
│   │   │   ├── user.js             # 用户接口
│   │   │   ├── area.js             # 区域接口
│   │   │   ├── device.js           # 设备接口
│   │   │   ├── file.js             # 文件接口
│   │   │   ├── request.js          # Axios配置
│   │   │   └── visualization.js    # 可视化接口
│   │   ├── components/             # Vue组件
│   │   │   ├── BaiduMapViewer.vue  # 百度地图组件
│   │   │   ├── CesiumViewer.vue    # 三维视图组件
│   │   │   ├── FileUpload.vue      # 文件上传组件
│   │   │   └── Notification.vue    # 通知组件
│   │   ├── views/                  # 页面视图
│   │   │   ├── Login.vue           # 登录页
│   │   │   └ Home.vue              # 主页（含所有功能模块）
│   │   ├── router/                 # 路由配置
│   │   ├── utils/                  # 工具函数
│   │   ├── assets/                 # 静态资源
│   │   ├── App.vue                 # 应用入口
│   │   └── main.js                 # 主入口文件
│   ├── index.html                  # HTML模板
│   ├── vite.config.js              # Vite配置
│   └── package.json                # 依赖配置
├── project/                        # 后端项目
│   ├── src/main/java/cn/my/project/
│   │   ├── controller/             # 控制器层
│   │   │   ├── AuthController.java         # 认证控制器
│   │   │   ├── UserController.java         # 用户管理
│   │   │   ├── AreaController.java         # 区域管理
│   │   │   ├── DeviceController.java       # 设备管理
│   │   │   ├── MonitorController.java      # 监测数据
│   │   │   ├── AlertController.java        # 预警管理
│   │   │   ├── ThresholdController.java    # 阈值管理
│   │   │   ├── StatisticsController.java   # 统计分析
│   │   │   ├── VisualizationController.java # 可视化接口
│   │   │   ├── FileUploadController.java   # 文件上传
│   │   │   ├── OperationLogController.java # 操作日志
│   │   │   ├── SensorDataController.java   # 传感器数据
│   │   │   ├── DataProcessController.java  # 数据处理
│   │   │   └── CaptchaController.java      # 验证码
│   │   ├── entity/                 # 实体类
│   │   │   ├── User.java           # 用户实体
│   │   │   ├── MonitorArea.java    # 遗产地区域
│   │   │   ├── MonitorDevice.java  # 监测设备
│   │   │   ├── SensorData.java     # 传感器数据
│   │   │   ├── AlertRecord.java    # 预警记录
│   │   │   ├── AlertRule.java      # 预警规则
│   │   │   ├── ThresholdTemplate.java # 阈值模板
│   │   │   ├── OperationLog.java   # 操作日志
│   │   │   ├── FileInfo.java       # 文件信息
│   │   │   └── EnvironmentDataRealtime.java # 实时环境数据
│   │   ├── mapper/                 # Mapper接口
│   │   ├── service/                # 服务层
│   │   ├── config/                 # 配置类
│   │   ├── interceptor/            # 拦截器
│   │   │   ├── JwtInterceptor.java # JWT认证拦截器
│   │   │   ├── PermissionInterceptor.java # 权限拦截器
│   │   ├── utils/                  # 工具类
│   │   │   ├── JwtUtil.java        # JWT工具
│   │   │   ├── PermissionUtil.java # 权限工具
│   │   └── common/                 # 公共类
│   │       └── Result.java         # 统一响应封装
│   ├── uploads/                    # 上传文件存储目录
│   └── pom.xml                     # Maven配置
└── README.md                       # 项目说明文档
```

## API接口概览

### 认证接口 (/auth)

| 接口 | 方法 | 说明 |
|------|------|------|
| /auth/login | POST | 账号密码登录 |
| /auth/phone-login | POST | 手机号+图形验证码登录 |
| /auth/check-phone-login | GET | 检查是否可使用手机登录 |
| /auth/validate-password | POST | 验证密码强度 |

### 用户接口 (/user)

| 接口 | 方法 | 权限 | 说明 |
|------|------|------|------|
| /user/list | GET | admin | 获取用户列表 |
| /user/{id} | GET | admin | 获取用户详情 |
| /user | POST | admin | 添加用户 |
| /user/{id} | PUT | admin | 更新用户 |
| /user/{id} | DELETE | admin | 删除用户 |

### 区域接口 (/area)

| 接口 | 方法 | 说明 |
|------|------|------|
| /area/list | GET | 获取区域列表 |
| /area/{id} | GET | 获取区域详情 |
| /area | POST | 添加区域 |
| /area/{id} | PUT | 更新区域 |
| /area/{id} | DELETE | 删除区域 |

### 设备接口 (/device)

| 接口 | 方法 | 权限 | 说明 |
|------|------|------|------|
| /device/list | GET | admin | 获取设备列表 |
| /device/{id} | GET | admin | 获取设备详情 |
| /device | POST | admin | 添加设备 |
| /device/{id} | PUT | admin | 更新设备 |
| /device/{id} | DELETE | admin | 删除设备 |
| /device/upload-photo | POST | admin | 上传设备照片 |

### 监测接口 (/monitor)

| 接口 | 方法 | 说明 |
|------|------|------|
| /monitor/realtime/{areaId} | GET | 获取实时监测数据 |
| /monitor/trend/{areaId} | GET | 获取趋势数据 |

### 预警接口 (/alert)

| 接口 | 方法 | 权限 | 说明 |
|------|------|------|------|
| /alert/records | GET | 全部 | 获取预警记录 |
| /alert/process/{id} | POST | admin/monitor | 处理预警 |
| /alert/add | POST | admin | 添加预警 |

### 可视化接口 (/visualization)

| 接口 | 方法 | 说明 |
|------|------|------|
| /visualization/areas-with-devices | GET | 获取区域及设备信息 |
| /visualization/area/{areaId} | GET | 获取区域详情 |
| /visualization/device/{deviceId} | GET | 获取设备详情 |
| /visualization/devices | GET | 获取所有设备点位 |
| /visualization/realtime-data | GET | 获取实时环境数据 |

## 数据库设计

### 核心数据表

| 表名 | 说明 |
|------|------|
| user | 用户表 |
| monitor_area | 遗产地区域表 |
| monitor_device | 监测设备表 |
| sensor_data | 传感器数据表 |
| environment_data_realtime | 实时环境数据表 |
| alert_record | 预警记录表 |
| alert_rule | 预警规则表 |
| threshold_template | 阈值模板表 |
| operation_log | 操作日志表 |
| file_info | 文件信息表 |
| user_area_permission | 用户区域权限表 |

## 安装与运行

### 环境要求

- JDK 8+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 后端运行

```bash
# 进入后端项目目录
cd project

# 编译项目
mvn clean package -DskipTests

# 运行项目
java -jar target/project_back-0.0.1-SNAPSHOT.jar
```

### 前端运行

```bash
# 进入前端项目目录
cd front

# 安装依赖
npm install

# 开发模式运行
npm run dev

# 生产构建
npm run build
```

### 配置说明

后端配置文件位于 `project/src/main/resources/application.yml`（需自行创建），主要配置项：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/heritage_monitor?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

file:
  upload-path: ./uploads/
```

前端配置位于 `front/vite.config.js`，需配置后端API地址：

```javascript
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
```

## 系统截图

### 登录页面
- 支持账号密码和手机号两种登录方式
- 现代化UI设计，左侧展示系统功能介绍

### 主页面功能
- 左侧导航菜单，根据用户角色动态显示可访问功能
- 二维地图/三维视图切换
- 实时数据监测面板
- 设备管理卡片式布局
- 预警记录列表

## 版本信息

- **版本**: 2.0.0
- **创建日期**: 2026年5月
- **更新日期**: 2026年5月

## 许可证

本项目仅供学习和研究使用。

---

**守护历史，传承未来** 🏛️