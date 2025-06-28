# AI推荐接口性能优化方案

## 问题分析

`ai_recommends` 接口平均响应时间 10s，主要耗时点：

1. **数据库查询**：用户画像查询 (~100ms)
2. **天气API调用**：墨迹天气API (~2-3s)
3. **AI文案生成**：阿里云 DashScope API (~3-5s)
4. **AI图片生成**：Gitee AI API (~3-5s)

## 优化策略

### 1. 缓存优化

#### 1.1 多层缓存架构
```
用户请求 → 结果缓存 → AI内容缓存 → 数据缓存 → 外部API
```

#### 1.2 缓存策略
- **结果缓存**：30分钟，避免重复计算
- **AI内容缓存**：2小时，相同提示词复用
- **用户画像缓存**：1小时，减少数据库查询
- **天气数据缓存**：30分钟，减少API调用

#### 1.3 缓存Key设计
```java
// 结果缓存
"ai_recommend:{cityId}:{openId}"

// AI内容缓存  
"ai_recommend_content:{promptHash}"

// 用户画像缓存
"user_portrait:{openId}"

// 天气数据缓存
"weather_hourly:{fid}"
```

### 2. 异步处理

#### 2.1 图片生成异步化
- 主流程：返回文案推荐（无图片）
- 异步流程：生成图片并更新缓存
- 用户体验：先看到推荐，图片稍后加载

#### 2.2 异步实现
```java
CompletableFuture.runAsync(() -> {
    // 异步生成图片
    String imgUrl = generateClothingImage(rec, potrait);
    if (imgUrl != null) {
        rec.setImgUrl(imgUrl);
        // 更新缓存
        redisUtil.set(cacheKey, rec, 1800);
    }
});
```

### 3. HTTP客户端优化

#### 3.1 连接池配置
```java
// 通用连接池
maxConnections: 200
maxIdleTime: 60s
maxLifeTime: 5min

// AI专用连接池
maxConnections: 50
maxIdleTime: 120s
maxLifeTime: 10min
```

#### 3.2 超时配置
```java
// 通用API
connectTimeout: 10s
responseTimeout: 30s

// AI API
connectTimeout: 15s
responseTimeout: 60s
```

### 4. 性能监控

#### 4.1 监控注解
```java
@PerformanceMonitor(
    description = "AI穿衣推荐服务", 
    slowThreshold = 5000, 
    logParams = true
)
```

#### 4.2 监控指标
- 方法执行时间
- 慢查询警告（>5s）
- 参数和返回值记录
- 异常监控

## 预期效果

### 1. 响应时间优化
- **首次请求**：从 10s 降至 3-5s
- **缓存命中**：从 10s 降至 50-100ms
- **图片异步**：文案推荐 1-2s，图片稍后加载

### 2. 缓存命中率
- **结果缓存**：相同用户+城市，30分钟内 100% 命中
- **AI内容缓存**：相同天气+用户画像，2小时内复用
- **数据缓存**：减少 80% 的外部API调用

### 3. 系统稳定性
- 连接池复用，减少连接建立开销
- 异步处理，避免长时间阻塞
- 超时控制，防止资源耗尽

## 实施步骤

### 第一阶段：缓存优化
1. ✅ 添加Redis缓存层
2. ✅ 实现多层缓存策略
3. ✅ 优化缓存Key设计

### 第二阶段：异步处理
1. ✅ 图片生成异步化
2. ✅ 主流程优化
3. ✅ 缓存更新机制

### 第三阶段：HTTP优化
1. ✅ 连接池配置
2. ✅ 超时设置
3. ✅ 专用客户端

### 第四阶段：监控完善
1. ✅ 性能监控注解
2. ✅ 慢查询告警
3. ✅ 日志优化

## 测试验证

### 1. 性能测试
```bash
# 测试缓存命中
curl -X GET "http://localhost:8084/weather/ai_recommends?cityId=101010100&openId=test123"

# 测试异步图片
curl -X GET "http://localhost:8084/weather/ai_recommends?cityId=101010100&openId=test123"
```

### 2. 监控指标
- 接口响应时间
- 缓存命中率
- 外部API调用次数
- 系统资源使用率

### 3. 压力测试
- 并发用户数：100
- 请求频率：每秒10次
- 测试时长：10分钟

## 注意事项

### 1. 缓存一致性
- 用户画像更新时清除缓存
- 天气数据定时刷新
- AI内容缓存基于内容hash

### 2. 异常处理
- 缓存失败时降级到原流程
- 异步任务异常不影响主流程
- 外部API失败时的重试机制

### 3. 资源管理
- 连接池大小根据实际负载调整
- 缓存过期时间根据业务需求设置
- 异步任务数量控制

## 后续优化

### 1. 预加载策略
- 热门城市天气数据预加载
- 用户画像预加载
- AI模型预热

### 2. 智能缓存
- 基于用户行为的缓存策略
- 动态调整缓存时间
- 缓存容量自适应

### 3. 分布式优化
- Redis集群部署
- 负载均衡
- 服务降级策略 