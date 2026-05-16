<template>
  <div class="cesium-wrapper">
    <div class="cesium-controls">
      <h4>视角模式</h4>
      <div class="view-mode-buttons">
        <button :class="['mode-btn', {active: viewMode === 'space'}]" @click="switchViewMode('space')">🛰️ 太空视角</button>
        <button :class="['mode-btn', {active: viewMode === 'ground'}]" @click="switchViewMode('ground')">🏞️ 地面视角</button>
        <button :class="['mode-btn', {active: viewMode === 'heritage'}]" @click="switchViewMode('heritage')">🏛️ 遗产地视角</button>
      </div>
      
      <h4 style="margin-top: 20px;">遗产地快速定位</h4>
      <button v-for="area in areas" :key="area.id" @click="flyToArea(area)" :class="['view-btn', {active: selectedArea && selectedArea.id === area.id}]">
        {{ area.areaName }}
      </button>
      
      <h4 style="margin-top: 20px;">漫游模式</h4>
      <div class="tour-controls">
        <button :class="['tour-btn', {active: tourMode === 'free'}]" @click="setTourMode('free')">自由视角</button>
        <button :class="['tour-btn', {active: tourMode === 'path'}]" @click="startPathTour" :disabled="!selectedArea">固定路径</button>
        <button v-if="tourMode === 'path' && isTourPlaying" @click="stopPathTour" class="stop-btn">停止漫游</button>
      </div>
      
      <h4 style="margin-top: 20px;">时段对比</h4>
      <div class="time-controls">
        <select v-model="selectedSeason" @change="loadSeasonData">
          <option value="spring">春季</option>
          <option value="summer">夏季</option>
          <option value="autumn">秋季</option>
          <option value="winter">冬季</option>
        </select>
        <select v-model="selectedTimeSlot" @change="loadTimeSlotData">
          <option value="morning">早晨 (6-12)</option>
          <option value="afternoon">下午 (12-18)</option>
          <option value="evening">傍晚 (18-24)</option>
          <option value="night">夜间 (0-6)</option>
        </select>
      </div>
      
      <h4 style="margin-top: 20px;">风险等级图例</h4>
      <div class="risk-legend">
        <div class="legend-item"><span class="legend-color safe"></span>安全</div>
        <div class="legend-item"><span class="legend-color low"></span>低风险</div>
        <div class="legend-item"><span class="legend-color medium"></span>中风险</div>
        <div class="legend-item"><span class="legend-color high"></span>高风险</div>
      </div>
      
      <h4 style="margin-top: 20px;">图层控制</h4>
      <label class="control-label">
        <input type="checkbox" v-model="showTerrain" @change="toggleTerrain">
        <span>地形</span>
      </label>
      <label class="control-label">
        <input type="checkbox" v-model="showMarkers" @change="toggleMarkers">
        <span>监测点位</span>
      </label>
      <label class="control-label">
        <input type="checkbox" v-model="showBoundaries" @change="toggleBoundaries">
        <span>遗产边界</span>
      </label>
      <label class="control-label">
        <input type="checkbox" v-model="showEnvironment" @change="toggleEnvironment">
        <span>周边环境</span>
      </label>
      <label class="control-label">
        <input type="checkbox" v-model="showAdministrative" @change="toggleAdministrative">
        <span>行政区划</span>
      </label>
    </div>
    
    <div id="cesium-container" class="cesium-viewer"></div>
    
    <!-- 点位详情弹窗 -->
    <div v-if="selectedDevice" class="device-detail-popup">
      <div class="popup-header">
        <h3>{{ selectedDevice.deviceName }}</h3>
        <button class="close-btn" @click="selectedDevice = null">&times;</button>
      </div>
      <div class="popup-content">
        <div class="detail-item">
          <span>设备类型：</span>
          <strong>{{ selectedDevice.deviceType }}</strong>
        </div>
        <div class="detail-item">
          <span>状态：</span>
          <strong :class="selectedDevice.status">{{ selectedDevice.status === 'online' ? '在线' : '离线' }}</strong>
        </div>
        <div class="detail-item">
          <span>风险等级：</span>
          <strong :class="'risk-' + selectedDevice.riskLevel">{{ getRiskText(selectedDevice.riskLevel) }}</strong>
        </div>
        <div v-if="deviceData" class="env-data">
          <h4>实时监测数据</h4>
          <div class="data-grid">
            <div v-if="deviceData.temperature" class="data-item">
              <span>温度：</span><strong>{{ deviceData.temperature }}℃</strong>
            </div>
            <div v-if="deviceData.humidity" class="data-item">
              <span>湿度：</span><strong>{{ deviceData.humidity }}%</strong>
            </div>
            <div v-if="deviceData.pm25" class="data-item">
              <span>PM2.5：</span><strong>{{ deviceData.pm25 }}</strong>
            </div>
            <div v-if="deviceData.windSpeed" class="data-item">
              <span>风速：</span><strong>{{ deviceData.windSpeed }}m/s</strong>
            </div>
          </div>
        </div>
        <div v-if="selectedDevice.photoUrl" class="device-photo">
          <img :src="selectedDevice.photoUrl" alt="设备照片" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getAreasWithDevices, getDeviceDetail, getAllRealtimeData } from '../api/visualization'

export default {
  name: 'CesiumViewer',
  data() {
    return {
      viewer: null,
      areas: [],
      devices: [],
      showMarkers: true,
      showBoundaries: true,
      showTerrain: true,
      showEnvironment: true,
      showAdministrative: false,
      selectedDevice: null,
      deviceData: null,
      markers: [],
      boundaries: [],
      boundaryLines: [], // 地面视角时使用的边界轮廓线
      environmentEntities: [],
      areaMask: null, // 遮罩层，用于隐藏遗产边界外的区域
      administrativeEntities: [],
      viewMode: null, // space, ground, heritage - 默认不选中
      tourMode: 'free', // free, path
      isTourPlaying: false,
      tourAnimation: null,
      selectedArea: null,
      selectedSeason: 'spring',
      selectedTimeSlot: 'morning',
      seasonData: {},
      timeSlotData: {}
    }
  },
  mounted() {
    console.log('CesiumViewer mounted')
    // 延迟初始化，确保DOM已经渲染
    this.$nextTick(() => {
      setTimeout(() => {
        this.initCesium()
        this.loadData()
        this.setupClickHandler()
      }, 100)
    })
    
    // 监听窗口大小变化
    window.addEventListener('resize', this.handleResize)
  },
  beforeUnmount() {
    console.log('CesiumViewer beforeUnmount - 清理资源')
    if (this.tourAnimation) {
      cancelAnimationFrame(this.tourAnimation)
      this.tourAnimation = null
    }
    if (this.viewer) {
      try {
        this.viewer.destroy()
        this.viewer = null
      } catch (error) {
        console.error('销毁viewer失败:', error)
      }
    }
    window.removeEventListener('resize', this.handleResize)
  },
  methods: {
    initCesium() {
      // 检查Cesium是否加载
      if (typeof Cesium === 'undefined') {
        console.error('Cesium未加载，请在index.html中引入Cesium库')
        setTimeout(() => this.initCesium(), 1000)
        return
      }
      
      const container = document.getElementById('cesium-container')
      if (!container) {
        console.error('Cesium容器不存在')
        setTimeout(() => this.initCesium(), 500)
        return
      }
      
      console.log('开始创建Cesium，容器尺寸:', container.offsetWidth, 'x', container.offsetHeight)
      
      // 如果容器高度为0，延迟初始化
      if (container.offsetHeight === 0) {
        console.warn('Cesium容器高度为0，延迟初始化')
        setTimeout(() => this.initCesium(), 500)
        return
      }
      
      try {
        // 禁用Cesium Ion默认Token，避免401错误
        Cesium.Ion.defaultAccessToken = undefined
        
        this.viewer = new Cesium.Viewer('cesium-container', {
          // 使用默认椭球地形（不需要Token）
          terrainProvider: new Cesium.EllipsoidTerrainProvider(),

          // 使用高德地图影像（国内访问稳定，不需要Token）
          imageryProvider: new Cesium.UrlTemplateImageryProvider({
            url: 'https://webst02.is.autonavi.com/appmaptile?style=6&x={x}&y={y}&z={z}',
            minimumLevel: 3,
            maximumLevel: 18
          }),

          baseLayerPicker: false,
          geocoder: false,
          homeButton: true,
          sceneModePicker: true,
          navigationHelpButton: true,
          animation: true,
          timeline: true,
          fullscreenButton: true,
          vrButton: false,
          scene3DOnly: false,
          shadows: false,
          shouldAnimate: true,
          selectionIndicator: false, // 禁用选中指示器，避免相机锁定
          infoBox: false // 禁用内置InfoBox，使用自定义弹窗
        })
        
        console.log('Cesium创建成功')
        
        // 启用光照效果
        this.viewer.scene.globe.enableLighting = false
        
        // 启用深度测试
        this.viewer.scene.globe.depthTestAgainstTerrain = false
        
        // 设置初始视角（中国区域）
        this.viewer.camera.setView({
          destination: Cesium.Cartesian3.fromDegrees(105.0, 35.0, 5000000),
          orientation: {
            heading: Cesium.Math.toRadians(0),
            pitch: Cesium.Math.toRadians(-45),
            roll: 0
          }
        })
        
        // 添加海洋/陆地边界效果
        this.viewer.scene.globe.showGroundAtmosphere = true
        
        // 添加大气层效果
        this.viewer.scene.skyAtmosphere.show = true
        
        // 强制渲染
        this.viewer.scene.requestRender()
      } catch (error) {
        console.error('Cesium初始化失败:', error)
      }
    },
    
    async loadData() {
      try {
        const res = await getAreasWithDevices()
        if (res.code === 200) {
          this.areas = res.data
          this.renderAreas()
          this.loadSeasonData()
        }
      } catch (error) {
        console.error('加载数据失败', error)
      }
    },
    
    async loadSeasonData() {
      // 加载季节性数据（模拟不同季节的环境数据）
      try {
        const res = await getAllRealtimeData()
        if (res.code === 200) {
          // 这里可以根据实际需求从后端获取不同季节的数据
          // 目前使用模拟数据
          this.seasonData = {
            spring: res.data,
            summer: res.data,
            autumn: res.data,
            winter: res.data
          }
          this.updateMarkersWithSeasonData()
        }
      } catch (error) {
        console.error('加载季节数据失败', error)
      }
    },
    
    async loadTimeSlotData() {
      // 加载时段数据
      this.updateMarkersWithSeasonData()
    },
    
    updateMarkersWithSeasonData() {
      // 根据选择的季节和时段更新标记点的颜色和数据
      if (!this.viewer) return

      // 清除现有标记和边界
      this.markers.forEach(marker => this.viewer.entities.remove(marker))
      this.markers = []
      this.boundaries.forEach(boundary => this.viewer.entities.remove(boundary))
      this.boundaries = []
      this.boundaryLines.forEach(line => this.viewer.entities.remove(line))
      this.boundaryLines = []
      // 清除遮罩
      if (this.areaMask) {
        this.viewer.entities.remove(this.areaMask)
        this.areaMask = null
      }

      // 重新渲染区域
      this.renderAreas()
      // 如果当前不显示周边环境，重新创建遮罩
      if (!this.showEnvironment) {
        this.renderAreaMask()
      }
    },
    
    renderAreas() {
      if (!this.viewer) return

      this.areas.forEach(area => {
        // 渲染区域边界（遗产本体）- 自动生成边界点
        if (area.latitude && area.longitude && this.showBoundaries) {
          this.renderBoundary(area)
        }

        // 渲染设备点位
        if (area.devices && this.showMarkers) {
          area.devices.forEach(device => {
            this.renderDevice(device, area)
          })
        }
        
        // 渲染周边环境（植被、水系、道路）
        if (this.showEnvironment) {
          this.renderEnvironment(area)
        }
      })
      
      // 渲染行政区划
      if (this.showAdministrative) {
        this.renderAdministrative()
      }
    },
    
    renderBoundary(area) {
      try {
        // 如果没有boundaryPoints，根据经纬度自动生成矩形边界
        let points
        if (area.boundaryPoints) {
          points = JSON.parse(area.boundaryPoints)
        } else {
          points = this.generateBoundaryPoints(
            parseFloat(area.longitude),
            parseFloat(area.latitude)
          )
        }
        const positions = points.map(p => Cesium.Cartesian3.fromDegrees(p[0], p[1], 0))
        const closedPositions = [...positions, positions[0]]
        const riskColor = this.getRiskColor(area.riskLevel)

        // 创建填充多边形（用于高空视角）
        const boundary = this.viewer.entities.add({
          name: area.areaName + '边界',
          polygon: {
            hierarchy: positions,
            material: riskColor.withAlpha(0.4),
            outline: true,
            outlineColor: riskColor,
            outlineWidth: 3,
            height: 0
          },
          description: `
            <div style="padding: 10px;">
              <h3>${area.areaName}</h3>
              <p><strong>遗产类型：</strong>${this.getHeritageTypeName(area.heritageType)}</p>
              <p><strong>风险等级：</strong>${this.getRiskText(area.riskLevel)}</p>
              <p><strong>描述：</strong>${area.description || '暂无描述'}</p>
            </div>
          `
        })
        this.boundaries.push(boundary)

        // 创建边界轮廓线（用于地面视角）
        const boundaryLine = this.viewer.entities.add({
          name: area.areaName + '边界线',
          polyline: {
            positions: closedPositions,
            width: 4,
            material: riskColor,
            clampToGround: true
          },
          description: `
            <div style="padding: 10px;">
              <h3>${area.areaName}</h3>
              <p><strong>遗产类型：</strong>${this.getHeritageTypeName(area.heritageType)}</p>
              <p><strong>风险等级：</strong>${this.getRiskText(area.riskLevel)}</p>
            </div>
          `
        })
        boundaryLine.show = false // 默认隐藏，地面视角时显示
        this.boundaryLines.push(boundaryLine)
      } catch (error) {
        console.error('渲染边界失败', error)
      }
    },
    
    renderEnvironment(area) {
      // 渲染周边环境要素（贴合地面）
      if (!area.latitude || !area.longitude) return

      const centerLon = parseFloat(area.longitude)
      const centerLat = parseFloat(area.latitude)

      // 模拟植被区域（绿色多边形，贴合地面）
      const vegetationPoints = [
        [centerLon - 0.005, centerLat + 0.005],
        [centerLon - 0.003, centerLat + 0.005],
        [centerLon - 0.003, centerLat + 0.003],
        [centerLon - 0.005, centerLat + 0.003]
      ]

      const vegetation = this.viewer.entities.add({
        name: area.areaName + '植被区',
        polygon: {
          hierarchy: Cesium.Cartesian3.fromDegreesArray(vegetationPoints.flat()),
          material: Cesium.Color.GREEN.withAlpha(0.3),
          height: 0  // 放在椭球表面
        }
      })
      this.environmentEntities.push(vegetation)

      // 模拟水系（蓝色折线，贴合地面）
      const waterPath = [
        Cesium.Cartesian3.fromDegrees(centerLon - 0.01, centerLat - 0.005),
        Cesium.Cartesian3.fromDegrees(centerLon, centerLat - 0.003),
        Cesium.Cartesian3.fromDegrees(centerLon + 0.01, centerLat - 0.005)
      ]

      const water = this.viewer.entities.add({
        name: area.areaName + '水系',
        polyline: {
          positions: waterPath,
          width: 5,
          material: Cesium.Color.BLUE.withAlpha(0.7),
          clampToGround: true
        }
      })
      this.environmentEntities.push(water)

      // 模拟道路（灰色折线，贴合地面）
      const roadPath = [
        Cesium.Cartesian3.fromDegrees(centerLon - 0.008, centerLat + 0.002),
        Cesium.Cartesian3.fromDegrees(centerLon + 0.008, centerLat + 0.002)
      ]

      const road = this.viewer.entities.add({
        name: area.areaName + '道路',
        polyline: {
          positions: roadPath,
          width: 3,
          material: Cesium.Color.GRAY,
          clampToGround: true
        }
      })
      this.environmentEntities.push(road)
    },

    renderAdministrative() {
      // 渲染行政区划边界（简化示例）
      // 实际应用中可以加载GeoJSON或其他矢量数据
      const provinces = [
        { name: '北京市', lon: 116.4, lat: 39.9 },
        { name: '陕西省', lon: 108.9, lat: 34.3 },
        { name: '甘肃省', lon: 103.8, lat: 36.0 }
      ]
      
      provinces.forEach(province => {
        const label = this.viewer.entities.add({
          position: Cesium.Cartesian3.fromDegrees(province.lon, province.lat, 100000),
          label: {
            text: province.name,
            font: '20px sans-serif',
            fillColor: Cesium.Color.WHITE,
            outlineColor: Cesium.Color.BLACK,
            outlineWidth: 2,
            style: Cesium.LabelStyle.FILL_AND_OUTLINE,
            pixelOffset: new Cesium.Cartesian2(0, 0),
            scaleByDistance: new Cesium.NearFarScalar(1.5e2, 1.5, 8.0e6, 0.0)
          }
        })
        this.administrativeEntities.push(label)
      })
    },
    
    renderDevice(device, area) {
      if (!device.latitude || !device.longitude) return

      const marker = this.viewer.entities.add({
        name: device.deviceName,
        // 使用高度0，配合 CLAMP_TO_GROUND 让点位贴合地面
        position: Cesium.Cartesian3.fromDegrees(
          parseFloat(device.longitude),
          parseFloat(device.latitude),
          0
        ),
        point: {
          pixelSize: 15,
          color: this.getRiskColor(device.riskLevel || 'safe'),
          outlineColor: Cesium.Color.WHITE,
          outlineWidth: 2,
          heightReference: Cesium.HeightReference.CLAMP_TO_GROUND
        },
        label: {
          text: device.deviceName,
          font: '14px sans-serif',
          fillColor: Cesium.Color.WHITE,
          outlineColor: Cesium.Color.BLACK,
          outlineWidth: 2,
          style: Cesium.LabelStyle.FILL_AND_OUTLINE,
          verticalOrigin: Cesium.VerticalOrigin.BOTTOM,
          pixelOffset: new Cesium.Cartesian2(0, -20),
          heightReference: Cesium.HeightReference.CLAMP_TO_GROUND,
          scaleByDistance: new Cesium.NearFarScalar(1.5e2, 1.0, 8.0e6, 0.0)
        },
        properties: {
          deviceId: device.id,
          areaName: area.areaName,
          deviceType: device.deviceType,
          status: device.status,
          riskLevel: device.riskLevel
        },
        description: `
          <div style="padding: 10px; min-width: 200px;">
            <h3>${device.deviceName}</h3>
            <p><strong>设备类型：</strong>${device.deviceType}</p>
            <p><strong>状态：</strong>${device.status === 'online' ? '在线' : '离线'}</p>
            <p><strong>风险等级：</strong>${this.getRiskText(device.riskLevel)}</p>
            <p><strong>所属区域：</strong>${area.areaName}</p>
          </div>
        `
      })
      
      this.markers.push(marker)
    },
    
    setupClickHandler() {
      if (!this.viewer) return

      // 设置点击事件处理
      this.viewer.screenSpaceEventHandler.setInputAction((click) => {
        // 确保清除任何实体追踪状态
        if (this.viewer.trackedEntity) {
          this.viewer.trackedEntity = undefined
        }

        const pickedObject = this.viewer.scene.pick(click.position)
        if (Cesium.defined(pickedObject) && pickedObject.id) {
          const entity = pickedObject.id
          if (entity.properties && entity.properties.deviceId) {
            this.showDeviceDetail(entity.properties.deviceId.getValue())
          }
        }
      }, Cesium.ScreenSpaceEventType.LEFT_CLICK)
    },
    
    switchViewMode(mode) {
      this.viewMode = mode
      if (!this.viewer) return
      
      switch (mode) {
        case 'space':
          // 太空视角 - 高空俯瞰
          // 恢复图层显示：显示填充多边形，隐藏边界线
          this.boundaries.forEach(boundary => boundary.show = this.showBoundaries)
          this.boundaryLines.forEach(line => line.show = false)
          this.environmentEntities.forEach(entity => entity.show = this.showEnvironment)
          this.viewer.camera.flyTo({
            destination: Cesium.Cartesian3.fromDegrees(105.0, 35.0, 10000000),
            orientation: {
              heading: Cesium.Math.toRadians(0),
              pitch: Cesium.Math.toRadians(-90),
              roll: 0
            },
            duration: 3
          })
          break
        case 'ground':
          // 地面视角 - 低空平视
          // 地面视角时：隐藏填充多边形，显示边界轮廓线
          this.boundaries.forEach(boundary => boundary.show = false)
          this.boundaryLines.forEach(line => line.show = this.showBoundaries)
          this.environmentEntities.forEach(entity => entity.show = false)
          if (this.selectedArea) {
            const area = this.selectedArea
            this.viewer.camera.flyTo({
              destination: Cesium.Cartesian3.fromDegrees(
                parseFloat(area.longitude),
                parseFloat(area.latitude),
                parseFloat(area.altitude || 0) + 100
              ),
              orientation: {
                heading: Cesium.Math.toRadians(0),
                pitch: Cesium.Math.toRadians(-15),
                roll: 0
              },
              duration: 3
            })
          }
          break
        case 'heritage':
          // 遗产地视角 - 中等高度斜视
          // 恢复图层显示：显示填充多边形，隐藏边界线
          this.boundaries.forEach(boundary => boundary.show = this.showBoundaries)
          this.boundaryLines.forEach(line => line.show = false)
          this.environmentEntities.forEach(entity => entity.show = this.showEnvironment)
          if (this.selectedArea) {
            this.flyToArea(this.selectedArea)
          }
          // 未选择遗产地时不自动定位
          break
      }
    },
    
    setTourMode(mode) {
      this.tourMode = mode
      if (mode === 'free') {
        this.stopPathTour()
      }
    },
    
    startPathTour() {
      if (!this.selectedArea || !this.viewer) return
      
      this.tourMode = 'path'
      this.isTourPlaying = true
      
      const area = this.selectedArea
      const centerLon = parseFloat(area.longitude)
      const centerLat = parseFloat(area.latitude)
      const altitude = parseFloat(area.altitude || 0)
      
      // 定义漫游路径关键点
      const pathPoints = [
        { lon: centerLon, lat: centerLat, height: altitude + 500, heading: 0, pitch: -45 },
        { lon: centerLon + 0.005, lat: centerLat, height: altitude + 300, heading: 90, pitch: -30 },
        { lon: centerLon, lat: centerLat + 0.005, height: altitude + 400, heading: 180, pitch: -40 },
        { lon: centerLon - 0.005, lat: centerLat, height: altitude + 350, heading: 270, pitch: -35 },
        { lon: centerLon, lat: centerLat, height: altitude + 500, heading: 360, pitch: -45 }
      ]
      
      let currentIndex = 0
      const animatePath = () => {
        if (!this.isTourPlaying || currentIndex >= pathPoints.length) {
          this.stopPathTour()
          return
        }
        
        const point = pathPoints[currentIndex]
        this.viewer.camera.flyTo({
          destination: Cesium.Cartesian3.fromDegrees(point.lon, point.lat, point.height),
          orientation: {
            heading: Cesium.Math.toRadians(point.heading),
            pitch: Cesium.Math.toRadians(point.pitch),
            roll: 0
          },
          duration: 5,
          complete: () => {
            currentIndex++
            if (this.isTourPlaying) {
              setTimeout(() => animatePath(), 1000)
            }
          }
        })
      }
      
      animatePath()
    },
    
    stopPathTour() {
      this.isTourPlaying = false
      this.tourMode = 'free'
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

    getRiskColor(riskLevel) {
      const colors = {
        safe: Cesium.Color.GREEN,
        low: Cesium.Color.YELLOW,
        medium: Cesium.Color.ORANGE,
        high: Cesium.Color.RED
      }
      return colors[riskLevel] || Cesium.Color.GREEN
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
    
    flyToArea(area) {
      if (!this.viewer || !area.latitude || !area.longitude) return
      
      this.selectedArea = area
      
      let cameraPos = {
        destination: Cesium.Cartesian3.fromDegrees(
          parseFloat(area.longitude),
          parseFloat(area.latitude),
          parseFloat(area.altitude || 0) + 1000
        ),
        orientation: {
          heading: Cesium.Math.toRadians(0),
          pitch: Cesium.Math.toRadians(-45),
          roll: 0
        },
        duration: 3
      }
      
      // 如果有自定义相机位置，使用自定义位置
      if (area.cameraPosition) {
        try {
          const pos = JSON.parse(area.cameraPosition)
          cameraPos = {
            destination: Cesium.Cartesian3.fromDegrees(pos.longitude, pos.latitude, pos.height),
            orientation: {
              heading: Cesium.Math.toRadians(pos.heading || 0),
              pitch: Cesium.Math.toRadians(pos.pitch || -45),
              roll: pos.roll || 0
            },
            duration: 3
          }
        } catch (error) {
          console.error('解析相机位置失败', error)
        }
      }
      
      this.viewer.camera.flyTo(cameraPos)
    },
    
    toggleTerrain() {
      if (!this.viewer) return

      // 注意: Cesium.createWorldTerrain() 需要 Cesium Ion Token
      // 本项目不使用 Ion Token，通过控制影像层透明度实现地形显示切换
      const imageryLayers = this.viewer.imageryLayers
      if (this.showTerrain) {
        // 显示地形：影像层完全不透明
        imageryLayers.get(0).alpha = 1.0
        this.viewer.scene.globe.depthTestAgainstTerrain = true
        this.viewer.scene.globe.showGroundAtmosphere = true
      } else {
        // 隐藏地形效果：影像层半透明
        imageryLayers.get(0).alpha = 0.3
        this.viewer.scene.globe.depthTestAgainstTerrain = false
        this.viewer.scene.globe.showGroundAtmosphere = false
      }
    },
    
    toggleMarkers() {
      this.markers.forEach(marker => {
        marker.show = this.showMarkers
      })
    },
    
    toggleBoundaries() {
      if (this.viewMode === 'ground') {
        // 地面视角：控制边界轮廓线
        this.boundaries.forEach(boundary => boundary.show = false)
        this.boundaryLines.forEach(line => line.show = this.showBoundaries)
      } else {
        // 其他视角：控制填充多边形
        this.boundaries.forEach(boundary => boundary.show = this.showBoundaries)
        this.boundaryLines.forEach(line => line.show = false)
      }
    },
    
    toggleEnvironment() {
      if (this.showEnvironment) {
        // 勾选时：显示周边环境，隐藏遮罩
        if (this.environmentEntities.length === 0) {
          this.areas.forEach(area => this.renderEnvironment(area))
        } else {
          this.environmentEntities.forEach(entity => {
            entity.show = true
          })
        }
        // 隐藏遮罩层
        if (this.areaMask) {
          this.areaMask.show = false
        }
      } else {
        // 不勾选时：隐藏周边环境，显示遮罩（只显示遗产地范围）
        this.environmentEntities.forEach(entity => {
          entity.show = false
        })
        // 渲染或显示遮罩层
        this.renderAreaMask()
      }
    },

    renderAreaMask() {
      if (!this.viewer || this.areas.length === 0) return

      // 如果已存在遮罩，直接显示
      if (this.areaMask) {
        this.areaMask.show = true
        return
      }

      // 收集所有遗产边界作为"洞"
      const holes = []
      this.areas.forEach(area => {
        if (area.latitude && area.longitude) {
          let points
          if (area.boundaryPoints) {
            try {
              points = JSON.parse(area.boundaryPoints)
            } catch (e) {
              console.error('解析边界点失败', e)
            }
          }
          // 如果没有boundaryPoints，自动生成
          if (!points || points.length < 3) {
            points = this.generateBoundaryPoints(
              parseFloat(area.longitude),
              parseFloat(area.latitude)
            )
          }
          const holePositions = points.map(p => Cesium.Cartesian3.fromDegrees(p[0], p[1]))
          holes.push(new Cesium.PolygonHierarchy(holePositions))
        }
      })

      // 如果没有有效的边界，不创建遮罩
      if (holes.length === 0) return

      // 创建一个覆盖全球的大矩形作为外边界
      const outerRing = Cesium.Cartesian3.fromDegreesArray([
        -180, 85,  // 西北角
        180, 85,   // 东北角
        180, -85,  // 东南角
        -180, -85, // 西南角
      ])

      this.areaMask = this.viewer.entities.add({
        name: '遗产地范围外遮罩',
        polygon: {
          hierarchy: new Cesium.PolygonHierarchy(outerRing, holes),
          material: Cesium.Color.BLACK.withAlpha(0.6),
          // 放在椭球表面（EllipsoidTerrainProvider的地面高度）
          height: 0,
          outline: false
        }
      })
    },
    
    toggleAdministrative() {
      if (this.showAdministrative && this.administrativeEntities.length === 0) {
        this.renderAdministrative()
      } else {
        this.administrativeEntities.forEach(entity => {
          entity.show = this.showAdministrative
        })
      }
    },
    
    async showDeviceDetail(deviceId) {
      try {
        const res = await getDeviceDetail(deviceId)
        if (res.code === 200) {
          this.selectedDevice = res.data.device
          this.deviceData = res.data.latestData
        }
      } catch (error) {
        console.error('获取设备详情失败', error)
      }
    },
    
    handleResize() {
      // 窗口大小变化时，Cesium会自动调整，但我们可以强制重新渲染
      if (this.viewer) {
        setTimeout(() => {
          this.viewer.resize()
          this.viewer.scene.requestRender()
        }, 100)
      }
    }
  }
}
</script>

<style scoped>
.cesium-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  background: #000;
}

.cesium-viewer {
  width: 100%;
  height: 100%;
  background: #000;
}

.cesium-controls {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(255, 255, 255, 0.95);
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.3);
  z-index: 1000;
  max-width: 250px;
  max-height: 90vh;
  overflow-y: auto;
}

.cesium-controls h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #333;
  border-bottom: 2px solid #4CAF50;
  padding-bottom: 5px;
}

.view-mode-buttons {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.mode-btn, .tour-btn {
  display: block;
  width: 100%;
  padding: 8px;
  background: #f0f0f0;
  color: #333;
  border: 2px solid transparent;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
}

.mode-btn:hover, .tour-btn:hover {
  background: #e0e0e0;
}

.mode-btn.active, .tour-btn.active {
  background: #4CAF50;
  color: white;
  border-color: #45a049;
}

.view-btn {
  display: block;
  width: 100%;
  padding: 8px;
  margin-bottom: 5px;
  background: #2196F3;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background 0.3s;
}

.view-btn:hover {
  background: #1976D2;
}

.view-btn.active {
  background: #0D47A1;
  box-shadow: 0 0 0 2px rgba(33, 150, 243, 0.5);
}

.tour-controls {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.stop-btn {
  background: #f44336;
  color: white;
  padding: 8px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.stop-btn:hover {
  background: #d32f2f;
}

.time-controls {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.time-controls select {
  padding: 6px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 12px;
  background: white;
}

.risk-legend {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.legend-item {
  display: flex;
  align-items: center;
  font-size: 12px;
}

.legend-color {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  margin-right: 8px;
  border: 2px solid white;
  box-shadow: 0 0 3px rgba(0,0,0,0.3);
}

.legend-color.safe { background: #00ff00; }
.legend-color.low { background: #ffff00; }
.legend-color.medium { background: #ff9900; }
.legend-color.high { background: #ff0000; }

.control-label {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
  cursor: pointer;
}

.control-label input {
  margin-right: 8px;
}

.device-detail-popup {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.4);
  z-index: 2000;
  min-width: 400px;
  max-width: 600px;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px 8px 0 0;
}

.popup-header h3 {
  margin: 0;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: white;
  opacity: 0.8;
  transition: opacity 0.3s;
}

.close-btn:hover {
  opacity: 1;
}

.popup-content {
  padding: 20px;
}

.detail-item {
  margin-bottom: 12px;
  font-size: 14px;
}

.detail-item span {
  color: #666;
}

.detail-item strong {
  color: #333;
  margin-left: 8px;
}

.env-data {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.env-data h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #333;
}

.data-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.data-item {
  font-size: 14px;
  padding: 8px;
  background: #f5f5f5;
  border-radius: 4px;
}

.device-photo {
  margin-top: 20px;
}

.device-photo img {
  width: 100%;
  border-radius: 4px;
}

.risk-safe { color: #4CAF50; }
.risk-low { color: #FFC107; }
.risk-medium { color: #FF9800; }
.risk-high { color: #F44336; }

/* 滚动条样式 */
.cesium-controls::-webkit-scrollbar {
  width: 6px;
}

.cesium-controls::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.cesium-controls::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
}

.cesium-controls::-webkit-scrollbar-thumb:hover {
  background: #555;
}
</style>
