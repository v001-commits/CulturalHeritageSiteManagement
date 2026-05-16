<template>
  <div class="baidu-map-wrapper">
    <div class="map-controls">
      <h4>图层控制</h4>
      <label class="control-label">
        <input type="checkbox" v-model="showDevices" @change="toggleDevices">
        <span>监测设备</span>
      </label>
      <label class="control-label">
        <input type="checkbox" v-model="showAreas" @change="toggleAreas">
        <span>区域边界</span>
      </label>
      <label class="control-label">
        <input type="checkbox" v-model="showLabels" @change="toggleLabels">
        <span>设备标签</span>
      </label>
      
      <h4 style="margin-top: 20px;">风险等级筛选</h4>
      <label class="control-label">
        <input type="checkbox" v-model="riskFilter.safe" @change="filterDevices">
        <span class="risk-safe">● 安全</span>
      </label>
      <label class="control-label">
        <input type="checkbox" v-model="riskFilter.low" @change="filterDevices">
        <span class="risk-low">● 低风险</span>
      </label>
      <label class="control-label">
        <input type="checkbox" v-model="riskFilter.medium" @change="filterDevices">
        <span class="risk-medium">● 中风险</span>
      </label>
      <label class="control-label">
        <input type="checkbox" v-model="riskFilter.high" @change="filterDevices">
        <span class="risk-high">● 高风险</span>
      </label>
      
      <h4 style="margin-top: 20px;">快速定位</h4>
      <div v-if="areas.length === 0" class="loading-tip">
        <p style="color: #999; font-size: 12px; text-align: center; margin: 10px 0;">
          正在加载区域数据...
        </p>
      </div>
      <button 
        v-for="area in areas" 
        :key="area.id" 
        @click="flyToArea(area)" 
        class="location-btn"
        :disabled="!map"
      >
        📍 {{ area.areaName }}
      </button>
      
      <h4 style="margin-top: 20px;">地图操作</h4>
      <button @click="forceRefreshMap" class="location-btn" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
        🔄 刷新地图
      </button>
      <div v-if="mapLoadError" class="error-tip">
        <p style="color: #f44336; font-size: 12px; margin: 10px 0 0 0;">
          ⚠️ 地图加载异常，请点击刷新
        </p>
      </div>
    </div>
    
    <div id="baidu-map-container" class="baidu-map"></div>
    
    <!-- 设备详情弹窗 -->
    <div v-if="selectedDevice" class="device-info-window">
      <div class="info-header">
        <h3>{{ selectedDevice.deviceName }}</h3>
        <button class="close-btn" @click="closeDeviceInfo">&times;</button>
      </div>
      <div class="info-content">
        <div class="info-section">
          <h4>基本信息</h4>
          <div class="info-row">
            <span class="label">设备类型：</span>
            <span class="value">{{ selectedDevice.deviceType }}</span>
          </div>
          <div class="info-row">
            <span class="label">所属区域：</span>
            <span class="value">{{ getAreaName(selectedDevice.areaId) }}</span>
          </div>
          <div class="info-row">
            <span class="label">设备状态：</span>
            <span :class="['value', 'status-' + selectedDevice.status]">
              {{ selectedDevice.status === 'online' ? '● 在线' : '● 离线' }}
            </span>
          </div>
          <div class="info-row">
            <span class="label">风险等级：</span>
            <span :class="['value', 'risk-' + selectedDevice.riskLevel]">
              {{ getRiskText(selectedDevice.riskLevel) }}
            </span>
          </div>
          <div class="info-row">
            <span class="label">位置坐标：</span>
            <span class="value">{{ selectedDevice.latitude }}, {{ selectedDevice.longitude }}</span>
          </div>
          <div v-if="selectedDevice.altitude" class="info-row">
            <span class="label">海拔高度：</span>
            <span class="value">{{ selectedDevice.altitude }}m</span>
          </div>
        </div>
        
        <div v-if="deviceData" class="info-section realtime-data">
          <h4>实时监测数据</h4>
          <div class="data-grid">
            <div v-if="deviceData.temperature !== null && deviceData.temperature !== undefined" class="data-card">
              <div class="data-icon">🌡️</div>
              <div class="data-info">
                <div class="data-label">温度</div>
                <div class="data-value">{{ deviceData.temperature }}℃</div>
              </div>
            </div>
            <div v-if="deviceData.humidity !== null && deviceData.humidity !== undefined" class="data-card">
              <div class="data-icon">💧</div>
              <div class="data-info">
                <div class="data-label">湿度</div>
                <div class="data-value">{{ deviceData.humidity }}%</div>
              </div>
            </div>
            <div v-if="deviceData.pm25 !== null && deviceData.pm25 !== undefined" class="data-card">
              <div class="data-icon">🌫️</div>
              <div class="data-info">
                <div class="data-label">PM2.5</div>
                <div class="data-value">{{ deviceData.pm25 }}</div>
              </div>
            </div>
            <div v-if="deviceData.so2 !== null && deviceData.so2 !== undefined" class="data-card">
              <div class="data-icon">☁️</div>
              <div class="data-info">
                <div class="data-label">SO₂</div>
                <div class="data-value">{{ deviceData.so2 }}</div>
              </div>
            </div>
            <div v-if="deviceData.no2 !== null && deviceData.no2 !== undefined" class="data-card">
              <div class="data-icon">🌬️</div>
              <div class="data-info">
                <div class="data-label">NO₂</div>
                <div class="data-value">{{ deviceData.no2 }}</div>
              </div>
            </div>
            <div v-if="deviceData.windSpeed !== null && deviceData.windSpeed !== undefined" class="data-card">
              <div class="data-icon">💨</div>
              <div class="data-info">
                <div class="data-label">风速</div>
                <div class="data-value">{{ deviceData.windSpeed }} m/s</div>
              </div>
            </div>
            <div v-if="deviceData.windDirection" class="data-card">
              <div class="data-icon">🧭</div>
              <div class="data-info">
                <div class="data-label">风向</div>
                <div class="data-value">{{ deviceData.windDirection }}</div>
              </div>
            </div>
            <div v-if="deviceData.lightIntensity !== null && deviceData.lightIntensity !== undefined" class="data-card">
              <div class="data-icon">☀️</div>
              <div class="data-info">
                <div class="data-label">光照</div>
                <div class="data-value">{{ deviceData.lightIntensity }} lux</div>
              </div>
            </div>
          </div>
          <div v-if="deviceData.recordTime" class="update-time">
            <span>更新时间：{{ formatTime(deviceData.recordTime) }}</span>
          </div>
        </div>
        
        <div v-if="selectedDevice.photoUrl" class="info-section device-photo">
          <h4>现场实景照片</h4>
          <div class="photo-container">
            <img :src="selectedDevice.photoUrl" alt="设备现场照片" @error="handleImageError" @click="previewImage" />
            <div class="photo-overlay">
              <span>点击查看大图</span>
            </div>
          </div>
        </div>
        
        <div class="info-section action-buttons">
          <button @click="viewHistoryTrend" class="action-btn primary">
            <span class="btn-icon">📊</span>
            <span>查看历史趋势</span>
          </button>
          <button @click="refreshData" class="action-btn secondary">
            <span class="btn-icon">🔄</span>
            <span>刷新数据</span>
          </button>
        </div>
      </div>
    </div>
    
    <!-- 图片预览弹窗 -->
    <div v-if="showImagePreview" class="image-preview-overlay" @click="showImagePreview = false">
      <div class="image-preview-container">
        <img :src="previewImageUrl" alt="预览图片" />
        <button class="preview-close-btn" @click="showImagePreview = false">&times;</button>
      </div>
    </div>
  </div>
</template>

<script>
import { getAreasWithDevices, getDeviceDetail } from '../api/visualization'

export default {
  name: 'BaiduMapViewer',
  data() {
    return {
      map: null,
      areas: [],
      devices: [],
      showDevices: true,
      showAreas: true,
      showLabels: true,
      selectedDevice: null,
      deviceData: null,
      markers: [],
      polygons: [],
      labels: [],
      riskFilter: {
        safe: true,
        low: true,
        medium: true,
        high: true
      },
      showImagePreview: false,
      previewImageUrl: '',
      mapLoaded: false,
      mapLoadError: false,
      dataLoading: false
    }
  },
  mounted() {
    // 延迟初始化，确保DOM已经渲染
    this.$nextTick(() => {
      setTimeout(() => {
        this.initBaiduMap()
      }, 100)
    })
    
    // 监听窗口大小变化
    window.addEventListener('resize', this.handleResize)
  },
  activated() {
    // 组件被激活时（从v-show=false变为true，或从keep-alive缓存中恢复）
    console.log('BaiduMapViewer activated')
    this.$nextTick(() => {
      const container = document.getElementById('baidu-map-container')
      if (!container) {
        console.warn('百度地图容器不存在')
        return
      }
      
      // 检查容器是否可见
      if (container.offsetHeight === 0) {
        console.log('容器尚未显示，等待显示后再处理')
        // 使用MutationObserver监听容器尺寸变化
        const observer = new MutationObserver(() => {
          if (container.offsetHeight > 0) {
            console.log('容器已显示，开始处理')
            observer.disconnect()
            if (this.map) {
              console.log('重置百度地图尺寸')
              this.map.reset()
              if (this.areas.length > 0) {
                const firstArea = this.areas[0]
                if (firstArea.latitude && firstArea.longitude) {
                  this.map.setCenter(new BMap.Point(firstArea.longitude, firstArea.latitude))
                }
              }
            } else {
              console.log('初始化百度地图')
              this.initBaiduMap()
            }
          }
        })
        observer.observe(container, { attributes: true, attributeFilter: ['style'] })
        // 5秒后自动断开观察器
        setTimeout(() => observer.disconnect(), 5000)
        return
      }
      
      if (this.map) {
        console.log('重置百度地图尺寸')
        // 百度地图需要调用reset方法来重新计算容器尺寸
        setTimeout(() => {
          this.map.reset()
          // 如果有中心点，重新定位
          if (this.areas.length > 0) {
            const firstArea = this.areas[0]
            if (firstArea.latitude && firstArea.longitude) {
              this.map.setCenter(new BMap.Point(firstArea.longitude, firstArea.latitude))
            }
          }
        }, 100)
      } else {
        // 如果地图还未初始化，尝试初始化
        console.log('地图未初始化，尝试初始化')
        setTimeout(() => {
          this.initBaiduMap()
        }, 100)
      }
    })
  },
  beforeUnmount() {
    if (this.map) {
      this.map.clearOverlays()
    }
    window.removeEventListener('resize', this.handleResize)
  },
  methods: {
    initBaiduMap() {
      // 检查百度地图API是否加载
      if (typeof BMap === 'undefined') {
        console.error('百度地图API未加载，请检查网络连接')
        this.mapLoadError = true
        setTimeout(() => this.initBaiduMap(), 2000)
        return
      }
      
      console.log('百度地图API已加载，开始初始化地图')
      this.createMap()
    },
    
    createMap() {
      const container = document.getElementById('baidu-map-container')
      if (!container) {
        console.error('地图容器不存在')
        this.mapLoadError = true
        return
      }
      
      console.log('开始创建百度地图，容器尺寸:', container.offsetWidth, 'x', container.offsetHeight)
      
      try {
        this.map = new BMap.Map('baidu-map-container', {
          enableMapClick: false  // 禁用底图可点功能，提高性能
        })
        
        // 初始化地图，设置中心点坐标和地图级别（中国中心）
        const point = new BMap.Point(105.0, 35.0)
        this.map.centerAndZoom(point, 5)
        this.map.enableScrollWheelZoom(true)
        
        console.log('百度地图对象创建成功，等待瓦片加载...')
        
        // 监听瓦片加载事件
        let tilesLoadedCount = 0
        this.map.addEventListener('tilesloaded', () => {
          tilesLoadedCount++
          console.log(`地图瓦片加载事件触发 (${tilesLoadedCount}次)`)
          if (tilesLoadedCount === 1) {
            this.mapLoaded = true
            this.mapLoadError = false
            console.log('✓ 地图瓦片首次加载完成')
          }
        })
        
        // 监听地图加载错误
        this.map.addEventListener('error', (e) => {
          console.error('百度地图加载错误:', e)
          this.mapLoadError = true
        })
        
        // 添加地图控件
        this.map.addControl(new BMap.NavigationControl({
          anchor: BMAP_ANCHOR_TOP_LEFT,
          type: BMAP_NAVIGATION_CONTROL_LARGE
        }))
        this.map.addControl(new BMap.ScaleControl({
          anchor: BMAP_ANCHOR_BOTTOM_LEFT
        }))
        this.map.addControl(new BMap.MapTypeControl({
          anchor: BMAP_ANCHOR_TOP_RIGHT,
          mapTypes: [BMAP_NORMAL_MAP, BMAP_HYBRID_MAP]
        }))
        
        // 不设置自定义样式，使用默认样式以减少加载问题
        // 如果瓦片加载有问题，自定义样式可能会加剧问题
        
        // 添加加载超时检测（延长到15秒）
        setTimeout(() => {
          if (!this.mapLoaded) {
            console.error('⚠️ 地图瓦片加载超时（15秒）')
            console.error('可能的原因：')
            console.error('1. 百度地图AK配额用尽或被限制')
            console.error('2. 网络防火墙拦截了百度地图请求')
            console.error('3. 百度地图服务暂时不可用')
            console.error('4. 浏览器缓存损坏')
            console.error('建议操作：')
            console.error('- 清除浏览器缓存（Ctrl+Shift+Delete）')
            console.error('- 检查浏览器控制台Network标签，查看瓦片请求状态')
            console.error('- 验证百度地图AK是否有效：https://lbsyun.baidu.com/apiconsole/key')
            this.mapLoadError = true
          }
        }, 15000)
        
        // 延迟加载数据，确保地图完全初始化
        setTimeout(() => {
          // 强制地图重新计算尺寸
          if (this.map) {
            this.map.reset()
            console.log('地图尺寸已重置')
          }
          this.loadData()
        }, 500)
      } catch (error) {
        console.error('百度地图初始化失败:', error)
        this.mapLoadError = true
      }
    },
    
    async loadData() {
      this.dataLoading = true
      try {
        console.log('📡 开始加载区域和设备数据...')
        const res = await getAreasWithDevices()
        if (res.code === 200) {
          this.areas = res.data
          console.log('✓ 数据加载成功，区域数量:', this.areas.length)
          
          // 统计设备总数
          let deviceCount = 0
          this.areas.forEach(area => {
            if (area.devices) {
              deviceCount += area.devices.length
            }
          })
          console.log('✓ 设备总数:', deviceCount)
          
          this.renderData()
        } else {
          console.error('❌ 数据加载失败:', res.message)
        }
      } catch (error) {
        console.error('❌ 加载数据失败:', error.message)
        // 不显示弹窗，只在控制台输出错误
      } finally {
        this.dataLoading = false
      }
    },
    
    renderData() {
      if (!this.map) return
      
      // 清除现有覆盖物
      this.clearOverlays()
      
      // 渲染区域边界
      if (this.showAreas) {
        this.areas.forEach(area => {
          this.renderAreaBoundary(area)
        })
      }
      
      // 渲染设备点位
      if (this.showDevices) {
        this.areas.forEach(area => {
          if (area.devices) {
            area.devices.forEach(device => {
              if (this.shouldShowDevice(device)) {
                this.renderDeviceMarker(device, area)
              }
            })
          }
        })
      }
    },
    
    clearOverlays() {
      this.markers.forEach(marker => this.map.removeOverlay(marker))
      this.polygons.forEach(polygon => this.map.removeOverlay(polygon))
      this.labels.forEach(label => this.map.removeOverlay(label))
      this.markers = []
      this.polygons = []
      this.labels = []
    },
    
    shouldShowDevice(device) {
      const riskLevel = device.riskLevel || 'safe'
      return this.riskFilter[riskLevel]
    },
    
    renderAreaBoundary(area) {
      // 必须有经纬度才能渲染
      if (!area.latitude || !area.longitude) return

      try {
        let points
        let center

        // 如果有边界点则使用，否则根据经纬度自动生成
        if (area.boundaryPoints) {
          points = JSON.parse(area.boundaryPoints)
        } else {
          points = this.generateBoundaryPoints(
            parseFloat(area.longitude),
            parseFloat(area.latitude)
          )
        }

        const baiduPoints = points.map(p => new BMap.Point(p[0], p[1]))

        const polygon = new BMap.Polygon(baiduPoints, {
          strokeColor: this.getRiskColor(area.riskLevel),
          strokeWeight: 3,
          strokeOpacity: 0.9,
          strokeStyle: 'solid',
          fillColor: this.getRiskColor(area.riskLevel),
          fillOpacity: 0.25
        })

        this.map.addOverlay(polygon)
        this.polygons.push(polygon)

        // 添加区域标签（使用中心点或多边形中心）
        if (area.longitude && area.latitude) {
          center = new BMap.Point(parseFloat(area.longitude), parseFloat(area.latitude))
        } else {
          center = this.getPolygonCenter(baiduPoints)
        }

        const label = new BMap.Label(area.areaName, {
          position: center,
          offset: new BMap.Size(0, 0)
        })
        label.setStyle({
          color: '#333',
          fontSize: '15px',
          fontWeight: 'bold',
          border: '2px solid ' + this.getRiskColor(area.riskLevel),
          background: 'rgba(255, 255, 255, 0.95)',
          padding: '6px 12px',
          borderRadius: '6px',
          boxShadow: '0 2px 8px rgba(0,0,0,0.2)'
        })
        this.map.addOverlay(label)
        this.labels.push(label)

        // 添加点击事件
        polygon.addEventListener('click', () => {
          this.showAreaInfo(area)
        })
      } catch (error) {
        console.error('渲染区域边界失败', error)
      }
    },

    // 根据中心经纬度自动生成矩形边界点
    generateBoundaryPoints(centerLon, centerLat, radiusKm = 0.3) {
      // radiusKm: 边界半径，默认0.3公里
      // 1度纬度约等于111公里，经度随纬度变化
      const latOffset = radiusKm / 111
      const lonOffset = radiusKm / (111 * Math.cos(centerLat * Math.PI / 180))

      // 返回矩形的四个角点（顺时针）
      return [
        [centerLon - lonOffset, centerLat - latOffset], // 西南
        [centerLon + lonOffset, centerLat - latOffset], // 东南
        [centerLon + lonOffset, centerLat + latOffset], // 东北
        [centerLon - lonOffset, centerLat + latOffset]  // 西北
      ]
    },
    
    renderDeviceMarker(device, area) {
      if (!device.latitude || !device.longitude) return
      
      const point = new BMap.Point(parseFloat(device.longitude), parseFloat(device.latitude))
      
      // 创建自定义图标
      const iconSize = new BMap.Size(32, 32)
      const icon = new BMap.Icon(
        this.getMarkerIconUrl(device.riskLevel),
        iconSize,
        {
          imageSize: iconSize
        }
      )
      
      const marker = new BMap.Marker(point, { icon })
      this.map.addOverlay(marker)
      this.markers.push(marker)
      
      // 添加点击事件
      marker.addEventListener('click', () => {
        this.showDeviceInfo(device)
      })
      
      // 添加鼠标悬停效果
      marker.addEventListener('mouseover', () => {
        marker.setAnimation(BMAP_ANIMATION_BOUNCE)
      })
      
      marker.addEventListener('mouseout', () => {
        marker.setAnimation(null)
      })
      
      // 添加标签
      if (this.showLabels) {
        const label = new BMap.Label(device.deviceName, {
          offset: new BMap.Size(18, -18)
        })
        label.setStyle({
          color: '#333',
          fontSize: '12px',
          border: 'none',
          background: 'rgba(255, 255, 255, 0.95)',
          padding: '4px 10px',
          borderRadius: '4px',
          boxShadow: '0 2px 6px rgba(0,0,0,0.2)',
          whiteSpace: 'nowrap'
        })
        marker.setLabel(label)
      }
    },
    
    async showDeviceInfo(device) {
      try {
        console.log('📱 点击设备:', device.deviceName, '(ID:', device.id, ')')
        
        // 获取设备详情
        const res = await getDeviceDetail(device.id)
        
        if (res.code === 200) {
          this.selectedDevice = res.data.device
          this.deviceData = res.data.latestData
          
          console.log('✓ 设备数据加载成功')
          console.log('  - 设备信息:', this.selectedDevice)
          console.log('  - 实时数据:', this.deviceData)
          
          // 地图中心移动到设备位置
          const point = new BMap.Point(
            parseFloat(device.longitude),
            parseFloat(device.latitude)
          )
          this.map.panTo(point)
        } else {
          console.error('❌ 获取设备详情失败:', res.message)
        }
      } catch (error) {
        console.error('❌ 获取设备详情失败:', error.message)
      }
    },
    
    showAreaInfo(area) {
      const infoWindow = new BMap.InfoWindow(`
        <div style="padding: 10px; min-width: 200px;">
          <h3 style="margin: 0 0 10px 0; font-size: 16px;">${area.areaName}</h3>
          <p style="margin: 5px 0;"><strong>遗产类型：</strong>${this.getHeritageTypeName(area.heritageType)}</p>
          <p style="margin: 5px 0;"><strong>风险等级：</strong><span style="color: ${this.getRiskColor(area.riskLevel)}">${this.getRiskText(area.riskLevel)}</span></p>
          <p style="margin: 5px 0;"><strong>描述：</strong>${area.description || '暂无描述'}</p>
          <p style="margin: 5px 0;"><strong>设备数量：</strong>${area.devices ? area.devices.length : 0}个</p>
        </div>
      `, {
        width: 280,
        height: 180,
        title: '区域信息'
      })
      
      const center = this.getPolygonCenterFromArea(area)
      this.map.openInfoWindow(infoWindow, center)
    },
    
    getPolygonCenterFromArea(area) {
      if (area.latitude && area.longitude) {
        return new BMap.Point(parseFloat(area.longitude), parseFloat(area.latitude))
      }
      if (area.boundaryPoints) {
        try {
          const points = JSON.parse(area.boundaryPoints)
          const baiduPoints = points.map(p => new BMap.Point(p[0], p[1]))
          return this.getPolygonCenter(baiduPoints)
        } catch (error) {
          return new BMap.Point(116.404, 39.915)
        }
      }
      return new BMap.Point(116.404, 39.915)
    },
    
    flyToArea(area) {
      if (!this.map || !area.latitude || !area.longitude) {
        console.warn('无法定位到区域:', area)
        return
      }
      
      console.log('快速定位到:', area.areaName, area.latitude, area.longitude)
      
      const point = new BMap.Point(parseFloat(area.longitude), parseFloat(area.latitude))
      
      // 先移动到目标位置
      this.map.panTo(point)
      
      // 延迟缩放，创建动画效果
      setTimeout(() => {
        this.map.setZoom(14)
      }, 300)
      
      setTimeout(() => {
        this.map.setZoom(15)
      }, 600)
      
      // 显示提示信息
      const infoWindow = new BMap.InfoWindow(`
        <div style="padding: 10px; min-width: 200px;">
          <h3 style="margin: 0 0 10px 0; font-size: 16px; color: #667eea;">${area.areaName}</h3>
          <p style="margin: 5px 0;"><strong>遗产类型：</strong>${this.getHeritageTypeName(area.heritageType)}</p>
          <p style="margin: 5px 0;"><strong>风险等级：</strong><span style="color: ${this.getRiskColor(area.riskLevel)}">${this.getRiskText(area.riskLevel)}</span></p>
          <p style="margin: 5px 0;"><strong>描述：</strong>${area.description || '暂无描述'}</p>
        </div>
      `, {
        width: 280,
        height: 150,
        title: '区域信息'
      })
      
      setTimeout(() => {
        this.map.openInfoWindow(infoWindow, point)
      }, 800)
    },
    
    toggleDevices() {
      console.log('切换监测设备显示:', this.showDevices)
      this.renderData()
    },
    
    toggleAreas() {
      console.log('切换区域边界显示:', this.showAreas)
      this.renderData()
    },
    
    toggleLabels() {
      console.log('切换设备标签显示:', this.showLabels)
      this.renderData()
    },
    
    filterDevices() {
      console.log('风险等级筛选:', this.riskFilter)
      this.renderData()
    },
    
    getRiskColor(riskLevel) {
      const colors = {
        safe: '#4CAF50',
        low: '#FFC107',
        medium: '#FF9800',
        high: '#F44336'
      }
      return colors[riskLevel] || '#4CAF50'
    },
    
    getRiskText(riskLevel) {
      const texts = {
        safe: '安全',
        low: '低风险',
        medium: '中风险',
        high: '高风险'
      }
      return texts[riskLevel] || '未知'
    },
    
    getHeritageTypeName(type) {
      const names = {
        ancient_building: '古建筑',
        cave: '石窟寺',
        site: '遗址',
        garden: '园林'
      }
      return names[type] || '未知类型'
    },
    
    getAreaName(areaId) {
      const area = this.areas.find(a => a.id === areaId)
      return area ? area.areaName : '未知区域'
    },
    
    getMarkerIconUrl(riskLevel) {
      // 使用SVG数据URL作为图标
      const color = this.getRiskColor(riskLevel)
      const svg = `
        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32">
          <defs>
            <filter id="shadow" x="-50%" y="-50%" width="200%" height="200%">
              <feGaussianBlur in="SourceAlpha" stdDeviation="2"/>
              <feOffset dx="0" dy="2" result="offsetblur"/>
              <feComponentTransfer>
                <feFuncA type="linear" slope="0.3"/>
              </feComponentTransfer>
              <feMerge>
                <feMergeNode/>
                <feMergeNode in="SourceGraphic"/>
              </feMerge>
            </filter>
          </defs>
          <circle cx="16" cy="16" r="14" fill="${color}" stroke="white" stroke-width="3" filter="url(#shadow)"/>
          <circle cx="16" cy="16" r="6" fill="white" opacity="0.8"/>
        </svg>
      `
      return 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(svg)))
    },
    
    getPolygonCenter(points) {
      let x = 0, y = 0
      points.forEach(p => {
        x += p.lng
        y += p.lat
      })
      return new BMap.Point(x / points.length, y / points.length)
    },
    
    formatTime(time) {
      if (!time) return '-'
      const date = new Date(time)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    },
    
    handleImageError(e) {
      e.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgZmlsbD0iI2YwZjBmMCIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LXNpemU9IjE2IiBmaWxsPSIjOTk5IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+5pqC5peg5Zu+54mHPC90ZXh0Pjwvc3ZnPg=='
    },
    
    previewImage() {
      this.previewImageUrl = this.selectedDevice.photoUrl
      this.showImagePreview = true
    },
    
    viewHistoryTrend() {
      console.log('📊 查看历史趋势:', this.selectedDevice.deviceName)
      
      // 触发事件传递给父组件
      this.$emit('view-trend', this.selectedDevice)
      
      // 显示提示信息
      alert(`即将查看设备"${this.selectedDevice.deviceName}"的历史趋势数据\n\n功能开发中，敬请期待！`)
      
      // 暂时关闭设备详情窗口
      // this.closeDeviceInfo()
    },
    
    async refreshData() {
      if (!this.selectedDevice) return
      
      console.log('🔄 刷新设备数据:', this.selectedDevice.deviceName)
      
      try {
        const res = await getDeviceDetail(this.selectedDevice.id)
        if (res.code === 200) {
          this.selectedDevice = res.data.device
          this.deviceData = res.data.latestData
          console.log('✓ 数据刷新成功')
          
          // 显示刷新成功提示
          this.$nextTick(() => {
            const updateTime = document.querySelector('.update-time')
            if (updateTime) {
              updateTime.style.color = '#4CAF50'
              updateTime.style.fontWeight = 'bold'
              setTimeout(() => {
                updateTime.style.color = '#999'
                updateTime.style.fontWeight = 'normal'
              }, 2000)
            }
          })
        } else {
          console.error('❌ 刷新失败:', res.message)
          alert('刷新失败: ' + res.message)
        }
      } catch (error) {
        console.error('❌ 刷新数据失败', error)
        alert('刷新失败: ' + error.message)
      }
    },
    
    closeDeviceInfo() {
      this.selectedDevice = null
      this.deviceData = null
    },
    
    handleResize() {
      // 窗口大小变化时，重新调整地图尺寸
      if (this.map) {
        setTimeout(() => {
          this.map.reset()
        }, 100)
      }
    },
    
    forceRefreshMap() {
      // 强制刷新地图
      if (this.map) {
        console.log('🔄 强制刷新地图...')
        this.mapLoaded = false
        this.mapLoadError = false
        
        // 清除覆盖物
        this.clearOverlays()
        
        // 重新设置中心点，触发瓦片重新加载
        const center = this.map.getCenter()
        this.map.panTo(center)
        
        // 强制重绘
        this.map.reset()
        
        // 显示刷新提示
        console.log('✓ 地图已刷新，正在重新加载数据...')
        
        // 重新加载数据
        setTimeout(() => {
          this.loadData()
        }, 500)
      } else {
        console.warn('地图对象不存在，尝试重新初始化...')
        this.initBaiduMap()
      }
    }
  }
}
</script>

<style scoped>
.baidu-map-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.baidu-map {
  width: 100%;
  height: 100%;
}

.map-controls {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(255, 255, 255, 0.98);
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  z-index: 1000;
  min-width: 200px;
  max-height: calc(100% - 20px);
  overflow-y: auto;
  overflow-x: hidden;
}

.map-controls h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #333;
  font-weight: 600;
  border-bottom: 2px solid #2196F3;
  padding-bottom: 6px;
}

.control-label {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  padding: 5px;
  border-radius: 4px;
}

.control-label:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #2196F3;
}

.control-label:active {
  background: rgba(102, 126, 234, 0.2);
  transform: scale(0.98);
}

.control-label input {
  margin-right: 8px;
  cursor: pointer;
  width: 16px;
  height: 16px;
}

.location-btn {
  display: block;
  width: 100%;
  padding: 10px;
  margin-bottom: 6px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.location-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.2);
}

.location-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.location-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: #ccc;
}

.device-info-window {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.3);
  z-index: 2000;
  min-width: 500px;
  max-width: 700px;
  max-height: 85vh;
  overflow-y: auto;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 24px;
  border-bottom: 1px solid #eee;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px 12px 0 0;
}

.info-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.close-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  font-size: 28px;
  cursor: pointer;
  color: white;
  padding: 0;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: rotate(90deg);
}

.info-content {
  padding: 24px;
}

.info-section {
  margin-bottom: 24px;
}

.info-section:last-child {
  margin-bottom: 0;
}

.info-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
  display: flex;
  align-items: center;
}

.info-section h4::before {
  content: '';
  width: 4px;
  height: 18px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin-right: 10px;
  border-radius: 2px;
}

.info-row {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
  line-height: 1.6;
}

.info-row .label {
  color: #666;
  min-width: 90px;
  font-weight: 500;
}

.info-row .value {
  color: #333;
  font-weight: 500;
  flex: 1;
}

.status-online {
  color: #4CAF50;
}

.status-offline {
  color: #F44336;
}

.risk-safe { color: #4CAF50; font-weight: 600; }
.risk-low { color: #FFC107; font-weight: 600; }
.risk-medium { color: #FF9800; font-weight: 600; }
.risk-high { color: #F44336; font-weight: 600; }

.realtime-data {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
}

.data-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.data-card {
  background: white;
  padding: 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  transition: all 0.3s;
}

.data-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.data-icon {
  font-size: 32px;
  margin-right: 12px;
}

.data-info {
  flex: 1;
}

.data-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.data-value {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.update-time {
  text-align: center;
  font-size: 12px;
  color: #999;
  padding-top: 12px;
  border-top: 1px dashed #ddd;
}

.device-photo {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
}

.photo-container {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
}

.photo-container img {
  width: 100%;
  display: block;
  border-radius: 8px;
  transition: all 0.3s;
}

.photo-container:hover img {
  transform: scale(1.05);
}

.photo-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.7), transparent);
  color: white;
  padding: 12px;
  text-align: center;
  font-size: 14px;
  opacity: 0;
  transition: opacity 0.3s;
}

.photo-container:hover .photo-overlay {
  opacity: 1;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.action-btn {
  flex: 1;
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s;
}

.action-btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.action-btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
}

.action-btn.secondary {
  background: #f0f0f0;
  color: #333;
}

.action-btn.secondary:hover {
  background: #e0e0e0;
}

.btn-icon {
  font-size: 18px;
}

.image-preview-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  z-index: 3000;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.image-preview-container {
  position: relative;
  max-width: 90%;
  max-height: 90%;
}

.image-preview-container img {
  max-width: 100%;
  max-height: 90vh;
  border-radius: 8px;
}

.preview-close-btn {
  position: absolute;
  top: -40px;
  right: 0;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  font-size: 32px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.preview-close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: rotate(90deg);
}

/* 滚动条样式 */
.map-controls::-webkit-scrollbar,
.device-info-window::-webkit-scrollbar {
  width: 8px;
}

.map-controls::-webkit-scrollbar-track,
.device-info-window::-webkit-scrollbar-track {
  background: #e8e8e8;
  border-radius: 4px;
  margin: 4px 0;
}

.map-controls::-webkit-scrollbar-thumb,
.device-info-window::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
  border: 2px solid #e8e8e8;
}

.map-controls::-webkit-scrollbar-thumb:hover,
.device-info-window::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #5a6fd6 0%, #6a4192 100%);
}
</style>
