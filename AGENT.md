# Agent 指南：HealthTrack 全栈项目

本文件用于指导 AI 助手在本仓库中的工作方式，目标是**帮我把 HealthTrack 这个全栈小项目做完、跑通、方便展示**，而不是搞复杂的多代理 / MCP / 自动化工作流。

---

## 1. 项目简介

**HealthTrack** 是一个课程/作业用的全栈小项目，用来管理用户的健康与就医信息，包括：

HealthTrack 是一个个人健康与健身管理平台，类似于 MyChart 或 Fitbit 等应用，帮助用户跟踪健康指标、管理医疗预约，并与他人共同参与健康活动。

1.用户可以通过提供姓名、唯一的 Health ID（类似于社会安全号 SSN）、电子邮箱地址和电话号码来注册 HealthTrack。每个 HealthTrack 账号只能记录一个电话号码，但可以关联多个电子邮箱地址。电子邮箱和电话号码在用于关键功能（例如接收提醒）之前必须经过验证。验证的具体过程可以忽略。在平台系统实现时默认已验证！！

2.用户可以将多个医疗服务提供者（例如医生、专科医生、治疗师等）关联到自己的账号上，但必须在其中指定一位作为自己的主治医生（primary care physician）。医疗服务提供者通过唯一的执业许可证号（medical license number）标识。提供者必须经过验证（例如与官方注册表进行确认）后，才能正式关联到用户的档案中。
用户也必须能够取消关联（unlink）某个提供者。系统需要能够同时记录已验证和未验证的提供者信息。验证过程本身与 E/R 模型设计无关。在平台系统实现时默认都已验证！！

3.在 HealthTrack 网络中，两个或更多用户可以被组织成一个“家庭组（Family Group）”。这允许一个用户（例如父母）在获得适当权限的前提下，帮助管理另一位用户（例如孩子）的健康档案。

4.HealthTrack 支持两种类型的操作（动作）：
a. 与提供者预约（Book an Appointment with a provider）。
b. 为其他用户创建健康挑战（Create a Wellness Challenge for other users）。

5.预约（Book an Appointment）：
预约可以通过指定提供者的执业许可证号或已验证的电子邮箱地址来进行，同时需要提供：
预约日期和时间
咨询类型（例如：面诊 In-Person，线上 Virtual）
备注（可选，用于描述症状或要咨询的问题）
每一次预约交易都有一个唯一的 ID。

6.创建健康挑战（Create a Wellness Challenge）：
用户可以通过指定其他人的电子邮箱地址或电话号码，邀请他们加入一个挑战，并设置：
a. 挑战目标（例如：“一个月走满 100 英里”）
b. 开始日期和结束日期
典型用例是“开启一个健身目标（Start a Fitness Goal）”，一个挑战可以在朋友和家庭成员之间分享。每个挑战都有一个唯一的 ID。
系统需要能够明确记录：
a. 哪些用户参与了该挑战
b. 并能够跟踪他们在挑战中的进度。

7.向尚未关联HealthTrack账号的电子邮箱地址或电话号码发送的挑战邀请或数据共享邀请，被视为对新用户的邀请。被邀请人可以在15 天内通过注册 HealthTrack 来接受邀请。超过 15 天后，邀请即自动作废。对于发送到未与账号关联或未验证的邮箱/电话的邀请，也是同样的规则：15 天内有效，逾期取消。
对于每一条邀请，系统都需要在数据库中记录：
a. 邀请发起的日期和时间（initiated_at）
b. 邀请完成（被接受）或过期的日期和时间（completed / expired）。

8.预约可以在预定时间前最多 24 小时内被取消。被取消的预约也必须记录在数据库中，同时记录取消的原因（例如：“患者改期（Patient Rescheduled）”、“医生无法出诊（Provider Unavailable）”等）。

9.用户的健康数据和历史应以每月总结报告（monthly summary reports）的形式进行组织与存储。

10.系统还需要提供搜索功能，例如：
a. 按提供者搜索
b. 按预约日期搜索
c. 按给定月份查询总步数
等等。

你需要为 HealthTrack 个人健康平台的数据库开发一个应用系统。需要实现如下菜单和功能；下面列出的所有应用都必须实现。
主菜单（Main menu）：
a. Account Info（账号信息）
b. Book an Appointment (with a provider)（与提供者预约）
c. Create a Wellness Challenge (for other users)（为其他用户创建健康挑战）
d. Monthly Health Summary (key metrics and appointments for the month)（月度健康总结：该月关键指标和预约情况）
e. Search Records (based on user Health ID, provider, appointment type, date range, etc.)
（记录查询：基于用户 Health ID、提供者、预约类型、日期范围等进行查询）
f. Sign Out（退出登录）
所以该系统需要有登录注册功能，以及用户的身份可能不同，分别是用户和provider

账号功能（Account functions）：
a. 修改个人信息（Modify personal details）
b. 添加/删除电子邮箱地址（Add/remove email address）
c. 添加/删除电话号码（Add/remove phone number）
d. 添加/删除/关联医疗服务提供者（Add/remove/link a healthcare provider）

统计与汇总功能（Summary functions）：
a. 在给定日期范围内，统计某一用户的总预约次数。
b. 按月计算某一健康指标（例如体重、血压）的平均值 / 最小值 / 最大值。
c. 找出参与人数最多的健康挑战。
d. 找出最活跃的用户（例如记录健康数据最多，或者完成挑战最多的用户）。

综上，我总结了一下项目的需求：
1. 需要有登录和注册功能。

2. 进入系统之后，首先是登录页面，要求用户先选择登录身份是普通用户还是医疗服务提供者，若是普通用户，则输入自己的Health ID，和密码。
   Health ID规定为9位的数字序列，不可以0开头。
   分别有登录和注册两个按钮，点击登录按钮则校验用户是否输入Health ID和密码，未输入则提示用户输入Health ID或密码，
   若均已输入则校验Health ID是否已经注册，若未注册提示不存在该用户，
   若已经注册则检验密码是否正确，密码正确则进入系统主页面，否则提示密码错误。

   若身份为医疗服务提供者，则输入自己的执业许可证号（medical license number）标识和密码，而不是Health ID。其他流程与普通用户登录校验一致。

3. 注册功能
   则需要用户在注册页面的表格中先选择注册的用户身份是普通用户还是医疗服务提供者，
   若是普通用户则输入姓名，Health ID，密码，确认密码，以及电话号和邮箱，电话号只能输入一个，而邮箱则可以输入多个。均默认已通过验证。电话号要求为11位1开头的数字序列，邮箱需要是xxx@xx.com的格式。密码和确认密码须一致，不一致则提示密码与确认密码不一致。

   若是医疗服务提供者，则输入姓名，唯一的执业许可证号（medical license number）标识而不是Health ID，其他要求则与普通用户输入一致。
   特别注意，注册时均需验证普通用户的Health ID，或医疗服务提供者的执业许可证号（medical license number）标识是否已经被注册了，若已经被注册，则需提示更换Health ID或执业许可证号（medical license number）标识。

4. 账号功能
   在主界面，用户可点击右上角的个人主页，进入个人信息页面，在该页面将显示用户的Health ID或执业许可证号（medical license number）标识，电子邮箱，电话号，若用户身份是普通用户的话，则可以显示关联的医疗服务提供者，一个用户可关联多个医疗服务提供者，但必须指定一个为自己的主治医生，这些显示出来的信息都可以进行添加或删除。特别是添加关联的医疗服务提供者时，有一个下拉框，会显示系统中已注册的医疗服务提供者的名字，选择一个来作为自己的关联的医疗服务提供者。

5. 预约功能
   在主页面，普通用户可以使用预约功能，发起与医疗服务提供者的预约，预约的对象必须是普通用户自己关联的某一个医疗服务提供者，同时需要提供：预约日期和时间、咨询类型（例如：面诊 In-Person，线上 Virtual）、备注（可选，用于描述症状或要咨询的问题），每一次预约交易都有一个唯一的 ID。
   
   医疗服务提供者则可在系统中查看到用户发起的与自己的预约，只能看到跟自己有关的预约，在“我的预约”显示该预约的详细信息。同时可以选择同意或拒绝，拒绝需要填写理由。因此在数据库中，每条预约记录需要有一个“状态”字段，用于记录该预约的状态，是“已发起”、“已同意”还是“已拒绝”。当用户发起一个与某医疗服务提供者的预约后，该预约的状态为“已发起”、对应的医疗服务提供者同意后，该预约状态为“已同意”，拒绝则状态为“已拒绝”，并显示理由。

   因此在该功能里，普通用户可以看到自己发起的预约，已同意的预约和被拒绝的预约。医疗服务提供者同样可以看到与自己相关的预约，分为待处理的预约，已同意的预约和被拒绝的预约

6. 创建健康挑战
   在主页面，普通用户可以使用创建挑战功能，通过指定其他人的电子邮箱地址或电话号码，邀请他们加入一个挑战，并设置：
   a. 挑战目标（例如：“一个月走满 100 英里”）
   b. 开始日期和结束日期
   典型用例是“开启一个健身目标（Start a Fitness Goal）”，一个挑战可以在朋友和家庭成员之间分享。每个挑战都有一个唯一的 ID。
   系统需要能够明确记录：
   a. 哪些用户参与了该挑战
   b. 并能够跟踪他们在挑战中的进度。
   同时，用户可以看到自己创建的健康挑战和参与的健康挑战，一个健康挑战可邀请多人，因此每个挑战都有一个唯一的 ID，同时会记录该挑战的参与人数。

   每个挑战邀请也有一个唯一的id，记录了挑战的id和被邀请人的Health ID，两者共同组成一个挑战邀请的主键，一个挑战邀请同样有三个状态，“已发起”、“已同意”和“已拒绝”，设定规则和预约相同，因此用户可看到自己发起的挑战邀请的每个用户的接受情况，同一个挑战的邀请的用户的接受状态显示在同一个挑战中。被邀请的用户同意参与挑战后，对应挑战的“参与人数字段”会自动加1。

   用户同意参加挑战后，就会生成一条“挑战参与”的数据，有唯一的id，记录了挑战的id和参与者的Health ID，两者共同组成一个挑战参与的主键，该条数据也有一个字段为“完成状态”，该状态有挑战的发起者进行设置。
   
   因此在该功能里，用户可以看到自己发起的挑战、待处理的挑战，未完成的挑战和已完成的挑战。

7. 月度健康总结
   在主界面，用户可选择上传自己的健康状态，如体重、体脂率，血糖、血压、血脂，当日行走步数等指标，并记录日期。
   在“每月总结报告（monthly summary reports）”板块，可根据当月用户自行上传提供的健康数据来进行可视化。同时也要可视化出当月的预约情况，如共预约了几次，预约的日期分散情况。

8. 搜索功能
   a. 在给定日期范围内，统计某一用户的总预约次数。用户的搜索关键词使用health ID，均采用下拉框的形式
   b. 按月计算某一健康指标（例如体重、血压）的平均值 / 最小值 / 最大值。该功能通过选取某个用户，以及某个健康指标，按月计算该健康指标的平均值 / 最小值 / 最大值。并可视化出来
   c. 找出参与人数最多的健康挑战。根据每条挑战记录的“参与人数”字段选出参与人数最大的挑战，展示该挑战的详细信息。
   d. 找出最活跃的用户（例如记录健康数据最多，或者完成挑战最多的用户）。

如果有模棱两可的地方，请与我进行确认！！！

项目结构大致如下：

.
├── agent.md                  # 本文件
├── README.md
├── backend/                  # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/...
│       ├── java/com/healthtrack/...
│       └── resources/
│           ├── application.properties
│           └── schema.sql    # MySQL 建表脚本
└── frontend/                 # Vite 前端（默认 5173 端口）
    ├── package.json
    └── src/...

---

## 2. 技术栈与运行方式

### 2.1 后端（backend）

- Java 17
- Spring Boot 3.x
- Spring Web + Spring Data JPA + Hibernate
- MySQL 8.x（数据库名：`healthtrack`）

典型启动方式（在 backend 目录）：

mvn spring-boot:run


数据库初始化：

- 使用 backend/src/main/resources/schema.sql 在 MySQL 中创建表，记住我的mysql版本为8.0+ 不要写一些非法的语法，创建的表结构和字段一定要和后端对应的上！
- 若已手工建表，建议将 spring.jpa.hibernate.ddl-auto 调整为 none 或 validate，
  避免 Hibernate 自动改表结构。

---

### 2.2 前端（frontend）

- 基于 Vite 的前端工程，vue框架
- 开发服务器默认地址：`http://localhost:5173`
- 通过调用后端 `http://localhost:8080/api/**` 接口工作

在 frontend 目录：

npm install
npm run dev

然后在浏览器访问：`http://localhost:5173`

---

## 3. Agent 的目标与边界

在这个项目里，AI 助手（Agent）的主要职责是：

1. 帮助项目跑得起来、能演示  
   - 后端能正常启动，核心接口可用（用户、医生、预约、健康挑战等）。
   - 前端能编译运行，可以基本访问/展示后端数据。

2. 协助完成与课程相关的需求  
   - 按老师 / 作业说明的要求完善接口与页面。
   - 不追求大型生产级架构，优先完成度、可读性和可讲解性。

3. 修 bug、补文档、优化可读性  
   - 排查编译错误、运行时错误、CORS 问题等。
   - 必要时增加注释和简单文档说明，方便答辩/展示。

4. 避免无关复杂机制  
   - 不使用 MCP、多代理调度、复杂自动化工作流等。
   - 尽量少引入与作业无关的新依赖。

不做 / 尽量避免做的事情：

- 不重写整个项目的技术栈（例如改成 Node.js / Nest / Next 等）。
- 不引入 CI/CD、Kubernetes、消息队列等与课程无关的基础设施。
- 不做与当前任务无关的大规模重构（以易懂、可交、可演示为主）。

---

## 4. 开发约定

### 4.1 代码修改原则

- 小步修改：一次改动尽量集中在一个功能点上。
- 保持风格一致：遵循项目已有命名、包结构与代码格式。
- 优先兼容前后端契约：
  - 若要修改后端接口的 URL、请求体/响应体结构，必须同步修改前端调用。
  - 优先“在现有接口基础上扩展”而不是“推倒重写”。

### 4.2 数据库与实体

- 数据库结构以 schema.sql 为准，JPA 实体应尽量与其保持一致。
- 涉及主键 / 外键约束的字段（如 wellness_challenges.id、challenge_participants.challenge_id 等）
  避免随意修改类型，以免 Hibernate 在启动时尝试 alter table 导致外键冲突。
- 如需变更数据结构：
  1. 先修改 schema.sql；
  2. 再更新对应的 Entity、Repository、Service、Controller；
  3. 必要时将 spring.jpa.hibernate.ddl-auto 设置为 none，仅通过脚本建表。

---

## 5. CORS 与前后端联调

开发环境下：

- 前端：`http://localhost:5173`
- 后端：`http://localhost:8080`

为避免浏览器 CORS 报错（如 `No 'Access-Control-Allow-Origin' header`），后端建议配置全局 CORS（示意代码，仅供参考，不一定要写在本文件中）：

package com.healthtrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}

也可以在单个控制器类上使用（示意）：

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UserController {
    ...
}

前端侧也可以在 Vite 中配置代理，将 /api 代理到 http://localhost:8080，从而避免跨域检查。

---

## 6. 推荐的开发流程

每次改动建议遵循以下流程：

1. 确认环境 OK  
   - 安装：JDK 17、Maven、Node.js、MySQL 8。
   - 已在 MySQL 中执行 schema.sql，成功创建 healthtrack 数据库和相关表。

2. 启动后端  
   - 在 backend/：mvn spring-boot:run  
   - 确认日志中 Tomcat 启动在 8080 端口，无致命错误。

3. 启动前端  
   - 在 frontend/：npm run dev  
   - 浏览器访问：http://localhost:5173

4. 联调 & 修复  
   - 在浏览器开发者工具中查看 Network 面板，排查 4xx/5xx 或 CORS 错误。
   - 出现问题时，优先检查：
     - 接口路径是否与后端 Controller 一致；
     - 请求方法/参数是否正确；
     - 后端日志中是否有异常栈打印。

5. 更新文档（可选但推荐）  
   - 若修改了启动方式、主要接口或数据库结构，更新 README.md / agent.md 做简单说明，
     方便之后复盘和答辩。

---

## 7. 总结

对本仓库，AI 助手只需要记住三点：

1. 目标简单明确：  
   让 HealthTrack 全栈项目能正常启动、跑通核心流程，可以用于演示和交作业。

2. 改动克制：  
   在现有框架内修 bug、补功能、优化代码，不引入与课程无关的复杂机制。

3. 前后端 / 数据库保持一致：  
   接口契约、实体类、数据库结构三者要对齐，避免“前端调不到、后端报错、DB 约束冲突”的情况。

