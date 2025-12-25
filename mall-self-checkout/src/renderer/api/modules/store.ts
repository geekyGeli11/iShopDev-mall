import { request } from '../request'
import type { ApiResponse } from '../types'

// API 端点
const API_ENDPOINTS = {
  STORE_SCHOOLS: '/selfcheck/store/schools',
  STORE_BY_SCHOOL: '/selfcheck/store/stores',
  STORE_ALL: '/selfcheck/store/stores/all',
  STORE_DETAIL: '/selfcheck/store/stores',
  STORE_GROUP_BY_SCHOOL: '/selfcheck/store/stores/groupBySchool',
  SCHOOL_DETAIL: '/selfcheck/store/schools'
} as const

/**
 * 门店相关API
 */
export class StoreApi {
  /**
   * 获取所有启用的学校列表
   * @returns 学校列表
   */
  static async getSchoolList(): Promise<ApiResponse<Array<{
    id: number
    schoolName: string
    address: string
    status: boolean
    createTime: string
  }>>> {
    return request.get(API_ENDPOINTS.STORE_SCHOOLS)
  }

  /**
   * 根据学校ID获取门店列表
   * @param schoolId 学校ID
   * @returns 门店列表
   */
  static async getStoresBySchool(schoolId: number): Promise<ApiResponse<Array<{
    id: number
    name: string
    address: string
    status: boolean
    schoolId: number
    createTime: string
  }>>> {
    return request.get(API_ENDPOINTS.STORE_BY_SCHOOL, { schoolId })
  }

  /**
   * 获取所有可用门店地址
   * @returns 门店地址列表
   */
  static async getAllStoreAddresses(): Promise<ApiResponse<Array<{
    id: number
    name: string
    address: string
    schoolName: string
  }>>> {
    return request.get(API_ENDPOINTS.STORE_ALL)
  }

  /**
   * 根据ID获取门店详情
   * @param storeId 门店ID
   * @returns 门店详情
   */
  static async getStoreDetail(storeId: number): Promise<ApiResponse<{
    id: number
    name: string
    address: string
    status: boolean
    schoolId: number
    schoolName: string
    createTime: string
  }>> {
    return request.get(`${API_ENDPOINTS.STORE_DETAIL}/${storeId}`)
  }

  /**
   * 获取按学校分组的门店列表
   * @returns 按学校分组的门店列表
   */
  static async getStoresGroupBySchool(): Promise<ApiResponse<Array<{
    schoolId: number
    schoolName: string
    stores: Array<{
      id: number
      name: string
      address: string
      status: boolean
    }>
  }>>> {
    return request.get(API_ENDPOINTS.STORE_GROUP_BY_SCHOOL)
  }

  /**
   * 根据学校ID获取学校详情
   * @param schoolId 学校ID
   * @returns 学校详情
   */
  static async getSchoolDetail(schoolId: number): Promise<ApiResponse<{
    id: number
    schoolName: string
    address: string
    status: boolean
    createTime: string
  }>> {
    return request.get(`${API_ENDPOINTS.SCHOOL_DETAIL}/${schoolId}`)
  }
}
