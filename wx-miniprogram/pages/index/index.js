const citySelector = requirePlugin('citySelector');
Page({
  data: {
    location: "哈尔滨市",
    currentTemp: "25°C",
    tempDescription: "较冷",
    feelsLike: "23°C",
    warning: "无",
    adcode: '',
    aqi: "50 优",
    loading: true,
    weatherDetails: {
      condition: "晴天",
      humidity: "50%",
      pressure: "1013hPa",
      windSpeed: "2m/s",
      windForce: "2级",
      windDirection: "东南风",
      sunrise: "06:00",
      sunset: "19:30",
      uvi: "1 弱"
    },
    selectedCity: '定位中...',
    isLocating: false,
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

  // 打开城市选择器插件
  openCitySelector() {
    const key = 'MAHBZ-QQZLW-4KKRI-RIUZG-F5YEF-OFFH3';  //为腾讯位置服务key
    const referer = 'AI看天气';     // 必须填写应用名称
    const hotCitys = '北京,上海,广州,深圳,哈尔滨'; // 自定义热门城市（最多12个）

    console.log('url:'+`plugin://citySelector/index?key=${key}&referer=${referer}&hotCitys=${hotCitys}`)

    wx.navigateTo({
      url: `plugin://citySelector/index?key=${key}&referer=${referer}&hotCitys=${hotCitys}`
    });
  },

  // 获取选择的城市信息
  onShow() {
    const cityInfo = citySelector.getCity();
    if (cityInfo) {
      this.setData({
        selectedCity: cityInfo.fullname || cityInfo.name,
        location: cityInfo.fullname || cityInfo.name
      });
      console.log('城市详细信息:', {
        adcode: cityInfo.id,
        pinyin: cityInfo.pinyin,
        location: cityInfo.location
      });
      this.getRealTimeWeather(cityInfo.id);
    }
  },

  onLoad: function() {
    this.handleLocate();
  },

  showAuthModal() {
    wx.showModal({
      title: '需要位置权限',
      content: '请前往设置开启定位权限',
      success: (res) => {
        if (res.confirm) wx.openSetting()
      }
    })
  },
  // 清空插件缓存
  onUnload() {
    citySelector.clearCity();
  },

  onPullDownRefresh: function() {
    // 下拉刷新时获取最新天气数据
    this.getRealTimeWeather();
  },

  // 触发定位操作
  handleLocate() {
    if (this.data.isLocating) return;
    this.setData({ isLocating: true });
    
    // 检查定位权限
    wx.getSetting({
      success: (res) => {
        if (res.authSetting['scope.userLocation']) {
          // 已授权，直接获取位置
          this._getLocation()
            .then(coords => this._reverseGeocode(coords))
            .then(({ city, adcode }) => {
              this._handleSuccess(city);
              return this._loadAllWeatherData(adcode);
            })
            .catch(err => {
              console.error('定位失败:', err);
              this._handleError(err);
              // 定位失败时使用默认城市
              this.setData({
                selectedCity: '哈尔滨市',
                location: '哈尔滨市'
              });
              // 使用默认城市的adcode
              this._loadAllWeatherData('230100');
            })
            .finally(() => this.setData({ isLocating: false }));
        } else {
          // 未授权，显示授权弹窗
          wx.authorize({
            scope: 'scope.userLocation',
            success: () => {
              // 用户同意授权，重新获取位置
              this._getLocation()
                .then(coords => this._reverseGeocode(coords))
                .then(({ city, adcode }) => {
                  this._handleSuccess(city);
                  return this._loadAllWeatherData(adcode);
                })
                .catch(err => {
                  console.error('授权后定位失败:', err);
                  this._handleError(err);
                  // 定位失败时使用默认城市
                  this.setData({
                    selectedCity: '哈尔滨市',
                    location: '哈尔滨市'
                  });
                  this._loadAllWeatherData('230100');
                })
                .finally(() => this.setData({ isLocating: false }));
            },
            fail: () => {
              // 用户拒绝授权，使用默认城市
              console.log('用户拒绝授权');
              this.setData({
                selectedCity: '哈尔滨市',
                location: '哈尔滨市',
                isLocating: false
              });
              this._loadAllWeatherData('230100');
            }
          });
        }
      },
      fail: (err) => {
        console.error('获取权限设置失败：', err);
        this._handleError('获取权限设置失败');
        // 权限检查失败时使用默认城市
        this.setData({
          selectedCity: '哈尔滨市',
          location: '哈尔滨市',
          isLocating: false
        });
        this._loadAllWeatherData('230100');
      }
    });
  },

  // 获取设备地理位置
  _getLocation() {
    return new Promise((resolve, reject) => {
      wx.getLocation({
        type: 'gcj02',
        altitude: true,
        success: res => {
          console.log('获取位置成功:', res);
          resolve({
            lat: res.latitude,
            lng: res.longitude
          });
        },
        fail: err => {
          console.error('获取位置失败:', err);
          if (err.errMsg.includes('auth deny')) {
            this._showAuthGuide();
          }
          reject('定位失败：' + err.errMsg);
        }
      });
    });
  },

  // 处理成功结果
  _handleSuccess(city) {
    const pureCity = city.replace(/ $/, ''); 
    this.setData({ selectedCity: pureCity });
  },

  // 异常处理
  _handleError(err) {
    console.error(err);
    wx.showToast({
      title: '定位失败，尝试IP定位...',
      icon: 'none',
      duration: 2000
    });
  },

  // 逆地理编码（腾讯地图API）
  _reverseGeocode({ lat, lng }) {
    return new Promise((resolve, reject) => {
      wx.request({
        url: 'https://apis.map.qq.com/ws/geocoder/v1/',
        data: {
          location: `${lat},${lng}`,
          key: 'MAHBZ-QQZLW-4KKRI-RIUZG-F5YEF-OFFH3',
          get_poi: 0
        },
        success: res => {
          if (res.data.status === 0) {
            console.log('逆地理编码结果:', res.data.result);
            const adcode = res.data.result.ad_info.adcode;
            console.log('区划代码:', adcode);
            // 更新全局 adcode
            getApp().globalData.adcode = adcode;
            // 返回包含城市名和adcode的对象
            resolve({
              city: res.data.result.address_component.city,
              adcode: adcode
            });
          } else {
            reject('逆解析失败：' + res.data.message);
          }
        },
        fail: err => reject('网络请求失败：' + err.errMsg)
      });
    });
  },

  // 加载所有天气数据
  _loadAllWeatherData(adcode) {
    if (!adcode) {
      console.error('缺少城市编码');
      return Promise.reject('缺少城市编码');
    }

    wx.showLoading({
      title: '加载中...',
      duration: 5000
    });

    // 并行发起所有请求
    return Promise.all([
      this._requestWithRetry(() => this.getRealTimeWeather(adcode)),
      this._requestWithRetry(() => this.getRealTimeAqi(adcode)),
      this._requestWithRetry(() => this.getRealTimeWeatherAlert(adcode)),
      this._requestWithRetry(() => this.getForecast10DaysWeather(adcode))
    ]).catch(err => {
      console.error('加载天气数据失败：', err);
      wx.showToast({
        title: '部分数据加载失败',
        icon: 'none',
        duration: 2000
      });
    }).finally(() => {
      wx.hideLoading();
      wx.stopPullDownRefresh();
      this.setData({ loading: false });
    });
  },

  // 请求重试包装器
  _requestWithRetry(requestFn, maxRetries = 2) {
    return new Promise((resolve, reject) => {
      let retryCount = 0;

      const attempt = () => {
        requestFn()
          .then(resolve)
          .catch(err => {
            if (retryCount < maxRetries) {
              retryCount++;
              console.log(`请求失败，第${retryCount}次重试`);
              setTimeout(attempt, 1000); // 延迟1秒后重试
            } else {
              reject(err);
            }
          });
      };

      attempt();
    });
  },

  // 显示授权引导
  _showAuthGuide() {
    wx.showModal({
      title: '需要位置权限',
      content: '请授权位置权限以获取准确的天气信息',
      confirmText: '去授权',
      success: (res) => {
        if (res.confirm) {
          wx.openSetting({
            success: (settingRes) => {
              if (settingRes.authSetting['scope.userLocation']) {
                this.handleLocate();
              }
            }
          });
        }
      }
    });
  },

  // 修改现有的请求方法，返回Promise
  getRealTimeWeather: function(adcode) {
    return new Promise((resolve, reject) => {
      if (!adcode) {
        reject('未传入城市编码');
        return;
      }

      wx.request({
        url: `http://127.0.0.1:8084/weather/real_time_weather`,
        method: 'GET',
        data: { cityId: adcode },
        success: (res) => {
          if (res.data && res.data.code === '200') {
            // 原有的数据处理逻辑
            const weatherData = res.data.result.realTimeMojiWeatherData.realTimeWeatherCondition;
            this.setData({
              currentTemp: weatherData.temp + '°C',
              tempDescription: weatherData.tips || '暂无描述',
              feelsLike: weatherData.realFeel + '°C',
              weatherDetails: {
                condition: weatherData.condition || '未知',
                humidity: (weatherData.humidity || '0') + '% ' + this._getHumidityLevel(weatherData.humidity),
                pressure: (weatherData.pressure || '0') + 'hPa\n' + this._getPressureLevel(weatherData.pressure),
                uvi: weatherData.uvi + ' ' + this._getUviLevel(weatherData.uvi),
                windSpeed: (weatherData.windSpeed || '0') + 'm/s\n' + this._getWindSpeedLevel(weatherData.windSpeed),
                windForce: weatherData.windForce || '1级',
                windDirection: weatherData.windDir || '未知',
                sunrise: this._formatTime(weatherData.sunRise) || '06:00',
                sunset: this._formatTime(weatherData.sunSet) || '19:30'
              }
            });
            resolve(res.data);
          } else {
            reject('获取天气数据失败');
          }
        },
        fail: reject
      });
    });
  },

  // 同样修改其他请求方法为Promise形式
  getRealTimeAqi: function(adcode) {
    return new Promise((resolve, reject) => {
      if (!adcode) {
        reject('未传入城市编码');
        return;
      }

      wx.request({
        url: `http://127.0.0.1:8084/weather/aqi_real_time`,
        method: 'GET',
        data: { cityId: adcode },
        success: (res) => {
          if (res.data && res.data.code === '200') {
            const aqiData = res.data.result.aqiRealTimeMojiData.aqiRealTime;
            console.log('aqi实时数据:', aqiData.value);
            
            // 获取 AQI 等级
            const aqiLevel = this._getAqiLevel(aqiData.value);
            const aqiText = `${aqiData.value} ${aqiLevel}`;
            console.log('处理后的 AQI 文本:', aqiText);

            // 更新 AQI 数据
            this.setData({
              aqi: aqiText
            }, () => {
              console.log('AQI 数据更新后的 weatherDetails:', this.data.weatherDetails);
              resolve(res.data);
            });
          } else {
            reject('获取AQI数据失败');
          }
        },
        fail: reject
      });
    });
  },

  getRealTimeWeatherAlert: function(adcode) {
    return new Promise((resolve, reject) => {
      if (!adcode) {
        reject('未传入城市编码');
        return;
      }

      wx.request({
        url: `http://127.0.0.1:8084/weather/weather_alert`,
        method: 'GET',
        data: { cityId: adcode },
        success: (res) => {
          if (res.data && res.data.code === '200') {
            const alertData = res.data.result.weatherMojiAlertData.weatherAlerts;
            let warningText = '无';
            if (alertData && alertData.length > 0) {
              warningText = alertData.map(alert => alert.type).join('\n');
            }
            this.setData({ warning: warningText });
            resolve(res.data);
          } else {
            reject('获取预警信息失败');
          }
        },
        fail: reject
      });
    });
  },
  
  //获取十天天气预报
  getForecast10DaysWeather: function(adcode) {
    return new Promise((resolve, reject) => {
      if (!adcode) {
        reject('未传入城市编码');
        return;
      }

      wx.request({
        url: `http://127.0.0.1:8084/weather/forecast_15days_weather`,
        method: 'GET',
        data: { cityId: adcode },
        success: (res) => {
          if (res.data && res.data.code === '200') {
            const forecastData = res.data.result.forecast15DaysMojiData.forecast15DaysList;
            
            // 获取从第二个元素开始的10条数据
            const tenDaysForecast = forecastData.slice(1, 11).map(item => {
              // 处理日期格式
              const date = new Date(item.predictDate);
              const month = String(date.getMonth() + 1).padStart(2, '0');
              const day = String(date.getDate()).padStart(2, '0');
              
              // 判断是否是今天
              const today = new Date();
              const isToday = date.getDate() === today.getDate() && 
                            date.getMonth() === today.getMonth() && 
                            date.getFullYear() === today.getFullYear();
              
              // 获取星期几
              const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
              const weekDay = weekDays[date.getDay()];
              
              return {
                day: isToday ? '今天' : weekDay,
                date: `${month}/${day}`,
                icon: this._getWeatherIcon(item.conditionDay),
                condition: item.conditionDay,
                lowTemp: item.tempNight + '°',
                highTemp: item.tempDay + '°',
                windDir: item.windDirDay || '无风',
                windLevel: item.windLevelDay || '0',
                rainChance: null
              };
            });

            console.log('处理后的10日预报数据:', tenDaysForecast);

            // 更新预报数据
            this.setData({
              forecast: tenDaysForecast
            }, () => {
              console.log('10日天气预报数据已更新:', this.data.forecast);
              resolve(res.data);
            });
          } else {
            reject('获取10日天气预报信息失败');
          }
        },
        fail: reject
      });
    });
  },

  // 根据天气状况获取对应的图标
  _getWeatherIcon: function(condition) {
    const iconMap = {
      '晴': 'sunny',
      '多云': 'partly-cloudy',
      '阴': 'cloudy',
      '小雨': 'light-rain',
      '中雨': 'moderate-rain',
      '大雨': 'heavy-rain',
      '暴雨': 'storm-rain',
      '雷阵雨': 'thunderstorm',
      '小雪': 'light-snow',
      '中雪': 'moderate-snow',
      '大雪': 'heavy-snow',
      '雾': 'fog',
      '霾': 'haze'
    };
    
    return iconMap[condition] || 'cloudy';
  },

  // 辅助方法
  _getUviLevel: function(uvi) {
    const uviValue = parseFloat(uvi);
    if (isNaN(uviValue)) return '未知';
    if (uviValue >= 0 && uviValue <= 2) return '弱';
    if (uviValue >= 3 && uviValue <= 5) return '中等';
    if (uviValue >= 6 && uviValue <= 7) return '高';
    if (uviValue >= 8 && uviValue <= 10) return '很高';
    if (uviValue >= 11) return '极高';
    return '未知';
  },

  _getAqiLevel: function(aqi) {
    const aqiValue = parseFloat(aqi);
    if (isNaN(aqiValue)) return '未知';
    if (aqiValue >= 0 && aqiValue <= 50) return '优';
    if (aqiValue >= 51 && aqiValue <= 100) return '良';
    if (aqiValue >= 101 && aqiValue <= 150) return '轻度污染';
    if (aqiValue >= 151 && aqiValue <= 200) return '中度污染';
    if (aqiValue >= 201 && aqiValue <= 300) return '重度污染';
    return '优';
  },

  _getWindSpeedLevel: function(windSpeed){
    const windSpeedVal = parseFloat(windSpeed);
    if (isNaN(windSpeedVal)) return '';
    if (windSpeedVal >= 0 && windSpeedVal <= 0.2) return '无风';
    if (windSpeedVal >= 0.3 && windSpeedVal <= 1.5) return '微风';
    if (windSpeedVal >= 1.6 && windSpeedVal <= 3.3) return '清风';
    if (windSpeedVal >= 3.4 && windSpeedVal <= 5.4) return '舒适';
    if (windSpeedVal >= 5.5 && windSpeedVal <= 7.9) return '和风';
    if (windSpeedVal >= 8.0 && windSpeedVal <= 10.7) return '劲风';
    if (windSpeedVal >= 10.8 && windSpeedVal <= 13.8) return '强风';
    if (windSpeedVal >= 13.9 && windSpeedVal <= 17.1) return '疾风';
    if (windSpeedVal >= 17.2 && windSpeedVal <= 20.7) return '狂风';
    if (windSpeedVal >= 20.8) return '暴风';
    return '微风';
  },

  _getPressureLevel: function(pressure){
    const pressureVal = parseFloat(pressure);
    if (isNaN(pressureVal)) return '';
    if (pressureVal >= 1000 && pressureVal <= 1020) return '舒适';
    if (pressureVal >= 980 && pressureVal <= 1000) return '轻微不适';
    if (pressureVal >= 970 && pressureVal <= 980) return '头晕';
    if (pressureVal >= 960 && pressureVal <= 970) return '头疼';
    if (pressureVal >= 1020 && pressureVal <= 1030) return '呼吸畅快';
    if (pressureVal <= 960) return '极度不适';
    if (pressureVal >= 1030) return '呼吸困难';
    return '';
  },

  _getHumidityLevel: function(humidity){
    const humidityVal = parseFloat(humidity);
    if (isNaN(humidityVal)) return '';
    if (humidityVal >= 40 && humidityVal <= 60) return '舒适';
    if (humidityVal >= 60 && humidityVal <= 90) return '潮湿';
    if (humidityVal <= 40) return '干燥';
    if (humidityVal >= 90) return '闷热';
    return '';
  },

  _formatTime: function(timeStr) {
    if (!timeStr) return '00:00';
    const timeMatch = timeStr.match(/(\d{2}:\d{2})/);
    return timeMatch ? timeMatch[1] : '00:00';
  },
})

