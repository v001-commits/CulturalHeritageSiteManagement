import request from './request'

/**
 * 获取所有区域及其设备信息
 */
export function getAreasWithDevices() {
  return request({
    url: '/visualization/areas-with-devices',
    method: 'get'
  })
}

/**
 * 获取指定区域的详细信息
 */
export function getAreaDetail(areaId) {
  return request({
    url: `/visualization/area/${areaId}`,
    method: 'get'
  })
}

/**
 * 获取指定设备的详细信息
 */
export function getDeviceDetail(deviceId) {
  return request({
    url: `/visualization/device/${deviceId}`,
    method: 'get'
  })
}

/**
 * 获取所有设备点位信息
 */
export function getAllDevices() {
  return request({
    url: '/visualization/devices',
    method: 'get'
  })
}

/**
 * 更新设备风险等级
 */
export function updateDeviceRiskLevel(deviceId, riskLevel) {
  return request({
    url: `/visualization/device/${deviceId}/risk-level`,
    method: 'post',
    params: { riskLevel }
  })
}

/**
 * 更新区域风险等级
 */
export function updateAreaRiskLevel(areaId, riskLevel) {
  return request({
    url: `/visualization/area/${areaId}/risk-level`,
    method: 'post',
    params: { riskLevel }
  })
}

/**
 * 获取所有实时环境数据
 */
export function getAllRealtimeData() {
  return request({
    url: '/visualization/realtime-data',
    method: 'get'
  })
}

/**
 * 根据季节获取环境数据
 */
export function getSeasonData(season) {
  return request({
    url: '/visualization/season-data',
    method: 'get',
    params: { season }
  })
}

/**
 * 根据时段获取环境数据
 */
export function getTimeSlotData(timeSlot, areaId) {
  return request({
    url: '/visualization/timeslot-data',
    method: 'get',
    params: { timeSlot, areaId }
  })
}

/**
 * 保存或更新实时环境数据
 */
export function saveRealtimeData(data) {
  return request({
    url: '/visualization/realtime-data',
    method: 'post',
    data
  })
}
