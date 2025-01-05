export function getEnv() {
  const {miniProgram:{envVersion}} = wx.getAccountInfoSync()
  console.log('当前环境：'+envVersion)
  switch (envVersion){
    case 'develop':
      return 'dev'
    case 'pro':
      return 'pro'
    default:
      return 'unknown'
  }
}