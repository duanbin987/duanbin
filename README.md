# 飞花令 · 诗词学习系统

面向小学生的「飞花令」诗词闯关游戏，三端分离架构。

## 项目结构

```
duanbin/
├── miniprogram/          # uni-app 微信小程序（Vue 3）
├── poetry-admin/
│   ├── web/              # Vue 3 管理后台
│   └── server/           # Spring Boot 2.7 后端 API
└── README.md
```

## 技术栈

| 模块 | 技术 |
|------|------|
| 小程序 | uni-app + Vue 3 + Vite |
| 管理后台 | Vue 3 + Vite + Element Plus |
| 后端 | Spring Boot 2.7 + MyBatis-Plus + Redis + **JDK 1.8** |
| 数据库 | MySQL 5.7 |
| 缓存 | Redis（端口 6333） |

## 快速启动

### 1. 数据库（MySQL 5.7）

环境要求：**MySQL 5.7.x**（建议 5.7.28+），字符集 `utf8mb4`。

```bash
mysql -u root -p < poetry-admin/server/src/main/resources/db/schema.sql
mysql -u root -p < poetry-admin/server/src/main/resources/db/seed-data.sql
```

若尚未创建用户权限，可先登录 MySQL 执行：

```sql
CREATE DATABASE IF NOT EXISTS feihua_poetry DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

默认管理员：`admin` / `admin123`

修改数据库连接：[`poetry-admin/server/src/main/resources/application.yml`](poetry-admin/server/src/main/resources/application.yml)

### 2. 后端

**环境要求：JDK 1.8**（如 `1.8.0_191`）。请勿用 JDK 11+ 运行后端，否则易出现 JWT 等兼容问题。

```bash
cd poetry-admin/server
# 确认 Java 版本
java -version
mvn spring-boot:run
```

IDEA 配置：`File → Project Structure → Project SDK` 选择 **1.8 (jdk1.8.0_191)**，`Language level` 选 **8**。

API 地址：`http://localhost:8080`

### 3. 管理后台

```bash
cd poetry-admin/web
npm install
npm run dev
```

访问：`http://localhost:5173`（若端口被占用，终端会提示改用 5174 等）

**启动失败常见原因：**

| 现象 | 处理 |
|------|------|
| `Vite requires Node.js version 18` | 已降级为 Vite 4，需重新 `npm install`；或升级 Node 到 18+ |
| `ENOENT` / 找不到 `vite` | 先进入 `poetry-admin/web` 再执行，不要在上级目录运行 |
| 端口 5173 被占用 | 关闭占用进程，或使用终端提示的新端口 |
| 页面能开但登录失败 | 先启动 Spring Boot（8080）；账号 `admin` / `admin123`；**必须用 `npm run dev`**（`preview` 或无代理的静态服务无法转发 `/api`） |

### 4. 小程序（uni-app）

```bash
cd miniprogram
npm install
npm run dev:mp-weixin
```

编译产物在 `miniprogram/dist/dev/mp-weixin/`，用**微信开发者工具**导入该目录。

开发配置：

- 修改 API 地址：[`miniprogram/src/config/env.js`](miniprogram/src/config/env.js)
- `manifest.json` 中已设置 `urlCheck: false`，便于本地调试
- 在 `src/manifest.json` → `mp-weixin.appid` 填入你的小程序 AppID

**推荐**：也可使用 [HBuilderX](https://www.dcloud.io/hbuilderx.html) 打开 `miniprogram` 目录，运行到微信开发者工具。

## 小程序说明

- 基于 **uni-app（Vue 3）** 开发，一套代码可编译到微信小程序
- 使用 `uni.request` 调用后端 API
- 游戏进度保存在本地 Storage

## 构建发布

```bash
# 小程序生产包
cd miniprogram
npm run build:mp-weixin
# 产物：dist/build/mp-weixin/
```

## Node 版本建议

uni-app Vite 模板建议使用 **Node 18+**。若本地为 Node 16，升级后重新 `npm install`。

## MySQL 5.7 说明

- 表结构 SQL 已按 **MySQL 5.7** 语法编写（`utf8mb4`、InnoDB），无 MySQL 8 专有特性。
- Java 端仍使用 **mysql-connector-java 8.0.x** 驱动连接 5.7 服务端，这是官方推荐做法，无需降级到 5.1 驱动。
- 若连接失败，请检查：
  - MySQL 服务已启动，端口 3306 可访问
  - `application.yml` 中用户名、密码与本地一致
  - 数据库 `feihua_poetry` 已创建并执行过 `schema.sql`
