// components/location-compo/location-compo.js
Component({

  /**
   * 组件的属性列表
   */
  properties: {
    showAddress: {
      type: Boolean,
      value: true // 是否显示详细地址
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    locationInfo: '', // 详细地址
    latitude: '', // 纬度
    longitude: '', // 经度
    loading: false, // 加载状态
    locationAuthDenied: false // 位置权限状态
  },
  lifetimes: {
    attached: function () {
      this.checkLocationAuth();
    }
  },

  /**
   * 组件的方法列表
   */
  methods: {
    // 检查位置权限
    checkLocationAuth: function() {
      const that = this;
      wx.getSetting({
        success: (res) => {
          if (!res.authSetting['scope.userLocation']) {
            that.setData({ locationAuthDenied: true });
            that.requestLocationAuth();
          } else {
            that.getLocation();
          }
        }
      });
    },

    // 请求位置权限
    requestLocationAuth: function() {
      const that = this;
      wx.authorize({
        scope: 'scope.userLocation',
        success: () => {
          that.setData({ locationAuthDenied: false });
          that.getLocation();
        },
        fail: () => {
          that.setData({ locationAuthDenied: true });
          wx.showToast({
            title: '需要位置权限才能获取位置信息',
            icon: 'none'
          });
        }
      });
    },

    // 获取位置信息
    getLocation: function () {
      const that = this;
      this.setData({ loading: true });

      wx.getLocation({
        type: 'gcj02',
        success: function (res) {
          that.setData({
            latitude: res.latitude,
            longitude: res.longitude
          });
          
          if (that.data.showAddress) {
            that.getDetailAddress(res.latitude, res.longitude);
          }
          
          // 触发位置获取成功事件
          that.triggerEvent('locationSuccess', {
            latitude: res.latitude,
            longitude: res.longitude
          });
        },
        fail: function (err) {
          console.error('获取位置失败：', err);
          wx.showToast({
            title: '获取位置失败',
            icon: 'none'
          });
          that.triggerEvent('locationFail', err);
        },
        complete: function() {
          that.setData({ loading: false });
        }
      });
    },

    // 获取详细地址
    getDetailAddress: function (latitude, longitude) {
      const that = this;
      wx.request({
        url: `https://apis.map.qq.com/ws/geocoder/v1/?location=${latitude},${longitude}&key=YOUR_MAP_KEY&get_poi=1`,
        success: function (res) {
          if (res.data.status === 0) {
            const address = res.data.result.address;
            that.setData({
              locationInfo: address
            });
            that.triggerEvent('addressSuccess', {
              address: address,
              detail: res.data.result
            });
          }
        },
        fail: function (err) {
          console.error('获取详细地址失败：', err);
          that.triggerEvent('addressFail', err);
        }
      });
    },

    // 重新获取位置
    refreshLocation: function() {
      this.getLocation();
    }
  }
})