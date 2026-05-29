import { useState, useEffect } from 'react';
import { View, Text, ScrollView } from '@tarojs/components';
import Taro, { useDidShow } from '@tarojs/taro';
import EmptyState from '../../components/EmptyState';
import LoadingSkeleton from '../../components/LoadingSkeleton';
import { PAGE_SIZE, DEBOUNCE_FAVORITE } from '../../utils/constants';
import { getFavorites, toggleFavorite, HistoryItem as HistoryItemType } from '../../utils/api';
import './index.scss';

export default function Favorites() {
  const [list, setList] = useState<HistoryItemType[]>([]);
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [favoriteLock, setFavoriteLock] = useState(false);

  const fetchList = (pageNum: number, isRefresh: boolean) => {
    if (isRefresh) {
      setLoading(true);
    } else {
      setLoadingMore(true);
    }

    getFavorites(pageNum, PAGE_SIZE)
      .then((res) => {
        if (isRefresh) {
          setList(res.list);
        } else {
          setList((prev) => [...prev, ...res.list]);
        }
        setTotal(res.total);
        setPage(pageNum);
      })
      .catch(() => {
        Taro.showToast({ title: '加载失败，请下拉刷新', icon: 'none' });
      })
      .finally(() => {
        setLoading(false);
        setLoadingMore(false);
      });
  };

  useDidShow(() => {
    fetchList(1, true);
  });

  useEffect(() => {
    fetchList(1, true);
  }, []);

  const handleRefresh = () => {
    fetchList(1, true);
  };

  const handleLoadMore = () => {
    if (loadingMore || page * PAGE_SIZE >= total) return;
    fetchList(page + 1, false);
  };

  const handleClick = (id: string) => {
    Taro.navigateTo({ url: `/pages/result/result?id=${id}` });
  };

  const handleUnfavorite = (id: string) => {
    if (favoriteLock) return;
    setFavoriteLock(true);
    toggleFavorite(id)
      .then(() => {
        setList((prev) => prev.filter((item) => item.id !== id));
        setTotal((prev) => prev - 1);
        Taro.showToast({ title: '已取消收藏', icon: 'success' });
      })
      .catch(() => {
        Taro.showToast({ title: '操作失败，请重试', icon: 'error' });
      })
      .finally(() => {
        setTimeout(() => setFavoriteLock(false), DEBOUNCE_FAVORITE);
      });
  };

  const handleBack = () => {
    Taro.navigateBack();
  };

  const hasMore = page * PAGE_SIZE < total;

  if (loading) {
    return (
      <View className="favorites-page">
        <View className="favorites-page__navbar">
          <Text className="favorites-page__back" onClick={handleBack}>← 返回</Text>
          <Text className="favorites-page__nav-title">我的收藏</Text>
          <View className="favorites-page__nav-placeholder" />
        </View>
        <LoadingSkeleton type="list" count={3} />
      </View>
    );
  }

  if (!loading && list.length === 0) {
    return (
      <View className="favorites-page">
        <View className="favorites-page__navbar">
          <Text className="favorites-page__back" onClick={handleBack}>← 返回</Text>
          <Text className="favorites-page__nav-title">我的收藏</Text>
          <View className="favorites-page__nav-placeholder" />
        </View>
        <EmptyState
          description="还没有收藏，去生成喜欢的怼人话术吧"
          actionText="去首页"
          onAction={() => Taro.switchTab({ url: '/pages/index/index' })}
        />
      </View>
    );
  }

  return (
    <View className="favorites-page">
      <View className="favorites-page__navbar">
        <Text className="favorites-page__back" onClick={handleBack}>← 返回</Text>
        <Text className="favorites-page__nav-title">我的收藏</Text>
        <View className="favorites-page__nav-placeholder" />
      </View>

      <ScrollView
        className="favorites-page__list"
        scrollY
        onScrollToLower={handleLoadMore}
        onRefresherRefresh={handleRefresh}
        refresherEnabled
        refresherTriggered={false}
      >
        {list.map((item) => (
          <View key={item.id} className="favorite-item" onClick={() => handleClick(item.id)}>
            <View className="favorite-item__body">
              <Text className="favorite-item__scene">"{item.scene}"</Text>
              <Text className="favorite-item__style">
                {item.style === 'diplomatic' && '🤝'}
                {item.style === 'passive_aggressive' && '🎭'}
                {item.style === 'crazy' && '🤪'}
                {item.style === 'literary' && '📝'}
                {item.style === 'bossy' && '🕶️'}
                {' '}{item.styleName}
              </Text>
              <Text className="favorite-item__preview">{item.contentPreview}</Text>
              <Text className="favorite-item__time">{item.createdAt}</Text>
            </View>
            <View className="favorite-item__heart" onClick={(e) => {
              e.stopPropagation();
              handleUnfavorite(item.id);
            }}>
              <Text className="favorite-item__heart-icon">❤️</Text>
              <Text className="favorite-item__heart-text">已收藏</Text>
            </View>
          </View>
        ))}

        <View className="favorites-page__footer">
          {loadingMore ? (
            <Text className="favorites-page__loading">加载中...</Text>
          ) : hasMore ? (
            <Text className="favorites-page__loading">上拉加载更多</Text>
          ) : (
            <Text className="favorites-page__end">—— 没有更多了 ——</Text>
          )}
        </View>
      </ScrollView>

      <View className="favorites-page__safe-bottom" />
    </View>
  );
}
