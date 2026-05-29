import Taro from '@tarojs/taro';
import { getDeviceId } from './device';

const BASE_URL = __API_BASE_URL__;
const GENERATE_TIMEOUT = 10000;
const DEFAULT_TIMEOUT = 5000;

interface ApiResponse<T> {
  code: number;
  message: string;
  data: T | null;
}

function request<T>(
  method: 'GET' | 'POST' | 'DELETE',
  path: string,
  data?: Record<string, unknown>,
  timeout: number = DEFAULT_TIMEOUT,
): Promise<T> {
  return new Promise((resolve, reject) => {
    Taro.request({
      url: `${BASE_URL}${path}`,
      method,
      data,
      timeout,
      header: {
        'Content-Type': 'application/json',
        'X-Device-Id': getDeviceId(),
      },
      success: (res) => {
        const body = res.data as ApiResponse<T>;
        if (res.statusCode === 200 && body.code === 0) {
          resolve(body.data as T);
        } else if (res.statusCode === 429 || body.code === 42902) {
          setTimeout(() => {
            request<T>(method, path, data, timeout).then(resolve).catch(reject);
          }, 1000);
        } else {
          reject(body);
        }
      },
      fail: (err) => {
        console.error('[API] 请求失败:', method, path, JSON.stringify(err));
        Taro.showToast({ title: '网络不给力，请检查网络后重试', icon: 'none' });
        reject({ code: -1, message: '网络不给力，请检查网络后重试' });
      },
    });
  });
}

// --- API-01: 生成怼人话术 ---

export interface GenerateResponse {
  id: string;
  scene: string;
  style: string;
  content: string;
  favorited: boolean;
  createdAt: string;
}

export function generate(scene: string, style: string): Promise<GenerateResponse> {
  return request<GenerateResponse>('POST', '/generate', { scene, style }, GENERATE_TIMEOUT);
}

// --- API-02: 获取历史记录 ---

export interface HistoryItem {
  id: string;
  scene: string;
  style: string;
  styleName: string;
  contentPreview: string;
  favorited: boolean;
  createdAt: string;
}

export interface HistoryResponse {
  list: HistoryItem[];
  total: number;
  page: number;
  pageSize: number;
}

export function getHistory(page: number, pageSize: number = 20): Promise<HistoryResponse> {
  return request<HistoryResponse>('GET', `/history?page=${page}&page_size=${pageSize}`);
}

// --- API-03: 删除单条历史 ---

export function deleteHistory(id: string): Promise<void> {
  return request<void>('DELETE', `/history/${id}`);
}

// --- API-04: 清空全部历史 ---

export function clearHistory(): Promise<void> {
  return request<void>('DELETE', '/history');
}

// --- API-05: 收藏/取消收藏 ---

export interface FavoriteResponse {
  id: string;
  favorited: boolean;
}

export function toggleFavorite(id: string): Promise<FavoriteResponse> {
  return request<FavoriteResponse>('POST', `/favorite/${id}`);
}

// --- API-06: 获取收藏列表 ---

export function getFavorites(page: number, pageSize: number = 20): Promise<HistoryResponse> {
  return request<HistoryResponse>('GET', `/favorites?page=${page}&page_size=${pageSize}`);
}

// --- API-07: 获取使用次数 ---

export interface UsageResponse {
  dailyCount: number;
  totalCount: number;
  dailyLimit: number;
}

export function getUsage(): Promise<UsageResponse> {
  return request<UsageResponse>('GET', '/usage');
}

// --- API-08: 生成分享图片 ---

export interface ShareImageResponse {
  imageUrl: string;
}

export function shareImage(id: string): Promise<ShareImageResponse> {
  return request<ShareImageResponse>('POST', '/share-image', { id });
}

// --- API-09: 获取记录详情 (返回类型复用 GenerateResponse) ---

export function getRecordDetail(id: string): Promise<GenerateResponse> {
  return request<GenerateResponse>('GET', `/record/${id}`);
}
