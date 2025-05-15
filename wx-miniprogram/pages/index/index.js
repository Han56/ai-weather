Page({
  data: {
    location: "哈尔滨市",
    currentTemp: "25°C",
    tempDescription: "较冷",
    feelsLike: "23°C",
    warning: "森林火险",
    weatherDetails: {
      condition: "晴天",
      humidity: "50%",
      pressure: "1013hPa",
      aqi: "50 优",
      windSpeed: "2m/s",
      windForce: "2级",
      windDirection: "东南风",
      sunrise: "06:00",
      sunset: "18:00",
    },
    selectorVisible: true,
    selectedProvince: null,
    selectedCity: null,
    forecast: [
      {
        day: "今天",
        icon: "cloudy",
        lowTemp: "6°",
        highTemp: "15°",
        rangePosition: 20,
        rangeWidth: 40,
        rainChance: null,
        color: "default",
      },
      {
        day: "周日",
        icon: "cloudy",
        lowTemp: "9°",
        highTemp: "11°",
        rangePosition: 45,
        rangeWidth: 10,
        rainChance: null,
        color: "default",
      },
      {
        day: "周一",
        icon: "rainy",
        lowTemp: "9°",
        highTemp: "15°",
        rangePosition: 45,
        rangeWidth: 30,
        rainChance: "80%",
        color: "default",
      },
      {
        day: "周二",
        icon: "partly-cloudy",
        lowTemp: "12°",
        highTemp: "23°",
        rangePosition: 25,
        rangeWidth: 55,
        rainChance: null,
        color: "yellow",
      },
      {
        day: "周三",
        icon: "thunderstorm",
        lowTemp: "15°",
        highTemp: "19°",
        rangePosition: 60,
        rangeWidth: 20,
        rainChance: "85%",
        color: "yellow",
      },
      {
        day: "周四",
        icon: "rainy",
        lowTemp: "12°",
        highTemp: "16°",
        rangePosition: 50,
        rangeWidth: 20,
        rainChance: "75%",
        color: "default",
      },
      {
        day: "周五",
        icon: "thunderstorm",
        lowTemp: "11°",
        highTemp: "15°",
        rangePosition: 45,
        rangeWidth: 20,
        rainChance: "75%",
        color: "default",
      },
      {
        day: "周六",
        icon: "rainy",
        lowTemp: "7°",
        highTemp: "12°",
        rangePosition: 30,
        rangeWidth: 25,
        rainChance: "60%",
        color: "default",
      },
      {
        day: "周日",
        icon: "cloudy",
        lowTemp: "7°",
        highTemp: "15°",
        rangePosition: 30,
        rangeWidth: 40,
        rainChance: null,
        color: "default",
      },
      {
        day: "周一",
        icon: "cloudy",
        lowTemp: "5°",
        highTemp: "15°",
        rangePosition: 20,
        rangeWidth: 50,
        rainChance: null,
        color: "default",
      },
    ],
  },

  // 显示城市选择器
  showSelector() {
    console.log('打开城市选择器')
    this.setData({
      selectorVisible: true
    });
  },

  // 关闭城市选择器
  onSelectorClose() {
    this.setData({
      selectorVisible: false
    });
  },

  // 当用户选择了城市之后的回调函数
  onSelectCity(e) {
    const { province, city } = e.detail;
    console.log(e.detail)
    // 更新全局变量
    getApp().globalData.cityId = city.id;
    
    // 更新页面显示
    this.setData({
      location: city.name,
      selectorVisible: false
    });

    // 重新获取天气数据
    this.getRealTimeWeather();
  },

  onLoad: function() {
    // 页面加载时获取天气数据
    this.getRealTimeWeather();
  },

  onPullDownRefresh: function() {
    // 下拉刷新时获取最新天气数据
    this.getRealTimeWeather();
  },

  // 获取实时天气数据
  getRealTimeWeather: function() {
    const cityId = getApp().globalData.cityId;
    console.log(cityId)
    wx.showLoading({
      title: '加载中...',
    });

    wx.request({
      url: 'http://127.0.0.1:8084/weather/real_time_weather',
      method: 'GET',
      data: {
        cityId: '2'
      },
      success: (res) => {
        console.log(res.data)
        if (res.data && res.data.code === 200) {
          const weatherData = res.data.data;
          this.setData({
            currentTemp: weatherData.temperature + '°C',
            tempDescription: weatherData.weatherDesc,
            feelsLike: weatherData.feelsLike + '°C',
            warning: weatherData.warning || '无预警',
            weatherDetails: {
              condition: weatherData.weatherDesc,
              humidity: weatherData.humidity + '%',
              pressure: weatherData.pressure + 'hPa',
              aqi: weatherData.aqi + ' ' + weatherData.aqiLevel,
              windSpeed: weatherData.windSpeed + 'm/s',
              windForce: weatherData.windForce + '级',
              windDirection: weatherData.windDirection,
              sunrise: weatherData.sunrise,
              sunset: weatherData.sunset
            }
          });
        } else {
          wx.showToast({
            title: '获取天气数据失败',
            icon: 'none'
          });
        }
      },
      fail: (err) => {
        console.error('获取天气数据失败：', err);
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
      },
      complete: () => {
        wx.hideLoading();
        wx.stopPullDownRefresh();
      }
    });
  },

  // 获取天气数据的方法
  getWeatherData: () => {

    // wx.request({
    //   url: 'http://127.0.0.1:8084/weather/forecast_15days_weather',
    //   success: (res) => {
    //     this.setData({
          
    //     });
    //   },
    //   complete: () => {
    //     wx.stopPullDownRefresh(); // 停止下拉刷新动画
    //   }
    // });
  },
})

