import { useState } from 'react';
import { View, Text } from '@tarojs/components';
import Taro, { useDidShow } from '@tarojs/taro';
import { getUsage, UsageResponse } from '../../utils/api';
import './index.scss';

export default function Mine() {
  const [usage, setUsage] = useState<UsageResponse>({
    dailyCount: 0,
    totalCount: 0,
    dailyLimit: 50,
  });

  useDidShow(() => {
    getUsage()
      .then((res) => {
        setUsage(res);
      })
      .catch(() => {
        // 静默失败，保留上次数据
      });
  });

  const handleFavorites = () => {
    Taro.navigateTo({ url: '/pages/favorites/favorites' });
  };

  const handleAbout = () => {
    Taro.showModal({
      title: '关于我们',
      content: 'AI吵架生成器 v1.0.0\n\n用幽默化解情绪，把负面情绪变成趣味内容。\n\n选择场景和风格，AI 帮你生成高情商、阴阳怪气、发疯文学等风格的精彩回怼文案。',
      showCancel: false,
      confirmText: '知道了',
    });
  };

  return (
    <View className="mine-page">
      <View className="mine-page__navbar">
        <Text className="mine-page__nav-title">我的</Text>
      </View>

      <View className="mine-page__stats">
        <View className="mine-page__stat-item">
          <Text className="mine-page__stat-num">{usage.dailyCount}</Text>
          <Text className="mine-page__stat-label">今日使用</Text>
        </View>
        <View className="mine-page__stat-divider" />
        <View className="mine-page__stat-item">
          <Text className="mine-page__stat-num">{usage.totalCount}</Text>
          <Text className="mine-page__stat-label">累计生成</Text>
        </View>
        <View className="mine-page__stat-divider" />
        <View className="mine-page__stat-item">
          <Text className="mine-page__stat-num">{usage.dailyLimit}</Text>
          <Text className="mine-page__stat-label">每日上限</Text>
        </View>
      </View>

      <View className="mine-page__menu">
        <View className="mine-page__cell" onClick={handleFavorites}>
          <Text className="mine-page__cell-left">❤️ 我的收藏</Text>
          <Text className="mine-page__cell-right">▶</Text>
        </View>
        <View className="mine-page__cell" onClick={handleAbout}>
          <Text className="mine-page__cell-left">📋 关于我们</Text>
          <Text className="mine-page__cell-right">▶</Text>
        </View>
      </View>

      <View className="mine-page__safe-bottom" />
    </View>
  );
}
