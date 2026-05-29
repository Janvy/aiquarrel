# AI吵架生成器 — 前端 API 接入文档

> 面向前端开发工程师（Taro + React + TypeScript），文档中所有 TypeScript 类型可直接复制使用。

---

## 一、通用规范

| 项目 | 规范 |
|------|------|
| 基础路径 | `https://api.aiquarrel.com/api/v1` |
| 请求格式 | `application/json` |
| 鉴权方式 | `X-Device-Id` 请求头（UUID v4，客户端首次启动生成并持久化） |
| 编码 | UTF-8 |
| 超时 | 生成接口 **10s**，其他接口 **5s** |

### 统一响应格式

所有接口返回以下结构：

```typescript
interface ApiResponse<T> {
  code: number;    // 0=成功，其他=错误
  message: string; // 描述信息
  data: T | null;  // 业务数据
}
```

### 请求封装示例

```typescript
// utils/api.ts
import Taro from '@tarojs/taro';
import { getDeviceId } from './device';

const BASE_URL = 'https://api.aiquarrel.com/api/v1';

export async function request<T>(path: string, options: {
  method?: 'GET' | 'POST' | 'DELETE';
  data?: Record<string, unknown>;
  timeout?: number;
} = {}): Promise<T> {
  const deviceId = getDeviceId();
  return new Promise((resolve, reject) => {
    Taro.request({
      url: BASE_URL + path,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        'X-Device-Id': deviceId,
      },
      timeout: options.timeout || 15000,
      success(res) {
        const body = res.data as ApiResponse<T>;
        if (body.code === 0) {
          resolve(body.data as T);
        } else {
          reject(body);
        }
      },
      fail() {
        reject({ code: -1, message: '网络不给力，请检查网络后重试' });
      },
    });
  });
}
```

---

## 二、错误码速查

| 错误码 | 含义 | 触发条件 | 前端处理 |
|--------|------|----------|----------|
| 0 | 成功 | — | 正常处理 data |
| -1 | 网络异常 | 请求失败（本地定义） | Toast "网络不给力，请检查网络后重试" |
| 40001 | 场景输入为空或超长 | scene 为空或 len > 200 | Toast "请输入1-200字的场景描述" |
| 40002 | 风格参数无效 | style 不在枚举范围内 | Toast "请选择有效风格" |
| 40003 | 记录ID不存在 | 操作不存在的记录 | Toast "记录不存在" |
| 40004 | 参数校验失败 | @Valid 校验不通过 | Toast message 字段内容 |
| 40005 | 记录不属于当前设备 | device_id 不匹配（越权） | Toast "无权操作此记录" |
| 42901 | 今日生成次数已达上限 | 当日计数 ≥ 50 | Toast "今日生成次数已用完，明天再来吧" + 按钮置灰 |
| 42902 | 请求频率过高 | 设备/IP 级 QPS 超限 | Toast message，延迟 1s 可重试 |
| 50001 | AI 服务异常 | DeepSeek API 超时/异常 | Toast "AI正在开小差，请稍后再试" |
| 50002 | 数据库异常 | MySQL 连接/写入失败 | Toast "服务异常，请稍后再试" |
| 50003 | 图片生成失败 | CDN 上传异常 | Toast "图片生成失败，请重试" |
| 50004 | Redis 不可用 | Redis 连接失败 | 后端自动降级，前端无感 |

---

## 三、风格枚举

```typescript
// constants.ts
export const STYLES = [
  { value: 'diplomatic',          label: '高情商',   emoji: '🤝' },
  { value: 'passive_aggressive',  label: '阴阳怪气', emoji: '🎭' },
  { value: 'crazy',               label: '发疯文学', emoji: '🤪' },
  { value: 'literary',            label: '文艺',     emoji: '📝' },
  { value: 'bossy',               label: '霸总',     emoji: '🕶️' },
] as const;

export type StyleValue = typeof STYLES[number]['value'];

export function getStyleName(value: StyleValue): string {
  return STYLES.find(s => s.value === value)?.label ?? value;
}
```

---

## 四、接口详细定义

### 4.1 生成怼人话术 (API-01)

```
POST /api/v1/generate
Content-Type: application/json
X-Device-Id: <uuid>
```

**请求体：**

```typescript
interface GenerateRequest {
  scene: string;  // 场景描述，1~200字符
  style: string;  // 风格枚举值
}
```

```json
{
  "scene": "同事甩锅给我",
  "style": "passive_aggressive"
}
```

**响应体：**

```typescript
interface GenerateResponse {
  id: string;           // 记录ID，格式: gen_yyyyMMdd_xxxxxx
  scene: string;        // 原始场景
  style: string;        // 风格枚举值
  content: string;      // AI 生成的完整文案
  favorited: boolean;   // 是否已收藏
  createdAt: string;    // ISO 8601 时间戳，如 "2026-05-29T14:30:00Z"
}
```

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "gen_20260529_000001",
    "scene": "同事甩锅给我",
    "style": "passive_aggressive",
    "content": "你这锅甩得挺有水平啊，要不要考虑去参加铁饼比赛？",
    "favorited": false,
    "createdAt": "2026-05-29T14:30:00Z"
  }
}
```

**可能返回的错误码：** 40001, 40002, 40004, 42901, 50001

**调用示例：**

```typescript
import { request } from '@/utils/api';

const result = await request<GenerateResponse>('/generate', {
  method: 'POST',
  data: { scene: '同事甩锅给我', style: 'passive_aggressive' },
  timeout: 10000,
});
```

**特殊说明：**
- 输入命中 Level1 敏感词时，`content` 返回拒答兜底文案 `"这个问题有点难，换个说法试试？"`，`id` 为空字符串
- 输出命中 Level2 敏感词时，对应词替换为 `***`
- 每日限额 50 次/设备，跨天自动重置

---

### 4.2 获取历史记录 (API-02)

```
GET /api/v1/history?page=1&page_size=20
X-Device-Id: <uuid>
```

**请求参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | number | 否 | 1 | 页码，从1开始 |
| page_size | number | 否 | 20 | 每页条数，最大50 |

**响应体：**

```typescript
interface HistoryResponse {
  list: HistoryItem[];
  total: number;    // 总记录数
  page: number;     // 当前页码
  pageSize: number; // 每页条数
}

interface HistoryItem {
  id: string;             // 记录ID
  scene: string;          // 场景描述
  style: string;          // 风格枚举值
  styleName: string;      // 风格中文名，如 "阴阳怪气版"
  contentPreview: string; // 内容前30字 + "..."
  favorited: boolean;     // 是否已收藏
  createdAt: string;      // ISO 8601 时间戳
}
```

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "id": "gen_20260529_000001",
        "scene": "同事甩锅给我",
        "style": "passive_aggressive",
        "styleName": "阴阳怪气版",
        "contentPreview": "你这锅甩得挺有水平啊，要不要考虑去...",
        "favorited": true,
        "createdAt": "2026-05-29T14:30:00Z"
      }
    ],
    "total": 50,
    "page": 1,
    "pageSize": 20
  }
}
```

**判断是否还有更多：** `page * pageSize < total`

**调用示例：**

```typescript
const history = await request<HistoryResponse>(
  `/history?page=${page}&page_size=20`
);
```

---

### 4.3 删除单条历史 (API-03)

```
DELETE /api/v1/history/{id}
X-Device-Id: <uuid>
```

**响应体：** 无 data（仅 code + message）

```json
{ "code": 0, "message": "success", "data": null }
```

**可能返回的错误码：** 40003, 40005

**调用示例：**

```typescript
await request(`/history/${id}`, { method: 'DELETE' });
```

**特殊说明：** 删除历史时后端会同步清理该条记录的收藏数据。

---

### 4.4 清空全部历史 (API-04)

```
DELETE /api/v1/history
X-Device-Id: <uuid>
```

**响应体：** 无 data

```json
{ "code": 0, "message": "success", "data": null }
```

**调用示例：**

```typescript
await request('/history', { method: 'DELETE' });
```

**特殊说明：** 除历史记录外，后端同步清理所有关联的收藏数据。

---

### 4.5 收藏/取消收藏 (API-05)

```
POST /api/v1/favorite/{id}
X-Device-Id: <uuid>
```

**响应体：**

```typescript
interface FavoriteResponse {
  id: string;       // 记录ID
  favorited: boolean; // 切换后的状态
}
```

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "gen_20260529_000001",
    "favorited": true
  }
}
```

**可能返回的错误码：** 40003, 40005

**调用示例：**

```typescript
const result = await request<FavoriteResponse>(`/favorite/${id}`, {
  method: 'POST',
});
// 切换行为：已收藏 → 取消收藏；未收藏 → 收藏
// result.favorited 为切换后的状态
```

---

### 4.6 获取收藏列表 (API-06)

```
GET /api/v1/favorites?page=1&page_size=20
X-Device-Id: <uuid>
```

**请求参数：** 同 API-02

**响应体：** 格式同 [HistoryResponse](#42-获取历史记录-api-02)，list 中所有项的 `favorited` 恒为 `true`

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "id": "gen_20260529_000001",
        "scene": "亲戚催婚",
        "style": "literary",
        "styleName": "文艺版",
        "contentPreview": "我在等世上唯一契合的灵魂...",
        "favorited": true,
        "createdAt": "2026-05-29T10:15:00Z"
      }
    ],
    "total": 6,
    "page": 1,
    "pageSize": 20
  }
}
```

**调用示例：**

```typescript
const favorites = await request<HistoryResponse>(
  `/favorites?page=${page}&page_size=20`
);
```

---

### 4.7 获取使用次数 (API-07)

```
GET /api/v1/usage
X-Device-Id: <uuid>
```

**响应体：**

```typescript
interface UsageResponse {
  dailyCount: number; // 今日已使用次数
  totalCount: number; // 累计使用次数
  dailyLimit: number; // 每日上限（固定50）
}
```

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "dailyCount": 12,
    "totalCount": 128,
    "dailyLimit": 50
  }
}
```

**调用示例：**

```typescript
const usage = await request<UsageResponse>('/usage');
// 判断是否可以继续生成: usage.dailyCount < usage.dailyLimit
```

---

### 4.8 生成分享图片 (API-08)

```
POST /api/v1/share-image
Content-Type: application/json
X-Device-Id: <uuid>
```

**请求体：**

```typescript
interface ShareImageRequest {
  id: string;  // 记录ID
}
```

**响应体：**

```typescript
interface ShareImageResponse {
  imageUrl: string; // 图片 URL
}
```

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "imageUrl": "/api/v1/record/gen_20260529_000001"
  }
}
```

**可能返回的错误码：** 40003, 40005, 50003

**调用示例：**

```typescript
const result = await request<ShareImageResponse>('/share-image', {
  method: 'POST',
  data: { id: 'gen_20260529_000001' },
});
```

> **注意：** V1 阶段后端返回的是记录详情路径而非真实 CDN 图片 URL。前端应优先尝试调用此接口，如果 imageUrl 为相对路径或 `50003`，则降级为使用 Taro Canvas 在前端自行绘制分享卡片。

---

### 4.9 获取记录详情 (API-09)

```
GET /api/v1/record/{id}
X-Device-Id: <uuid>
```

**响应体：** 格式同 [GenerateResponse](#41-生成怼人话术-api-01)

```typescript
// 复用 GenerateResponse 类型
```

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "gen_20260529_000001",
    "scene": "同事甩锅给我",
    "style": "passive_aggressive",
    "content": "你这锅甩得挺有水平啊，要不要考虑去参加铁饼比赛？",
    "favorited": false,
    "createdAt": "2026-05-29T14:30:00Z"
  }
}
```

**可能返回的错误码：** 40003, 40005

**调用示例：**

```typescript
const record = await request<GenerateResponse>(`/record/${id}`);
```

**特殊说明：**
- 结果页从路由参数拿到 `id` 后，通过此接口获取完整数据
- 校验 `device_id` 归属，设备不匹配返回 `40005`
- **不要在 URL 参数中传递大段文案**，只传 `id`

---

## 五、页面 → 接口调用关系

| 页面 | 接口调用 |
|------|----------|
| 首页 (index) | API-01 生成 |
| 结果页 (result) | API-09 进入时获取详情，API-01 切换风格/再来一句，API-05 收藏，API-08 分享 |
| 历史页 (history) | API-02 分页列表，API-03 单条删除，API-04 清空 |
| 我的页 (mine) | API-07 获取次数 |
| 收藏页 (favorites) | API-06 分页列表，API-05 取消收藏 |

| 页面 | 进入方式 | 路由参数 | 数据获取 |
|------|----------|----------|----------|
| 结果页 | 从首页生成跳转 | `?id=gen_xxx` | API-09 `GET /record/{id}` |
| 结果页 | 从历史列表点击 | `?id=gen_xxx` | API-09 `GET /record/{id}` |
| 结果页 | 从收藏列表点击 | `?id=gen_xxx` | API-09 `GET /record/{id}` |

---

## 六、关键业务规则

1. **生成流程：** 后端先校验每日限额 → 敏感词过滤输入 → 调用 AI → 敏感词过滤输出 → 持久化 → 返回。敏感词命中不会返回错误码，而是返回兜底文案。
2. **风格切换：** 结果页切换风格 Tab 时，前端用同 `scene` + 新 `style` 重新调用 API-01，后端**不扣减每日次数**。
3. **再来一句：** 同 `scene` + 同 `style` 重新调用 API-01，后端**正常扣减每日次数**。
4. **收藏双写：** 后端同时维护 `t_favorite` 表和 `t_generation_record.favorited` 字段，前端只需关注 API 返回的 `favorited` 字段。
5. **设备隔离：** 所有数据按 `X-Device-Id` 隔离，切换设备/清除小程序数据会导致历史不可见（生成新 UUID）。
6. **分页上限：** `page_size` 最大 50，超出会被截断为 50。
