export default defineAppConfig({
  pages: [
    'pages/index/index',
    'pages/history/history',
    'pages/mine/mine',
    'pages/result/result',
    'pages/favorites/favorites',
  ],
  window: {
    backgroundTextStyle: 'light',
    navigationBarBackgroundColor: '#FF6B6B',
    navigationBarTitleText: 'AI吵架生成器',
    navigationBarTextStyle: 'white',
  },
  tabBar: {
    color: '#999999',
    selectedColor: '#FF6B6B',
    backgroundColor: '#FFFFFF',
    borderStyle: 'white',
    list: [
      {
        pagePath: 'pages/index/index',
        text: '首页',
        iconPath: 'assets/icons/home.png',
        selectedIconPath: 'assets/icons/home-active.png',
      },
      {
        pagePath: 'pages/history/history',
        text: '历史',
        iconPath: 'assets/icons/history.png',
        selectedIconPath: 'assets/icons/history-active.png',
      },
      {
        pagePath: 'pages/mine/mine',
        text: '我的',
        iconPath: 'assets/icons/mine.png',
        selectedIconPath: 'assets/icons/mine-active.png',
      },
    ],
  },
});
