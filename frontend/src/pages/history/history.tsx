import { useState, useEffect } from 'react';
import { View, Text, ScrollView } from '@tarojs/components';
import Taro, { useDidShow } from '@tarojs/taro';
import EmptyState from '../../components/EmptyState';
import LoadingSkeleton from '../../components/LoadingSkeleton';
import { PAGE_SIZE } from '../../utils/constants';
import { getHistory, deleteHistory, clearHistory, HistoryItem as HistoryItemType } from '../../utils/api';
import './index.scss';

export default function History() {
  const [list, setList] = useState<HistoryItemType[]>([]);
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [swipedId, setSwipedId] = useState<string | null>(null);
  const [touchStartX, setTouchStartX] = useState(0);

  const fetchList = (pageNum: number, isRefresh: boolean) => {
    if (isRefresh) {
      setLoading(true);
    } else {
      setLoadingMore(true);
    }

    getHistory(pageNum, PAGE_SIZE)
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

  const handleDelete = (id: string) => {
    Taro.showModal({
      title: '提示',
      content: '确定删除这条记录吗？',
      success: (res) => {
        if (res.confirm) {
          deleteHistory(id)
            .then(() => {
              setList((prev) => prev.filter((item) => item.id !== id));
              setTotal((prev) => prev - 1);
              Taro.showToast({ title: '已删除', icon: 'success' });
            })
            .catch(() => {
              Taro.showToast({ title: '删除失败，请重试', icon: 'error' });
            });
        }
      },
    });
    setSwipedId(null);
  };

  const handleClearAll = () => {
    Taro.showModal({
      title: '清空历史',
      content: '确定清空所有历史记录？',
      success: (res) => {
        if (res.confirm) {
          clearHistory()
            .then(() => {
              setList([]);
              setTotal(0);
              Taro.showToast({ title: '已清空', icon: 'success' });
            })
            .catch(() => {
              Taro.showToast({ title: '清空失败，请重试', icon: 'error' });
            });
        }
      },
    });
  };

  const handleTouchStart = (id: string, e: { touches: { clientX: number }[] }) => {
    setTouchStartX(e.touches[0].clientX);
  };

  const handleTouchEnd = (id: string, e: { changedTouches: { clientX: number }[] }) => {
    const deltaX = e.changedTouches[0].clientX - touchStartX;
    if (deltaX < -60) {
      setSwipedId(id);
    } else if (deltaX > 60) {
      setSwipedId(null);
    }
  };

  const hasMore = page * PAGE_SIZE < total;

  if (loading) {
    return (
      <View className="history-page">
        <View className="history-page__navbar">
          <Text className="history-page__nav-title">历史记录</Text>
        </View>
        <LoadingSkeleton type="list" count={3} />
      </View>
    );
  }

  if (!loading && list.length === 0) {
    return (
      <View className="history-page">
        <View className="history-page__navbar">
          <Text className="history-page__nav-title">历史记录</Text>
        </View>
        <EmptyState
          description="还没有生成过怼人话术，去首页试试吧"
          actionText="去首页"
          onAction={() => Taro.switchTab({ url: '/pages/index/index' })}
        />
      </View>
    );
  }

  return (
    <View className="history-page">
      <View className="history-page__navbar">
        <Text className="history-page__nav-title">历史记录</Text>
        <Text className="history-page__clear" onClick={handleClearAll}>🗑️ 清空</Text>
      </View>

      <ScrollView
        className="history-page__list"
        scrollY
        onScrollToLower={handleLoadMore}
        onRefresherRefresh={handleRefresh}
        refresherEnabled
        refresherTriggered={false}
      >
        {list.map((item) => (
          <View
            key={item.id}
            className="history-item-outer"
            onTouchStart={(e) => handleTouchStart(item.id, e)}
            onTouchEnd={(e) => handleTouchEnd(item.id, e)}
          >
            <View className="history-item-content" onClick={() => handleClick(item.id)}>
              <Text className="history-item-content__scene">"{item.scene}"</Text>
              <View className="history-item-content__meta">
                <Text className="history-item-content__style">
                  {item.style === 'diplomatic' && '🤝'}
                  {item.style === 'passive_aggressive' && '🎭'}
                  {item.style === 'crazy' && '🤪'}
                  {item.style === 'literary' && '📝'}
                  {item.style === 'bossy' && '🕶️'}
                  {' '}{item.styleName}
                </Text>
                <Text className="history-item-content__time">{item.createdAt}</Text>
              </View>
              <Text className="history-item-content__preview">{item.contentPreview}</Text>
            </View>

            {swipedId === item.id && (
              <View className="history-item-delete" onClick={() => handleDelete(item.id)}>
                <Text className="history-item-delete__text">删除</Text>
              </View>
            )}
          </View>
        ))}

        <View className="history-page__footer">
          {loadingMore ? (
            <Text className="history-page__loading">加载中...</Text>
          ) : hasMore ? (
            <Text className="history-page__loading">上拉加载更多</Text>
          ) : (
            <Text className="history-page__end">—— 没有更多了 ——</Text>
          )}
        </View>
      </ScrollView>

      <View className="history-page__safe-bottom" />
    </View>
  );
}
