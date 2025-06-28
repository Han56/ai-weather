# Redis配置说明

## 配置概述

本项目已成功配置Redis，支持缓存功能。Redis配置信息如下：

- **Host**: 39.104.14.216
- **Port**: 6379
- **Database**: 0
- **连接池**: Lettuce连接池
- **序列化**: Jackson2JsonRedisSerializer

## 配置文件

### 1. 主配置文件
- `application.yml` - 主配置文件
- `application-dev.yml` - 开发环境配置
- `application-prod.yml` - 生产环境配置

### 2. Redis配置项
```yaml
spring:
  redis:
    host: 39.104.14.216
    port: 6379
    password: # 请在这里填入您的Redis密码
    database: 0
    timeout: 10000ms
    lettuce:
      pool:
        max-active: 8
        max-wait: -1ms
        max-idle: 8
        min-idle: 0
```

## 重要提醒

⚠️ **请务必在配置文件中填入正确的Redis密码！**

在以下文件中找到 `password:` 字段并填入您的Redis密码：
- `application.yml`
- `application-dev.yml` 
- `application-prod.yml`

## 使用说明

### 1. 注入RedisUtil
```java
@Autowired
private RedisUtil redisUtil;
```

### 2. 基本操作示例
```java
// 设置缓存
redisUtil.set("key", "value");

// 设置缓存并指定过期时间（秒）
redisUtil.set("key", "value", 3600);

// 获取缓存
Object value = redisUtil.get("key");

// 删除缓存
redisUtil.del("key");

// 检查key是否存在
boolean exists = redisUtil.hasKey("key");
```

### 3. Hash操作示例
```java
// 设置Hash
redisUtil.hset("hashKey", "field", "value");

// 获取Hash
Object value = redisUtil.hget("hashKey", "field");

// 获取所有Hash字段
Map<Object, Object> allFields = redisUtil.hmget("hashKey");
```

### 4. List操作示例
```java
// 添加元素到List
redisUtil.lSet("listKey", "value");

// 获取List元素
List<Object> list = redisUtil.lGet("listKey", 0, -1);
```

### 5. Set操作示例
```java
// 添加元素到Set
redisUtil.sSet("setKey", "value1", "value2");

// 获取Set所有元素
Set<Object> set = redisUtil.sGet("setKey");
```

## 测试接口

项目提供了Redis测试接口，可以通过以下URL进行测试：

- `GET /redis/testConnection` - 测试Redis连接
- `POST /redis/set` - 设置缓存
- `GET /redis/get` - 获取缓存
- `DELETE /redis/delete` - 删除缓存
- `POST /redis/setWithExpire` - 设置缓存并指定过期时间
- `GET /redis/hasKey` - 检查key是否存在

### 测试示例
```bash
# 首先测试Redis连接
curl -X GET "http://localhost:8084/redis/testConnection"

# 设置缓存
curl -X POST "http://localhost:8084/redis/set?key=test&value=hello"

# 获取缓存
curl -X GET "http://localhost:8084/redis/get?key=test"

# 设置缓存并指定过期时间
curl -X POST "http://localhost:8084/redis/setWithExpire?key=test&value=hello&expireSeconds=60"
```

## 注意事项

1. **密码配置**: 确保在配置文件中正确设置Redis密码
2. **网络连接**: 确保应用服务器能够访问Redis服务器
3. **防火墙**: 检查防火墙是否允许6379端口的连接
4. **连接池**: 根据实际需求调整连接池配置
5. **序列化**: 使用Jackson序列化，支持复杂对象的存储

## 故障排除

### 常见问题

1. **连接失败**
   - 检查Redis服务器是否运行
   - 验证host和port配置
   - 确认网络连接正常

2. **认证失败**
   - 检查密码是否正确
   - 确认Redis服务器是否需要密码认证

3. **JSON解析错误**
   - 错误信息：`Unrecognized token 'xxx': was expecting (JSON String, Number, Array, Object...)`
   - 原因：Redis中存储的数据不是JSON格式，但序列化器试图解析为JSON
   - 解决方案：已更新Redis配置使用更灵活的序列化策略
   - 使用 `getString()` 方法获取字符串值
   - 使用 `/redis/testConnection` 接口测试连接

4. **序列化错误**
   - 确保存储的对象是可序列化的
   - 检查对象是否有循环引用

### 测试步骤

1. **首先测试连接**：
   ```bash
   curl -X GET "http://localhost:8084/redis/testConnection"
   ```

2. **如果连接成功，测试基本操作**：
   ```bash
   curl -X POST "http://localhost:8084/redis/set?key=test&value=hello"
   curl -X GET "http://localhost:8084/redis/get?key=test"
   ```

### 日志查看
在开发环境中，Redis操作的日志级别设置为debug，可以在控制台查看详细的Redis操作日志。 