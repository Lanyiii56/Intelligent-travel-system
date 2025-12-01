# Git 协作指南

> 本文档详细介绍如何使用 Git 和 GitHub 进行团队协作开发

## 📋 目录

1. [初次使用配置](#1-初次使用配置)
2. [克隆项目](#2-克隆项目)
3. [日常开发流程](#3-日常开发流程)
4. [分支管理](#4-分支管理)
5. [提交代码](#5-提交代码)
6. [同步代码](#6-同步代码)
7. [解决冲突](#7-解决冲突)
8. [Pull Request 流程](#8-pull-request-流程)
9. [常用命令速查](#9-常用命令速查)
10. [IDEA 图形化操作](#10-idea-图形化操作)

---

## 1. 初次使用配置

### 1.1 安装 Git

**macOS:**
```bash
brew install git
```

**Windows:**
下载安装 https://git-scm.com/download/win

### 1.2 配置用户信息

```bash
git config --global user.name "你的名字"
git config --global user.email "你的邮箱@example.com"
```

### 1.3 配置 SSH 密钥（推荐）

```bash
# 生成密钥
ssh-keygen -t ed25519 -C "你的邮箱@example.com"

# 查看公钥
cat ~/.ssh/id_ed25519.pub
```

将公钥添加到 GitHub：
1. 打开 https://github.com/settings/keys
2. 点击 **New SSH key**
3. 粘贴公钥内容
4. 点击 **Add SSH key**

### 1.4 配置 SSH 走 443 端口（解决网络问题）

```bash
# 创建或编辑 ~/.ssh/config
echo "Host github.com
  Hostname ssh.github.com
  Port 443
  User git" >> ~/.ssh/config
```

### 1.5 测试连接

```bash
ssh -T git@github.com
# 成功会显示: Hi xxx! You've successfully authenticated...
```

---

## 2. 克隆项目

### 2.1 使用 SSH（推荐）

```bash
git clone git@github.com:Lanyiii56/Intelligent-travel-system.git
```

### 2.2 使用 HTTPS

```bash
git clone https://github.com/Lanyiii56/Intelligent-travel-system.git
```

### 2.3 克隆后安装依赖

```bash
cd Intelligent-travel-system

# 安装前端依赖
cd frontend
npm install

# 后端依赖会由 Maven 自动下载
```

---

## 3. 日常开发流程

### 标准工作流程

```
1. 拉取最新代码  →  2. 创建功能分支  →  3. 编写代码  →  4. 提交代码  →  5. 推送分支  →  6. 创建 PR
```

### 3.1 开始工作前：拉取最新代码

```bash
# 切换到主分支
git checkout main

# 拉取最新代码
git pull origin main
```

### 3.2 创建功能分支

```bash
# 创建并切换到新分支
git checkout -b feature/你的功能名

# 示例
git checkout -b feature/login-page      # 成员1: 登录页面
git checkout -b feature/spot-list       # 成员2: 景点列表
git checkout -b feature/recommend       # 成员3: 推荐功能
git checkout -b feature/order-system    # 成员4: 订单系统
```

### 3.3 编写代码

在你的模块目录下进行开发...

### 3.4 查看修改状态

```bash
# 查看哪些文件被修改
git status

# 查看具体修改内容
git diff
```

---

## 4. 分支管理

### 4.1 分支命名规范

| 分支类型 | 命名格式 | 示例 |
|----------|----------|------|
| 功能开发 | `feature/功能名` | `feature/login-page` |
| Bug 修复 | `fix/问题描述` | `fix/login-error` |
| 热修复 | `hotfix/问题描述` | `hotfix/security-issue` |
| 文档更新 | `docs/文档名` | `docs/readme-update` |

### 4.2 分支操作

```bash
# 查看所有分支
git branch -a

# 切换分支
git checkout 分支名

# 创建新分支
git checkout -b 新分支名

# 删除本地分支
git branch -d 分支名

# 删除远程分支
git push origin --delete 分支名
```

---

## 5. 提交代码

### 5.1 添加文件到暂存区

```bash
# 添加单个文件
git add 文件路径

# 添加所有修改
git add .

# 添加指定目录
git add frontend/src/pages/
```

### 5.2 提交代码

```bash
git commit -m "提交信息"
```

### 5.3 提交信息规范

```
<类型>: <简短描述>

[可选的详细描述]
```

**类型说明：**

| 类型 | 说明 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat: 添加用户登录功能` |
| `fix` | 修复 Bug | `fix: 修复登录验证失败问题` |
| `docs` | 文档更新 | `docs: 更新 README` |
| `style` | 代码格式 | `style: 格式化代码` |
| `refactor` | 重构 | `refactor: 重构用户服务` |
| `test` | 测试 | `test: 添加登录单元测试` |
| `chore` | 构建/工具 | `chore: 更新依赖版本` |

**示例：**

```bash
git commit -m "feat: 完成用户登录页面"
git commit -m "fix: 修复登录时密码验证错误"
git commit -m "docs: 添加 API 接口文档"
```

### 5.4 推送到远程

```bash
# 首次推送新分支
git push -u origin 分支名

# 后续推送
git push
```

---

## 6. 同步代码

### 6.1 拉取远程更新

```bash
# 拉取并合并（推荐）
git pull origin main

# 或者分步操作
git fetch origin
git merge origin/main
```

### 6.2 将主分支更新合并到功能分支

```bash
# 在功能分支上
git checkout feature/your-feature

# 合并主分支的更新
git merge main

# 或使用 rebase（保持提交历史整洁）
git rebase main
```

---

## 7. 解决冲突

### 7.1 冲突产生原因

当两个人修改了同一个文件的同一部分时，Git 无法自动合并，需要手动解决。

### 7.2 冲突标记

```
<<<<<<< HEAD
你的代码
=======
别人的代码
>>>>>>> branch-name
```

### 7.3 解决步骤

1. **打开冲突文件**，找到冲突标记
2. **决定保留哪些代码**（可能需要合并双方的修改）
3. **删除冲突标记**（`<<<<<<<`, `=======`, `>>>>>>>`）
4. **保存文件**
5. **标记为已解决并提交**

```bash
git add 冲突文件
git commit -m "fix: 解决合并冲突"
```

### 7.4 使用 VS Code 解决冲突

VS Code 会高亮显示冲突，并提供按钮：
- **Accept Current Change** - 保留你的修改
- **Accept Incoming Change** - 保留别人的修改
- **Accept Both Changes** - 保留双方修改
- **Compare Changes** - 对比查看

---

## 8. Pull Request 流程

### 8.1 什么是 Pull Request (PR)

PR 是请求将你的分支代码合并到主分支的过程，便于代码审查。

### 8.2 创建 PR

1. 推送你的分支到远程
   ```bash
   git push -u origin feature/your-feature
   ```

2. 打开 GitHub 仓库页面

3. 点击 **Compare & pull request** 按钮

4. 填写 PR 信息：
   - **Title**: 简短描述这次改动
   - **Description**: 详细说明改动内容

5. 点击 **Create pull request**

### 8.3 PR 模板

```markdown
## 改动说明
简要描述这次 PR 的改动内容

## 改动类型
- [ ] 新功能
- [ ] Bug 修复
- [ ] 文档更新
- [ ] 代码重构

## 测试情况
- [ ] 本地测试通过
- [ ] 前端页面正常显示
- [ ] 后端接口正常响应

## 相关截图（如有）
```

### 8.4 代码审查

- 团队成员可以在 PR 页面评论、提出修改建议
- 根据反馈修改代码后，再次 push 即可更新 PR
- 审查通过后，点击 **Merge pull request** 合并

### 8.5 合并后清理

```bash
# 切换回主分支
git checkout main

# 拉取最新代码
git pull origin main

# 删除本地功能分支
git branch -d feature/your-feature
```

---

## 9. 常用命令速查

### 基础操作

| 命令 | 说明 |
|------|------|
| `git status` | 查看当前状态 |
| `git add .` | 添加所有修改 |
| `git commit -m "信息"` | 提交代码 |
| `git push` | 推送到远程 |
| `git pull` | 拉取远程更新 |

### 分支操作

| 命令 | 说明 |
|------|------|
| `git branch` | 查看本地分支 |
| `git branch -a` | 查看所有分支 |
| `git checkout 分支名` | 切换分支 |
| `git checkout -b 新分支` | 创建并切换分支 |
| `git merge 分支名` | 合并分支 |

### 查看历史

| 命令 | 说明 |
|------|------|
| `git log --oneline` | 查看简洁提交历史 |
| `git log -5` | 查看最近5条提交 |
| `git diff` | 查看未暂存的修改 |
| `git diff --staged` | 查看已暂存的修改 |

### 撤销操作

| 命令 | 说明 |
|------|------|
| `git checkout -- 文件` | 撤销文件修改 |
| `git reset HEAD 文件` | 取消暂存 |
| `git reset --soft HEAD^` | 撤销上次提交（保留修改） |
| `git reset --hard HEAD^` | 撤销上次提交（丢弃修改）⚠️ |

### 远程操作

| 命令 | 说明 |
|------|------|
| `git remote -v` | 查看远程仓库 |
| `git fetch origin` | 获取远程更新（不合并） |
| `git push -u origin 分支` | 推送并关联远程分支 |

---

## 📌 团队协作最佳实践

### ✅ 推荐做法

1. **每天开始工作前先 pull**
2. **小步提交，频繁推送**
3. **写清晰的提交信息**
4. **只修改自己模块的文件**
5. **遇到冲突及时沟通**
6. **合并前先在本地测试**

### ❌ 避免做法

1. **不要直接在 main 分支开发**
2. **不要强制推送 `git push -f`**
3. **不要提交 node_modules 等依赖目录**
4. **不要提交包含密码的配置文件**
5. **不要忽略冲突直接覆盖**

---

## 🆘 常见问题

### Q: push 被拒绝怎么办？

```bash
# 先拉取远程更新
git pull origin main

# 解决冲突后再推送
git push
```

### Q: 不小心提交了错误的文件？

```bash
# 撤销上次提交（保留文件修改）
git reset --soft HEAD^

# 重新选择要提交的文件
git add 正确的文件
git commit -m "新的提交信息"
```

### Q: 想放弃本地所有修改？

```bash
# ⚠️ 危险操作，会丢失所有未提交的修改
git checkout .
git clean -fd
```

### Q: 切换分支时提示有未提交的修改？

```bash
# 方法1: 提交修改
git add .
git commit -m "WIP: 临时保存"

# 方法2: 暂存修改
git stash
git checkout 其他分支
# 回来后恢复
git stash pop
```

---

## 10. IDEA 图形化操作

> 使用 IntelliJ IDEA / WebStorm 进行 Git 操作，无需记忆命令行

### 10.1 首次配置 IDEA

#### 配置 Git 路径
1. 打开 **Settings** (`Cmd + ,` 或 `Ctrl + Alt + S`)
2. 搜索 **Git**
3. 确认 Git 可执行文件路径正确（通常自动检测）
4. 点击 **Test** 验证

#### 配置 GitHub 账号
1. **Settings** → **Version Control** → **GitHub**
2. 点击 **+** 添加账号
3. 选择 **Log In via GitHub** 或 **Log In with Token**
4. 授权登录

### 10.2 克隆项目

1. 打开 IDEA，选择 **Get from VCS**
2. URL 填入：`https://github.com/Lanyiii56/Intelligent-travel-system.git`
3. 选择本地目录
4. 点击 **Clone**

### 10.3 拉取最新代码 (Pull)

**方法1：工具栏**
- 点击工具栏的 **蓝色向下箭头** ↓ 图标

**方法2：菜单**
- **Git** → **Pull**

**方法3：快捷键**
- macOS: `Cmd + T`
- Windows: `Ctrl + T`

### 10.4 创建分支

1. 点击右下角的分支名（如 `main`）
2. 选择 **New Branch**
3. 输入分支名，如 `feature/login-page`
4. 勾选 **Checkout branch**
5. 点击 **Create**

### 10.5 切换分支

1. 点击右下角的分支名
2. 在列表中选择目标分支
3. 点击 **Checkout**

### 10.6 提交代码 (Commit)

**方法1：快捷键（推荐）**
- macOS: `Cmd + K`
- Windows: `Ctrl + K`

**方法2：菜单**
- **Git** → **Commit**

**提交步骤：**
1. 在左侧勾选要提交的文件
2. 在下方输入提交信息（遵循规范，如 `feat: 添加登录功能`）
3. 点击 **Commit** 仅提交到本地
4. 或点击 **Commit and Push** 提交并推送

### 10.7 推送代码 (Push)

**方法1：快捷键**
- macOS: `Cmd + Shift + K`
- Windows: `Ctrl + Shift + K`

**方法2：菜单**
- **Git** → **Push**

**方法3：工具栏**
- 点击 **绿色向上箭头** ↑ 图标

### 10.8 查看修改历史

**查看文件历史：**
1. 右键点击文件
2. **Git** → **Show History**

**查看项目历史：**
1. 点击底部 **Git** 标签页
2. 选择 **Log** 查看提交历史

### 10.9 解决冲突

当 Pull 或 Merge 出现冲突时：

1. IDEA 会弹出冲突对话框
2. 点击 **Merge** 打开三栏合并工具：
   - 左侧：你的代码
   - 中间：合并结果
   - 右侧：远程代码
3. 使用 `>>` 和 `<<` 按钮选择保留哪边的代码
4. 或直接在中间编辑最终结果
5. 点击 **Apply** 完成合并

### 10.10 查看当前修改

**方法1：快捷键**
- macOS: `Cmd + 9`
- Windows: `Alt + 9`

**方法2：**
- 点击底部 **Git** 标签页 → **Local Changes**

### 10.11 撤销修改

**撤销未提交的修改：**
1. 右键点击文件
2. **Git** → **Rollback**

**撤销已提交但未推送：**
1. 打开 **Git** → **Log**
2. 右键点击要撤销的提交
3. 选择 **Undo Commit**

### 10.12 IDEA Git 操作速查表

| 操作 | macOS 快捷键 | Windows 快捷键 | 菜单位置 |
|------|-------------|----------------|----------|
| 提交 | `Cmd + K` | `Ctrl + K` | Git → Commit |
| 推送 | `Cmd + Shift + K` | `Ctrl + Shift + K` | Git → Push |
| 拉取 | `Cmd + T` | `Ctrl + T` | Git → Pull |
| 更新项目 | `Cmd + T` | `Ctrl + T` | Git → Update Project |
| 查看历史 | - | - | Git → Show History |
| 查看修改 | `Cmd + 9` | `Alt + 9` | View → Tool Windows → Git |
| 撤销修改 | `Cmd + Z` | `Ctrl + Z` | Git → Rollback |

### 10.13 IDEA 完整工作流程示例

#### 场景：成员1 开发登录功能

```
1. 拉取最新代码
   - 快捷键 Cmd+T (或点击 ↓ 图标)

2. 创建功能分支
   - 点击右下角 main → New Branch
   - 输入 feature/login-page → Create

3. 编写代码
   - 修改 frontend/src/pages/Login.vue
   - 修改 backend/.../auth/controller/AuthController.java

4. 提交代码
   - 快捷键 Cmd+K
   - 勾选修改的文件
   - 输入 "feat: 完成登录页面表单验证"
   - 点击 Commit

5. 继续开发...再次提交

6. 推送到远程
   - 快捷键 Cmd+Shift+K
   - 点击 Push

7. 在 GitHub 创建 Pull Request
   - 打开 https://github.com/Lanyiii56/Intelligent-travel-system
   - 点击 Compare & pull request
   - 填写说明 → Create pull request

8. 代码审查通过后合并

9. 切换回 main 分支，拉取最新代码
   - 点击右下角分支名 → main → Checkout
   - Cmd+T 拉取
```

---

## 📞 需要帮助？

遇到 Git 问题时：
1. 先 `git status` 查看当前状态
2. 查阅本文档对应章节
3. 在群里询问团队成员
4. 搜索错误信息

**仓库地址**: https://github.com/Lanyiii56/Intelligent-travel-system
