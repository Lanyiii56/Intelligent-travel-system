// 高德地图配置
// 请到 https://lbs.amap.com/ 申请你自己的 Key

export const AMAP_CONFIG = {
  // 高德地图 Web JS API Key
  // 申请地址: https://console.amap.com/dev/key/app
  key: 'your-amap-key-here',
  
  // 安全密钥 (可选，用于更安全的调用)
  securityJsCode: '',
  
  // API 版本
  version: '2.0',
  
  // 需要加载的插件
  plugins: [
    'AMap.Driving',      // 驾车路线规划
    'AMap.Walking',      // 步行路线规划
    'AMap.Transfer',     // 公交路线规划
    'AMap.Marker',       // 标记点
    'AMap.Polyline',     // 折线
    'AMap.InfoWindow',   // 信息窗口
    'AMap.Geocoder',     // 地理编码
    'AMap.PlaceSearch',  // 地点搜索
  ]
};

// 使用说明:
// 1. 访问 https://lbs.amap.com/ 注册账号
// 2. 创建应用，获取 Web JS API Key
// 3. 将上面的 'your-amap-key-here' 替换为你的 Key
// 4. 如果需要更高安全性，可以配置 securityJsCode
