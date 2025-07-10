const { baseUrl } = require('../../utils/config');

Page({
  data: {
    showRecommendation: false,
    generating: false,
    outfitImageUrl: '',
    recommendationText: '根据当前天气状况，建议着装偏向保暖舒适。',
    recommendationTags: [],

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
    today: (() => {
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    })(),
    loading: true
  },

  onLoad: function() {
    wx.showLoading({
      title: '加载中...',
      mask: true
    });
    
    const appid = 'wx3e463a85be4880fc';
    const secret = '412e616cfaa20f7612aac9012776062b';
    
    wx.login({
      success: (res) => {
        if (res.code) {
          console.log('临时登录凭证：', res.code);
          
          wx.request({
            url: `${baseUrl}/weixin/getOpenId`,
            method: 'GET',
            data: {
              js_code: res.code
            },
            success: (loginRes) => {
              if (loginRes.data && loginRes.data.success && loginRes.data.openid) {
                console.log('用户openid：', loginRes.data.openid);
                // 存储到全局变量
                const app = getApp()
                app.globalData.openId = loginRes.data.openid
                this.setData({
                  isLoggedIn: true,
                  openid: loginRes.data.openid
                });
                wx.setStorage({
                  key: 'openid',
                  data: loginRes.data.openid
                });
                // 获取生活指数数据
                this.getLiveIndex().then((data) => {
                  // console.log('生活指数获取成功：', data);
                  // console.log('当前today值：', this.data.today);
                  // console.log('当前liveIndex数据：', this.data.liveIndex);
                }).catch((error) => {
                  console.error('生活指数获取失败：', error);
                }).finally(() => {
                  wx.hideLoading();
                  this.setData({ loading: false });
                });
              } else {
                console.log('获取openid失败：', loginRes.data);
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

  // 页面显示时检查是否需要重置推荐状态
  onShow: function() {
    const app = getApp();
    // 检查是否有画像更新标记
    if (app.globalData.portraitUpdated) {
      // 重置推荐相关状态，回到初始状态
      this.setData({
        showRecommendation: false,
        generating: false,
        outfitImageUrl: '',
        recommendationText: '根据当前天气状况，建议着装偏向保暖舒适。',
        recommendationTags: []
      });
      // 清除更新标记
      app.globalData.portraitUpdated = false;
    }
  },

  // 生成今日穿衣推荐
  generateRecommendation: function() {
    this.setData({ generating: true });
    wx.showLoading({
      title: '检查用户画像...',
      mask: true,
    });

    // 首先检查用户是否已设置画像
    const openId = this.data.openid;
    if (!openId) {
      wx.hideLoading();
      this.setData({ generating: false });
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }

    // 请求用户画像设置信息接口来检查用户画像是否已设置
    wx.request({
      url: `${baseUrl}/api/potraitSettingInfo/${openId}`,
      method: 'GET',
      success: (res) => {
        if (res.data && res.statusCode === 200) {
          // 用户已设置画像，继续生成推荐
          this._generateRecommendationContent();
        } else {
          // 用户未设置画像，跳转到设置页面
          wx.hideLoading();
          this.setData({ generating: false });
          wx.showModal({
            title: '提示',
            content: '请先完善您的用户画像，以获得更精准的AI穿衣推荐',
            confirmText: '去设置',
            cancelText: '取消',
            success: (modalRes) => {
              if (modalRes.confirm) {
                wx.navigateTo({
                  url: "/pages/portrait-settings/portrait-settings",
                });
              }
            }
          });
        }
      },
      fail: (err) => {
        console.error('检查用户画像失败：', err);
        wx.hideLoading();
        this.setData({ generating: false });
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
      }
    });
  },

  // 生成推荐内容的具体逻辑
  _generateRecommendationContent: function() {
    wx.showLoading({
      title: 'AI正在生成...',
      mask: true,
    });

    const cityId = getApp().globalData.adcode;
    const openId = this.data.openid;

    if (!cityId) {
      wx.hideLoading();
      this.setData({ generating: false });
      wx.showToast({
        title: '请先在首页选择城市',
        icon: 'none'
      });
      return;
    }

    // 第一次请求获取基础推荐信息
    wx.request({
      url: `${baseUrl}/weather/ai_recommends`,
      method: 'GET',
      data: {
        cityId: cityId,
        openId: openId
      },
      success: (res) => {
        wx.hideLoading();
        if (res.data.success && res.data.code === '200' && res.data.result) {
          const result = res.data.result;
          const clothingInfo = result.clothingInfo || {};
          
          const tags = [
            clothingInfo.top,
            clothingInfo.bottom,
            clothingInfo.shoes,
          ].filter(tag => !!tag);

          this.setData({
            outfitImageUrl: result.imgUrl || '',
            recommendationText: result.detailedRecommendation.content,
            recommendationTags: tags,
            showRecommendation: true,
            generating: false,
          });

          // 如果第一次请求没有图片，开始轮询检查图片
          if (!result.imgUrl) {
            this._pollForImage(cityId, openId);
          }
        } else {
          this.setData({ generating: false });
          wx.showToast({
            title: res.data.message || 'AI推荐生成失败',
            icon: 'none',
            duration: 2000
          });
        }
      },
      fail: (err) => {
        wx.hideLoading();
        this.setData({ generating: false });
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
        console.error('Failed to generate recommendation:', err);
      }
    });
  },

  // 轮询检查图片是否生成完成
  _pollForImage: function(cityId, openId) {
    let pollCount = 0;
    const maxPolls = 10; // 最大轮询次数，约20秒
    const pollInterval = 2000; // 轮询间隔2秒

    const poll = () => {
      pollCount++;
      
      wx.request({
        url: `${baseUrl}/weather/ai_recommends`,
        method: 'GET',
        data: {
          cityId: cityId,
          openId: openId
        },
        success: (res) => {
          if (res.data.success && res.data.code === '200' && res.data.result) {
            const result = res.data.result;
            
            // 如果获取到图片，更新界面并停止轮询
            if (result.imgUrl) {
              this.setData({
                outfitImageUrl: result.imgUrl
              });
              return;
            }
          }
          
          // 如果还没获取到图片且未超过最大轮询次数，继续轮询
          if (pollCount < maxPolls) {
            setTimeout(poll, pollInterval);
          }
        },
        fail: (err) => {
          console.error('轮询图片失败：', err);
          // 即使失败也继续轮询，除非超过最大次数
          if (pollCount < maxPolls) {
            setTimeout(poll, pollInterval);
          }
        }
      });
    };

    // 开始轮询
    setTimeout(poll, pollInterval);
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
        url: `${baseUrl}/weather/live_index`,
        method: 'GET',
        data: { cityId: adcode },
        success: (res) => {
          // console.log('生活指数接口返回：', res.data);
          if (res.data.success && res.data.code === '200') {
            const result = res.data.result;
            if (result.code === 0) {
              // 处理生活指数数据，去掉"指数"后缀
              const liveIndex = result.liveIndex;
              if (liveIndex && liveIndex.listMap) {
                Object.keys(liveIndex.listMap).forEach(date => {
                  liveIndex.listMap[date] = liveIndex.listMap[date].map(item => ({
                    ...item,
                    displayName: item.name.replace('指数', '')
                  }));
                });
              }
              
              // console.log('处理后的生活指数数据：', liveIndex);
              this.setData({
                liveIndex: liveIndex
              });
              resolve(res.data);
            } else {
              // console.error('生活指数接口返回错误：', result);
              reject('获取生活指数失败');
              wx.showToast({
                title: '获取生活指数失败',
                icon: 'error'
              });
            }
          } else {
            console.error('生活指数接口请求失败：', res.data);
            reject('获取生活指数失败');
            wx.showToast({
              title: '获取生活指数失败',
              icon: 'error'
            });
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

  // 根据生活指数更新穿搭建议 (此功能现在由 generateRecommendation 处理，暂时注释或移除)
  /*
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
  */

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

  // 右上角转发
  onShareAppMessage: function () {
    return {
      title: 'AI智能穿搭推荐',
      path: '/pages/aiDress/aiDress', // 跳转到当前页面
      imageUrl: '' // 可选，分享卡片图片
    }
  },

  // 分享到朋友圈
  onShareTimeline: function () {
    return {
      title: 'AI智能穿搭推荐',
      query: '', // 可选，分享参数
      imageUrl: '' // 可选，分享卡片图片
    }
  },
})