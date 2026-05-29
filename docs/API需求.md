# AI吵架生成器 — API 接口需求

## 1. 接口总览

| 编号 | 接口名称 | 方法 | 路径 | 说明 |
|------|----------|------|------|------|
| API-01 | 生成怼人话术 | POST | /api/v1/generate | 核心生成接口 |
| API-02 | 获取历史记录 | GET | /api/v1/history | 分页获取历史 |
| API-03 | 删除历史记录 | DELETE | /api/v1/history/:id | 删除单条 |
| API-04 | 清空历史记录 | DELETE | /api/v1/history | 清空全部 |
| API-05 | 收藏/取消收藏 | POST | /api/v1/favorite/:id | 切换收藏状态 |
| API-06 | 获取收藏列表 | GET | /api/v1/favorites | 分页获取收藏 |
| API-07 | 获取使用次数 | GET | /api/v1/usage | 今日+累计次数 |
| API-08 | 生成分享图片 | POST | /api/v1/share-image | 生成分享卡片图 |
| API-09 | 获取记录详情 | GET | /api/v1/record/{id} | 获取单条完整内容 |

---

## 2. 接口详细定义

### 2.1 生成怼人话术 (API-01)

```
POST /api/v1/generate
```

**请求体：**

```json
{
  "scene": "同事甩锅给我",
  "style": "passive_aggressive"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| scene | string | 是 | 场景描述，1~200字符 |
| style | string | 是 | 风格枚举值 |

**风格枚举（style）：**

| 枚举值 | 风格名称 |
|--------|----------|
| diplomatic | 高情商版 |
| passive_aggressive | 阴阳怪气版 |
| crazy | 发疯文学版 |
| literary | 文艺版 |
| bossy | 霸总版 |

**请求头：**

| Header | 值 | 说明 |
|--------|-----|------|
| Content-Type | application/json | — |
| X-Device-Id | 设备唯一标识 | 用于关联历史记录（V1 无登录态） |

**响应：**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "gen_20260525_001",
    "scene": "同事甩锅给我",
    "style": "passive_aggressive",
    "content": "你这锅甩得挺有水平啊，要不要考虑去参加铁饼比赛？为国争光不比在这甩锅强？",
    "favorited": false,
    "created_at": "2026-05-25T14:30:00Z"
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 生成记录唯一ID |
| scene | string | 原始场景 |
| style | string | 使用的风格枚举 |
| content | string | 生成的文案 |
| favorited | boolean | 是否已收藏 |
| created_at | string | ISO 8601 时间戳 |

**错误码：**

| code | 说明 |
|------|------|
| 40001 | 场景输入为空或超长 |
| 40002 | 风格参数无效 |
| 42901 | 今日生成次数已达上限 |
| 50001 | AI 服务异常 |

---

### 2.2 获取历史记录 (API-02)

```
GET /api/v1/history?page=1&page_size=20
```

**请求参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| page_size | int | 否 | 20 | 每页条数，最大50 |

**请求头：**

| Header | 值 | 说明 |
|--------|-----|------|
| X-Device-Id | 设备唯一标识 | 按设备隔离数据 |

**响应：**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "id": "gen_20260525_001",
        "scene": "同事甩锅给我",
        "style": "passive_aggressive",
        "style_name": "阴阳怪气版",
        "content_preview": "你这锅甩得挺有水平啊...",
        "favorited": true,
        "created_at": "2026-05-25T14:30:00Z"
      }
    ],
    "total": 50,
    "page": 1,
    "page_size": 20
  }
}
```

| 字段 | 说明 |
|------|------|
| content_preview | 内容前30字 + "..."，用于列表展示 |
| total | 总记录数 |
| has_more | （前端计算：page * page_size < total）|

---

### 2.3 删除历史记录 (API-03)

```
DELETE /api/v1/history/:id
```

**请求头：** `X-Device-Id`

**响应：** `{"code": 0, "message": "success"}`

---

### 2.4 清空历史记录 (API-04)

```
DELETE /api/v1/history
```

**请求头：** `X-Device-Id`

**响应：** `{"code": 0, "message": "success"}`

---

### 2.5 收藏/取消收藏 (API-05)

```
POST /api/v1/favorite/:id
```

**请求头：** `X-Device-Id`

**响应：**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "gen_20260525_001",
    "favorited": true
  }
}
```

每次调用切换状态（收藏→取消，取消→收藏），favorited 返回切换后的状态。

---

### 2.6 获取收藏列表 (API-06)

```
GET /api/v1/favorites?page=1&page_size=20
```

**请求头：** `X-Device-Id`

**响应：** 格式同 API-02，list 中仅包含 `favorited: true` 的记录。

---

### 2.7 获取使用次数 (API-07)

```
GET /api/v1/usage
```

**请求头：** `X-Device-Id`

**响应：**

```json
{
  "code": 0,
  "data": {
    "daily_count": 12,
    "total_count": 128,
    "daily_limit": 50
  }
}
```

| 字段 | 说明 |
|------|------|
| daily_count | 今日已使用次数 |
| total_count | 累计使用次数 |
| daily_limit | 每日上限（超过后返回 42901） |

---

### 2.8 生成分享图片 (API-08)

```
POST /api/v1/share-image
```

**请求体：**

```json
{
  "id": "gen_20260525_001"
}
```

**响应：**

```json
{
  "code": 0,
  "data": {
    "image_url": "https://cdn.example.com/share/gen_20260525_001.png"
  }
}
```

| 字段 | 说明 |
|------|------|
| image_url | 生成的分享图片 CDN 地址 |

> 备选方案：如果服务端生成图片成本高，可改为前端 Canvas 绘制，此接口仅用于生成图片所需的排版参数。

---

### 2.9 获取记录详情 (API-09)

```
GET /api/v1/record/{id}
```

**请求头：**

| Header | 值 | 说明 |
|--------|-----|------|
| X-Device-Id | 设备唯一标识 | 用于校验记录归属 |

**响应：**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "gen_20260525_001",
    "scene": "同事甩锅给我",
    "style": "passive_aggressive",
    "content": "你这锅甩得挺有水平啊，要不要考虑去参加铁饼比赛？为国争光不比在这甩锅强？",
    "favorited": false,
    "created_at": "2026-05-25T14:30:00Z"
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 记录唯一ID |
| scene | string | 原始场景描述 |
| style | string | 风格枚举值 |
| content | string | AI生成的完整文案 |
| favorited | boolean | 当前收藏状态 |
| created_at | string | ISO 8601 时间戳 |

**错误码：**

| code | 说明 |
|------|------|
| 40003 | 记录ID不存在 |
| 40005 | 记录不属于当前设备（device_id 不匹配）|

> **用途**：前端从历史列表或收藏列表进入结果页时，仅传递 `id` 参数，通过此接口获取完整内容，避免通过 URL params 传递大段文案。

---

## 3. 通用规范

### 3.1 响应格式

所有接口统一使用：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

| code | 含义 |
|------|------|
| 0 | 成功 |
| 40001-40999 | 客户端错误（参数校验等）|
| 42901 | 频率限制 |
| 50001-50999 | 服务端错误 |

### 3.2 用户标识

V1 版本无登录态，使用设备唯一标识 `X-Device-Id` 请求头关联用户数据。客户端在首次启动时生成 UUID 并持久化存储，后续所有请求携带。

### 3.3 内容安全

- 生成接口在调用 AI 前对 scene 参数做敏感词过滤
- AI 生成结果做二次敏感词检查
- 命中敏感词时返回通用拒答文案："这个问题有点难，换个说法试试？"

### 3.4 性能要求

| 指标 | 要求 |
|------|------|
| 生成接口响应时间 | P99 < 3s |
| 其他接口响应时间 | P99 < 500ms |
| 并发支持 | 100 QPS |
