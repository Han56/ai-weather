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
    isLoggedIn: false,
    openid: null,
    liveIndex: {
      city: {},
      listMap: {}
    },
    today: new Date().toISOString().split('T')[0],
    loading: true
  },

  onLoad: function() {
    wx.showLoading({
      title: '加载中...',
      mask: true
    });
    
    const appid = 'x';
    const secret = 'x';
    
    wx.login({
      success: (res) => {
        if (res.code) {
          console.log('临时登录凭证：', res.code);
          
          wx.request({
            url: `https://api.weixin.qq.com/sns/jscode2session?appid=${appid}&secret=${secret}&js_code=${res.code}&grant_type=authorization_code`,
            success: (loginRes) => {
              if (loginRes.data.openid) {
                console.log('用户openid：', loginRes.data.openid);
                this.setData({
                  isLoggedIn: true,
                  openid: loginRes.data.openid
                });
                wx.setStorage({
                  key: 'openid',
                  data: loginRes.data.openid
                });
                // 获取生活指数数据
                this.getLiveIndex().finally(() => {
                  wx.hideLoading();
                  this.setData({ loading: false });
                });
              } else {
                console.log('获取openid失败：', loginRes.data.errmsg);
                wx.showToast({
                  title: '登录失败',
                  icon: 'error'
                });
                wx.hideLoading();
                this.setData({ loading: false });
              }
            },
            fail: (err) => {
              console.error('请求失败：', err);
              wx.showToast({
                title: '登录失败',
                icon: 'error'
              });
              wx.hideLoading();
              this.setData({ loading: false });
            }
          });
        } else {
          console.log('登录失败：', res.errMsg);
          wx.showToast({
            title: '登录失败',
            icon: 'error'
          });
          wx.hideLoading();
          this.setData({ loading: false });
        }
      },
      fail: (err) => {
        console.error('wx.login调用失败：', err);
        wx.showToast({
          title: '登录失败',
          icon: 'error'
        });
        wx.hideLoading();
        this.setData({ loading: false });
      }
    });
  },

  // 获取生活指数
  getLiveIndex: function() {
    return new Promise((resolve, reject) => {
      const adcode = getApp().globalData.adcode;
      if (!adcode) {
        reject('未获取到城市编码');
        return;
      }

      wx.request({
        url: 'http://127.0.0.1:8084/weather/live_index',
        method: 'GET',
        data: { cityId: adcode },
        success: (res) => {
          if (res.data.success && res.data.code === '200') {
            const result = res.data.result;
            // console.log(result)
            if (result.code === 0) {
              // 处理生活指数数据，去掉"指数"后缀
              const liveIndex = result.liveIndex;
              if (liveIndex.listMap) {
                Object.keys(liveIndex.listMap).forEach(date => {
                  liveIndex.listMap[date] = liveIndex.listMap[date].map(item => ({
                    ...item,
                    displayName: item.name.replace('指数', '')
                  }));
                });
              }
              
              this.setData({
                liveIndex: liveIndex
              });
              // 更新穿搭建议
              this.updateDressAdvice();
              resolve(res.data);
            } else {
              reject('获取生活指数失败');
              wx.showToast({
                title: '获取生活指数失败',
                icon: 'error'
              });
            }
          }
        },
        fail: (err) => {
          console.error('请求生活指数失败：', err);
          reject(err);
          wx.showToast({
            title: '网络请求失败',
            icon: 'error'
          });
        }
      });
    });
  },

  // 显示生活指数详情
  showIndexDetail: function(e) {
    const indexData = e.currentTarget.dataset.index;
    wx.showModal({
      title: indexData.name,
      content: indexData.desc,
      showCancel: false,
      confirmText: '知道了'
    });
  },

  // 根据生活指数更新穿搭建议
  updateDressAdvice: function() {
    const listMap = this.data.liveIndex.listMap;
    const today = this.data.today;
    // console.log('当前日期:', today);
    // console.log('生活指数数据:', listMap);
    
    // 获取当前日期的生活指数列表
    const todayIndex = listMap[today];
    // console.log('今日生活指数:', todayIndex);
    
    if (todayIndex) {
      const dressIndex = todayIndex.find(item => item.name === '穿衣指数');
      const uvIndex = todayIndex.find(item => item.name === '紫外线指数');
      
      let advice = '根据当前天气状况，建议着装：\n';
      
      if (dressIndex) {
        advice += `1. 穿衣指数：${dressIndex.status}\n`;
        advice += `   ${dressIndex.desc}\n`;
      }
      
      if (uvIndex) {
        advice += `2. 防晒建议：${uvIndex.desc}\n`;
      }
      
      advice += '\n温馨提示：\n';
      advice += '1. 请根据实际天气情况适当调整着装\n';
      advice += '2. 注意查看实时天气变化\n';
      advice += '3. 特殊天气请做好防护措施';
      
      this.setData({
        'aiAdvice.content': advice
      });

      // 更新生活指数展示
      this.setData({
        'liveIndex.listMap': {
          [today]: todayIndex
        }
      });
    } else {
      console.log('未找到今日生活指数数据');
      wx.showToast({
        title: '暂无今日生活指数数据',
        icon: 'none'
      });
    }
  },

  // 联系我们
  onContact: () => {
    wx.showModal({
      title: "联系我们",
      content: "关注公众号《码农智涵的程序人生》",
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