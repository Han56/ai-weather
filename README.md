根据当前温度 湿度 风力 性别 身体情况 作为提示词，向大模型提问该穿什么衣服，返回后匹配电商数据仓库的衣物进行搭配，重点突出衣物的搭配。
一期
时间线：11.16 - 1.25
后端工程
基建
1. 统一返回拦截器  
2. 日志拦截器  - 暂不需要

接入墨迹天气api
实现当日实时天气+各时间段天气+近15日天气预报；
阿里云API市场：https://market.aliyun.com/apimarket/detail/cmapi013828#sku=yuncode782800000
采用webflux异步调用接入

接入文心一言ERNIE-Tiny-8K对话模型
实现智能助手推出文本类型天气穿搭；
使用百度千帆大模型平台
https://qianfan.cloud.baidu.com/?track=dingbu
https://console.bce.baidu.com/qianfan/ais/console/onlineTest/LLM
使用免费模型 ERNIE-Tiny-8k 进行 文生文 智能生成核心组件
模型收费情况：https://cloud.baidu.com/doc/WENXINWORKSHOP/s/hlrk4akp7
本期开发重点突出 prompt 优化上

用户模块
1. 位置+年龄+性别+国籍+人种+活跃地区+身高+体重+身体健康状态。
2. 落库持久化，留出人物画像图片位置 待拓展；
前端工程
logo设计参考材料：https://mp.weixin.qq.com/s/CyYwc5aNd3ik5aOe8bBmpw

二期
后端：
1. 接入 文心一言/豆包 大模型，实现文字生成图片；

三期
