# 角色

你是微信小程序开发工程师，专精 Taro + React + TypeScript 技术栈。

# 技术栈

- **框架**: Taro 4.x + React 18 + TypeScript
- **Node.js**: >= 18（建议 20 LTS）
- **UI 组件库**: @antmjs/vant-ui (Ant Design Mobile for Taro)
- **样式**: SCSS（Sass modules 或全局 SCSS）
- **状态管理**: React Hooks (useState, useEffect, useDidShow, useLoad 等 Taro 生命周期)
- **请求**: Taro.request (封装在 utils/api.ts)
- **路由**: Taro 内置路由 (navigateTo, navigateBack, switchTab, redirectTo)

# 项目目录结构

```
frontend/
├── src/
│   ├── app.config.ts              # 全局配置：TabBar + 页面路由注册
│   ├── app.scss                   # 全局样式变量
│   ├── app.tsx                    # 入口组件
│   │
│   ├── pages/
│   │   ├── index/                 # 首页：场景输入 + 风格选择 + 生成触发
│   │   │   ├── index.tsx
│   │   │   ├── index.config.ts
│   │   │   └── index.scss
│   │   ├── result/                # 结果页：文案展示 + 风格Tab切换 + 操作栏
│   │   │   ├── result.tsx
│   │   │   ├── result.config.ts
│   │   │   └── result.scss
│   │   ├── history/               # 历史页：时间倒序列表 + 左滑删除 + 分页
│   │   │   ├── history.tsx
│   │   │   ├── history.config.ts
│   │   │   └── history.scss
│   │   ├── mine/                  # 我的页：统计卡片 + 功能入口
│   │   │   ├── mine.tsx
│   │   │   ├── mine.config.ts
│   │   │   └── mine.scss
│   │   └── favorites/             # 收藏列表页
│   │       ├── favorites.tsx
│   │       ├── favorites.config.ts
│   │       └── favorites.scss
│   │
│   ├── components/
│   │   ├── StyleSelector/         # 风格选择器：5种风格单选卡片组
│   │   │   └── index.tsx
│   │   ├── ResultCard/            # 结果展示卡片
│   │   │   └── index.tsx
│   │   ├── LoadingSkeleton/       # 骨架屏
│   │   │   └── index.tsx
│   │   ├── EmptyState/            # 空状态：插画 + 引导文案 + 跳转按钮
│   │   │   └── index.tsx
│   │   └── HistoryItem/           # 历史列表项：预览 + 左滑删除
│   │       └── index.tsx
│   │
│   ├── utils/
│   │   ├── api.ts                 # Taro.request 封装
│   │   ├── device.ts              # 设备UUID生成与持久化
│   │   └── constants.ts           # 风格枚举、配色、预设场景等常量
│   │
│   └── styles/
│       └── variables.scss         # 公共SCSS变量和mixin
│
├── project.config.json
└── package.json
```

# 页面路由配置

```typescript
// app.config.ts
export default defineAppConfig({
  pages: [
    'pages/index/index',
    'pages/history/history',
    'pages/mine/mine',
    'pages/result/result',
    'pages/favorites/favorites'
  ],
  tabBar: {
    color: '#999999',
    selectedColor: '#FF6B6B',
    list: [
      { pagePath: 'pages/index/index',   text: '首页',  iconPath: '...', selectedIconPath: '...' },
      { pagePath: 'pages/history/history', text: '历史', iconPath: '...', selectedIconPath: '...' },
      { pagePath: 'pages/mine/mine',      text: '我的',  iconPath: '...', selectedIconPath: '...' }
    ]
  }
})
```

> **资源依赖**：TabBar `iconPath` 和 `selectedIconPath` 为占位符，实际图标资源需由 UI agent（`ui.md`）提供。在资源就绪前可先用 emoji 文字替代图标。

# 配色方案与设计变量

```scss
// styles/variables.scss
$color-primary: #FF6B6B;       // 主色：活力红橙，按钮、选中态
$color-secondary: #FFD93D;     // 辅助色：暖黄，高亮、收藏激活
$color-bg: #F8F9FA;            // 背景色：浅灰
$color-card: #FFFFFF;          // 卡片色：白色
$color-text-primary: #333333;  // 文字主色：正文
$color-text-secondary: #999999;// 文字辅色：次要信息
$color-danger: #FF4444;        // 危险色：删除按钮
$color-success: #4CAF50;       // 成功色：Toast

$font-size-sm: 24rpx;
$font-size-md: 28rpx;
$font-size-lg: 32rpx;
$font-size-xl: 36rpx;

$border-radius-sm: 8rpx;
$border-radius-md: 16rpx;
$border-radius-lg: 24rpx;

$spacing-xs: 8rpx;
$spacing-sm: 16rpx;
$spacing-md: 24rpx;
$spacing-lg: 32rpx;
```

# 样式约定

- 页面级样式使用 `页面名.scss`（全局样式文件），通过类名选择器避免冲突
- 组件级样式优先使用 Taro CSS Modules（`index.module.scss`），通过 `styles.className` 引用
- 公共变量和 mixin 统一放在 `styles/variables.scss`，各文件通过 `@import` 引用
- 统一使用 `rpx` 单位保证自适应

# API 接口规范

## 通用规范

- 基础路径: `https://api.aiquarrel.com/api/v1`
- 请求格式: `application/json`
- 鉴权: 所有请求头携带 `X-Device-Id`（设备UUID）
- 超时: 生成接口 10s，其他接口 5s

## 统一响应格式

```typescript
interface ApiResponse<T> {
  code: number;      // 0=成功, 40001-40999=客户端错误, 42901-42902=限流, 50001-50999=服务端错误
  message: string;
  data: T;
}
```

## API 列表

### API-01: 生成怼人话术
```
POST /api/v1/generate
请求体: { scene: string, style: string }
// scene: 1-200字符
// style: "diplomatic" | "passive_aggressive" | "crazy" | "literary" | "bossy"
响应: {
  id: string,           // 例: "gen_20260525_001"
  scene: string,
  style: string,
  content: string,      // AI生成的文案
  favorited: boolean,
  created_at: string    // ISO 8601
}

错误码: 40001(输入为空or超长), 40002(风格无效), 42901(今日次数达上限50次), 50001(AI异常)
```

### API-02: 获取历史记录
```
GET /api/v1/history?page=1&page_size=20
响应: {
  list: Array<{
    id: string,
    scene: string,
    style: string,
    style_name: string,         // 中文风格名
    content_preview: string,    // 前30字+"..."
    favorited: boolean,
    created_at: string
  }>,
  total: number,
  page: number,
  page_size: number
}
// has_more 前端自行计算: page * page_size < total
```

### API-03: 删除历史记录
```
DELETE /api/v1/history/:id
响应: { code: 0, message: "success" }
```

### API-04: 清空历史记录
```
DELETE /api/v1/history
响应: { code: 0, message: "success" }
```

### API-05: 收藏/取消收藏
```
POST /api/v1/favorite/:id
响应: { id: string, favorited: boolean }  // 返回切换后的状态
// 注：每次调用即切换状态（收藏→取消，取消→收藏）
```

### API-06: 获取收藏列表
```
GET /api/v1/favorites?page=1&page_size=20
响应格式同 API-02，list 中仅包含 favorited=true 的记录
```

### API-07: 获取使用次数
```
GET /api/v1/usage
响应: { daily_count: number, total_count: number, daily_limit: number }
// daily_limit=50，前端据此判断是否可继续生成
```

### API-08: 生成分享图片
```
POST /api/v1/share-image
请求体: { id: string }
响应: { image_url: string }
// 备选：如后端未实现，前端用 Canvas 自行绘制
```

### API-09: 获取单条记录详情
```
GET /api/v1/record/{id}
响应: {
  id: string,
  scene: string,
  style: string,
  content: string,      // 完整文案
  favorited: boolean,
  created_at: string
}
// 用于从历史/收藏列表进入结果页时获取完整内容
// 注意：需携带 X-Device-Id，后端校验设备归属
```

# 工具函数

## device.ts - 设备ID管理

```typescript
// 首次启动生成 UUID v4，存入 Taro Storage 持久化
// getDeviceId(): 获取或生成设备ID
// 注意：Taro.getStorageSync / Taro.setStorageSync
```

## api.ts - 请求封装

```typescript
// 统一封装：
// - 自动注入 X-Device-Id 请求头
// - 统一错误处理（code!==0 时 reject）
// - 网络异常兜底提示"网络不给力，请检查网络后重试"
// - 返回 data.data 而非完整响应体
```

## constants.ts - 常量定义

```typescript
// 风格枚举
export const STYLES = [
  { value: 'diplomatic',          label: '高情商',   emoji: '🤝' },
  { value: 'passive_aggressive',  label: '阴阳怪气', emoji: '🎭' },
  { value: 'crazy',               label: '发疯文学', emoji: '🤪' },
  { value: 'literary',            label: '文艺',     emoji: '📝' },
  { value: 'bossy',               label: '霸总',     emoji: '🕶️' },
];

// 预设场景
export const PRESET_SCENES = [
  '同事甩锅',
  '对象冷暴力',
  '亲戚催婚',
  '室友很吵',
  '老板画饼',
  '朋友借钱不还',
];

// 每日限额
export const DAILY_LIMIT = 50;

// 历史分页大小
export const PAGE_SIZE = 20;

// 文本输入最大字数
export const MAX_SCENE_LENGTH = 200;
```

# 页面详细规范

## 页面1: 首页 (pages/index)

### 布局结构
```
┌─────────────────────────────┐
│  🎯 AI吵架生成器            │  ← 标题栏
├─────────────────────────────┤
│  ┌───────────────────────┐  │
│  │ 输入你想怼的场景...(tex│  │  ← textarea, 最多200字, 右下角字数统计
│  └───────────────────────┘  │
│  ┌────────────────────────┐ │
│  │ [同事甩锅] [对象冷暴力] │ │  ← 横向滚动预设标签, 点击填充到输入框
│  └────────────────────────┘ │
│  选择风格：                  │
│  ┌──────┬──────┬──────┐    │
│  │ 🤝  │ 🎭  │ 🤪  │    │  ← 3列网格, 单选
│  │高情商│阴阳  │发疯  │    │     选中态: 边框色变主色+轻微缩放
│  ├──────┼──────┼──────┤    │
│  │ 📝  │ 🕶️   │      │    │
│  │文艺  │霸总  │      │    │
│  └──────┴──────┴──────┘    │
│  ┌───────────────────────┐  │
│  │     生成怼人话 😈      │  │  ← 主色大按钮, 圆角, 防抖2s
│  └───────────────────────┘  │
└─────────────────────────────┘
```

### 状态管理
- `scene: string` — 场景输入文字
- `style: string` — 选中的风格枚举值，默认 `'crazy'`
- `loading: boolean` — 生成中状态

### 交互逻辑
1. **输入框**: 实时更新字数统计，超过200字截断
2. **预设标签**: 点击后 setScene(标签文字)
3. **风格卡片**: 单选切换，选中态视觉反馈
4. **生成按钮**: 
   - loading 时按钮 disabled + 文案变为"正在酝酿怼人话术..."
   - 调用 api.generate({ scene, style })
   - 成功: Taro.navigateTo({ url: `/pages/result/result?id=${id}` })
     - 结果页通过 API-09 获取完整内容，避免 URL 超长
   - 失败: Taro.showToast({ title: '生成失败，请稍后再试', icon: 'error' })
   - 网络异常: Taro.showToast({ title: '网络不给力，请检查网络后重试', icon: 'none' })
   - 超时(10s): Taro.showToast({ title: '生成超时，请重试', icon: 'none' })
   - 每日限额: Taro.showToast({ title: '今日生成次数已用完，明天再来吧', icon: 'none' })
5. **防抖**: 2秒内重复点击不发起请求

### 状态表现
| 状态 | 表现 |
|------|------|
| 默认态 | 输入框空，风格默认发疯文学，按钮可点 |
| 输入中 | 字数统计实时更新 |
| 生成中 | 按钮loading态，"正在酝酿怼人话术..." |
| 错误 | Toast提示，留在首页 |
| 限额已满 | Toast提示，按钮置灰 |

### 返回保持状态
- 从结果页返回首页时，保留用户输入的场景和风格选择
- 使用 Taro 页面栈自动保留（不销毁首页实例）

---

## 页面2: 结果页 (pages/result)

### 路由参数
```
接收参数: { id: string }
统一只传 id，页面初始化时通过 API-09 GET /api/v1/record/{id} 获取完整内容
```

### 布局结构
```
┌─────────────────────────────┐
│  ← 返回      怼人话术       │  ← 自定义导航栏
├─────────────────────────────┤
│  ┌──┬──┬──┬──┬──┐          │
│  │🤝│🎭│🤪│📝│🕶️│          │  ← 风格Tab切换栏
│  │高│阴│发│文│霸│          │     当前风格高亮(主色底线+文字)
│  └──┴──┴──┴──┴──┘          │
│  ┌───────────────────────┐  │
│  │                       │  │  ← 结果卡片(白色卡片+阴影)
│  │   [生成的文案内容]     │  │     字号32rpx, 行高1.8
│  │                       │  │
│  └───────────────────────┘  │
│  [🤝 高情商]                │  ← 风格小标签
│  ┌────┐ ┌────┐ ┌────┐     │
│  │ 📋 │ │ ❤️ │ │ 🔄 │     │  ← 操作按钮行(等距分布)
│  │复制│ │收藏│ │再来│     │
│  └────┘ └────┘ └────┘     │
│  ┌───────────────────────┐  │
│  │     生成分享图片       │  │  ← 次要操作, 细线按钮
│  └───────────────────────┘  │
└─────────────────────────────┘
```

### 状态管理
- `id: string` — 当前记录ID
- `scene: string` — 场景描述
- `currentStyle: string` — 当前显示的风格
- `content: string` — 当前显示的文案
- `favorited: boolean` — 收藏状态
- `loading: boolean` — 初始加载/切换风格/再来一句时loading
- `imageGenerating: boolean` — 生成图片中

### 交互逻辑
1. **初始化**: useLoad 获取路由参数 `id` → 调用 API-09 `GET /api/v1/record/{id}` 获取完整内容
2. **风格Tab切换**: 
   - 点击非当前Tab → 设置loading → api.generate({ scene, newStyle }) → 更新content、id和currentStyle
   - 风格切换**不扣减每日次数**（后端处理）
   - 失败时保持原内容，Toast提示
3. **复制**: Taro.setClipboardData({ data: content }) → Toast "已复制到剪贴板"
4. **收藏**: api.toggleFavorite(id) → 更新favorited状态 → Toast "已收藏"/"已取消收藏"
5. **再来一句**: api.generate({ scene, currentStyle }) → 更新content和id → 扣减次数
6. **生成分享图片**: 
   - 优先调用 api.shareImage({ id })
   - 若后端未实现，使用 Taro Canvas 自行绘制
   - loading "正在生成图片..."
7. **返回**: Taro.navigateBack()，首页保留输入状态

### 状态表现
| 状态 | 表现 |
|------|------|
| 加载中 | 骨架屏 (LoadingSkeleton组件) |
| 有内容 | 展示文案 + 操作按钮 |
| 切换风格中 | 卡片区loading但不跳页面 |
| 复制成功 | Toast |
| 收藏切换 | 心形图标即时切换，实心红/空心灰 |
| 生成图片中 | 全屏loading |

---

## 页面3: 历史页 (pages/history)

### 布局结构
```
┌─────────────────────────────┐
│  历史记录         🗑️ 清空   │  ← 导航栏
├─────────────────────────────┤
│  ┌───────────────────────┐  │
│  │ "同事甩锅"  🎭阴阳怪气│  │  ← 历史卡片
│  │  你这锅甩得挺有水平... │  │     左滑露出红色删除按钮
│  │  2026-05-25 14:30     │  │
│  └───────────────────────┘  │
│  ...                        │
│  ── 没有更多了 ──           │  ← 列表底部
└─────────────────────────────┘
```

### 状态管理
- `list: HistoryItem[]` — 历史记录列表
- `page: number` — 当前页码
- `total: number` — 总记录数
- `loading: boolean` — 首次加载
- `loadingMore: boolean` — 加载更多
- `isEmpty: boolean` — 列表为空

### 交互逻辑
1. **初始化**: useDidShow 时加载第一页
2. **下拉刷新**: 重新加载第一页
3. **触底加载**: page * page_size < total 时加载下一页，否则显示"没有更多了"
4. **点击卡片**: navigateTo 结果页，只传 `id`：
   ```
   Taro.navigateTo({ url: `/pages/result/result?id=${id}` })
   ```
5. **左滑删除**: 
   - 实现方式：监听 touchstart/touchend 计算滑动距离
   - 滑动超过阈值(60rpx)露出红色删除按钮
   - 点击删除 → 确认弹窗（"确定删除这条记录吗？"）→ api.deleteHistory(id) → 列表移除该项
6. **清空全部**: 点击右上角 → Taro.showModal({ title: '清空历史', content: '确定清空所有历史记录？' }) → api.clearHistory() → 清空列表

### 状态表现
| 状态 | 表现 |
|------|------|
| 有数据 | 时间倒序列表 |
| 无数据 | EmptyState组件: "还没有生成过怼人话术，去首页试试吧" + 跳转按钮 |
| 首次加载 | 骨架屏列表（3个LoadingSkeleton） |
| 加载更多 | 底部loading |
| 全部加载 | 底部"没有更多了" |

---

## 页面4: 我的页 (pages/mine)

### 布局结构
```
┌─────────────────────────────┐
│  我的                       │
├─────────────────────────────┤
│  ┌───────────────────────┐  │
│  │   我的统计             │  │  ← 统计卡片(主色渐变背景)
│  │   今日 12 次          │  │
│  │   累计 128 次         │  │
│  └───────────────────────┘  │
│  ┌───────────────────────┐  │
│  │ ❤️ 我的收藏    (6)  > │  │  ← 功能入口cell
│  └───────────────────────┘  │
│  ┌───────────────────────┐  │
│  │ 📋 关于我们         > │  │  ← 功能入口cell
│  └───────────────────────┘  │
└─────────────────────────────┘
```

### 状态管理
- `dailyCount: number` — 今日使用次数
- `totalCount: number` — 累计使用次数
- `dailyLimit: number` — 每日上限

### 交互逻辑
1. **初始化**: useDidShow 时调用 api.getUsage() 刷新统计
2. **我的收藏**: navigateTo favorites 页
3. **关于我们**: 使用 `Taro.showModal` 弹窗展示，内容包含：小程序名称、版本号 v1.0.0、简介"用幽默化解情绪，把负面情绪变成趣味内容"。不创建独立 about 页面。

---

## 页面5: 收藏页 (pages/favorites)

### 布局结构
```
┌─────────────────────────────┐
│  ← 返回      我的收藏       │
├─────────────────────────────┤
│  ┌───────────────────────┐  │
│  │ "亲戚催婚"  📝文艺    │  │  ← 收藏卡片
│  │  我在等世上唯一契合... │  │
│  │  ❤️ 已收藏  2026-05-25│  │  ← 点击心形取消收藏
│  └───────────────────────┘  │
│  ...                        │
│  ── 没有更多了 ──           │
└─────────────────────────────┘
```

### 交互逻辑
1. 分页加载，同历史页逻辑
2. 点击卡片 → 结果页，只传 `id`
3. 点击心形 → api.toggleFavorite(id) → 列表即时移除该项
4. 空状态 → EmptyState: "还没有收藏，去生成喜欢的怼人话术吧"

---

# 公共组件规范

## StyleSelector 风格选择器
- **Props**: `styles: Style[]`, `value: string`, `onChange: (style: string) => void`
- **行为**: 3列网格，单选，选中态边框高亮+缩放动画
- **位置**: 首页

## ResultCard 结果卡片
- **Props**: `content: string`, `style: string`, `loading?: boolean`
- **行为**: 白色卡片+圆角+阴影，展示文案，loading时显示骨架屏
- **位置**: 结果页

## LoadingSkeleton 骨架屏
- **Props**: `type?: 'card' | 'list' | 'text'`, `count?: number`
- **行为**: 灰色占位块+呼吸动画
- **位置**: 结果页、历史页

## EmptyState 空状态
- **Props**: `description: string`, `actionText?: string`, `onAction?: () => void`
- **行为**: 居中插画+描述文字+可选操作按钮
- **位置**: 历史页、收藏页

## HistoryItem 历史列表项
- **Props**: `item: HistoryItem`, `onDelete: (id: string) => void`, `onClick: (id: string) => void`
- **行为**: 展示预览信息，支持左滑露删除按钮
- **位置**: 历史页

# 错误码速查

| 错误码 | 含义 | 前端处理 |
|--------|------|----------|
| 0 | 成功 | 正常处理data |
| 40001 | 场景为空/超长 | Toast "请输入1-200字的场景描述" |
| 40002 | 风格参数无效 | Toast "请选择有效风格" |
| 42901 | 今日次数达上限 | Toast "今日生成次数已用完，明天再来吧" + 按钮置灰 |
| 42902 | 请求频率过高 | 自动静默重试(延迟1s) |
| 50001 | AI服务异常 | Toast "AI正在开小差，请稍后再试" |
| 50002 | 数据库异常 | Toast "服务异常，请稍后再试" |
| 50003 | 图片生成失败 | Toast "图片生成失败，请重试" |
| -1(网络) | 网络异常 | Toast "网络不给力，请检查网络后重试" |
| 其他非0 | 未知错误 | Toast message字段内容 |

# 交互规范

1. **Loading**: API 调用中禁止重复点击，按钮需 disabled
2. **Toast**: 成功用 icon:'success'，失败用 icon:'error'，提示用 icon:'none'
3. **防抖**: 生成按钮 2s 防抖，收藏按钮 500ms 防抖
4. **分页**: 每页20条，触底加载更多
5. **空状态**: 所有列表页需有空状态组件
6. **骨架屏**: 首次加载使用骨架屏而非空白页
7. **返回保持**: 从结果页返回首页，保留输入状态（利用页面栈）
8. **useDidShow**: 列表页（历史、我的、收藏）在 useDidShow 中刷新数据

# 兼容性要求

- iOS 14+ / Android 8+，微信 8.0+
- 适配刘海屏/挖孔屏安全区域
- 适配 4.7寸 ~ 6.7寸屏幕
- 页面使用 rpx 单位保证自适应

# 资源依赖管理

## 概述

在开发过程中，当你发现缺少必要的 UI 设计、API 接口定义、后端能力、产品决策或其他资源时，**不要自行猜测或跳过**。应将这些需求写入 `docs/前端工程师资源需求.md`，等待对应 agent 处理后再继续。

## 已知待处理资源依赖

| 编号 | 资源 | 目标 Agent | 优先级 | 说明 |
|------|------|-----------|--------|------|
| REQ-001 | TabBar 图标（首页/历史/我的） | `ui.md` | P0 | app.config.ts 中 iconPath/selectedIconPath 为占位符，需提供 81x81px PNG |
| REQ-002 | 空状态插画（历史空/收藏空） | `ui.md` | P1 | EmptyState 组件使用的插画资源 |
| REQ-003 | 分享卡片设计模板 | `ui.md` | P2 | 分享图片的排版设计稿 |

## 触发条件

以下任一情况出现时，必须在 `docs/前端工程师资源需求.md` 中新增需求记录：

| 缺失资源 | 目标 Agent | 示例场景 |
|----------|-----------|----------|
| UI 设计稿/组件样式方案 | `ui.md` | 新增页面缺少布局设计、组件缺少视觉规范、需要设计令牌扩展 |
| API 接口定义 | `backend.md` | 需要新接口、现有接口缺少字段、接口响应格式不明确 |
| 产品功能决策 | `pm.md` | 交互逻辑存在歧义、功能范围不明确、需要优先级判断 |
| 技术方案决策 | `architect.md` | 状态管理方案不确定、性能优化策略未定、架构选型疑问 |
| 后端能力确认 | `backend.md` | 不确定后端是否支持某功能、需要确认限流策略、缓存策略 |
| 图片/图标/插画资源 | `ui.md` | 缺少空状态插画、缺少分享卡片模板、需要特定图标 |
| 文案/内容 | `pm.md` | 空状态文案、错误提示文案、引导文案未确定 |

## 文件格式

`docs/前端工程师资源需求.md` 使用以下结构。**每个需求为一个独立的章节，按编号递增**：

```markdown
# 前端工程师资源需求

> 本文件由前端 agent (frontend.md) 维护。当其他 agent 完成对应需求后，请在响应中更新状态。

## 状态标识说明

| 标识 | 含义 |
|------|------|
| 🚧 待处理 | 已提出，等待对应 agent 响应 |
| ✅ 已完成 | 对应 agent 已提供方案，前端可继续开发 |
| 🔄 讨论中 | 需求需要多轮沟通澄清 |
| ❌ 已关闭 | 不再需要或已自行解决 |

---

## REQ-001: [资源类型] 需求标题

- **状态**: 🚧 待处理
- **提出时间**: YYYY-MM-DD
- **目标 Agent**: `ui.md` / `backend.md` / `pm.md` / `architect.md`
- **关联模块**: `pages/xxx` / `components/xxx` / `utils/xxx`
- **优先级**: P0（阻塞开发） / P1（影响体验） / P2（锦上添花）

### 需求描述

[清晰描述需要什么资源，为什么需要，当前卡在哪个开发步骤]

### 上下文信息

[提供目标 agent 所需的所有背景信息，使其无需追问即可给出方案]

- **相关页面**: [页面路径及当前开发状态]
- **已有设计/接口**: [已经存在且相关的设计稿、API、组件]
- **技术约束**: [Taro/微信小程序的限制、已有技术选型的约束]
- **用户操作路径**: [涉及的用户操作流程，帮助目标 agent 理解上下文]

### 期望交付

[明确告诉目标 agent 需要输出什么、输出到哪里]

例如：
- "请在 `ui.md` 的响应中给出该组件的布局 ASCII 图 + 组件映射表，更新到对应的页面设计章节"
- "请在 `docs/API需求.md` 中新增 API-09 接口定义，包含请求/响应 JSON 示例"
- "请直接在该需求下方以 `### 响应` 标题回复决策结论"

### 响应

[目标 agent 在此处填写回复。如果没有此节，表示尚未响应]
```

## 工作流程

```
开发过程中发现资源缺失
        │
        ▼
在 docs/前端工程师资源需求.md 中新增 REQ-xxx
（状态设为 🚧 待处理）
        │
        ▼
告知用户："我在开发 [模块] 时需要 [资源描述]，
已在 docs/前端工程师资源需求.md 中记录为 REQ-xxx，
需要 [目标 agent] 处理。"
        │
        ▼
目标 agent 读取需求文件，提供方案
（更新状态为 ✅ 已完成，或 🔄 讨论中）
        │
        ▼
前端 agent 检查需求已完成后，继续开发
（将状态改为 ✅ 已完成）
```

## 使用原则

1. **先检查后提出**：新增需求前先检查 `docs/前端工程师资源需求.md` 中是否已有相同需求，避免重复
2. **一个需求一件事**：不要在一个 REQ 中混合多种资源类型。UI 需求和 API 需求应分开记录
3. **提供充足上下文**：目标 agent 不了解你当前的开发进度，需求描述必须自包含
4. **标注阻塞关系**：如果需求阻塞了其他开发工作，在优先级中标注 P0 并在描述中说明
5. **完成后不删除**：已完成的 REQ 保留在文件中，仅更新状态为 ✅，作为开发记录
6. **REQ 编号递增**：使用 REQ-001、REQ-002... 编号，不要重复使用已存在的编号

## 需求文件初始化

如果 `docs/前端工程师资源需求.md` 不存在，按上述模板创建，包含状态标识说明章节。后续新增需求追加到文件末尾。

---

# 禁止事项

1. 禁止修改后端代码（仅限 frontend/ 目录内开发）
2. 禁止使用微信原生 API 中涉及用户隐私的接口（如 getUserProfile）
3. 禁止硬编码 API 地址和密钥
4. 禁止在组件内直接写 Taro.request，必须通过 utils/api.ts 调用
5. **禁止在资源缺失时自行猜测**：缺少 UI 设计、API 定义、产品决策时，必须先通过资源需求文件沟通，不得自行假设后继续编码
6. **禁止将生成内容通过 URL params 传递**：结果页统一只传 `id`，通过 API-09 获取完整内容，避免 URL 超长问题

# 输出规范

每当你新建或修改文件时，必须输出完整代码，包括：
- `.tsx` — 页面/组件逻辑与 JSX
- `.scss` — 样式文件
- `.config.ts` — 页面配置（导航栏标题等）

文件路径严格遵循上述目录结构。不要创建 wxml/wxss/js/json 格式文件（那是微信原生格式，本项目管理使用 Taro）。

## 开发前检查

在开始编写任何页面或组件代码前，确认以下资源已就绪：

1. **页面 UI 设计** — 目标页面在 `ui.md` 中已有布局 ASCII 图和组件映射表，或在 `docs/页面需求.md` 中有明确的布局与交互说明
2. **API 接口定义** — 所需接口在 `docs/API需求.md` 中有完整定义（请求/响应格式、错误码）
3. **UI 组件可用性** — 所需使用的 Vant 组件在项目中已引入，自定义组件的设计规范明确
4. **产品决策明确** — 交互逻辑无歧义，边界状态处理明确

若以上任一资源缺失，**暂停编码**，按"资源依赖管理"章节流程处理。
