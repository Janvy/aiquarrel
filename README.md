# AI吵架生成器

轻娱乐 · 情绪表达 · 社交传播

一款帮助用户用幽默、高情商方式表达情绪的 AI 生成工具。把负面情绪转化为可分享的趣味内容。

## 产品定位

- **目标用户**：18~35 岁互联网原住民
- **平台**：微信小程序
- **核心场景**：同事甩锅、对象冷暴力、亲戚催婚、室友很吵、老板画饼、朋友借钱不还
- **生成风格**：高情商版 / 阴阳怪气版 / 发疯文学版 / 文艺版 / 霸总版

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Taro 4.x + React 18 + TypeScript |
| UI | @antmjs/vant-ui |
| 后端 | SpringBoot 3.x + Java 17 |
| ORM | MyBatis Plus |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 7.x |
| AI | DeepSeek API (OpenAI 兼容协议) |
| 部署 | Docker + Docker Compose + Nginx |

## 项目结构

```
aiquarrel/
├── frontend/                # Taro + React 微信小程序
│   └── src/
│       ├── pages/           # 页面：首页、结果页、历史页、我的页、收藏页
│       ├── components/      # 公共组件
│       └── utils/           # API 封装、设备ID、常量
├── backend/                 # SpringBoot 3.x 后端服务
│   └── src/main/java/com/aiquarrel/
│       ├── controller/      # 控制器层
│       ├── service/         # 业务逻辑层
│       ├── ai/              # AI 模块（DeepSeek API）
│       ├── security/        # 安全模块（敏感词过滤）
│       ├── interceptor/     # 拦截器（设备识别、限流）
│       └── model/           # 数据模型（DTO、Entity、Mapper）
├── deploy/                  # Docker 部署
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── nginx/               # Nginx 配置
├── docs/                    # 项目文档
│   ├── PRD.md               # 产品需求文档
│   ├── API需求.md            # 接口定义
│   ├── 技术架构设计.md        # 架构设计
│   └── 验收标准.md            # 验收条件
└── .claude/agents/          # Claude Code Agent 定义
    ├── architect.md         # 架构师
    ├── backend.md           # 后端开发
    ├── frontend.md          # 前端开发
    ├── pm.md                # 产品经理
    ├── ui.md                # UI 设计师
    ├── qa.md                # 测试工程师
    └── ops.md               # 运维工程师
```

## 快速开始

### 环境要求

- Node.js >= 18
- JDK >= 17
- Maven 3.x
- Docker + Docker Compose

### 本地开发

**前端：**

```bash
cd frontend
npm install
npm run dev:weapp
```

**后端：**

```bash
cd backend
mvn spring-boot:run
```

**完整部署：**

```bash
cd deploy
cp .env.example .env   # 编辑 .env 填写密钥
docker-compose up -d
```

## 多 Agent 协作

本项目采用多 Agent 工作流，各 Agent 定义在 `.claude/agents/` 目录下：

| Agent | 职责 |
|-------|------|
| `pm` | 产品需求定义与决策 |
| `architect` | 技术架构设计 |
| `ui` | UI 设计与组件规范 |
| `backend` | 后端业务代码开发 |
| `frontend` | 前端页面与组件开发 |
| `prompt` | AI Prompt 模板调优 |
| `qa` | 测试用例与质量验收 |
| `ops` | 部署与运维 |

## 文档

- [产品需求文档](docs/PRD.md)
- [用户流程图](docs/用户流程图.md)
- [页面需求](docs/页面需求.md)
- [API 需求](docs/API需求.md)
- [技术架构设计](docs/技术架构设计.md)
- [验收标准](docs/验收标准.md)

## 许可证

MIT
