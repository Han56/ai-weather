Page({
  data: {
    userPortrait: "",
    aiAdvice: {
      title: "今日穿搭建议",
      content: `根据当前天气状况，建议着装偏向保暖舒适。可以选择：
        1. 内搭：长袖T恤或轻薄毛衣
        2. 外套：轻便夹克或针织开衫
        3. 下装：常规牛仔裤或休闲长裤
        4. 鞋子：运动鞋或休闲鞋
        记得随身携带一件薄外套，以防天气变化。`,
    },
  },

  onLoad: () => {
    // 页面加载时的逻辑
  },

  // 意见反馈
  onFeedback: () => {
    wx.navigateTo({
      url: "/pages/feedback/feedback",
    })
  },

  // 联系我们
  onContact: () => {
    wx.showModal({
      title: "联系我们",
      content: "客服电话：400-880-0599\n工作时间：工作日9-18点",
      showCancel: false,
    })
  },

  // 隐私协议
  onPrivacy: () => {
    wx.navigateTo({
      url: "/pages/privacy/privacy",
    })
  },

  // 用户画像设置
  onPortraitSettings: () => {
    wx.navigateTo({
      url: "/pages/portrait-settings/portrait-settings",
    })
  },
})