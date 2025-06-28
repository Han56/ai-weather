// utils/config.js
const ENV = 'prod'; // 'dev' 或 'prod'

const config = {
  dev: {
    baseUrl: 'http://127.0.0.1:8084'
  },
  prod: {
    baseUrl: 'https://www.aiweather.top'
  }
};

module.exports = {
  baseUrl: config[ENV].baseUrl,
  ENV
}; 