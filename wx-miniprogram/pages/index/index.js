// index.js
import * as echarts from '../../components/ec-canvas/echarts'
import config from '../../config/index.js'

function initChart(canvas, width, height) {
  const chart = echarts.init(canvas, null, {
    width: width,
    height: height
  });
  canvas.setChart(chart);

  var option = {
    tooltip: {},
    grid: {
      left: '10%', // 左边距
      right: '10%', // 右边距
      top: '20%', // 上边距
      bottom: '20%', // 下边距
      width: '80%', // 宽度
      height: '60%', // 高度
      containLabel: true // 确保标签不超出图表
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['00', '02', '04', '06', '08', '10', '12']
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: [5, 4, 4, 5, 6, 6, 7],
      type: 'line',
      areaStyle: {}
    }]
  };
  chart.setOption(option);
  return chart;
}
Page({
  data:{
    tempData: '25℃',
    feel: '较冷',
    feelTempData: '13℃',
    maxTempData: '30℃', // 初始最高温度数据
    minTempData: '20℃',
    alert: '森林火险',
    tempData: '25℃',
    feelTempData: '23℃',
    maxTempData: '30℃',
    minTempData: '20℃',
    weatherCondition: '晴天', // 初始天气状况
    humidity: 50, // 初始湿度
    airPressure: 1013, // 初始气压
    aqi: '50 优', // 初始AQI
    windSpeed: 2, // 初始风速
    windPower: 2, // 初始风力
    windDirection: '东南风', // 初始风向
    sunriseTime: '06:00', // 初始日出时间
    sunsetTime: '18:00',
    ec: {
      onInit: initChart
    },
    forecastList:[
      {
        date:'今天',
        iconUrl:'../../image/weather-icon/W0.png',
        lowTemp:4,
        highTemp:10
      },
      {
        date:'周日',
        iconUrl:'../../image/weather-icon/W0.png',
        lowTemp:2,
        highTemp:13
      },
      {
        date:'周一',
        iconUrl:'../../image/weather-icon/W0.png',
        lowTemp:2,
        highTemp:14
      },
      {
        date:'周二',
        iconUrl:'../../image/weather-icon/W0.png',
        lowTemp:2,
        highTemp:15
      },
      {
        date:'周三',
        iconUrl:'../../image/weather-icon/W0.png',
        lowTemp:4,
        highTemp:14
      },
      {
        date:'周四',
        iconUrl:'../../image/weather-icon/W0.png',
        lowTemp:6,
        highTemp:10
      },
      {
        date:'周五',
        iconUrl:'../../image/weather-icon/W0.png',
        lowTemp:4,
        highTemp:12
      },
      {
        date:'周六',
        iconUrl:'../../image/weather-icon/W0.png',
        lowTemp:3,
        highTemp:13
      },
      {
        date:'周日',
        iconUrl:'../../image/weather-icon/W0.png',
        lowTemp:2,
        highTemp:12
      },
      {
        date:'周一',
        iconUrl:'../../image/weather-icon/W0.png',
        lowTemp:6,
        highTemp:10
      }
    ]
  },
  onLoad:function(){
    this.getRealTimeWeather();
    this.getRealTimeAQI();
  },
  // 请求实时天气状况
  getRealTimeWeather:function(){
    const that = this;
    wx.request({
      url: config.BASE_URL + 'real_time_weather',
      method:'GET',
      data:{
        cityId:'2'
      },
      success(res) {
        console.log('请求成功:', res.data);
        // 请求成功-填充数据
        const realTimeWeatherData = res.data.result.realTimeMojiWeatherData.realTimeWeatherCondition
        // console.log('天气状况'+realTimeWeatherData.condition)
        // 处理日出日落时间格式，截取时间部分
      const formatTime = (timeStr) => {
        return timeStr.split(' ')[1];
      };
        that.setData({
          weatherCondition:realTimeWeatherData.condition,
          humidity:realTimeWeatherData.humidity,
          airPressure:realTimeWeatherData.pressure,
          windDirection:realTimeWeatherData.windDir,
          windSpeed:realTimeWeatherData.windSpeed,
          feelTempData:realTimeWeatherData.realFeel,
          sunriseTime:formatTime(realTimeWeatherData.sunRise),
          sunsetTime:formatTime(realTimeWeatherData.sunSet)
        });
      },
      fail(err) {
        wx.showToast({
          title: '实时天气状况请求失败',
          duration:1000
        })
        console.log(err)
      }
    })
  },
  // 请求实时空气质量
  getRealTimeAQI:function(){
    const that = this;
    wx.request({
      url: config.BASE_URL + 'aqi_real_time',
      method:'GET',
      data:{
        cityId:'2'
      },
      success(res) {
        console.log('请求成功:', res.data);
        // 请求成功-填充数据
        const realTimeAQIData = res.data.result.aqiRealTimeMojiData.aqiRealTime
        console.log('AQI值 '+realTimeAQIData.value)
        // 处理AQI空气质量分级方法
      const formatAQIRes = (aqiStr) => {
        // 转化为float类型
        const aqiValue = parseFloat(aqiStr)
        if(!isNaN(aqiValue)){
          if (aqiValue >= 0 && aqiValue <= 50) {
            return aqiStr+' 优';
          } else if (aqiValue > 50 && aqiValue <= 100) {
            return aqiStr+' 良';
          } else if (aqiValue > 100 && aqiValue <= 150) {
            return aqiStr+' 轻度污染';
          } else if (aqiValue > 150 && aqiValue <= 200) {
            return aqiStr+' 中度污染';
          } else if (aqiValue > 200 && aqiValue <= 300) {
            return aqiStr+' 重度污染';
          } else if (aqiValue > 300) {
            return aqiStr+' 严重污染';
          }
        }
        return aqiStr;
      };
        that.setData({
          aqi:formatAQIRes(realTimeAQIData.value)
        });
      },
      fail(err) {
        wx.showToast({
          title: '实时AQI请求失败',
          duration:1000
        })
        console.log(err)
      }
    })
  }
})

