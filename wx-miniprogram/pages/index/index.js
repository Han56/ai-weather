// index.js
import * as echarts from '../../components/ec-canvas/echarts'
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
    maxTempData: '30℃', // 初始最高温度数据，后续需更新
    minTempData: '20℃',
    alert: '森林火险',
    tempData: '25℃',
    feelTempData: '23℃',
    maxTempData: '30℃',
    minTempData: '20℃',
    weatherCondition: '晴天', // 初始天气状况，后续需更新
    humidity: 50, // 初始湿度，后续需更新
    airPressure: 1013, // 初始气压，后续需更新
    aqi: '50 优', // 初始AQI，后续需更新
    windSpeed: 2, // 初始风速，后续需更新
    windPower: 2, // 初始风力，后续需更新
    windDirection: '东南风', // 初始风向，后续需更新
    sunriseTime: '06:00', // 初始日出时间，后续需更新
    sunsetTime: '18:00',
    ec: {
      onInit: initChart
    }
  }
})

