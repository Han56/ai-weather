# Redis + AOP 限流功能说明

## 功能概述

本项目实现了基于Redis和AOP模式的分布式限流功能，专门针对 `ai_recommends` 接口进行限流控制。

## 限流配置

### 1. AI推荐接口限流配置
```java
@RateLimited(
    limit = 20,                                    // 每日最多20次请求
    type = RateLimited.RateLimitType.DAILY,        // 按天限流
    message = "AI推荐服务每日限流20次，请明天再试"      // 限流提示信息
)
```

### 2. 限流类型
- **DAILY**: 按天限流（默认）
- **HOURLY**: 按小时限流
- **MINUTELY**: 按分钟限流

## 实现原理

### 1. 注解定义
- `@RateLimited`: 限流注解，用于标记需要限流的方法
- 支持配置限制次数、限流类型、过期时间和提示信息

### 2. AOP切面
- `RateLimitAspect`: 限流切面，拦截带有 `@RateLimited` 注解的方法
- 基于Redis实现分布式限流计数
- 支持多种限流策略

### 3. 限流Key生成规则
```
rate_limit:ai_recommends:{openId}:{时间后缀}
```

时间后缀格式：
- 按天：`yyyyMMdd`
- 按小时：`yyyyMMddHH`
- 按分钟：`yyyyMMddHHmm`

## 使用示例

### 1. 在方法上添加限流注解
```java
@GetMapping("/ai_recommends")
@RateLimited(limit = 20, type = RateLimited.RateLimitType.DAILY)
public ServiceResult<AiClothingRecommendationsResponse> aiRecommendations(
    @RequestParam String cityId, 
    @RequestParam String openId
) {
    // 业务逻辑
}
```

### 2. 自定义限流配置
```java
// 每小时限流5次
@RateLimited(limit = 5, type = RateLimited.RateLimitType.HOURLY)

// 每分钟限流10次
@RateLimited(limit = 10, type = RateLimited.RateLimitType.MINUTELY)

// 自定义提示信息
@RateLimited(limit = 50, message = "服务繁忙，请稍后再试")
```

## 测试接口

### 1. 限流测试接口
```bash
# 测试限流功能（每分钟限流5次）
curl -X GET "http://localhost:8084/rateLimit/test?openId=test123"

# 获取限流信息
curl -X GET "http://localhost:8084/rateLimit/info?openId=test123&type=DAILY"

# 重置限流计数
curl -X DELETE "http://localhost:8084/rateLimit/reset?openId=test123&type=DAILY"
```

### 2. AI推荐接口测试
```bash
# 正常请求
curl -X GET "http://localhost:8084/weather/ai_recommends?cityId=101010100&openId=test123"

# 当超过限制时会返回限流错误
# 响应示例：
{
    "success": false,
    "code": "429",
    "message": "AI推荐服务每日限流20次，请明天再试",
    "result": null
}
```

## 监控和日志

### 1. 日志记录
- 限流触发时会记录WARN级别日志
- 成功访问时会记录INFO级别日志
- 异常情况会记录ERROR级别日志

### 2. Redis监控
可以通过Redis客户端查看限流数据：
```bash
# 查看所有限流key
redis-cli keys "rate_limit:*"

# 查看特定用户的限流计数
redis-cli get "rate_limit:ai_recommends:test123:20241201"
```

## 配置说明

### 1. Redis配置
确保Redis配置正确，限流功能依赖Redis存储：
```yaml
spring:
  redis:
    host: 39.104.14.216
    port: 6380
    password: "Wang150460!"
```

### 2. AOP配置
项目已启用AOP功能，无需额外配置。

## 故障排除

### 1. 常见问题

**限流不生效**
- 检查Redis连接是否正常
- 确认AOP切面是否被正确加载
- 验证注解是否正确添加

**限流计数异常**
- 检查Redis中的数据格式
- 确认时间格式是否正确
- 验证openId参数是否传递

**性能问题**
- 考虑使用Redis集群
- 优化限流算法
- 调整限流策略

### 2. 调试方法

1. **查看日志**：检查应用日志中的限流相关信息
2. **Redis检查**：直接查看Redis中的限流数据
3. **接口测试**：使用测试接口验证限流功能
4. **监控工具**：使用Redis监控工具查看数据

## 扩展功能

### 1. 自定义限流策略
可以通过修改 `RateLimitAspect` 实现自定义限流策略：
- 滑动窗口限流
- 令牌桶限流
- 漏桶限流

### 2. 多维度限流
可以扩展支持多维度限流：
- 按IP限流
- 按用户等级限流
- 按接口类型限流

### 3. 限流统计
可以添加限流统计功能：
- 限流次数统计
- 限流用户分析
- 限流趋势监控 