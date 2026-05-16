<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-left">
        <h1 class="logo">{{ systemName }}系统</h1>
      </div>
      <div class="header-right">
        <span class="username">欢迎 用户：{{ currentUser.name || currentUser.username }} 角色：{{ getRoleName(currentUser.role) }}</span>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </header>

    <!-- 主体内容区域 -->
    <div class="main-container">
      <!-- 侧边菜单 -->
      <aside class="sidebar">
        <nav class="menu">
          <!-- 地图可视化菜单 - 所有用户可见 -->
          <div class="menu-item" :class="{active: activeMenu === 'map'}" @click="handleMenuClick('map')">
            <span class="menu-icon">🗺️</span>
            <span class="menu-text">地图可视化</span>
            <span class="menu-active" v-if="activeMenu === 'map'"></span>
          </div>

          <!-- 三维可视化菜单 - 所有用户可见 -->
          <div class="menu-item" :class="{active: activeMenu === '3d'}" @click="handleMenuClick('3d')">
            <span class="menu-icon">🌍</span>
            <span class="menu-text">三维可视化</span>
            <span class="menu-active" v-if="activeMenu === '3d'"></span>
          </div>

          <!-- 用户管理菜单 - 仅系统管理员可见 -->
          <div class="menu-item" :class="{active: activeMenu === 'user'}" v-if="isAdmin()" @click="handleMenuClick('user')">
            <span class="menu-icon">👥</span>
            <span class="menu-text">用户管理</span>
            <span class="menu-active" v-if="activeMenu === 'user'"></span>
          </div>

          <!-- 权限管理菜单 - 仅系统管理员可见 -->
          <div class="menu-item" :class="{active: activeMenu === 'permission'}" v-if="isAdmin()" @click="handleMenuClick('permission')">
            <span class="menu-icon">🔐</span>
            <span class="menu-text">权限管理</span>
            <span class="menu-active" v-if="activeMenu === 'permission'"></span>
          </div>

          <!-- 区域管理菜单 - 仅系统管理员可见 -->
          <div class="menu-item" :class="{active: activeMenu === 'area'}" v-if="isAdmin()" @click="handleMenuClick('area')">
            <span class="menu-icon">📍</span>
            <span class="menu-text">区域管理</span>
            <span class="menu-active" v-if="activeMenu === 'area'"></span>
          </div>

          <!-- 设备管理菜单 - 仅系统管理员可见 -->
          <div class="menu-item" :class="{active: activeMenu === 'device'}" v-if="isAdmin()" @click="handleMenuClick('device')">
            <span class="menu-icon">📟</span>
            <span class="menu-text">设备管理</span>
            <span class="menu-active" v-if="activeMenu === 'device'"></span>
          </div>

          <!-- 操作日志菜单 - 仅系统管理员可见 -->
          <div class="menu-item" :class="{active: activeMenu === 'log'}" v-if="isAdmin()" @click="handleMenuClick('log')">
            <span class="menu-icon">📋</span>
            <span class="menu-text">操作日志</span>
            <span class="menu-active" v-if="activeMenu === 'log'"></span>
          </div>

          <!-- 数据采集菜单 - 监测人员和管理员可见 -->
          <div class="menu-item" :class="{active: activeMenu === 'datacollect'}" v-if="isAdmin() || currentUser.role === 'monitor'" @click="handleMenuClick('datacollect')">
            <span class="menu-icon">📊</span>
            <span class="menu-text">数据采集</span>
            <span class="menu-active" v-if="activeMenu === 'datacollect'"></span>
          </div>

          <!-- 数据处理菜单 - 科研人员和管理员可见 -->
          <div class="menu-item" :class="{active: activeMenu === 'dataprocess'}" v-if="isAdmin() || currentUser.role === 'researcher'" @click="handleMenuClick('dataprocess')">
            <span class="menu-icon">⚙️</span>
            <span class="menu-text">数据处理</span>
            <span class="menu-active" v-if="activeMenu === 'dataprocess'"></span>
          </div>

          <!-- 环境监测菜单 - 所有角色可见 -->
          <div class="menu-item" :class="{active: activeMenu === 'monitor'}" @click="handleMenuClick('monitor')">
            <span class="menu-icon">📈</span>
            <span class="menu-text">环境监测</span>
            <span class="menu-active" v-if="activeMenu === 'monitor'"></span>
          </div>

          <!-- 阈值配置菜单 - 管理员可见 -->
          <div class="menu-item" :class="{active: activeMenu === 'threshold'}" v-if="isAdmin()" @click="handleMenuClick('threshold')">
            <span class="menu-icon">⚡</span>
            <span class="menu-text">阈值配置</span>
            <span class="menu-active" v-if="activeMenu === 'threshold'"></span>
          </div>

          <!-- 预警管理菜单 - 所有角色可见 -->
          <div class="menu-item" :class="{active: activeMenu === 'alert'}" @click="handleMenuClick('alert')">
            <span class="menu-icon">🔔</span>
            <span class="menu-text">预警管理</span>
            <span class="menu-active" v-if="activeMenu === 'alert'"></span>
          </div>

          <!-- 统计分析菜单 - 科研人员和管理员可见 -->
          <div class="menu-item" :class="{active: activeMenu === 'statistics'}" v-if="isAdmin() || currentUser.role === 'researcher'" @click="handleMenuClick('statistics')">
            <span class="menu-icon">📊</span>
            <span class="menu-text">统计分析</span>
            <span class="menu-active" v-if="activeMenu === 'statistics'"></span>
          </div>

          <!-- 文件管理菜单 - 所有角色可见 -->
          <div class="menu-item" :class="{active: activeMenu === 'file'}" @click="handleMenuClick('file')">
            <span class="menu-icon">📁</span>
            <span class="menu-text">文件管理</span>
            <span class="menu-active" v-if="activeMenu === 'file'"></span>
          </div>
        </nav>
      </aside>

      <!-- 主要内容区域 -->
      <main class="main-content" :class="{ 'no-scroll': activeMenu === 'map' || activeMenu === '3d' }">
        <div class="content-wrapper">
          <!-- 地图可视化页面 -->
          <div v-show="activeMenu === 'map'" class="map-content">
            <section class="welcome-section">
              <h2>二维地图可视化</h2>
            </section>
            <div class="map-container-full">
              <BaiduMapViewer ref="baiduMapRef" @view-trend="handleViewTrend" />
            </div>
          </div>

          <!-- 三维可视化页面 -->
          <div v-if="activeMenu === '3d'" class="map-content">
            <section class="welcome-section">
              <h2>三维可视化监测</h2>
            </section>
            <div class="map-container-full">
              <CesiumViewer ref="cesiumRef" />
            </div>
          </div>

          <!-- 用户管理页面 -->
          <div v-show="activeMenu === 'user'">
            <section class="welcome-section">
              <h2>用户管理</h2>
            </section>
            
            <section class="user-management">
              <div class="section-header">
                <h3>用户列表</h3>
                <button class="add-user-btn" @click="showAddUserDialog = true">添加用户</button>
              </div>

              <!-- 用户列表 -->
              <div class="user-list-container">
                <table class="user-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>用户名</th>
                      <th>姓名</th>
                      <th>邮箱</th>
                      <th>手机号</th>
                      <th>角色</th>
                      <th>状态</th>
                      <th>操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="user in userList" :key="user.id">
                      <td>{{ user.id }}</td>
                      <td>{{ user.username }}</td>
                      <td>{{ user.name }}</td>
                      <td>{{ user.email || '-' }}</td>
                      <td>{{ user.phone || '-' }}</td>
                      <td>{{ getRoleName(user.role) }}</td>
                      <td>
                        <span class="status-badge" :class="user.enable ? 'enabled' : 'disabled'">
                          {{ user.enable ? '启用' : '禁用' }}
                        </span>
                      </td>
                      <td class="action-buttons">
                        <button class="edit-btn" @click="handleEditUser(user)">编辑</button>
                        <button class="delete-btn" @click="handleDeleteUser(user.id)">删除</button>
                      </td>
                    </tr>
                  </tbody>
                </table>
                <div v-if="userList.length === 0" class="empty-state">
                  暂无用户数据
                </div>
              </div>
            </section>
          </div>
          
          <!-- 权限管理页面 -->
          <div v-show="activeMenu === 'permission'">
            <section class="welcome-section">
              <h2>权限管理</h2>
            </section>

            <section class="permission-management">
              <div class="role-cards">
                <div class="role-card">
                  <div class="role-header">
                    <span class="role-icon">👨‍💼</span>
                    <h3>系统管理员 (admin)</h3>
                  </div>
                  <div class="role-permissions">
                    <p>✓ 全量管控权限</p>
                    <p>✓ 系统全局配置</p>
                    <p>✓ 角色权限分配</p>
                    <p>✓ 数据维护</p>
                  </div>
                  <div class="role-count">用户数：{{ getRoleCount('admin') }}</div>
                </div>

                <div class="role-card">
                  <div class="role-header">
                    <span class="role-icon">🔧</span>
                    <h3>监测人员 (monitor)</h3>
                  </div>
                  <div class="role-permissions">
                    <p>✓ 维护设备</p>
                    <p>✓ 核验数据</p>
                    <p>✓ 查看监测数据</p>
                  </div>
                  <div class="role-count">用户数：{{ getRoleCount('monitor') }}</div>
                </div>

                <div class="role-card">
                  <div class="role-header">
                    <span class="role-icon">🔬</span>
                    <h3>科研人员 (researcher)</h3>
                  </div>
                  <div class="role-permissions">
                    <p>✓ 分析数据</p>
                    <p>✓ 导出报告</p>
                    <p>✓ 查看监测数据</p>
                  </div>
                  <div class="role-count">用户数：{{ getRoleCount('researcher') }}</div>
                </div>

                <div class="role-card">
                  <div class="role-header">
                    <span class="role-icon">🏛️</span>
                    <h3>遗产地管理员 (manager)</h3>
                  </div>
                  <div class="role-permissions">
                    <p>✓ 查看监测数据</p>
                    <p>✓ 接收预警</p>
                    <p>✓ 查看报告</p>
                  </div>
                  <div class="role-count">用户数：{{ getRoleCount('manager') }}</div>
                </div>
              </div>

              <div class="section-header" style="margin-top: 30px;">
                <h3>用户权限分配</h3>
                <button class="add-user-btn" @click="showUserPermissionDialog = true">分配权限</button>
              </div>

              <table class="user-table">
                <thead>
                  <tr>
                    <th>用户名</th>
                    <th>姓名</th>
                    <th>角色</th>
                    <th>已授权区域</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="user in userList.filter(u => u.role !== 'admin')" :key="user.id">
                    <td>{{ user.username }}</td>
                    <td>{{ user.name }}</td>
                    <td>{{ getRoleName(user.role) }}</td>
                    <td>{{ getUserAreaNames(user.id) }}</td>
                    <td>
                      <button class="edit-btn" @click="handleEditUserPermission(user)">编辑权限</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </section>
          </div>

          <!-- 区域管理页面 -->
          <div v-show="activeMenu === 'area'">
            <section class="welcome-section">
              <h2>区域管理</h2>
            </section>

            <section class="area-management">
              <div class="section-header">
                <h3>监测区域列表</h3>
                <button class="add-btn" @click="handleAddArea">添加区域</button>
              </div>

              <div class="area-list">
                <table class="user-table">
                  <thead>
                    <tr>
                      <th>区域编码</th>
                      <th>区域名称</th>
                      <th>描述</th>
                      <th>经纬度</th>
                      <th>操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="area in areaList" :key="area.id">
                      <td>{{ area.areaCode }}</td>
                      <td>{{ area.areaName }}</td>
                      <td>{{ area.description }}</td>
                      <td>{{ area.latitude }}, {{ area.longitude }}</td>
                      <td>
                        <button class="edit-btn" @click="handleEditArea(area)">编辑</button>
                        <button class="edit-btn" @click="handleManagePermission(area)">权限配置</button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </section>

            <!-- 添加/编辑区域弹窗 -->
            <div v-if="showAreaDialog" class="dialog-overlay">
              <div class="dialog">
                <div class="dialog-header">
                  <h3>{{ areaDialogMode === 'add' ? '添加区域' : '编辑区域' }}</h3>
                  <button class="close-btn" @click="showAreaDialog = false">&times;</button>
                </div>
                <div class="dialog-content">
                  <form @submit.prevent="submitAreaForm">
                    <div class="form-row">
                      <div class="form-item">
                        <label>区域编码 *</label>
                        <input v-model="areaForm.areaCode" required placeholder="如：AREA001" />
                      </div>
                      <div class="form-item">
                        <label>区域名称 *</label>
                        <input v-model="areaForm.areaName" required placeholder="如：故宫东区" />
                      </div>
                    </div>
                    <div class="form-row">
                      <div class="form-item">
                        <label>纬度</label>
                        <input v-model="areaForm.latitude" type="number" step="0.000001" placeholder="如：39.916345" />
                      </div>
                      <div class="form-item">
                        <label>经度</label>
                        <input v-model="areaForm.longitude" type="number" step="0.000001" placeholder="如：116.397026" />
                      </div>
                    </div>
                    <div class="form-row">
                      <div class="form-item">
                        <label>描述</label>
                        <input v-model="areaForm.description" placeholder="区域描述（可选）" />
                      </div>
                    </div>
                    <div class="dialog-footer">
                      <button type="button" class="cancel-btn" @click="showAreaDialog = false">取消</button>
                      <button type="submit" class="confirm-btn">确定</button>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>

          <!-- 设备管理页面 -->
          <div v-show="activeMenu === 'device'">
            <section class="welcome-section">
              <h2>设备管理</h2>
            </section>

            <section class="device-management">
              <!-- 统计卡片 -->
              <div class="device-stats">
                <div class="stat-card">
                  <div class="stat-icon total">📟</div>
                  <div class="stat-info">
                    <span class="stat-value">{{ deviceList.length }}</span>
                    <span class="stat-label">设备总数</span>
                  </div>
                </div>
                <div class="stat-card">
                  <div class="stat-icon online">✅</div>
                  <div class="stat-info">
                    <span class="stat-value">{{ deviceList.filter(d => d.status === 'online').length }}</span>
                    <span class="stat-label">在线设备</span>
                  </div>
                </div>
                <div class="stat-card">
                  <div class="stat-icon offline">⭕</div>
                  <div class="stat-info">
                    <span class="stat-value">{{ deviceList.filter(d => d.status === 'offline' || d.status === 'maintenance').length }}</span>
                    <span class="stat-label">离线/维护</span>
                  </div>
                </div>
                <div class="stat-card">
                  <div class="stat-icon alarm">🚨</div>
                  <div class="stat-info">
                    <span class="stat-value">{{ deviceList.filter(d => d.status === 'alarm').length }}</span>
                    <span class="stat-label">报警设备</span>
                  </div>
                </div>
              </div>

              <!-- 搜索和筛选工具栏 -->
              <div class="device-toolbar">
                <div class="search-box">
                  <input v-model="deviceSearchKeyword" type="text" placeholder="搜索设备名称..." @input="filterDevices" />
                  <span class="search-icon">🔍</span>
                </div>
                <div class="filter-group">
                  <select v-model="deviceFilterArea" @change="filterDevices">
                    <option value="">全部区域</option>
                    <option v-for="area in areaList" :key="area.id" :value="area.id">{{ area.areaName }}</option>
                  </select>
                  <select v-model="deviceFilterStatus" @change="filterDevices">
                    <option value="">全部状态</option>
                    <option value="online">在线</option>
                    <option value="offline">离线</option>
                    <option value="alarm">报警</option>
                    <option value="maintenance">维护中</option>
                  </select>
                  <select v-model="deviceFilterType" @change="filterDevices">
                    <option value="">全部类型</option>
                    <option value="temperature">温湿度传感器</option>
                    <option value="air">空气质量传感器</option>
                    <option value="soil">土壤监测仪</option>
                    <option value="water">水质监测仪</option>
                    <option value="crack">裂缝监测仪</option>
                    <option value="wind">风速风向仪</option>
                    <option value="camera">监控摄像头</option>
                    <option value="drone">无人机</option>
                    <option value="other">其他</option>
                  </select>
                </div>
                <button class="add-btn" @click="handleAddDevice">
                  <span class="btn-icon">+</span> 添加设备
                </button>
              </div>

              <!-- 设备卡片列表 -->
              <div class="device-card-list">
                <div v-for="device in filteredDeviceList" :key="device.id" class="device-card">
                  <div class="device-card-header">
                    <div class="device-type-icon" :class="device.deviceType">
                      {{ getDeviceTypeIcon(device.deviceType) }}
                    </div>
                    <div class="device-title">
                      <h4>{{ device.deviceName }}</h4>
                      <span class="device-area">📍 {{ getAreaName(device.areaId) }}</span>
                    </div>
                    <div class="device-status-tag" :class="device.status">
                      {{ getDeviceStatusName(device.status) }}
                    </div>
                  </div>

                  <div class="device-card-body">
                    <div class="device-info-row">
                      <span class="info-label">设备类型</span>
                      <span class="info-value">{{ getDeviceTypeName(device.deviceType) }}</span>
                    </div>
                    <div class="device-info-row">
                      <span class="info-label">风险等级</span>
                      <span class="risk-tag" :class="device.riskLevel">
                        {{ getRiskText(device.riskLevel) }}
                      </span>
                    </div>
                    <div class="device-info-row">
                      <span class="info-label">坐标位置</span>
                      <span class="info-value coord">{{ formatCoordinate(device.longitude, device.latitude) }}</span>
                    </div>
                    <div class="device-info-row">
                      <span class="info-label">海拔高度</span>
                      <span class="info-value">{{ device.altitude ? device.altitude + 'm' : '-' }}</span>
                    </div>
                    <div class="device-info-row">
                      <span class="info-label">最后数据</span>
                      <span class="info-value time">{{ formatDateTime(device.lastDataTime) || '暂无数据' }}</span>
                    </div>
                  </div>

                  <div class="device-card-footer">
                    <div class="device-photo" v-if="device.photoUrl">
                      <img :src="device.photoUrl" @click="previewDevicePhoto(device.photoUrl)" />
                    </div>
                    <div class="device-id">ID: {{ device.id }}</div>
                    <div class="device-actions">
                      <button class="action-btn edit" @click="handleEditDevice(device)" title="编辑">✏️</button>
                      <button class="action-btn delete" @click="handleDeleteDevice(device.id)" title="删除">🗑️</button>
                    </div>
                  </div>
                </div>
              </div>

              <div v-if="filteredDeviceList.length === 0" class="empty-state">
                <div class="empty-icon">📟</div>
                <p>{{ deviceList.length === 0 ? '暂无设备数据' : '没有找到匹配的设备' }}</p>
                <button v-if="deviceList.length > 0" class="reset-btn" @click="resetDeviceFilters">重置筛���</button>
              </div>
            </section>

            <!-- 添加/编辑设备弹窗 -->
            <div v-if="showDeviceDialog" class="dialog-overlay">
              <div class="dialog" style="max-width: 700px;">
                <div class="dialog-header">
                  <h3>{{ deviceDialogMode === 'add' ? '添加设备' : '编辑设备' }}</h3>
                  <button class="close-btn" @click="showDeviceDialog = false">&times;</button>
                </div>
                <div class="dialog-content">
                  <form @submit.prevent="submitDeviceForm">
                    <div class="form-row">
                      <div class="form-item">
                        <label>所属区域 *</label>
                        <select v-model="deviceForm.areaId" required>
                          <option value="">请选择区域</option>
                          <option v-for="area in areaList" :key="area.id" :value="area.id">{{ area.areaName }}</option>
                        </select>
                      </div>
                      <div class="form-item">
                        <label>设备名称 *</label>
                        <input v-model="deviceForm.deviceName" required placeholder="请输入设���名称" />
                      </div>
                    </div>
                    <div class="form-row">
                      <div class="form-item">
                        <label>设备类型 *</label>
                        <select v-model="deviceForm.deviceType" required>
                          <option value="">请选择类型</option>
                          <option value="temperature">温湿度传感器</option>
                          <option value="air">空气质量传感器</option>
                          <option value="soil">土壤监测仪</option>
                          <option value="water">水质监测仪</option>
                          <option value="crack">裂缝监测仪</option>
                          <option value="wind">风速风向仪</option>
                          <option value="camera">监控摄像头</option>
                          <option value="drone">无人机</option>
                          <option value="other">其他</option>
                        </select>
                      </div>
                      <div class="form-item">
                        <label>状态 *</label>
                        <select v-model="deviceForm.status" required>
                          <option value="online">在线</option>
                          <option value="offline">离线</option>
                          <option value="alarm">报警</option>
                          <option value="maintenance">维护中</option>
                        </select>
                      </div>
                    </div>
                    <div class="form-row">
                      <div class="form-item">
                        <label>风险等级</label>
                        <select v-model="deviceForm.riskLevel">
                          <option value="safe">安全</option>
                          <option value="low">低风险</option>
                          <option value="medium">中风险</option>
                          <option value="high">高风险</option>
                        </select>
                      </div>
                      <div class="form-item">
                        <label>经度</label>
                        <input v-model="deviceForm.longitude" type="number" step="0.000001" placeholder="如：116.397026" />
                      </div>
                    </div>
                    <div class="form-row">
                      <div class="form-item">
                        <label>纬度</label>
                        <input v-model="deviceForm.latitude" type="number" step="0.000001" placeholder="如：39.916345" />
                      </div>
                      <div class="form-item">
                        <label>海拔(m)</label>
                        <input v-model="deviceForm.altitude" type="number" step="0.01" placeholder="如：45.5" />
                      </div>
                    </div>
                    <div class="form-row">
                      <div class="form-item" style="flex: 1;">
                        <label>设备照片</label>
                        <div class="device-photo-upload">
                          <div v-if="deviceForm.photoUrl" class="device-photo-preview">
                            <img :src="deviceForm.photoUrl" />
                            <button type="button" class="photo-remove-btn" @click="deviceForm.photoUrl = ''">移除</button>
                          </div>
                          <div v-else class="photo-upload-area" @click="triggerDevicePhotoUpload">
                            <span>+ 上传照片</span>
                          </div>
                          <input type="file" ref="devicePhotoInput" @change="handleDevicePhotoUpload" accept="image/*" style="display: none;" />
                        </div>
                      </div>
                    </div>
                    <div class="dialog-footer">
                      <button type="button" class="cancel-btn" @click="showDeviceDialog = false">取消</button>
                      <button type="submit" class="confirm-btn">确定</button>
                    </div>
                  </form>
                </div>
              </div>
            </div>

            <!-- 设备照片预览弹窗 -->
            <div v-if="showDevicePhotoPreview" class="dialog-overlay" @click="showDevicePhotoPreview = false">
              <div class="dialog" style="max-width: 800px;" @click.stop>
                <div class="dialog-header">
                  <h3>照片预览</h3>
                  <button class="close-btn" @click="showDevicePhotoPreview = false">&times;</button>
                </div>
                <div class="dialog-content" style="text-align: center;">
                  <img :src="devicePhotoPreviewUrl" style="max-width: 100%; max-height: 500px; object-fit: contain;" />
                </div>
              </div>
            </div>
          </div>

          <!-- 操作日志页面 -->
          <div v-show="activeMenu === 'log'">
            <section class="welcome-section">
              <h2>操作日志</h2>
            </section>

            <section class="log-management">
              <div class="section-header">
                <h3>操作审计记录</h3>
                <select v-model="logFilter" @change="fetchLogList">
                  <option value="">全部类型</option>
                  <option value="参数调整">参数调整</option>
                  <option value="预警推送">预警推送</option>
                  <option value="报告导出">报告导出</option>
                  <option value="设备维护">设备维护</option>
                  <option value="数据核验">数据核验</option>
                </select>
              </div>

              <table class="user-table">
                <thead>
                  <tr>
                    <th>操作时间</th>
                    <th>操作人</th>
                    <th>操作类型</th>
                    <th>操作内容</th>
                    <th>操作结果</th>
                    <th>IP地址</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="log in logList" :key="log.id">
                    <td>{{ formatDate(log.createTime) }}</td>
                    <td>{{ log.username }}</td>
                    <td>{{ log.operationType }}</td>
                    <td>{{ log.operationContent }}</td>
                    <td>
                      <span class="status-badge" :class="log.operationResult === 'success' ? 'enabled' : 'disabled'">
                        {{ log.operationResult === 'success' ? '成功' : '失败' }}
                      </span>
                    </td>
                    <td>{{ log.ipAddress }}</td>
                  </tr>
                </tbody>
              </table>
            </section>
          </div>

          <!-- 数据采集页面 -->
          <div v-show="activeMenu === 'datacollect'">
            <section class="welcome-section">
              <h2>多源环境数据采集</h2>
            </section>

            <section class="data-collect-section">
              <div class="collect-tabs">
                <button :class="['tab-btn', {active: collectTab === 'manual'}]" @click="collectTab = 'manual'">人工录入</button>
                <button :class="['tab-btn', {active: collectTab === 'import'}]" @click="collectTab = 'import'">批量导入</button>
                <button :class="['tab-btn', {active: collectTab === 'list'}]" @click="collectTab = 'list'">数据列表</button>
              </div>

              <!-- 人工录入 -->
              <div v-if="collectTab === 'manual'" class="collect-form">
                <form @submit.prevent="submitSensorData">
                  <div class="form-row">
                    <div class="form-item">
                      <label>监测区域 *</label>
                      <select v-model="sensorForm.areaId" required>
                        <option value="">请选择区域</option>
                        <option v-for="area in authorizedAreaList" :key="area.id" :value="area.id">{{ area.areaName }}</option>
                      </select>
                    </div>
                    <div class="form-item">
                      <label>数据来源 *</label>
                      <select v-model="sensorForm.dataSource" required>
                        <option value="manual">人工录入</option>
                        <option value="sensor">传感器</option>
                        <option value="drone">无人机</option>
                        <option value="api">接口对接</option>
                      </select>
                    </div>
                  </div>
                  <div class="form-row">
                    <div class="form-item">
                      <label>温度(℃)</label>
                      <input type="number" step="0.01" v-model="sensorForm.temperature" placeholder="温度">
                    </div>
                    <div class="form-item">
                      <label>湿度(%)</label>
                      <input type="number" step="0.01" v-model="sensorForm.humidity" placeholder="湿度">
                    </div>
                    <div class="form-item">
                      <label>降水量(mm)</label>
                      <input type="number" step="0.01" v-model="sensorForm.precipitation" placeholder="降水量">
                    </div>
                  </div>
                  <div class="form-row">
                    <div class="form-item">
                      <label>风速(m/s)</label>
                      <input type="number" step="0.01" v-model="sensorForm.windSpeed" placeholder="风速">
                    </div>
                    <div class="form-item">
                      <label>风向</label>
                      <select v-model="sensorForm.windDirection">
                        <option value="">请选择</option>
                        <option value="东风">东风</option>
                        <option value="南风">南风</option>
                        <option value="西风">西风</option>
                        <option value="北风">北风</option>
                        <option value="东北">东北</option>
                        <option value="东南">东南</option>
                        <option value="西北">西北</option>
                        <option value="西南">西南</option>
                      </select>
                    </div>
                    <div class="form-item">
                      <label>光照强度(lux)</label>
                      <input type="number" step="0.01" v-model="sensorForm.lightIntensity" placeholder="光照强度">
                    </div>
                  </div>
                  <div class="form-row">
                    <div class="form-item">
                      <label>PM2.5(μg/m³)</label>
                      <input type="number" step="0.01" v-model="sensorForm.pm25" placeholder="PM2.5">
                    </div>
                    <div class="form-item">
                      <label>SO₂(μg/m³)</label>
                      <input type="number" step="0.01" v-model="sensorForm.so2" placeholder="SO₂">
                    </div>
                    <div class="form-item">
                      <label>NO₂(μg/m³)</label>
                      <input type="number" step="0.01" v-model="sensorForm.no2" placeholder="NO₂">
                    </div>
                  </div>
                  <div class="form-row">
                    <div class="form-item">
                      <label>土壤墒情(%)</label>
                      <input type="number" step="0.01" v-model="sensorForm.soilMoisture" placeholder="土壤墒情">
                    </div>
                    <div class="form-item">
                      <label>土壤pH值</label>
                      <input type="number" step="0.01" v-model="sensorForm.soilPh" placeholder="土壤pH值">
                    </div>
                    <div class="form-item">
                      <label>水质等级</label>
                      <select v-model="sensorForm.waterQuality">
                        <option value="">请选择</option>
                        <option value="优">优</option>
                        <option value="良">良</option>
                        <option value="中">中</option>
                        <option value="差">差</option>
                      </select>
                    </div>
                  </div>
                  <div class="form-row">
                    <div class="form-item">
                      <label>裂缝宽度(mm)</label>
                      <input type="number" step="0.01" v-model="sensorForm.crackWidth" placeholder="裂缝宽度">
                    </div>
                    <div class="form-item">
                      <label>风化程度</label>
                      <select v-model="sensorForm.weatheringDegree">
                        <option value="">请选择</option>
                        <option value="无">无</option>
                        <option value="轻微">轻微</option>
                        <option value="中度">中度</option>
                        <option value="严重">严重</option>
                      </select>
                    </div>
                    <div class="form-item">
                      <label>记录时间 *</label>
                      <input type="datetime-local" v-model="sensorForm.recordTime" required>
                    </div>
                  </div>
                  <div class="form-actions">
                    <button type="submit" class="submit-btn">提交数据</button>
                    <button type="button" class="reset-btn" @click="resetSensorForm">重置</button>
                  </div>
                </form>
              </div>

              <!-- 批量导入 -->
              <div v-if="collectTab === 'import'" class="import-section">
                <div class="import-tips">
                  <h4>导入说明</h4>
                  <p>1. 支持Excel(.xlsx, .xls)和CSV(.csv)格式文件</p>
                  <p>2. 文件首行必须为列名，支持的列包括：区域编码、温度、湿度、降水量、风速、风向、光照强度、PM2.5、SO₂、NO₂、土壤墒情、土壤pH、水质等级、裂缝宽度、风化程度、记录时间</p>
                  <p>3. 区域编码必填，其他字段选填</p>
                </div>
                <div class="import-upload">
                  <input type="file" ref="importFile" @change="handleImportFile" accept=".xlsx,.xls,.csv">
                  <button @click="$refs.importFile.click()" class="upload-btn">选择文件</button>
                  <span v-if="importFileName">{{ importFileName }}</span>
                  <button v-if="importFileName" @click="uploadImportFile" class="confirm-btn">开始导入</button>
                </div>
              </div>

              <!-- 数据列表 -->
              <div v-if="collectTab === 'list'" class="data-list">
                <div class="list-filter">
                  <select v-model="filterAreaId" @change="fetchSensorData">
                    <option v-for="area in authorizedAreaList" :key="area.id" :value="area.id">{{ area.areaName }}</option>
                  </select>
                  <select v-model="filterSource" @change="fetchSensorData">
                    <option value="">全部来源</option>
                    <option value="sensor">传感器</option>
                    <option value="drone">无人机</option>
                    <option value="manual">人工录入</option>
                    <option value="api">接口对接</option>
                    <option value="import">历史导入</option>
                  </select>
                </div>
                <table class="data-table">
                  <thead>
                    <tr>
                      <th>区域</th>
                      <th>来源</th>
                      <th>温度</th>
                      <th>湿度</th>
                      <th>PM2.5</th>
                      <th>风速</th>
                      <th>记录时间</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="item in sensorDataList" :key="item.id">
                      <td>{{ getAreaName(item.areaId) }}</td>
                      <td>{{ getSourceName(item.dataSource) }}</td>
                      <td>{{ item.temperature || '-' }}</td>
                      <td>{{ item.humidity || '-' }}</td>
                      <td>{{ item.pm25 || '-' }}</td>
                      <td>{{ item.windSpeed || '-' }}</td>
                      <td>{{ formatDateTime(item.recordTime) }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </section>
          </div>

          <!-- 数据处理页面 -->
          <div v-show="activeMenu === 'dataprocess'">
            <section class="welcome-section">
              <h2>数据处理与空间关联</h2>
            </section>

            <section class="data-process-section">
              <div class="process-tabs">
                <button :class="['tab-btn', {active: processTab === 'pending'}]" @click="processTab = 'pending'">待处理数据</button>
                <button :class="['tab-btn', {active: processTab === 'processed'}]" @click="processTab = 'processed'">已处理数据</button>
              </div>

              <!-- 待处理数据 -->
              <div v-if="processTab === 'pending'" class="pending-data">
                <table class="data-table">
                  <thead>
                    <tr>
                      <th>区域</th>
                      <th>来源</th>
                      <th>温度(℃)</th>
                      <th>湿度(%)</th>
                      <th>PM2.5</th>
                      <th>记录时间</th>
                      <th>操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="item in sensorDataList" :key="item.id">
                      <td>{{ getAreaName(item.areaId) }}</td>
                      <td>{{ getSourceName(item.dataSource) }}</td>
                      <td>{{ item.temperature || '-' }}</td>
                      <td>{{ item.humidity || '-' }}</td>
                      <td>{{ item.pm25 || '-' }}</td>
                      <td>{{ formatDateTime(item.recordTime) }}</td>
                      <td><button v-if="canProcessData()" class="process-btn" @click="processData(item.id)">处理</button></td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <!-- 已处理数据 -->
              <div v-if="processTab === 'processed'" class="processed-data">
                <div class="filter-bar">
                  <select v-model="processFilterAreaId" @change="fetchProcessedData">
                    <option v-for="area in authorizedAreaList" :key="area.id" :value="area.id">{{ area.areaName }}</option>
                  </select>
                </div>
                <table class="data-table">
                  <thead>
                    <tr>
                      <th>区域</th>
                      <th>来源</th>
                      <th>温度(℃)</th>
                      <th>湿度(%)</th>
                      <th>PM2.5</th>
                      <th>状态</th>
                      <th>异常原因</th>
                      <th>版本</th>
                      <th>处理时间</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="item in processedDataList" :key="item.id" :class="{anomaly: item.isAnomaly}">
                      <td>{{ getAreaName(item.areaId) }}</td>
                      <td>{{ getSourceName(item.dataSource) }}</td>
                      <td>{{ item.temperature || '-' }}</td>
                      <td>{{ item.humidity || '-' }}</td>
                      <td>{{ item.pm25 || '-' }}</td>
                      <td><span :class="['status-badge', item.isAnomaly ? 'anomaly' : 'normal']">{{ item.isAnomaly ? '异常' : '正常' }}</span></td>
                      <td>{{ item.anomalyReason || '-' }}</td>
                      <td>v{{ item.version }}</td>
                      <td>{{ formatDateTime(item.processTime) }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </section>
          </div>

          <!-- 环境监测页面 -->
          <div v-show="activeMenu === 'monitor'">
            <section class="welcome-section">
              <h2>环境监测与风险预警</h2>
            </section>

            <section class="monitor-section">
              <div class="area-selector">
                <label>选择监测区域：</label>
                <select v-model="selectedMonitorArea" @change="loadMonitorData">
                  <option v-for="area in authorizedAreaList" :key="area.id" :value="area.id">{{ area.areaName }}</option>
                </select>
              </div>

              <!-- 实时监测仪表盘（ECharts仪表盘图） -->
              <div class="gauge-dashboard">
                <div id="gauge-temp" class="gauge-item"></div>
                <div id="gauge-hum" class="gauge-item"></div>
                <div id="gauge-pm25" class="gauge-item"></div>
                <div id="gauge-wind" class="gauge-item"></div>
              </div>

              <!-- 趋势图表 -->
              <div class="chart-section">
                <div class="chart-header">
                  <h3>环境因子变化趋势</h3>
                  <div class="chart-controls">
                    <button :class="['time-btn', {active: trendHours === 24}]" @click="changeTrendHours(24)">24小时</button>
                    <button :class="['time-btn', {active: trendHours === 168}]" @click="changeTrendHours(168)">7天</button>
                  </div>
                </div>
                <div id="trend-chart" style="width: 100%; height: 400px;"></div>
              </div>

              <!-- 设备状态 -->
              <div class="device-status">
                <h3>监测设备状态</h3>
                <div class="device-list">
                  <div v-for="device in areaDevices" :key="device.id" :class="['device-item', device.status]">
                    <div class="device-name">{{ device.deviceName }}</div>
                    <div class="device-type">{{ device.deviceType }}</div>
                    <div :class="['device-badge', device.status]">{{ device.status === 'online' ? '在线' : '离线' }}</div>
                  </div>
                </div>
              </div>
            </section>
          </div>

          <!-- 阈值配置页面 -->
          <div v-show="activeMenu === 'threshold'">
            <section class="welcome-section">
              <h2>阈值适配与自定义设置</h2>
            </section>

            <section class="threshold-section">
              <div class="threshold-header">
                <select v-model="selectedThresholdArea" @change="loadThresholdRules">
                  <option v-for="area in authorizedAreaList" :key="area.id" :value="area.id">{{ area.areaName }}</option>
                </select>
                <button class="add-btn" @click="showAddRuleDialog = true">+ 添加规则</button>
              </div>

              <!-- 阈值模板 -->
              <div class="template-section">
                <h3>常用遗产类型阈值模板</h3>
                <div class="template-grid">
                  <div class="template-card" @click="applyTemplate('ancient_building')">
                    <div class="template-icon">🏛️</div>
                    <div class="template-name">木质古建筑</div>
                    <div class="template-desc">怕潮湿，温湿度严格控制</div>
                  </div>
                  <div class="template-card" @click="applyTemplate('cave')">
                    <div class="template-icon">⛰️</div>
                    <div class="template-name">石窟寺</div>
                    <div class="template-desc">怕酸雨，SO₂控制严格</div>
                  </div>
                  <div class="template-card" @click="applyTemplate('site')">
                    <div class="template-icon">🏺</div>
                    <div class="template-name">遗址</div>
                    <div class="template-desc">温湿度范围较宽</div>
                  </div>
                </div>
              </div>

              <!-- 当前规则列表 -->
              <div class="rules-section">
                <h3>当前阈值规则</h3>
                <table class="data-table">
                  <thead>
                    <tr>
                      <th>因子类型</th>
                      <th>最小阈值</th>
                      <th>最大阈值</th>
                      <th>告警级别</th>
                      <th>状态</th>
                      <th>操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="rule in thresholdRules" :key="rule.id">
                      <td>{{ getFactorName(rule.factorType) }}</td>
                      <td>{{ rule.thresholdMin || '-' }}</td>
                      <td>{{ rule.thresholdMax || '-' }}</td>
                      <td><span :class="['level-badge', rule.alertLevel]">{{ getLevelName(rule.alertLevel) }}</span></td>
                      <td><span :class="['status-badge', rule.enable ? 'normal' : 'anomaly']">{{ rule.enable ? '启用' : '禁用' }}</span></td>
                      <td>
                        <button class="edit-btn" @click="editRule(rule)">编辑</button>
                        <button class="delete-btn" @click="deleteRule(rule.id)">删除</button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </section>
          </div>

          <!-- 添加/编辑规则对话框 -->
          <div v-if="showAddRuleDialog" class="dialog-overlay">
            <div class="dialog">
              <div class="dialog-header">
                <h3>{{ ruleForm.id ? '编辑规则' : '添加规则' }}</h3>
                <button class="close-btn" @click="closeRuleDialog">&times;</button>
              </div>
              <div class="dialog-content">
                <form @submit.prevent="saveRule">
                  <div class="form-item">
                    <label>因子类型</label>
                    <select v-model="ruleForm.factorType" required>
                      <option value="temperature">温度</option>
                      <option value="humidity">湿度</option>
                      <option value="pm25">PM2.5</option>
                      <option value="so2">SO₂</option>
                    </select>
                  </div>
                  <div class="form-row">
                    <div class="form-item">
                      <label>最小阈值</label>
                      <input type="number" step="0.01" v-model="ruleForm.thresholdMin">
                    </div>
                    <div class="form-item">
                      <label>最大阈值</label>
                      <input type="number" step="0.01" v-model="ruleForm.thresholdMax">
                    </div>
                  </div>
                  <div class="form-item">
                    <label>告警级别</label>
                    <select v-model="ruleForm.alertLevel" required>
                      <option value="low">低</option>
                      <option value="medium">中</option>
                      <option value="high">高</option>
                    </select>
                  </div>
                  <div class="dialog-footer">
                    <button type="button" class="cancel-btn" @click="closeRuleDialog">取消</button>
                    <button type="submit" class="confirm-btn">保存</button>
                  </div>
                </form>
              </div>
            </div>
          </div>

          <!-- 预警管理页面 -->
          <div v-show="activeMenu === 'alert'">
            <section class="welcome-section">
              <h2>风险预警与推送</h2>
            </section>

            <section class="alert-section">
              <div class="alert-filter">
                <button :class="['filter-btn', {active: alertStatus === ''}]" @click="alertStatus = ''; loadAlertRecords()">全部</button>
                <button :class="['filter-btn', {active: alertStatus === 'pending'}]" @click="alertStatus = 'pending'; loadAlertRecords()">待处理</button>
                <button :class="['filter-btn', {active: alertStatus === 'processed'}]" @click="alertStatus = 'processed'; loadAlertRecords()">已处理</button>
                <button v-if="isAdmin()" class="add-btn" @click="showAddAlertDialog = true">+ 添加预警</button>
              </div>

              <div class="alert-list">
                <div v-for="alert in alertRecords" :key="alert.id" :class="['alert-card', alert.alertLevel]">
                  <div class="alert-header">
                    <span :class="['alert-level', alert.alertLevel]">{{ getLevelName(alert.alertLevel) }}</span>
                    <span class="alert-time">{{ formatDateTime(alert.createTime) }}</span>
                  </div>
                  <div class="alert-body">
                    <div class="alert-area">📍 {{ getAreaName(alert.areaId) }}</div>
                    <div class="alert-message">⚠️ {{ alert.alertMessage }}</div>
                    <div class="alert-factor">因子：{{ getFactorName(alert.factorType) }} = {{ alert.factorValue }}</div>
                    <div v-if="alert.suggestion" class="alert-suggestion">
                      <strong>💡 保护建议：</strong>{{ alert.suggestion }}
                    </div>
                  </div>
                  <div class="alert-footer">
                    <span :class="['alert-status', alert.status]">{{ alert.status === 'pending' ? '待处理' : '已处理' }}</span>
                    <button v-if="alert.status === 'pending' && canHandleAlert()" class="process-btn" @click="processAlert(alert.id)">标记已处理</button>
                  </div>
                </div>
              </div>
            </section>
          </div>

          <!-- 添加预警对话框 -->
          <div v-if="showAddAlertDialog" class="dialog-overlay">
            <div class="dialog">
              <div class="dialog-header">
                <h3>添加预警信息</h3>
                <button class="close-btn" @click="closeAddAlertDialog">&times;</button>
              </div>
              <div class="dialog-content">
                <form @submit.prevent="saveAlert">
                  <div class="form-item">
                    <label>区域 *</label>
                    <select v-model="alertForm.areaId" required>
                      <option v-for="area in authorizedAreaList" :key="area.id" :value="area.id">{{ area.areaName }}</option>
                    </select>
                  </div>
                  <div class="form-row">
                    <div class="form-item">
                      <label>因子类型 *</label>
                      <select v-model="alertForm.factorType" required>
                        <option value="temperature">温度</option>
                        <option value="humidity">湿度</option>
                        <option value="pm25">PM2.5</option>
                        <option value="so2">SO₂</option>
                      </select>
                    </div>
                    <div class="form-item">
                      <label>因子值 *</label>
                      <input type="number" step="0.01" v-model="alertForm.factorValue" required>
                    </div>
                  </div>
                  <div class="form-item">
                    <label>告警级别 *</label>
                    <select v-model="alertForm.alertLevel" required>
                      <option value="low">低</option>
                      <option value="medium">中</option>
                      <option value="high">高</option>
                    </select>
                  </div>
                  <div class="form-item">
                    <label>告警信息 *</label>
                    <input type="text" v-model="alertForm.alertMessage" required>
                  </div>
                  <div class="form-item">
                    <label>保护建议</label>
                    <textarea v-model="alertForm.suggestion" rows="3"></textarea>
                  </div>
                  <div class="dialog-footer">
                    <button type="button" class="cancel-btn" @click="closeAddAlertDialog">取消</button>
                    <button type="submit" class="confirm-btn">保存</button>
                  </div>
                </form>
              </div>
            </div>
          </div>

          <!-- 统计分析页面 -->
          <div v-show="activeMenu === 'statistics'">
            <section class="welcome-section">
              <h2>数据统计分析与报告</h2>
            </section>

            <section class="statistics-section">
              <div class="stats-filter">
                <select v-model="statsAreaId" @change="loadStatistics">
                  <option v-for="area in authorizedAreaList" :key="area.id" :value="area.id">{{ area.areaName }}</option>
                </select>
                <select v-model="statsPeriod" @change="loadStatistics">
                  <option value="month">月度</option>
                  <option value="quarter">季度</option>
                  <option value="year">年度</option>
                </select>
              </div>

              <!-- 环境因子趋势图 -->
              <div class="chart-card">
                <h3>环境因子变化趋势</h3>
                <div id="factor-trend-chart" style="width: 100%; height: 400px;"></div>
              </div>

              <!-- 预警次数统计 -->
              <div class="chart-card">
                <h3>风险预警次数分布</h3>
                <div id="alert-stats-chart" style="width: 100%; height: 350px;"></div>
              </div>

              <!-- 区域对比 -->
              <div class="chart-card">
                <h3>不同区域监测数据对比</h3>
                <div id="area-compare-chart" style="width: 100%; height: 400px;"></div>
              </div>

              <!-- 报告生成 -->
              <div class="report-section">
                <h3>监测报告生成</h3>
                <div class="report-form">
                  <select v-model="reportType">
                    <option value="month">月度报告</option>
                    <option value="year">年度报告</option>
                  </select>
                  <select v-model="reportFormat">
                    <option value="pdf">PDF格式</option>
                    <option value="excel">Excel格式</option>
                  </select>
                  <button class="export-btn" @click="generateReport">生成报告</button>
                </div>
              </div>

              <!-- 历史数据查询 -->
              <div class="history-section">
                <h3>历史数据追溯与查询</h3>
                <div class="history-filter">
                  <input type="date" v-model="historyStartDate" placeholder="开始日期">
                  <input type="date" v-model="historyEndDate" placeholder="结束日期">
                  <select v-model="historyAreaId">
                    <option v-for="area in authorizedAreaList" :key="area.id" :value="area.id">{{ area.areaName }}</option>
                  </select>
                  <select v-model="historyFactorType">
                    <option value="">全部因子</option>
                    <option value="temperature">温度</option>
                    <option value="humidity">湿度</option>
                    <option value="pm25">PM2.5</option>
                  </select>
                  <button class="search-btn" @click="searchHistory">查询</button>
                </div>
                <div class="chart-card">
                  <div id="history-chart" style="width: 100%; height: 350px;"></div>
                </div>
              </div>
            </section>
          </div>

          <!-- 文件管理页面 -->
          <div v-show="activeMenu === 'file'">
            <section class="welcome-section">
              <h2>文件管理</h2>
            </section>
            
            <section class="file-management">
              <div class="section-header">
                <h3>文件列表</h3>
                <div class="file-actions">
                  <button class="refresh-btn" @click="fetchFileList">刷新列表</button>
                </div>
              </div>

              <!-- 文件上传组件 - 仅管理员和监测人员可上传 -->
              <div v-if="isAdmin() || isMonitor()" class="file-upload-section">
                <FileUpload
                  :multiple="true"
                  @upload-success="handleUploadSuccess"
                  @upload-error="handleUploadError"
                />
              </div>

              <!-- 用户筛选 - 仅管理员可见 -->
              <div v-if="isAdmin()" class="user-filter-section">
                <div class="filter-controls">
                  <label>按用户筛选：</label>
                  <select v-model="selectedUserId" @change="handleUserFilterChange">
                    <option value="">全部用户</option>
                    <option v-for="user in userList" :key="user.id" :value="user.id">
                      {{ user.name }} ({{ user.username }})
                    </option>
                  </select>
                  <button class="reset-filter-btn" @click="resetUserFilter">重置筛选</button>
                </div>
              </div>

              <!-- 文件列表 -->
              <div class="file-list-container">
                <div class="file-grid">
                  <div v-for="file in fileList" :key="file.id" class="file-item">
                    <div class="file-icon">📄</div>
                    <div class="file-info">
                      <div class="file-name">{{ file.originalName }}</div>
                      <div class="file-meta">
                        <span>{{ formatFileSize(file.fileSize) }}</span>
                        <span>{{ formatDate(file.createTime) }}</span>
                        <span v-if="file.userName">上传者：{{ file.userName }}</span>
                      </div>
                    </div>
                    <div class="file-operations">
                      <button class="preview-btn" @click="handlePreview(file)">预览</button>
                      <button class="download-btn" @click="handleDownload(file)">下载</button>
                      <button v-if="isAdmin()" class="delete-btn" @click="handleDeleteFile(file.id)">删除</button>
                    </div>
                  </div>
                </div>
                <div v-if="fileList.length === 0" class="empty-state">
                  暂无文件数据
                </div>
              </div>
            </section>
          </div>
        </div>
      </main>
    </div>

    <!-- 添加/编辑用户对话框 -->
    <div v-if="showAddUserDialog || showEditUserDialog" class="dialog-overlay">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ showAddUserDialog ? '添加用户' : '编辑用户' }}</h3>
          <button class="close-btn" @click="closeDialog">&times;</button>
        </div>
        <div class="dialog-content">
          <form @submit.prevent="handleSubmitUser">
            <div class="form-row">
              <div class="form-item">
                <label>用户名</label>
                <input type="text" v-model="userForm.username" placeholder="请输入用户名" required />
              </div>
              <div class="form-item">
                <label>姓名</label>
                <input type="text" v-model="userForm.name" placeholder="请输入姓名" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-item">
                <label>邮箱</label>
                <input type="email" v-model="userForm.email" placeholder="请输入邮箱" />
              </div>
              <div class="form-item">
                <label>手机号</label>
                <input type="tel" v-model="userForm.phone" placeholder="请输入手机号" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-item">
                <label>角色</label>
                <select v-model="userForm.role" required>
                  <option value="admin">系统管理员</option>
                  <option value="monitor">监测人员</option>
                  <option value="researcher">科研人员</option>
                  <option value="manager">遗产地管理员</option>
                </select>
              </div>
            </div>
            <div class="form-row">
              <div class="form-item">
                <label>状态</label>
                <select v-model="userForm.enable">
                  <option :value="1">启用</option>
                  <option :value="0">禁用</option>
                </select>
              </div>
            </div>
            <div class="form-item" v-if="showAddUserDialog">
              <label>密码</label>
              <input type="password" v-model="userForm.password" placeholder="请输入密码（字母+数字+特殊符号，至少8位）" required />
              <div class="password-tip">密码必须包含字母、数字、特殊符号，且长度至少8位</div>
            </div>
            <div class="dialog-footer">
              <button type="button" class="cancel-btn" @click="closeDialog">取消</button>
              <button type="submit" class="confirm-btn">确定</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 删除确认对话框 -->
    <div v-if="showDeleteDialog" class="dialog-overlay">
      <div class="dialog">
        <div class="dialog-header">
          <h3>确认删除</h3>
          <button class="close-btn" @click="closeDeleteDialog">&times;</button>
        </div>
        <div class="dialog-content">
          <div class="delete-confirm-content">
            <div class="delete-icon">⚠️</div>
            <div class="delete-message">{{ deleteMessage }}</div>
          </div>
          <div class="dialog-footer">
            <button type="button" class="cancel-btn" @click="closeDeleteDialog">取消</button>
            <button type="button" class="confirm-btn" @click="confirmDelete">确定</button>
          </div>
        </div>
      </div>
    </div>
    <!-- 权限配置对话框 -->
    <div v-if="showPermissionDialog" class="dialog-overlay">
      <div class="dialog">
        <div class="dialog-header">
          <h3>区域权限配置 - {{ selectedArea?.areaName }}</h3>
          <button class="close-btn" @click="closePermissionDialog">&times;</button>
        </div>
        <div class="dialog-content">
          <div class="permission-config">
            <div class="permission-section">
              <h4>已授权用户</h4>
              <table class="permission-table">
                <thead>
                  <tr>
                    <th>用户名</th>
                    <th>姓名</th>
                    <th>角色</th>
                    <th>权限类型</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="perm in areaPermissions" :key="perm.id">
                    <td>{{ getUserById(perm.userId)?.username }}</td>
                    <td>{{ getUserById(perm.userId)?.name }}</td>
                    <td>{{ getRoleName(getUserById(perm.userId)?.role) }}</td>
                    <td>{{ getPermissionName(perm.permissionType) }}</td>
                    <td>
                      <button class="delete-btn" @click="handleRemovePermission(perm.id)">移除</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div class="permission-section">
              <h4>添加授权</h4>
              <div class="form-row">
                <select v-model="newPermission.userId">
                  <option value="">选择用户</option>
                  <option v-for="user in userList" :key="user.id" :value="user.id">
                    {{ user.name }} ({{ user.username }}) - {{ getRoleName(user.role) }}
                  </option>
                </select>
                <select v-model="newPermission.permissionType">
                  <option value="operate">操作权限</option>
                  <option value="analyze">分析权限</option>
                  <option value="view">查看权限</option>
                </select>
                <button class="confirm-btn" @click="handleAddPermission">添加</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 用户权限分配对话框 -->
    <div v-if="showUserPermissionDialog" class="dialog-overlay">
      <div class="dialog">
        <div class="dialog-header">
          <h3>用户权限分配 - {{ selectedUser?.name }}</h3>
          <button class="close-btn" @click="closeUserPermissionDialog">&times;</button>
        </div>
        <div class="dialog-content">
          <div class="permission-assign">
            <h4>选择授权区域</h4>
            <div v-for="area in areaList" :key="area.id" class="area-checkbox">
              <label>
                <input type="checkbox" :value="area.id" v-model="selectedAreas" />
                {{ area.areaName }} ({{ area.areaCode }})
              </label>
            </div>
            <div class="form-item" style="margin-top: 20px;">
              <label>权限类型</label>
              <select v-model="selectedPermissionType">
                <option value="operate">操作权限</option>
                <option value="analyze">分析权限</option>
                <option value="view">查看权限</option>
              </select>
            </div>
          </div>
          <div class="dialog-footer">
            <button class="cancel-btn" @click="closeUserPermissionDialog">取消</button>
            <button class="confirm-btn" @click="handleSaveUserPermission">保存</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 点位详情对话框 -->
    <div v-if="showAreaDetailDialog" class="dialog-overlay">
      <div class="dialog detail-dialog">
        <div class="dialog-header">
          <h3>{{ selectedAreaDetail?.areaName }} - 监测详情</h3>
          <button class="close-btn" @click="showAreaDetailDialog = false">&times;</button>
        </div>
        <div class="dialog-content">
          <div class="detail-section">
            <h4>实时监测数据</h4>
            <div class="data-grid">
              <div class="data-item"><span>温度：</span><strong>{{ currentEnvData?.temperature }}℃</strong></div>
              <div class="data-item"><span>湿度：</span><strong>{{ currentEnvData?.humidity }}%</strong></div>
              <div class="data-item"><span>PM2.5：</span><strong>{{ currentEnvData?.pm25 }}</strong></div>
              <div class="data-item"><span>风险等级：</span><strong :style="{color: getRiskTextColor(currentEnvData?.riskLevel)}">{{ getRiskText(currentEnvData?.riskLevel) }}</strong></div>
            </div>
          </div>
          <div class="detail-section">
            <h4>设备状态</h4>
            <div class="device-list">
              <div v-for="device in areaDevices" :key="device.id" class="device-item">
                <span>{{ device.deviceName }}</span>
                <span class="status-badge" :class="device.status === 'online' ? 'enabled' : 'disabled'">{{ device.status === 'online' ? '在线' : '离线' }}</span>
              </div>
            </div>
          </div>
          <div class="detail-section">
            <h4>历史趋势</h4>
            <div class="history-chart">
              <div v-for="item in areaHistory" :key="item.id" class="history-item">
                <span>{{ formatDate(item.recordTime) }}</span>
                <span>温度: {{ item.temperature }}℃</span>
                <span>湿度: {{ item.humidity }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeMount, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { getUserList, addUser, updateUser, deleteUser } from '../api/user'
import { getFileList, getFileListByUserId, deleteFile } from '../api/file'
import { getUserAuthorizedAreas } from '../api/area'
import { getDeviceList, addDevice, updateDevice, deleteDevice, uploadDevicePhoto } from '../api/device'
import request from '../api/request'
import FileUpload from '../components/FileUpload.vue'
import BaiduMapViewer from '../components/BaiduMapViewer.vue'
import CesiumViewer from '../components/CesiumViewer.vue'
import notification from '../utils/notification'

// 导入ECharts
import * as echarts from 'echarts'

const router = useRouter()

// 获取当前用户信息
const currentUser = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
const currentRole = ref(currentUser.value.role || 'user')

// 系统名称，可修改
const systemName = ref('文化遗产地环境监测')

// 当前激活的菜单
const activeMenu = ref('map') // 默认选中地图可视化菜单

// 地图组件引用，用于刷新数据
const baiduMapRef = ref(null)
const cesiumRef = ref(null)

// Cesium相关变量
let viewer = null
const show3DMarkers = ref(true)
const cesiumEntities = []
// 环境监测数据
const environmentData = ref([])
// 点位详情对话框
const showAreaDetailDialog = ref(false)
const selectedAreaDetail = ref(null)
const currentEnvData = ref(null)
const areaDevices = ref([])
const areaHistory = ref([])

// 数据采集相关变量
const collectTab = ref('manual')
const sensorForm = ref({
  areaId: '',
  dataSource: 'manual',
  temperature: null,
  humidity: null,
  precipitation: null,
  windSpeed: null,
  windDirection: '',
  lightIntensity: null,
  pm25: null,
  so2: null,
  no2: null,
  soilMoisture: null,
  soilPh: null,
  waterQuality: '',
  crackWidth: null,
  weatheringDegree: '',
  recordTime: ''
})
const sensorDataList = ref([])
const filterAreaId = ref('')
const filterSource = ref('')
const importFileName = ref('')

// 各菜单允许访问的角色
const menuRoles = {
  map: ['admin', 'monitor', 'researcher', 'manager'],
  '3d': ['admin', 'monitor', 'researcher', 'manager'],
  user: ['admin'],
  permission: ['admin'],
  area: ['admin'],
  device: ['admin'],
  log: ['admin'],
  datacollect: ['admin', 'monitor'],
  dataprocess: ['admin', 'researcher'],
  monitor: ['admin', 'monitor', 'researcher', 'manager'],
  threshold: ['admin'],
  alert: ['admin', 'monitor', 'researcher', 'manager'],
  statistics: ['admin', 'researcher'],
  file: ['admin', 'monitor', 'researcher', 'manager'],
}

// 监听菜单切换，按需加载数据
watch(activeMenu, (menu) => {
  // 角色守卫：无权限则跳回地图页
  const allowed = menuRoles[menu]
  if (allowed && !allowed.includes(currentRole.value)) {
    activeMenu.value = 'map'
    notification.error('权限不足，无法访问该功能')
    return
  }
  switch (menu) {
    case 'datacollect': fetchSensorData(); break
    case 'dataprocess': fetchSensorData(); fetchProcessedData(); break
    case 'monitor': loadMonitorData(); break
    case 'threshold': loadThresholdRules(); break
    case 'alert': loadAlertRecords(); break
    case 'file': fetchFileList(); break
    case 'user': if (isAdmin()) fetchUserList(); break
    case 'area': fetchAreaList(); break
    case 'device': if (isAdmin()) fetchDeviceList(); break
    case 'log': if (isAdmin()) fetchLogList(); break
    case 'statistics': loadStatistics(); break
  }
})

// 菜单点击处理函数 - 修复菜单bug的核心方法
const handleMenuClick = (menu) => {
  // 角色守卫：无权限则提示并返回
  const allowed = menuRoles[menu]
  if (allowed && !allowed.includes(currentRole.value)) {
    notification.error('权限不足，无法访问该功能')
    return
  }
  
  // 设置当前激活菜单
  activeMenu.value = menu
  
  // 根据菜单类型执行相应的初始化操作
  nextTick(() => {
    switch (menu) {
      case 'map':
        // 地图组件需要额外的延迟来确保容器尺寸正确
        setTimeout(() => {
          // 触发窗口resize事件，让地图组件重新计算尺寸
          window.dispatchEvent(new Event('resize'))
          // 刷新地图数据，显示新增的区域
          if (baiduMapRef.value && baiduMapRef.value.loadData) {
            baiduMapRef.value.loadData()
          }
        }, 150)
        break
      case '3d':
        setTimeout(() => {
          window.dispatchEvent(new Event('resize'))
          // 刷新三维地图数据
          if (cesiumRef.value && cesiumRef.value.loadData) {
            cesiumRef.value.loadData()
          }
        }, 150)
        break
      case 'monitor':
        loadMonitorData()
        break
      case 'threshold':
        loadThresholdRules()
        break
      case 'alert':
        loadAlertRecords()
        break
      case 'statistics':
        loadStatistics()
        break
    }
  })
}

// 处理查看趋势事件（从地图组件触发）
const handleViewTrend = (device) => {
  if (!device) return
  
  // 切换到环境监测页面
  activeMenu.value = 'monitor'
  
  // 设置选中的监测区域
  selectedMonitorArea.value = device.areaId
  
  // 等待DOM更新后加载监测数据
  nextTick(() => {
    loadMonitorData()
  })
}

// 监听标签切换
watch(collectTab, (newTab) => {
  if (newTab === 'list') {
    fetchSensorData()
  }
})

// 数据处理相关变量
const processTab = ref('pending')
const processedDataList = ref([])
const processFilterAreaId = ref('')

watch(processTab, (newTab) => {
  if (newTab === 'processed') {
    fetchProcessedData()
  } else if (newTab === 'pending') {
    fetchSensorData()
  }
})

// 环境监测相关变量
const selectedMonitorArea = ref(null)
const realtimeData = ref({})
const trendHours = ref(24)
let trendChart = null
let gaugeTemp = null
let gaugeHum = null
let gaugePm25 = null
let gaugeWind = null

// 阈值配置相关变量
const selectedThresholdArea = ref(1)
const thresholdRules = ref([])
const showAddRuleDialog = ref(false)
const ruleForm = ref({
  id: null,
  areaId: 1,
  factorType: 'temperature',
  thresholdMin: null,
  thresholdMax: null,
  alertLevel: 'medium',
  enable: 1
})

// 预警管理相关变量
const alertRecords = ref([])
const alertStatus = ref('')
const showAddAlertDialog = ref(false)
const alertForm = ref({
  areaId: 1,
  factorType: 'temperature',
  factorValue: null,
  alertLevel: 'medium',
  alertMessage: '',
  suggestion: ''
})

// 统计分析相关变量
const statsAreaId = ref('')
const statsPeriod = ref('month')
let factorTrendChart = null
let alertStatsChart = null
let areaCompareChart = null
const reportType = ref('month')
const reportFormat = ref('pdf')
const historyStartDate = ref('')
const historyEndDate = ref('')
const historyAreaId = ref('')
const historyFactorType = ref('')
let historyChart = null

// 删除确认对话框相关变量
const showDeleteDialog = ref(false)
const deleteMessage = ref('')
const deleteCallback = ref(null)
const deleteId = ref(null)

// 监听菜单切换
watch(
  () => activeMenu.value,
  (newMenu, oldMenu) => {
    if (newMenu === 'monitor') {
      setTimeout(() => {
        loadMonitorData()
      }, 200)
    } else if (newMenu === 'threshold') {
      setTimeout(() => {
        loadThresholdRules()
      }, 200)
    } else if (newMenu === 'alert') {
      setTimeout(() => {
        loadAlertRecords()
      }, 200)
    } else if (newMenu === 'statistics') {
      setTimeout(() => {
        loadStatistics()
      }, 200)
    }
  }
)

// 用户列表数据
const userList = ref([])
// 文件列表数据
const fileList = ref([])
// 区域列表数据
const areaList = ref([])
// 设备管理相关变量
const deviceList = ref([])
const filteredDeviceList = ref([])
const deviceSearchKeyword = ref('')
const deviceFilterArea = ref('')
const deviceFilterStatus = ref('')
const deviceFilterType = ref('')
const showDeviceDialog = ref(false)
const deviceDialogMode = ref('add')
const deviceForm = ref({
  id: null, areaId: '', deviceName: '', deviceType: '', status: 'online',
  riskLevel: 'safe', latitude: '', longitude: '', altitude: '', photoUrl: ''
})
const showDevicePhotoPreview = ref(false)
const devicePhotoPreviewUrl = ref('')
// 用户授权的区域列表（环境监测页面使用）
const authorizedAreaList = ref([])
// 区域添加/编辑弹窗
const showAreaDialog = ref(false)
const areaDialogMode = ref('add')
const areaForm = ref({ id: null, areaCode: '', areaName: '', description: '', latitude: '', longitude: '' })
// 区域权限列表
const areaPermissions = ref([])
// 操作日志列表
const logList = ref([])
// 日志筛选
const logFilter = ref('')
// 用户筛选相关数据
const selectedUserId = ref('')
// 添加用户对话框显示状态
const showAddUserDialog = ref(false)
// 编辑用户对话框显示状态
const showEditUserDialog = ref(false)
// 权限配置对话框
const showPermissionDialog = ref(false)
// 用户权限分配对话框
const showUserPermissionDialog = ref(false)
// 选中的区域
const selectedArea = ref(null)
// 选中的用户
const selectedUser = ref(null)
// 选中的区域列表
const selectedAreas = ref([])
// 选中的权限类型
const selectedPermissionType = ref('operate')
// 新权限
const newPermission = ref({ userId: '', permissionType: 'operate' })
// 用户表单数据
const userForm = ref({
  id: null,
  username: '',
  password: '',
  email: '',
  phone: '',
  name: '',
  role: 'user',
  enable: 1
})

// 角色名称映射
const roleNames = {
  admin: '系统管理员',
  monitor: '监测人员',
  researcher: '科研人员',
  manager: '遗产地管理员'
}

// 获取角色显示名称
const getRoleName = (role) => {
  return roleNames[role] || role
}

// 获取角色用户数量
const getRoleCount = (role) => {
  return userList.value.filter(user => user.role === role).length
}

// 获取权限类型名称
const getPermissionName = (type) => {
  const names = { operate: '操作权限', analyze: '分析权限', view: '查看权限' }
  return names[type] || type
}

// 根据ID获取用户
const getUserById = (userId) => {
  return userList.value.find(u => u.id === userId)
}

// 获取用户授权区域名称
const getUserAreaNames = (userId) => {
  const userPerms = areaPermissions.value.filter(p => p.userId === userId)
  return userPerms.map(p => {
    const area = areaList.value.find(a => a.id === p.areaId)
    return area?.areaName
  }).filter(Boolean).join(', ') || '未授权'
}

// 编辑用户权限
const handleEditUserPermission = async (user) => {
  selectedUser.value = user
  showUserPermissionDialog.value = true
  try {
    const response = await request.get(`/area/permission/user/${user.id}`)
    selectedAreas.value = response.data.map(p => p.areaId)
    if (response.data.length > 0) {
      selectedPermissionType.value = response.data[0].permissionType
    }
  } catch (error) {
    notification.error('获取用户权限失败')
  }
}

// 保存用户权限
const handleSaveUserPermission = async () => {
  try {
    // 先删除用户所有权限
    const response = await request.get(`/area/permission/user/${selectedUser.value.id}`)
    for (const perm of response.data) {
      await request.delete(`/area/permission/${perm.id}`)
    }
    // 添加新权限
    for (const areaId of selectedAreas.value) {
      await request.post('/area/permission', {
        userId: selectedUser.value.id,
        areaId: areaId,
        permissionType: selectedPermissionType.value
      })
    }
    notification.success('保存权限成功')
    closeUserPermissionDialog()
    fetchAreaList()
  } catch (error) {
    notification.error('保存权限失败')
  }
}

// 关闭用户权限对话框
const closeUserPermissionDialog = () => {
  showUserPermissionDialog.value = false
  selectedUser.value = null
  selectedAreas.value = []
  selectedPermissionType.value = 'operate'
}

// 获取环境监测数据
const fetchEnvironmentData = async () => {
  try {
    const response = await request.get('/environment/list')
    environmentData.value = response.data
  } catch (error) {
    notification.error('获取环境数据失败')
  }
}

// 处理区域点击
const handleAreaClick = async (areaId, areaName) => {
  selectedAreaDetail.value = { id: areaId, areaName }
  currentEnvData.value = environmentData.value.find(e => e.areaId === areaId)
  try {
    const [devicesRes, historyRes] = await Promise.all([
      request.get(`/environment/devices/${areaId}`),
      request.get(`/environment/history/${areaId}`)
    ])
    areaDevices.value = devicesRes.data
    areaHistory.value = historyRes.data
    showAreaDetailDialog.value = true
  } catch (error) {
    notification.error('获取详情失败')
  }
}

// 获取风险等级文本
const getRiskText = (level) => {
  const texts = { safe: '安全', low: '低风险', medium: '中风险', high: '高风险' }
  return texts[level] || '未知'
}

// 获取风险等级颜色
const getRiskTextColor = (level) => {
  const colors = { safe: '#00ff00', low: '#ffff00', medium: '#ff9900', high: '#ff0000' }
  return colors[level] || '#666'
}

// 获取区域列表
const fetchAreaList = async () => {
  try {
    const response = await request.get('/area/list')
    areaList.value = response.data
    // 同时获取所有权限
    const permResponse = await request.get('/area/permission/all')
    areaPermissions.value = permResponse.data
  } catch (error) {
    notification.error('获取区域列表失败')
  }
}
// 获取用户授权的区域列表（环境监测等页面使用）
const fetchAuthorizedAreaList = async () => {
  try {
    const response = await getUserAuthorizedAreas()
    authorizedAreaList.value = response.data || []
    // 如果有授权区域，默认选中第一个
    if (authorizedAreaList.value.length > 0 && !selectedMonitorArea.value) {
      selectedMonitorArea.value = authorizedAreaList.value[0].id
    }
  } catch (error) {
    console.error('获取授权区域列表失败:', error)
    authorizedAreaList.value = []
  }
}

const handleAddArea = () => {
  areaDialogMode.value = 'add'
  areaForm.value = { id: null, areaCode: '', areaName: '', description: '', latitude: '', longitude: '' }
  showAreaDialog.value = true
}

const handleEditArea = (area) => {
  areaDialogMode.value = 'edit'
  areaForm.value = { ...area }
  showAreaDialog.value = true
}

const submitAreaForm = async () => {
  try {
    if (areaDialogMode.value === 'add') {
      await request.post('/area', areaForm.value)
    } else {
      await request.put(`/area/${areaForm.value.id}`, areaForm.value)
    }
    showAreaDialog.value = false
    fetchAreaList()
  } catch (e) {
    notification.error('操作失败，请重试')
  }
}

// 管理区域权限
const handleManagePermission = async (area) => {
  selectedArea.value = area
  showPermissionDialog.value = true
  try {
    const response = await request.get(`/area/permission/area/${area.id}`)
    areaPermissions.value = response.data
  } catch (error) {
    notification.error('获取权限列表失败')
  }
}

// 添加权限
const handleAddPermission = async () => {
  if (!newPermission.value.userId) {
    notification.error('请选择用户')
    return
  }
  try {
    await request.post('/area/permission', {
      userId: newPermission.value.userId,
      areaId: selectedArea.value.id,
      permissionType: newPermission.value.permissionType
    })
    notification.success('添加权限成功')
    handleManagePermission(selectedArea.value)
    newPermission.value = { userId: '', permissionType: 'operate' }
  } catch (error) {
    notification.error('添加权限失败')
  }
}

// 移除权限
const handleRemovePermission = async (id) => {
  try {
    await request.delete(`/area/permission/${id}`)
    notification.success('移除权限成功')
    handleManagePermission(selectedArea.value)
  } catch (error) {
    notification.error('移除权限失败')
  }
}

// 关闭权限对话框
const closePermissionDialog = () => {
  showPermissionDialog.value = false
  selectedArea.value = null
  newPermission.value = { userId: '', permissionType: 'operate' }
}

// ============ 设备管理方法 ============
const fetchDeviceList = async () => {
  try {
    const response = await getDeviceList()
    deviceList.value = response.data || []
    filterDevices()
  } catch (error) {
    notification.error('获取设备列表失败')
  }
}

const filterDevices = () => {
  let result = deviceList.value
  if (deviceSearchKeyword.value) {
    const keyword = deviceSearchKeyword.value.toLowerCase()
    result = result.filter(d => d.deviceName?.toLowerCase().includes(keyword))
  }
  if (deviceFilterArea.value) {
    result = result.filter(d => d.areaId === deviceFilterArea.value)
  }
  if (deviceFilterStatus.value) {
    result = result.filter(d => d.status === deviceFilterStatus.value)
  }
  if (deviceFilterType.value) {
    result = result.filter(d => d.deviceType === deviceFilterType.value)
  }
  filteredDeviceList.value = result
}

const resetDeviceFilters = () => {
  deviceSearchKeyword.value = ''
  deviceFilterArea.value = ''
  deviceFilterStatus.value = ''
  deviceFilterType.value = ''
  filterDevices()
}

const handleAddDevice = () => {
  deviceDialogMode.value = 'add'
  deviceForm.value = {
    id: null, areaId: '', deviceName: '', deviceType: '', status: 'online',
    riskLevel: 'safe', latitude: '', longitude: '', altitude: '', photoUrl: ''
  }
  showDeviceDialog.value = true
}

const handleEditDevice = (device) => {
  deviceDialogMode.value = 'edit'
  deviceForm.value = { ...device }
  showDeviceDialog.value = true
}

const submitDeviceForm = async () => {
  try {
    if (deviceDialogMode.value === 'add') {
      await addDevice(deviceForm.value)
      notification.success('添加设备成功')
    } else {
      await updateDevice(deviceForm.value.id, deviceForm.value)
      notification.success('更新设备成功')
    }
    showDeviceDialog.value = false
    fetchDeviceList()
  } catch (error) {
    notification.error('操作失败，请重试')
  }
}

const handleDeleteDevice = (id) => {
  openDeleteDialog('确定要删除该设备吗？', id, async (deleteId) => {
    try {
      await deleteDevice(deleteId)
      notification.success('删除设备成功')
      fetchDeviceList()
    } catch (error) {
      notification.error('删除设备失败')
    }
  })
}

const triggerDevicePhotoUpload = () => {
  const input = document.querySelector('.device-photo-upload input[type="file"]')
  if (input) input.click()
}

const handleDevicePhotoUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  try {
    const response = await uploadDevicePhoto(file)
    deviceForm.value.photoUrl = response.data
    notification.success('照片上传成功')
  } catch (error) {
    notification.error('照片上传失败')
  }
  event.target.value = ''
}

const previewDevicePhoto = (url) => {
  devicePhotoPreviewUrl.value = url
  showDevicePhotoPreview.value = true
}

// 设备辅助方法
const getDeviceTypeName = (type) => {
  const names = {
    temperature: '温湿度传感器', air: '空气质量传感器', soil: '土壤监测仪',
    water: '水质监测仪', crack: '裂缝监测仪', wind: '风速风向仪',
    camera: '监控摄像头', drone: '无人机', other: '其他'
  }
  return names[type] || type || '-'
}

const getDeviceTypeIcon = (type) => {
  const icons = {
    temperature: '🌡️', air: '🌬️', soil: '🌱', water: '💧',
    crack: '📏', wind: '🍃', camera: '📷', drone: '🚁', other: '🔧'
  }
  return icons[type] || '📟'
}

const getDeviceStatusName = (status) => {
  const names = { online: '在线', offline: '离线', alarm: '报警', maintenance: '维护中' }
  return names[status] || status || '-'
}

const formatCoordinate = (lng, lat) => {
  if (!lng || !lat) return '-'
  return `${parseFloat(lng).toFixed(4)}, ${parseFloat(lat).toFixed(4)}`
}

// 获取操作日志列表
const fetchLogList = async () => {
  try {
    const params = logFilter.value ? { operationType: logFilter.value } : {}
    const response = await request.get('/log/list', { params })
    logList.value = response.data.records || []
  } catch (error) {
    notification.error('获取日志列表失败')
  }
}

// 初始化Cesium三维场景
const initCesium = () => {
  if (typeof Cesium === 'undefined') {
    notification.error('Cesium未加载，请刷新页面')
    return
  }

  viewer = new Cesium.Viewer('cesium-viewer', {
    imageryProvider: new Cesium.IonImageryProvider({ assetId: 3954 }),
    terrainProvider: Cesium.createWorldTerrain({ requestWaterMask: true, requestVertexNormals: true }),
    baseLayerPicker: false,
    geocoder: false,
    homeButton: false,
    sceneModePicker: false,
    navigationHelpButton: false,
    animation: false,
    timeline: false,
    fullscreenButton: false
  })

  viewer.scene.globe.enableLighting = true
  viewer.scene.globe.depthTestAgainstTerrain = true
  viewer.scene.fog.enabled = true
  viewer.scene.fog.density = 0.0002
  viewer.scene.skyAtmosphere.brightnessShift = 0.3

  const getRiskColor = (level) => {
    const colors = { safe: '#00ff00', low: '#ffff00', medium: '#ff9900', high: '#ff0000' }
    return colors[level] || '#00ff00'
  }

  areaList.value.forEach(area => {
    const envData = environmentData.value.find(e => e.areaId === area.id)
    const color = getRiskColor(envData?.riskLevel || 'safe')
    const entity = viewer.entities.add({
      position: Cesium.Cartesian3.fromDegrees(area.longitude, area.latitude, 100),
      billboard: { image: `data:image/svg+xml;base64,${btoa(`<svg width="32" height="32" viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg"><circle cx="16" cy="16" r="12" fill="${color}" opacity="0.8"/><circle cx="16" cy="16" r="8" fill="#ffffff"/></svg>`)}`, scale: 1.5, verticalOrigin: Cesium.VerticalOrigin.BOTTOM },
      label: { text: `${area.areaName}\n${envData?.riskLevel === 'safe' ? '安全' : envData?.riskLevel === 'low' ? '低风险' : envData?.riskLevel === 'medium' ? '中风险' : '高风险'}`, font: '14px Microsoft YaHei', fillColor: Cesium.Color.WHITE, outlineColor: Cesium.Color.BLACK, outlineWidth: 3, style: Cesium.LabelStyle.FILL_AND_OUTLINE, verticalOrigin: Cesium.VerticalOrigin.BOTTOM, pixelOffset: new Cesium.Cartesian2(0, -40), disableDepthTestDistance: Number.POSITIVE_INFINITY },
      properties: { areaId: area.id, areaName: area.areaName }
    })
    cesiumEntities.push(entity)
  })

  viewer.selectedEntityChanged.addEventListener((entity) => {
    if (entity && entity.properties) {
      handleAreaClick(entity.properties.areaId._value, entity.properties.areaName._value)
    }
  })

  viewer.camera.flyTo({ destination: Cesium.Cartesian3.fromDegrees(105, 35, 8000000), orientation: { heading: 0, pitch: Cesium.Math.toRadians(-45), roll: 0 } })
}

// 飞行到指定区域
const flyToArea = (areaId) => {
  const area = areaList.value.find(a => a.id === areaId)
  if (area && viewer) {
    viewer.camera.flyTo({ destination: Cesium.Cartesian3.fromDegrees(area.longitude, area.latitude, 5000), duration: 2 })
  }
}

// 切换三维标记显示
const toggle3DMarkers = () => {
  cesiumEntities.forEach(entity => { entity.show = show3DMarkers.value })
}


// 检查用户角色是否为管理员
const isAdmin = () => currentRole.value === 'admin'
const isMonitor = () => currentRole.value === 'monitor'
const isResearcher = () => currentRole.value === 'researcher'
const isManagerRole = () => currentRole.value === 'manager'
// 可操作预警（管理员+监测人员）
const canHandleAlert = () => isAdmin() || isMonitor()
// 可处理数据（管理员+科研人员）
const canProcessData = () => isAdmin() || isResearcher()

// 页面加载前检查用户信息
onBeforeMount(() => {
  // 从localStorage获取最新用户信息
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  currentUser.value = userInfo
  currentRole.value = userInfo.role || 'user'
})

// 获取用户列表
const fetchUserList = async () => {
  try {
    const response = await getUserList()
    // 处理不同的响应格式
    if (Array.isArray(response)) {
      // 如果直接返回数组
      userList.value = response
    } else if (response.data) {
      // 如果返回的是包含data字段的对象
      userList.value = response.data
    } else {
      // 其他情况，尝试直接使用
      userList.value = response
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    notification.error('获取用户列表失败')
  }
}


// 退出登录
const handleLogout = () => {
  // 清除所有用户信息
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  localStorage.removeItem('userInfo')
  // 跳转到登录页
  router.push('/login')
}

// 打开添加用户对话框
const openAddUserDialog = () => {
  showAddUserDialog.value = true
  resetUserForm()
}

// 打开编辑用户对话框
const handleEditUser = (user) => {
  showEditUserDialog.value = true
  userForm.value = { ...user }
}

// 删除用户
const handleDeleteUser = async (id) => {
  // 使用自定义删除确认对话框
  const performDelete = async (deleteId) => {
    try {
      await deleteUser(deleteId)
      // 重新获取用户列表
      fetchUserList()
      notification.success('删除用户成功')
    } catch (error) {
      console.error('删除用户失败:', error)
      notification.error('删除用户失败')
    }
  }
  
  openDeleteDialog('确定要删除这个用户吗？', id, performDelete)
}

// 提交用户表单
const handleSubmitUser = async () => {
  try {
    if (showAddUserDialog.value) {
      // 添加用户
      await addUser(userForm.value)
      notification.success('添加用户成功')
    } else {
      // 更新用户
      await updateUser(userForm.value)
      notification.success('更新用户成功')
    }
    // 关闭对话框
    closeDialog()
    // 重新获取用户列表
    fetchUserList()
  } catch (error) {
    console.error('提交用户表单失败:', error)
    notification.error('提交失败: ' + (error.message || '未知错误'))
  }
}

// 关闭对话框
const closeDialog = () => {
  showAddUserDialog.value = false
  showEditUserDialog.value = false
  resetUserForm()
}

// 打开删除确认对话框
const openDeleteDialog = (message, id, callback) => {
  deleteMessage.value = message
  deleteId.value = id
  deleteCallback.value = callback
  showDeleteDialog.value = true
}

// 关闭删除确认对话框
const closeDeleteDialog = () => {
  showDeleteDialog.value = false
  deleteMessage.value = ''
  deleteId.value = null
  deleteCallback.value = null
}

// 确认删除
const confirmDelete = () => {
  if (deleteCallback.value && deleteId.value !== null) {
    deleteCallback.value(deleteId.value)
  }
  closeDeleteDialog()
}

// 重置用户表单
const resetUserForm = () => {
  userForm.value = {
    id: null,
    username: '',
    password: '',
    email: '',
    phone: '',
    name: '',
    role: 'monitor',
    enable: 1
  }
}

// 获取文件列表
const fetchFileList = async () => {
  try {
    // 调用真实的API获取文件列表，传递module参数为default
    const response = await getFileList({ module: 'default' })
    fileList.value = response.data || []
  } catch (error) {
    console.error('获取文件列表失败:', error)
    notification.error('获取文件列表失败')
  }
}

// 根据用户ID获取文件列表
const fetchFileListByUserId = async (userId) => {
  try {
    const response = await getFileListByUserId(userId)
    fileList.value = response.data || []
    
    notification.success('获取用户文件列表成功')
  } catch (error) {
    console.error('获取用户文件列表失败:', error)
    notification.error('获取用户文件列表失败')
  }
}

// 处理文件预览
const handlePreview = async (file) => {
  try {
    notification.info('正在加载预览文件...')
    
    // 使用axios发送带token的请求获取文件流
    const response = await request({
      url: file.fileUrl,
      method: 'get',
      responseType: 'blob',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    
    // 创建blob URL
    const blobUrl = URL.createObjectURL(response)
    
    // 在新标签页中打开预览
    window.open(blobUrl, '_blank')
    
    notification.success('文件预览成功')
  } catch (error) {
    console.error('文件预览失败:', error)
    notification.error('文件预览失败: ' + (error.message || '未知错误'))
  }
}

// 处理文件下载
const handleDownload = (file) => {
  try {
    // 这里应该调用真实的下载API
    console.log('下载文件:', file.name)
    notification.success('开始下载文件')
  } catch (error) {
    console.error('下载文件失败:', error)
    notification.error('下载文件失败')
  }
}

// 处理文件删除
const handleDeleteFile = async (id) => {
  // 使用自定义删除确认对话框
  const performDelete = async (deleteId) => {
    try {
      // 调用真实的删除API
      await deleteFile(deleteId)
      fileList.value = fileList.value.filter(file => file.id !== deleteId)
      notification.success('删除文件成功')
    } catch (error) {
      console.error('删除文件失败:', error)
      notification.error('删除文件失败')
    }
  }
  
  openDeleteDialog('确定要删除这个文件吗？', id, performDelete)
}

// 格式化文件大小
const formatFileSize = (size) => {
  if (size < 1024) {
    return size + ' B'
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB'
  } else {
    return (size / (1024 * 1024)).toFixed(2) + ' MB'
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleString()
}

// 处理文件上传成功
const handleUploadSuccess = (result) => {
  notification.success('文件上传成功')
  // 刷新文件列表
  fetchFileList()
}

// 处理文件上传失败
const handleUploadError = (error) => {
  notification.error('文件上传失败: ' + (error.message || '未知错误'))
}

// 处理用户筛选变化
const handleUserFilterChange = () => {
  if (selectedUserId.value) {
    fetchFileListByUserId(selectedUserId.value)
  } else {
    fetchFileList()
  }
}

// 重置用户筛选
const resetUserFilter = () => {
  selectedUserId.value = ''
  fetchFileList()
}

// 组件挂载时获取用户列表和文件列表
onMounted(() => {
  // 获取文件列表
  fetchFileList()
  // 仅管理员获取用户列表和区域列表
  if (isAdmin()) {
    fetchUserList()
    fetchAreaList()
    fetchLogList()
  } else {
    fetchAreaList()
  }
  // 获取用户授权的区域列表
  fetchAuthorizedAreaList()
  // 获取环境数据
  fetchEnvironmentData()
})

// 数据采集相关方法
const submitSensorData = async () => {
  try {
    await request.post('/sensor/add', sensorForm.value)
    notification.success('数据提交成功')
    resetSensorForm()
    if (collectTab.value === 'list') {
      fetchSensorData()
    }
  } catch (error) {
    notification.error('数据提交失败')
  }
}

const resetSensorForm = () => {
  sensorForm.value = {
    areaId: '',
    dataSource: 'manual',
    temperature: null,
    humidity: null,
    precipitation: null,
    windSpeed: null,
    windDirection: '',
    lightIntensity: null,
    pm25: null,
    so2: null,
    no2: null,
    soilMoisture: null,
    soilPh: null,
    waterQuality: '',
    crackWidth: null,
    weatheringDegree: '',
    recordTime: ''
  }
}

const fetchSensorData = async () => {
  try {
    let url = '/sensor/list/0'
    if (filterAreaId.value) {
      url = `/sensor/list/${filterAreaId.value}`
    }
    const response = await request.get(url)
    sensorDataList.value = response.data || []
  } catch (error) {
    notification.error('获取数据失败')
  }
}

const handleImportFile = (event) => {
  const file = event.target.files[0]
  if (file) {
    importFileName.value = file.name
  }
}

const uploadImportFile = async () => {
  const file = document.querySelector('input[type="file"]').files[0]
  if (!file) return
  const formData = new FormData()
  formData.append('file', file)
  try {
    await request.post('/sensor/import', formData)
    notification.success('导入成功')
    importFileName.value = ''
    fetchSensorData()
  } catch (error) {
    notification.error('导入失败')
  }
}

const getSourceName = (source) => {
  const names = { sensor: '传感器', drone: '无人机', manual: '人工录入', api: '接口对接', import: '历史导入' }
  return names[source] || source
}

const getAreaName = (areaId) => {
  const area = areaList.value.find(a => a.id === areaId)
  return area?.areaName || '-'
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 数据处理相关方法
const processData = async (id) => {
  try {
    await request.post(`/data/process/${id}`)
    notification.success('数据处理成功')
    // 从待处理列表中移除已处理的数据
    sensorDataList.value = sensorDataList.value.filter(item => item.id !== id)
    // 刷新已处理数据列表
    fetchProcessedData()
  } catch (error) {
    notification.error('数据处理失败')
  }
}

const fetchProcessedData = async () => {
  try {
    const params = processFilterAreaId.value ? `?areaId=${processFilterAreaId.value}` : ''
    const response = await request.get(`/data/processed/list${params}`)
    processedDataList.value = response.data || []
  } catch (error) {
    notification.error('获取处理数据失败')
  }
}

// 环境监测相关方法
const loadMonitorData = async () => {
  await fetchRealtimeData()
  await fetchTrendData()
  await fetchDeviceStatus()
}

const fetchRealtimeData = async () => {
  try {
    const response = await request.get(`/monitor/realtime/${selectedMonitorArea.value}`)
    realtimeData.value = response.data || {}
    await nextTick()
    renderGauges(realtimeData.value)
  } catch (error) {
    notification.error('获取实时数据失败')
  }
}

const makeGaugeOption = (name, value, max, unit, color) => ({
  series: [{
    type: 'gauge',
    min: 0, max,
    progress: { show: true, width: 12 },
    axisLine: { lineStyle: { width: 12 } },
    axisTick: { show: false },
    splitLine: { length: 8, lineStyle: { width: 2 } },
    axisLabel: { distance: 15, fontSize: 10 },
    anchor: { show: true, showAbove: true, size: 18, itemStyle: { borderWidth: 2 } },
    title: { show: true, offsetCenter: [0, '70%'], fontSize: 13 },
    detail: {
      valueAnimation: true,
      formatter: `{value} ${unit}`,
      fontSize: 16,
      offsetCenter: [0, '40%'],
      color
    },
    data: [{ value: value ?? 0, name }],
    itemStyle: { color }
  }]
})

const renderGauges = (data) => {
  const gt = document.getElementById('gauge-temp')
  const gh = document.getElementById('gauge-hum')
  const gp = document.getElementById('gauge-pm25')
  const gw = document.getElementById('gauge-wind')
  if (!gt || !gh || !gp || !gw) return

  if (!gaugeTemp) gaugeTemp = echarts.init(gt)
  if (!gaugeHum) gaugeHum = echarts.init(gh)
  if (!gaugePm25) gaugePm25 = echarts.init(gp)
  if (!gaugeWind) gaugeWind = echarts.init(gw)

  gaugeTemp.setOption(makeGaugeOption('温度(℃)', data.temperature, 50, '℃', '#e74c3c'))
  gaugeHum.setOption(makeGaugeOption('湿度(%)', data.humidity, 100, '%', '#3498db'))
  gaugePm25.setOption(makeGaugeOption('PM2.5', data.pm25, 200, 'μg/m³', '#9b59b6'))
  gaugeWind.setOption(makeGaugeOption('风速(m/s)', data.windSpeed, 20, 'm/s', '#27ae60'))
}

const fetchTrendData = async () => {
  try {
    const response = await request.get(`/monitor/trend/${selectedMonitorArea.value}?hours=${trendHours.value}`)
    renderTrendChart(response.data)
  } catch (error) {
    notification.error('获取趋势数据失败')
  }
}

const fetchDeviceStatus = async () => {
  try {
    const response = await request.get(`/environment/devices/${selectedMonitorArea.value}`)
    areaDevices.value = response.data || []
  } catch (error) {
    console.error('获取设备状态失败')
  }
}

const changeTrendHours = (hours) => {
  trendHours.value = hours
  fetchTrendData()
}

const renderTrendChart = (data) => {
  const chartDom = document.getElementById('trend-chart')
  if (!chartDom) return

  if (!trendChart) {
    trendChart = echarts.init(chartDom)
  }

  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['温度', '湿度', 'PM2.5'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: data.times || [] },
    yAxis: { type: 'value' },
    series: [
      { name: '温度', type: 'line', data: data.temperature || [], smooth: true },
      { name: '湿度', type: 'line', data: data.humidity || [], smooth: true },
      { name: 'PM2.5', type: 'line', data: data.pm25 || [], smooth: true }
    ]
  }
  trendChart.setOption(option)
}

// 阈值配置相关方法
const loadThresholdRules = async () => {
  try {
    const response = await request.get(`/threshold/rules/${selectedThresholdArea.value}`)
    thresholdRules.value = response.data || []
  } catch (error) {
    notification.error('获取阈值规则失败')
  }
}

const applyTemplate = async (heritageType) => {
  try {
    const response = await request.get('/threshold/templates')
    const templates = response.data.filter(t => t.heritageType === heritageType)
    for (const tpl of templates) {
      await request.post('/threshold/rule', {
        areaId: selectedThresholdArea.value,
        factorType: tpl.factorType,
        thresholdMin: tpl.thresholdMin,
        thresholdMax: tpl.thresholdMax,
        alertLevel: 'medium',
        enable: 1
      })
    }
    notification.success('模板应用成功')
    loadThresholdRules()
  } catch (error) {
    notification.error('应用模板失败')
  }
}

const editRule = (rule) => {
  ruleForm.value = { ...rule }
  showAddRuleDialog.value = true
}

const saveRule = async () => {
  try {
    ruleForm.value.areaId = selectedThresholdArea.value
    await request.post('/threshold/rule', ruleForm.value)
    notification.success('保存成功')
    closeRuleDialog()
    loadThresholdRules()
  } catch (error) {
    notification.error('保存失败')
  }
}

const deleteRule = async (id) => {
  try {
    await request.delete(`/threshold/rule/${id}`)
    notification.success('删除成功')
    loadThresholdRules()
  } catch (error) {
    notification.error('删除失败')
  }
}

const closeRuleDialog = () => {
  showAddRuleDialog.value = false
  ruleForm.value = { id: null, areaId: 1, factorType: 'temperature', thresholdMin: null, thresholdMax: null, alertLevel: 'medium', enable: 1 }
}

const getFactorName = (type) => {
  const names = { temperature: '温度', humidity: '湿度', pm25: 'PM2.5', so2: 'SO₂' }
  return names[type] || type
}

const getLevelName = (level) => {
  const names = { low: '低', medium: '中', high: '高' }
  return names[level] || level
}

// 预警管理相关方法
const loadAlertRecords = async () => {
  try {
    const params = alertStatus.value ? `?status=${alertStatus.value}` : ''
    const response = await request.get(`/alert/records${params}`)
    alertRecords.value = response.data || []
  } catch (error) {
    notification.error('获取预警记录失败')
  }
}

const processAlert = async (id) => {
  try {
    await request.post(`/alert/process/${id}`)
    notification.success('处理成功')
    loadAlertRecords()
  } catch (error) {
    notification.error('处理失败')
  }
}

const saveAlert = async () => {
  try {
    await request.post('/alert/add', alertForm.value)
    notification.success('添加成功')
    closeAddAlertDialog()
    loadAlertRecords()
  } catch (error) {
    notification.error('添加失败')
  }
}

const closeAddAlertDialog = () => {
  showAddAlertDialog.value = false
  alertForm.value = { areaId: 1, factorType: 'temperature', factorValue: null, alertLevel: 'medium', alertMessage: '', suggestion: '' }
}

// 统计分析相关方法
const loadStatistics = async () => {
  const areaId = statsAreaId.value || 1
  try {
    const [trendRes, alertRes, compareRes] = await Promise.all([
      request.get(`/statistics/trend?areaId=${areaId}&period=${statsPeriod.value}`),
      request.get(`/statistics/alert?areaId=${areaId}`),
      request.get('/statistics/compare')
    ])
    await nextTick()
    if (trendRes.data) renderFactorTrendChart(trendRes.data)
    if (alertRes.data) renderAlertStatsChart(alertRes.data)
    if (compareRes.data) renderAreaCompareChart(compareRes.data)
  } catch (error) {
    notification.error('加载统计数据失败')
  }
}

const renderFactorTrendChart = (data) => {
  const chartDom = document.getElementById('factor-trend-chart')
  if (!chartDom) return
  if (!factorTrendChart) factorTrendChart = echarts.init(chartDom)

  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['温度', '湿度', 'PM2.5'] },
    xAxis: { type: 'category', data: data.xAxis },
    yAxis: { type: 'value' },
    series: [
      { name: '温度', type: 'line', data: data.temperature, smooth: true },
      { name: '湿度', type: 'line', data: data.humidity, smooth: true },
      { name: 'PM2.5', type: 'line', data: data.pm25, smooth: true }
    ]
  }
  factorTrendChart.setOption(option)
}

const renderAlertStatsChart = (data) => {
  const chartDom = document.getElementById('alert-stats-chart')
  if (!chartDom) return
  if (!alertStatsChart) alertStatsChart = echarts.init(chartDom)

  const option = {
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      name: '预警次数',
      type: 'pie',
      radius: '50%',
      data: data.levels.map((level, index) => ({ value: data.values[index], name: level }))
    }]
  }
  alertStatsChart.setOption(option)
}

const renderAreaCompareChart = (data) => {
  const chartDom = document.getElementById('area-compare-chart')
  if (!chartDom) return
  if (!areaCompareChart) areaCompareChart = echarts.init(chartDom)

  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['温度', '湿度', 'PM2.5'] },
    xAxis: { type: 'category', data: data.areas },
    yAxis: { type: 'value' },
    series: [
      { name: '温度', type: 'bar', data: data.temperature },
      { name: '湿度', type: 'bar', data: data.humidity },
      { name: 'PM2.5', type: 'bar', data: data.pm25 }
    ]
  }
  areaCompareChart.setOption(option)
}

const generateReport = async () => {
  try {
    const areaId = statsAreaId.value || 1
    const areaName = areaList.value.find(a => a.id == areaId)?.areaName || '全部区域'
    const response = await request.get(`/statistics/trend?areaId=${areaId}&period=${reportType.value === 'month' ? 'month' : 'year'}`)
    const data = response.data
    if (!data) { notification.error('获取数据失败'); return }

    const rows = [['时间', '温度(℃)', '湿度(%)', 'PM2.5(μg/m³)']]
    data.xAxis.forEach((x, i) => rows.push([x, data.temperature[i], data.humidity[i], data.pm25[i]]))
    const csv = rows.map(r => r.join(',')).join('\n')
    const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `${areaName}_${reportType.value === 'month' ? '月度' : '年度'}报告.csv`
    a.click()
    URL.revokeObjectURL(url)
    notification.success('报告已生成并下载')
  } catch (e) {
    notification.error('生成报告失败')
  }
}

const searchHistory = async () => {
  if (!historyStartDate.value || !historyEndDate.value) {
    notification.error('请选择开始和结束日期')
    return
  }
  try {
    const areaId = historyAreaId.value || 1
    const hours = Math.ceil((new Date(historyEndDate.value) - new Date(historyStartDate.value)) / 3600000)
    const response = await request.get(`/monitor/trend/${areaId}?hours=${Math.max(hours, 1)}`)
    await nextTick()
    renderHistoryChart(response.data, historyFactorType.value)
  } catch (error) {
    notification.error('查询历史数据失败')
  }
}

const renderHistoryChart = (data, factorType) => {
  const chartDom = document.getElementById('history-chart')
  if (!chartDom) return
  if (!historyChart) historyChart = echarts.init(chartDom)

  const seriesMap = {
    temperature: { name: '温度(℃)', data: data.temperature },
    humidity: { name: '湿度(%)', data: data.humidity },
    pm25: { name: 'PM2.5', data: data.pm25 }
  }
  const series = factorType && seriesMap[factorType]
    ? [{ name: seriesMap[factorType].name, type: 'line', data: seriesMap[factorType].data, smooth: true }]
    : Object.values(seriesMap).map(s => ({ name: s.name, type: 'line', data: s.data, smooth: true }))

  historyChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: series.map(s => s.name) },
    xAxis: { type: 'category', data: data.times || [] },
    yAxis: { type: 'value' },
    series
  })
}

// 组件卸载时的清理工作由各个子组件自己处理
// BaiduMapViewer和CesiumViewer都有beforeUnmount钩子
</script>

<style>
/* 确保html和body占满全屏 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  height: 100%;
  overflow: hidden;
}
</style>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100vh;
  background-color: var(--page-bg);
  margin: 0;
  padding: 0;
  overflow: hidden;
}

/* 顶部导航栏样式 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 64px;
  background: var(--gradient-heritage);
  box-shadow: var(--shadow-md);
  z-index: 100;
  width: 100%;
}

.logo {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-white);
  margin: 0;
  letter-spacing: 1px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.username {
  font-size: 14px;
  color: var(--text-white);
  font-weight: 500;
  opacity: 0.95;
}

.logout-btn {
  padding: 10px 20px;
  background-color: var(--danger-color);
  color: var(--text-white);
  border: none;
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: var(--shadow-sm);
}

.logout-btn:hover {
  background-color: #C44741;
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

/* 主体内容区域 */
.main-container {
  display: flex;
  flex: 1;
  width: 100%;
  overflow: hidden;
}

/* 侧边菜单样式 */
.sidebar {
  width: 260px;
  background: var(--gradient-primary);
  box-shadow: var(--shadow-lg);
  overflow-y: auto;
  padding: 24px 0;
}

.sidebar::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-thumb {
  background: var(--secondary-color);
  border-radius: 3px;
}

.sidebar::-webkit-scrollbar-thumb:hover {
  background: var(--secondary-light);
}

.menu {
  padding: 12px 16px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px 18px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  gap: 14px;
  color: rgba(255, 255, 255, 0.9);
  border-radius: var(--radius-lg);
  backdrop-filter: blur(10px);
  font-weight: 500;
}

.menu-item:hover {
  background: rgba(200, 169, 107, 0.2);
  color: var(--text-white);
  transform: translateX(6px);
  box-shadow: var(--shadow-md) rgba(0, 0, 0, 0.15);
}

.menu-item.active {
  background: rgba(255, 255, 255, 0.25);
  color: #fff;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.menu-icon {
  font-size: 20px;
  width: 28px;
  text-align: center;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.menu-text {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.menu-active {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 60%;
  background: #fff;
  border-radius: 0 4px 4px 0;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}

/* 主要内容区域 */
.main-content {
  flex: 1;
  padding: 10px;
  overflow-y: auto;
  background-color: #f5f7fa;
}
.main-content.no-scroll {
  overflow: hidden;
  padding: 0;
  display: flex;
  flex-direction: column;
}

.content-wrapper {
  width: 100%;
  height: 100%;
  margin: 0;
  display: flex;
  flex-direction: column;
}

.welcome-section {
  margin-bottom: 10px;
  padding: 10px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.welcome-section h2 {
  margin: 0;
  color: #333;
  text-align: center;
  font-size: 18px;
}

/* 地图可视化内容 */
.map-content {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.map-content .welcome-section {
  flex-shrink: 0;
  margin-bottom: 8px;
  padding: 8px 10px;
}

.map-content .welcome-section h2 {
  margin: 0;
  font-size: 16px;
}

/* 地图容器全屏样式 */
.map-container-full {
  width: 100%;
  flex: 1;
  min-height: 0;
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  background: #fff;
}

/* 地图容器样式 */
.map-container {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  padding: 10px;
  min-height: 0;
  height: calc(100vh - 120px); /* 增大地图高度，减少顶部空间占用 */
  display: flex;
  gap: 15px;
  overflow: hidden;
}

/* 图层控制面板样式 */
.map-controls {
  width: 180px;
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  height: fit-content;
  align-self: flex-start;
  flex-shrink: 0; /* 防止控制面板被压缩 */
  font-size: 13px;
}

.map-controls h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 14px;
  font-weight: 600;
}

.layer-control {
  margin-bottom: 10px;
}

.control-label {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 13px;
  color: #333;
  user-select: none;
  transition: color 0.3s;
}

.control-label:hover {
  color: #667eea;
}

.control-label input[type="checkbox"] {
  accent-color: #667eea;
  cursor: pointer;
  transform: scale(0.9);
}

/* Cesium三维场景样式 */
.cesium-container {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  padding: 10px;
  height: calc(100vh - 120px);
  display: flex;
  gap: 15px;
  overflow: hidden;
}

.cesium-controls {
  width: 180px;
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  height: fit-content;
  flex-shrink: 0;
  font-size: 13px;
}

.cesium-controls h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 14px;
  font-weight: 600;
}

.view-btn {
  width: 100%;
  padding: 8px;
  margin-bottom: 8px;
  background-color: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.view-btn:hover {
  background-color: #5568d3;
}

.risk-legend {
  margin-top: 10px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 12px;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 1px solid #333;
}

.cesium-viewer {
  flex: 1;
  border-radius: 8px;
  height: 100%;
  min-height: 0;
}

/* 地图占位符样式 */
.map-placeholder {
  text-align: center;
  color: #999;
}

.map-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

/* 权限管理区域 */
.permission-management {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  padding: 20px;
}

.role-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.role-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 24px;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.role-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.role-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.role-icon {
  font-size: 32px;
}

.role-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.role-permissions {
  margin-bottom: 16px;
}

.role-permissions p {
  margin: 8px 0;
  font-size: 14px;
  opacity: 0.9;
}

.role-count {
  font-size: 14px;
  font-weight: 500;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  opacity: 0.95;
}

/* 区域管理 */
.area-management {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  padding: 20px;
}

.area-list {
  margin-top: 20px;
}

/* ============ 设备管理样式 ============ */
.device-management {
  padding: 20px;
}

/* 统计卡片 */
.device-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 25px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.12);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.total { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-icon.online { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }
.stat-icon.offline { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.stat-icon.alarm { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }

.stat-info { display: flex; flex-direction: column; }
.stat-value { font-size: 28px; font-weight: 700; color: #333; line-height: 1.2; }
.stat-label { font-size: 13px; color: #888; margin-top: 4px; }

/* 工具栏 */
.device-toolbar {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-box {
  position: relative;
  flex: 1;
  min-width: 200px;
  max-width: 300px;
}

.search-box input {
  width: 100%;
  padding: 10px 15px 10px 40px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.2s;
}

.search-box input:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.1);
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 16px;
  color: #999;
}

.filter-group {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-group select {
  padding: 10px 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  background: white;
  cursor: pointer;
  min-width: 120px;
}

.filter-group select:focus {
  outline: none;
  border-color: #1890ff;
}

.add-btn .btn-icon { font-size: 18px; margin-right: 4px; }

/* 设备卡片列表 */
.device-card-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.device-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transition: transform 0.2s, box-shadow 0.2s;
  border: 1px solid #f0f0f0;
}

.device-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.device-card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
  border-bottom: 1px solid #e8e8e8;
}

.device-type-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  background: white;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
}

.device-type-icon.temperature { background: #fff2e8; }
.device-type-icon.air { background: #e6f7ff; }
.device-type-icon.soil { background: #f6ffed; }
.device-type-icon.water { background: #e6fffb; }
.device-type-icon.crack { background: #fff7e6; }
.device-type-icon.wind { background: #f9f0ff; }
.device-type-icon.camera { background: #fff0f6; }
.device-type-icon.drone { background: #fff2f0; }
.device-type-icon.other { background: #f5f5f5; }

.device-title { flex: 1; min-width: 0; }
.device-title h4 { font-size: 15px; font-weight: 600; color: #333; margin: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.device-area { font-size: 12px; color: #888; margin-top: 2px; display: block; }

.device-status-tag {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.device-status-tag.online { background: #d9f7be; color: #389e0d; }
.device-status-tag.offline { background: #f5f5f5; color: #888; }
.device-status-tag.alarm { background: #ffccc7; color: #cf1322; }
.device-status-tag.maintenance { background: #fff2e8; color: #fa8c16; }

.device-card-body { padding: 16px; }

.device-info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px dashed #f0f0f0;
}

.device-info-row:last-child { border-bottom: none; }

.info-label { font-size: 13px; color: #888; }
.info-value { font-size: 13px; color: #333; font-weight: 500; }
.info-value.coord { font-family: monospace; font-size: 12px; color: #666; }
.info-value.time { font-size: 12px; color: #999; }

.risk-tag {
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.risk-tag.safe { background: #d9f7be; color: #389e0d; }
.risk-tag.low { background: #fff7e6; color: #fa8c16; }
.risk-tag.medium { background: #ffe7ba; color: #d46b08; }
.risk-tag.high { background: #ffccc7; color: #cf1322; }

.device-card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #fafafa;
  border-top: 1px solid #f0f0f0;
}

.device-photo {
  width: 60px;
  height: 40px;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
}

.device-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.2s;
}

.device-photo img:hover { transform: scale(1.1); }

.device-id { font-size: 11px; color: #bbb; font-family: monospace; }

.device-actions { display: flex; gap: 8px; }

.action-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  transition: all 0.2s;
}

.action-btn.edit { background: #e6f7ff; }
.action-btn.edit:hover { background: #1890ff; color: white; }
.action-btn.delete { background: #fff2f0; }
.action-btn.delete:hover { background: #ff4d4f; color: white; }

/* 设备照片上传 */
.device-photo-upload { display: flex; gap: 15px; align-items: flex-start; }
.device-photo-preview { display: flex; flex-direction: column; gap: 10px; }
.device-photo-preview img { max-width: 200px; max-height: 150px; object-fit: cover; border-radius: 8px; border: 1px solid #ddd; }
.photo-remove-btn { padding: 4px 12px; background: #ff4d4f; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 12px; }
.photo-remove-btn:hover { background: #ff7875; }
.photo-upload-area {
  width: 150px; height: 100px;
  border: 2px dashed #ddd; border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; background: #fafafa; transition: all 0.2s;
}
.photo-upload-area:hover { border-color: #1890ff; background: #f0f7ff; }
.photo-upload-area span { color: #999; font-size: 14px; }

.reset-btn { padding: 8px 24px; background: #1890ff; color: white; border: none; border-radius: 6px; cursor: pointer; font-size: 14px; }
.reset-btn:hover { background: #40a9ff; }

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state p {
  font-size: 15px;
  margin-bottom: 20px;
}

/* 设备管理响应式 */
@media (max-width: 1200px) {
  .device-stats { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 768px) {
  .device-stats { grid-template-columns: repeat(2, 1fr); gap: 12px; }
  .stat-card { padding: 15px; }
  .stat-icon { width: 40px; height: 40px; font-size: 20px; }
  .stat-value { font-size: 22px; }
  .device-toolbar { flex-direction: column; align-items: stretch; }
  .search-box { max-width: none; }
  .filter-group { justify-content: flex-start; }
  .device-card-list { grid-template-columns: 1fr; }
}

/* 日志管理 */
.log-management {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  padding: 20px;
}

.log-management select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.permission-config {
  padding: 20px 0;
}

.permission-section {
  margin-bottom: 30px;
}

.permission-section h4 {
  margin-bottom: 15px;
  color: #333;
}

.permission-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.permission-table th,
.permission-table td {
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid #e0e0e0;
}

.permission-table th {
  background-color: #fafafa;
  font-weight: 600;
}

.permission-assign {
  padding: 20px 0;
}

.area-checkbox {
  margin: 10px 0;
}

.area-checkbox label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

/* 用户管理区域 */
.user-management {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  padding: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  color: #333;
}

.add-user-btn {
  padding: 10px 20px;
  background-color: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.add-user-btn:hover {
  background-color: #5568d3;
}

/* 用户列表表格 */
.user-list-container {
  overflow-x: auto;
}

.user-table {
  width: 100%;
  border-collapse: collapse;
}

.user-table th,
.user-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e0e0e0;
}

.user-table th {
  background-color: #fafafa;
  font-weight: 600;
  color: #333;
}

.user-table tr:hover {
  background-color: #f5f5f5;
}

/* 状态徽章 */
.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.enabled {
  background-color: #e8f5e8;
  color: #2e7d32;
}

.status-badge.disabled {
  background-color: #ffebee;
  color: #c62828;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
}

.edit-btn,
.delete-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.edit-btn {
  background-color: #2196f3;
  color: white;
}

.edit-btn:hover {
  background-color: #1976d2;
}

.delete-btn {
  background-color: #f44336;
  color: white;
}

.delete-btn:hover {
  background-color: #d32f2f;
}

/* 文件管理区域 */
.file-management {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  padding: 20px;
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}

/* 自定义滚动条样式 */
.file-management::-webkit-scrollbar {
  width: 8px;
}

.file-management::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.file-management::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 4px;
}

.file-management::-webkit-scrollbar-thumb:hover {
  background: #555;
}

.file-actions {
  display: flex;
  gap: 10px;
}

.refresh-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
  background-color: #909399;
  color: white;
}

.refresh-btn:hover {
  background-color: #73767a;
}

/* 用户筛选区域 */
.user-filter-section {
  margin: 20px 0;
  padding: 15px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
}

.filter-controls {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.filter-controls label {
  font-weight: 500;
  color: #333;
  white-space: nowrap;
}

.filter-controls select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  min-width: 200px;
}

.filter-controls select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.reset-filter-btn {
  padding: 8px 16px;
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.reset-filter-btn:hover {
  background-color: #5a6268;
}

/* 文件上传区域 */
.file-upload-section {
  margin: 20px 0;
  padding: 20px;
  background-color: #fafafa;
  border: 1px solid #eee;
  border-radius: 8px;
}

/* 文件列表容器 */
.file-list-container {
  margin-top: 20px;
}

/* 文件网格布局 */
.file-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

/* 文件项样式 */
.file-item {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  transition: all 0.3s;
  cursor: pointer;
}

.file-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

/* 文件图标 */
.file-icon {
  font-size: 48px;
  text-align: center;
}

/* 文件信息 */
.file-info {
  flex: 1;
}

.file-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-meta {
  display: flex;
  flex-direction: column;
  gap: 3px;
  font-size: 12px;
  color: #6c757d;
}

/* 文件操作按钮 */
.file-operations {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}

.file-operations .preview-btn, .file-operations .download-btn, .file-operations .delete-btn {
  flex: 1;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.preview-btn {
  background-color: #667eea;
  color: white;
}

.preview-btn:hover {
  background-color: #5568d3;
}

.download-btn {
  background-color: #2196f3;
  color: white;
}

.download-btn:hover {
  background-color: #1976d2;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 14px;
}

/* 对话框样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dialog {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e0e0e0;
}

.dialog-header h3 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.3s;
}

.close-btn:hover {
  background-color: #f5f5f5;
  color: #333;
}

.dialog-content {
  padding: 20px;
}

/* 删除确认对话框样式 */
.delete-confirm-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 20px 0;
}

.delete-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.delete-message {
  font-size: 16px;
  color: #333;
  margin-bottom: 20px;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.form-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.form-item input,
.form-item select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-item input:focus,
.form-item select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.password-tip {
  margin-top: 6px;
  font-size: 12px;
  color: #667eea;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
}

.cancel-btn,
.confirm-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #333;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}

.confirm-btn {
  background-color: #667eea;
  color: white;
}

.confirm-btn:hover {
  background-color: #5568d3;
}

/* 点位详情对话框 */
.detail-dialog {
  max-width: 700px;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h4 {
  margin-bottom: 12px;
  color: #333;
  font-size: 15px;
  border-bottom: 2px solid #667eea;
  padding-bottom: 8px;
}

.data-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.data-item {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.data-item span {
  color: #666;
  font-size: 14px;
}

.data-item strong {
  color: #333;
  font-size: 16px;
}

.device-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.device-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
}

.history-chart {
  max-height: 200px;
  overflow-y: auto;
}

.history-item {
  display: flex;
  justify-content: space-between;
  padding: 8px;
  border-bottom: 1px solid #eee;
  font-size: 13px;
}

/* 数据采集样式 */
.data-collect-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.collect-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  border-bottom: 2px solid #e0e0e0;
}

.tab-btn {
  padding: 10px 20px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
}

.tab-btn.active {
  color: #667eea;
  border-bottom-color: #667eea;
  font-weight: 500;
}

.collect-form {
  max-width: 1200px;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.submit-btn, .reset-btn, .upload-btn {
  padding: 10px 30px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.submit-btn {
  background: #667eea;
  color: white;
}

.reset-btn {
  background: #e0e0e0;
  color: #333;
}

.upload-btn {
  background: #667eea;
  color: white;
}

.import-section {
  padding: 20px;
}

.import-tips {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 20px;
}

.import-tips h4 {
  margin-bottom: 10px;
  color: #333;
}

.import-tips p {
  margin: 5px 0;
  font-size: 13px;
  color: #666;
}

.import-upload {
  display: flex;
  gap: 10px;
  align-items: center;
}

.import-upload input[type="file"] {
  display: none;
}

.data-list {
  padding: 20px;
}

.list-filter {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.list-filter select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th, .data-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e0e0e0;
}

.data-table th {
  background: #f8f9fa;
  font-weight: 500;
  color: #333;
}

.data-table tbody tr:hover {
  background: #f8f9fa;
}

/* 数据处理样式 */
.data-process-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.process-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  border-bottom: 2px solid #e0e0e0;
}

.process-btn {
  padding: 6px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
}

.process-btn:hover {
  background: #5568d3;
}

.filter-bar {
  margin-bottom: 15px;
}

.filter-bar select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.normal {
  background: #d4edda;
  color: #155724;
}

.status-badge.anomaly {
  background: #f8d7da;
  color: #721c24;
}

.data-table tr.anomaly {
  background: #fff3cd;
}

/* 环境监测样式 */
.monitor-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.area-selector {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.area-selector label {
  font-weight: 500;
  color: #333;
}

.area-selector select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.dashboard {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.gauge-dashboard {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.gauge-item {
  height: 220px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.dashboard-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  border-radius: 12px;
  color: white;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.card-icon {
  font-size: 40px;
}

.card-content {
  flex: 1;
}

.card-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 5px;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
}

.unit {
  font-size: 16px;
  margin-left: 5px;
  opacity: 0.8;
}

.chart-section {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-header h3 {
  margin: 0;
  color: #333;
}

.chart-controls {
  display: flex;
  gap: 10px;
}

.time-btn {
  padding: 6px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
}

.time-btn.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.device-status h3 {
  margin-bottom: 15px;
  color: #333;
}

.device-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.device-item {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  border-left: 4px solid #28a745;
}

.device-item.offline {
  border-left-color: #dc3545;
}

.device-name {
  font-weight: 500;
  margin-bottom: 5px;
}

.device-type {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
}

.device-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.device-badge.online {
  background: #d4edda;
  color: #155724;
}

.device-badge.offline {
  background: #f8d7da;
  color: #721c24;
}

/* 阈值配置样式 */
.threshold-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.threshold-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.threshold-header select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.add-btn {
  padding: 8px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.template-section {
  margin-bottom: 30px;
}

.template-section h3 {
  margin-bottom: 15px;
  color: #333;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.template-card {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  padding: 20px;
  border-radius: 12px;
  color: white;
  cursor: pointer;
  text-align: center;
  transition: transform 0.2s;
}

.template-card:hover {
  transform: translateY(-5px);
}

.template-icon {
  font-size: 40px;
  margin-bottom: 10px;
}

.template-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
}

.template-desc {
  font-size: 13px;
  opacity: 0.9;
}

.rules-section h3 {
  margin-bottom: 15px;
  color: #333;
}

.level-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.level-badge.low {
  background: #d1ecf1;
  color: #0c5460;
}

.level-badge.medium {
  background: #fff3cd;
  color: #856404;
}

.level-badge.high {
  background: #f8d7da;
  color: #721c24;
}

.edit-btn {
  padding: 4px 12px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  margin-right: 5px;
}

/* 预警管理样式 */
.alert-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.alert-filter {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.filter-btn {
  padding: 8px 20px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.filter-btn.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.alert-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.alert-card {
  background: white;
  border-left: 4px solid #ffc107;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.alert-card.high {
  border-left-color: #dc3545;
}

.alert-card.medium {
  border-left-color: #ffc107;
}

.alert-card.low {
  border-left-color: #28a745;
}

.alert-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.alert-level {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.alert-level.high {
  background: #f8d7da;
  color: #721c24;
}

.alert-level.medium {
  background: #fff3cd;
  color: #856404;
}

.alert-level.low {
  background: #d4edda;
  color: #155724;
}

.alert-time {
  color: #666;
  font-size: 13px;
}

.alert-body {
  margin-bottom: 15px;
}

.alert-area, .alert-message, .alert-factor {
  margin-bottom: 8px;
  font-size: 14px;
}

.alert-suggestion {
  background: #e7f3ff;
  padding: 10px;
  border-radius: 6px;
  margin-top: 10px;
  font-size: 14px;
  color: #004085;
}

.alert-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.alert-status {
  font-size: 13px;
  color: #666;
}

.alert-status.pending {
  color: #ffc107;
}

.alert-status.processed {
  color: #28a745;
}

/* 统计分析样式 */
.statistics-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.stats-filter {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.stats-filter select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.chart-card {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.chart-card h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
}

.report-section, .history-section {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.report-section h3, .history-section h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
}

.report-form, .history-filter {
  display: flex;
  gap: 10px;
  align-items: center;
}

.report-form select, .history-filter select, .history-filter input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.export-btn, .search-btn {
  padding: 8px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.export-btn:hover, .search-btn:hover {
  background: #5568d3;
}

/* ========== 文化遗产主题样式更新 ========== */

/* 按钮样式更新 - 使用文化遗产主题色 */
.add-user-btn, .add-btn, .submit-btn, .upload-btn, .confirm-btn, .edit-btn, .view-btn {
  background: var(--gradient-secondary);
  color: var(--text-primary);
  font-weight: 600;
  box-shadow: var(--shadow-sm);
}

.add-user-btn:hover, .add-btn:hover, .submit-btn:hover, .upload-btn:hover, .confirm-btn:hover, .edit-btn:hover, .view-btn:hover {
  background: var(--gradient-heritage);
  color: var(--text-white);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.delete-btn {
  background-color: var(--danger-color);
  box-shadow: var(--shadow-sm);
}

.delete-btn:hover {
  background-color: #C44741;
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.cancel-btn, .reset-btn {
  background-color: var(--border-color);
  color: var(--text-body);
}

.cancel-btn:hover, .reset-btn:hover {
  background-color: #D1D5DB;
  transform: translateY(-1px);
}

/* 卡片样式更新 */
.welcome-section {
  background: var(--gradient-heritage);
  box-shadow: var(--shadow-md);
}

.welcome-section h2 {
  color: var(--text-white);
  font-weight: 700;
  letter-spacing: 1px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* 角色卡片更新 */
.role-card {
  background: var(--gradient-heritage);
  box-shadow: var(--shadow-lg);
  border: 2px solid transparent;
}

.role-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-xl);
  border-color: var(--secondary-color);
}

/* 表格样式更新 */
.user-table th, .data-table th, .permission-table th {
  background: var(--primary-color);
  color: var(--text-white);
}

.user-table tr:hover, .data-table tr:hover {
  background-color: rgba(31, 78, 95, 0.05);
}

/* 状态徽章更新 */
.status-badge.enabled, .status-badge.normal, .device-badge.online {
  background-color: rgba(59, 162, 114, 0.15);
  color: var(--success-color);
  border: 1px solid var(--success-color);
}

.status-badge.disabled, .status-badge.anomaly, .device-badge.offline {
  background-color: rgba(217, 83, 79, 0.15);
  color: var(--danger-color);
  border: 1px solid var(--danger-color);
}

/* 对话框样式更新 */
.dialog {
  box-shadow: var(--shadow-xl);
}

.dialog-header {
  background: var(--gradient-primary);
  color: var(--text-white);
}

.dialog-header h3 {
  color: var(--text-white);
  font-weight: 700;
}

.close-btn {
  color: var(--text-white);
}

.close-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

/* 表单输入框更新 */
.form-item input:focus, .form-item select:focus, .filter-controls select:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(31, 78, 95, 0.1);
}

/* 标签页更新 */
.tab-btn.active, .filter-btn.active, .time-btn.active {
  background: var(--primary-color);
  color: var(--text-white);
  border-color: var(--primary-color);
  font-weight: 600;
}

/* 预警卡片更新 */
.alert-card {
  box-shadow: var(--shadow-md);
}

.alert-card:hover {
  box-shadow: var(--shadow-lg);
  transform: translateX(4px);
}

.alert-card.high {
  border-left-color: var(--danger-color);
  background: linear-gradient(to right, rgba(217, 83, 79, 0.05), var(--card-bg));
}

.alert-card.medium {
  border-left-color: var(--warning-color);
  background: linear-gradient(to right, rgba(247, 185, 85, 0.05), var(--card-bg));
}

.alert-card.low {
  border-left-color: var(--success-color);
  background: linear-gradient(to right, rgba(59, 162, 114, 0.05), var(--card-bg));
}

/* 仪表盘卡片更新 */
.dashboard-card {
  background: var(--gradient-heritage);
  box-shadow: var(--shadow-lg);
}

.dashboard-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
}

/* 图表卡片更新 */
.chart-section, .chart-card {
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border-color);
}

.chart-header h3, .chart-card h3 {
  color: var(--primary-color);
  font-weight: 700;
  position: relative;
  padding-left: 16px;
}

.chart-header h3::before, .chart-card h3::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background: var(--gradient-secondary);
  border-radius: 2px;
}

/* 模板卡片更新 */
.template-card {
  background: var(--gradient-heritage);
  box-shadow: var(--shadow-md);
  border: 2px solid transparent;
}

.template-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: var(--shadow-xl);
  border-color: var(--secondary-color);
}

/* 设备项更新 */
.device-item {
  box-shadow: var(--shadow-sm);
}

.device-item:hover {
  box-shadow: var(--shadow-md);
  transform: translateX(4px);
}

/* 文件项更新 */
.file-item {
  border: 2px solid var(--border-color);
  box-shadow: var(--shadow-sm);
}

.file-item:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-4px);
  border-color: var(--primary-color);
}

/* 仪表盘项更新 */
.gauge-item {
  box-shadow: var(--shadow-md);
  border: 2px solid var(--border-color);
}

.gauge-item:hover {
  box-shadow: var(--shadow-lg);
  border-color: var(--secondary-color);
}

/* 用户管理、区域管理等区域更新 */
.user-management, .area-management, .log-management, .permission-management,
.device-management, .data-collect-section, .data-process-section, .monitor-section, 
.threshold-section, .alert-section, .statistics-section, .file-management {
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border-color);
}

/* 区域选择器和筛选器更新 */
.area-selector, .stats-filter, .alert-filter {
  background: linear-gradient(135deg, rgba(31, 78, 95, 0.05), rgba(200, 169, 107, 0.05));
}

/* 导入提示更新 */
.import-tips {
  background: linear-gradient(135deg, rgba(31, 78, 95, 0.08), rgba(200, 169, 107, 0.08));
  border-left: 4px solid var(--primary-color);
}

.import-tips h4 {
  color: var(--primary-color);
  font-weight: 700;
}

/* 用户筛选区域更新 */
.user-filter-section, .file-upload-section {
  background: linear-gradient(135deg, rgba(31, 78, 95, 0.05), rgba(200, 169, 107, 0.05));
  border: 2px solid var(--border-color);
}

/* 预警建议更新 */
.alert-suggestion {
  background: linear-gradient(135deg, rgba(31, 78, 95, 0.1), rgba(200, 169, 107, 0.1));
  border-left: 4px solid var(--info-color);
  color: var(--text-primary);
}

/* 数据项更新 */
.data-item {
  background: linear-gradient(135deg, rgba(31, 78, 95, 0.05), rgba(200, 169, 107, 0.05));
  border: 1px solid var(--border-color);
}

.data-item:hover {
  border-color: var(--secondary-color);
  box-shadow: var(--shadow-sm);
}

/* 历史项更新 */
.history-item:hover {
  background: linear-gradient(to right, rgba(31, 78, 95, 0.05), transparent);
  padding-left: 16px;
}

/* 权限配置更新 */
.permission-section h4 {
  color: var(--primary-color);
  font-weight: 700;
  border-bottom: 2px solid var(--secondary-color);
}

/* 报告和历史区域更新 */
.report-section, .history-section {
  box-shadow: var(--shadow-md);
  border: 2px solid var(--border-color);
}

.report-section h3, .history-section h3 {
  color: var(--primary-color);
  font-weight: 700;
}

/* 导出和搜索按钮更新 */
.export-btn, .search-btn, .process-btn {
  background: var(--gradient-secondary);
  color: var(--text-primary);
  font-weight: 600;
  box-shadow: var(--shadow-sm);
}

.export-btn:hover, .search-btn:hover, .process-btn:hover {
  background: var(--gradient-heritage);
  color: var(--text-white);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.refresh-btn {
  background: var(--primary-color);
  color: var(--text-white);
}

.refresh-btn:hover {
  background: var(--primary-dark);
}

/* 级别徽章更新 */
.level-badge.low, .alert-level.low {
  background-color: rgba(59, 162, 114, 0.15);
  color: var(--success-color);
  border: 1px solid var(--success-color);
}

.level-badge.medium, .alert-level.medium {
  background-color: rgba(247, 185, 85, 0.15);
  color: var(--warning-color);
  border: 1px solid var(--warning-color);
}

.level-badge.high, .alert-level.high {
  background-color: rgba(217, 83, 79, 0.15);
  color: var(--danger-color);
  border: 1px solid var(--danger-color);
}

/* 详情区域更新 */
.detail-section h4 {
  color: var(--primary-color);
  font-weight: 700;
  border-bottom: 3px solid var(--secondary-color);
}

/* 密码提示更新 */
.password-tip {
  color: var(--primary-color);
  font-weight: 500;
}

/* 空状态更新 */
.empty-state {
  color: var(--text-secondary);
}

/* 区域复选框更新 */
.area-checkbox label:hover {
  color: var(--primary-color);
}

/* 筛选按钮更新 */
.reset-filter-btn {
  background-color: var(--primary-color);
  color: var(--text-white);
}

.reset-filter-btn:hover {
  background-color: var(--primary-dark);
}

/* 预览和下载按钮更新 */
.preview-btn {
  background: var(--gradient-secondary);
  color: var(--text-primary);
  font-weight: 600;
}

.preview-btn:hover {
  background: var(--gradient-heritage);
  color: var(--text-white);
}

.download-btn {
  background-color: var(--info-color);
}

.download-btn:hover {
  background-color: #3A8FD9;
}
</style>