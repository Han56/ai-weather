App({
  globalData: {
    adcode: '230100', // 默认哈尔滨市的adcode
    userInfo: null,
    openId: null // 添加openId字段
  },
  onLaunch: function () {
    // 展示本地存储能力
    var logs = wx.getStorageSync("logs") || []
    logs.unshift(Date.now())
    wx.setStorageSync("logs", logs)

    // 获取用户信息
    wx.getSetting({
      success: (res) => {
        if (res.authSetting["scope.userInfo"]) {
          // 已经授权，可以直接调用 getUserInfo 获取头像昵称，不会弹框
          wx.getUserInfo({
            success: (res) => {
              // 可以将 res 发送给后台解码出 unionId
              // this.globalData.userInfo = res.userInfo
            },
          })
        }
      },
    })
  },
})

