Page({
  data: {
    location: "长沙市 岳麓区",
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

  onLoad: () => {
    // 这里可以添加获取实时天气数据的API调用
    // 例如：this.getWeatherData();
  },

  onPullDownRefresh: () => {
    // 下拉刷新时获取最新天气数据
    // this.getWeatherData();
    wx.stopPullDownRefresh() // 停止下拉刷新动画
  },

  // 获取天气数据的方法
  getWeatherData: () => {
    // 实际应用中，这里会调用微信小程序的wx.request API来获取天气数据
    // 例如：
    /*
    wx.request({
      url: 'https://api.weather.com/...',
      success: (res) => {
        this.setData({
          // 更新数据
        });
      },
      complete: () => {
        wx.stopPullDownRefresh(); // 停止下拉刷新动画
      }
    });
    */
  },
})

