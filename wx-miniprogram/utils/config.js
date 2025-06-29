// utils/config.js
const ENV = 'dev'; // 'dev'、'pre' 或 'prod'

const config = {
  dev: {
    baseUrl: 'http://127.0.0.1:8084'
  },
  pre: {
    baseUrl: 'http://39.104.14.216:8084'
  },
  prod: {
    baseUrl: 'https://www.aiweather.top'
  }
};

module.exports = {
  baseUrl: config[ENV].baseUrl,
  ENV
}; 