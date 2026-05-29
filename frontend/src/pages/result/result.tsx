import { useState, useCallback } from 'react';
import { View, Text, Button, Canvas } from '@tarojs/components';
import Taro, { useLoad } from '@tarojs/taro';
import ResultCard from '../../components/ResultCard';
import LoadingSkeleton from '../../components/LoadingSkeleton';
import { STYLES, DEBOUNCE_FAVORITE } from '../../utils/constants';
import { getRecordDetail, generate, toggleFavorite, shareImage } from '../../utils/api';
import { drawShareCard } from '../../utils/shareCard';
import './index.scss';

export default function Result() {
  const [id, setId] = useState('');
  const [scene, setScene] = useState('');
  const [currentStyle, setCurrentStyle] = useState('');
  const [content, setContent] = useState('');
  const [favorited, setFavorited] = useState(false);
  const [loading, setLoading] = useState(true);
  const [styleLoading, setStyleLoading] = useState(false);
  const [imageGenerating, setImageGenerating] = useState(false);
  const [favoriteLock, setFavoriteLock] = useState(false);

  useLoad((options) => {
    const recordId = options?.id;
    if (recordId) {
      setId(recordId);
      getRecordDetail(recordId)
        .then((res) => {
          setScene(res.scene);
          setCurrentStyle(res.style);
          setContent(res.content);
          setFavorited(res.favorited);
        })
        .catch(() => {
          Taro.showToast({ title: '加载失败，请返回重试', icon: 'error' });
        })
        .finally(() => {
          setLoading(false);
        });
    } else {
      Taro.showToast({ title: '参数错误', icon: 'error' });
      setLoading(false);
    }
  });

  const handleStyleSwitch = (newStyle: string) => {
    if (newStyle === currentStyle || styleLoading) return;
    setStyleLoading(true);
    generate(scene, newStyle)
      .then((res) => {
        setId(res.id);
        setContent(res.content);
        setCurrentStyle(newStyle);
        setFavorited(res.favorited);
      })
      .catch(() => {
        Taro.showToast({ title: '切换风格失败，请重试', icon: 'error' });
      })
      .finally(() => {
        setStyleLoading(false);
      });
  };

  const handleCopy = () => {
    Taro.setClipboardData({
      data: content,
      success: () => {
        Taro.showToast({ title: '已复制到剪贴板', icon: 'success' });
      },
    });
  };

  const handleFavorite = () => {
    if (favoriteLock) return;
    setFavoriteLock(true);
    toggleFavorite(id)
      .then((res) => {
        setFavorited(res.favorited);
        Taro.showToast({ title: res.favorited ? '已收藏' : '已取消收藏', icon: 'success' });
      })
      .catch(() => {
        Taro.showToast({ title: '操作失败，请重试', icon: 'error' });
      })
      .finally(() => {
        setTimeout(() => setFavoriteLock(false), DEBOUNCE_FAVORITE);
      });
  };

  const handleRegenerate = () => {
    if (styleLoading) return;
    setStyleLoading(true);
    generate(scene, currentStyle)
      .then((res) => {
        setId(res.id);
        setContent(res.content);
        setFavorited(res.favorited);
      })
      .catch((err) => {
        if (err?.code === 42901) {
          Taro.showToast({ title: '今日生成次数已用完，明天再来吧', icon: 'none' });
        } else {
          Taro.showToast({ title: '生成失败，请稍后再试', icon: 'error' });
        }
      })
      .finally(() => {
        setStyleLoading(false);
      });
  };

  const handleShareImage = useCallback(() => {
    setImageGenerating(true);

    const currentStyleInfo = STYLES.find((s) => s.value === currentStyle);
    const doCanvasDraw = () => {
      const query = Taro.createSelectorQuery();
      query
        .select('#shareCanvas')
        .fields({ node: true, size: true })
        .exec((res) => {
          if (!res || !res[0] || !res[0].node) {
            // Canvas 2D not available, try API fallback
            shareImage(id)
              .then((r) => {
                if (r.imageUrl) Taro.showToast({ title: '图片已生成', icon: 'success' });
              })
              .catch(() => Taro.showToast({ title: '图片生成失败，请重试', icon: 'none' }))
              .finally(() => setImageGenerating(false));
            return;
          }

          const canvas = res[0].node;
          const ctx = canvas.getContext('2d');
          const dpr = Taro.getSystemInfoSync().pixelRatio || 2;
          canvas.width = 750 * dpr;
          canvas.height = 940 * dpr;
          ctx.scale(dpr, dpr);

          drawShareCard(ctx as unknown as CanvasRenderingContext2D, {
            scene,
            styleLabel: currentStyleInfo?.label || '',
            styleEmoji: currentStyleInfo?.emoji || '',
            content,
          });

          Taro.canvasToTempFilePath({
            canvas,
            width: 750,
            height: 940,
            destWidth: 750,
            destHeight: 940,
            success: (fileRes) => {
              Taro.showToast({ title: '图片已生成', icon: 'success' });
              setImageGenerating(false);
            },
            fail: () => {
              Taro.showToast({ title: '图片生成失败，请重试', icon: 'none' });
              setImageGenerating(false);
            },
          });
        });
    };

    // 优先尝试 Canvas 生成
    doCanvasDraw();
  }, [id, scene, currentStyle, content]);

  const handleBack = () => {
    Taro.navigateBack();
  };

  if (loading) {
    return (
      <View className="result-page">
        <View className="result-page__navbar">
          <Text className="result-page__back" onClick={handleBack}>← 返回</Text>
          <Text className="result-page__nav-title">怼人话术</Text>
          <View className="result-page__nav-placeholder" />
        </View>
        <LoadingSkeleton type="card" />
      </View>
    );
  }

  const currentStyleInfo = STYLES.find((s) => s.value === currentStyle);

  return (
    <View className="result-page">
      <View className="result-page__navbar">
        <Text className="result-page__back" onClick={handleBack}>← 返回</Text>
        <Text className="result-page__nav-title">怼人话术</Text>
        <View className="result-page__nav-placeholder" />
      </View>

      <View className="result-page__tabs">
        {STYLES.map((s) => (
          <View
            key={s.value}
            className={`result-page__tab ${currentStyle === s.value ? 'result-page__tab--active' : ''}`}
            onClick={() => handleStyleSwitch(s.value)}
          >
            <Text className="result-page__tab-emoji">{s.emoji}</Text>
            <Text className="result-page__tab-label">{s.label.slice(0, 2)}</Text>
          </View>
        ))}
      </View>

      <ResultCard
        content={content}
        styleLabel={currentStyleInfo?.label || ''}
        styleEmoji={currentStyleInfo?.emoji || ''}
        loading={styleLoading}
      />

      <View className="result-page__actions">
        <Button className="result-page__action-btn" onClick={handleCopy}>
          <Text className="result-page__action-icon">📋</Text>
          <Text className="result-page__action-text">复制</Text>
        </Button>
        <Button className="result-page__action-btn" onClick={handleFavorite}>
          <Text className="result-page__action-icon">{favorited ? '❤️' : '🤍'}</Text>
          <Text className="result-page__action-text">{favorited ? '已收藏' : '收藏'}</Text>
        </Button>
        <Button className="result-page__action-btn" onClick={handleRegenerate}>
          <Text className="result-page__action-icon">🔄</Text>
          <Text className="result-page__action-text">再来</Text>
        </Button>
      </View>

      <View className="result-page__share-area">
        <Button
          className="result-page__share-btn"
          onClick={handleShareImage}
          loading={imageGenerating}
          disabled={imageGenerating}
        >
          {imageGenerating ? '正在生成图片...' : '生成分享图片'}
        </Button>
      </View>

      <Canvas
        className="result-page__share-canvas"
        type="2d"
        id="shareCanvas"
      />

      <View className="result-page__safe-bottom" />
    </View>
  );
}
