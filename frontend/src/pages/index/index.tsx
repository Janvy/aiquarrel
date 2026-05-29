import { useState, useRef, useCallback } from 'react';
import { View, Text, Textarea } from '@tarojs/components';
import Taro from '@tarojs/taro';
import StyleSelector from '../../components/StyleSelector';
import { STYLES, PRESET_SCENES, MAX_SCENE_LENGTH, DEBOUNCE_GENERATE } from '../../utils/constants';
import { generate } from '../../utils/api';
import './index.scss';

export default function Index() {
  const [scene, setScene] = useState('');
  const [style, setStyle] = useState('crazy');
  const [loading, setLoading] = useState(false);
  const lastClickRef = useRef(0);

  const handleGenerate = useCallback(() => {
    const now = Date.now();
    if (now - lastClickRef.current < DEBOUNCE_GENERATE) return;
    lastClickRef.current = now;

    setLoading(true);
    generate(scene, style)
      .then((res) => {
        Taro.navigateTo({ url: `/pages/result/result?id=${res.id}` });
      })
      .catch((err) => {
        const code = err?.code;
        if (code === 42901) {
          Taro.showToast({ title: '今日生成次数已用完，明天再来吧', icon: 'none' });
        } else if (code === 40001) {
          Taro.showToast({ title: '请输入1-200字的场景描述', icon: 'none' });
        } else if (code === 40002) {
          Taro.showToast({ title: '请选择有效风格', icon: 'none' });
        } else if (code === 50001) {
          Taro.showToast({ title: 'AI正在开小差，请稍后再试', icon: 'none' });
        } else {
          Taro.showToast({ title: err?.message || '生成失败，请稍后再试', icon: 'error' });
        }
      })
      .finally(() => {
        setLoading(false);
      });
  }, [scene, style]);

  const handleSceneChange = (e: { detail: { value: string } }) => {
    let val = e.detail.value;
    if (val.length > MAX_SCENE_LENGTH) {
      val = val.slice(0, MAX_SCENE_LENGTH);
    }
    setScene(val);
  };

  const handlePresetClick = (preset: string) => {
    setScene(preset);
  };

  return (
    <View className="index-page">
      <View className="index-page__header">
        <Text className="index-page__title">🎯 AI吵架生成器</Text>
        <Text className="index-page__subtitle">用幽默化解情绪，把憋屈变成精彩</Text>
      </View>

      <View className="index-page__input-card">
        <Textarea
          className="index-page__textarea"
          placeholder="输入你想怼的场景..."
          placeholderClass="index-page__placeholder"
          value={scene}
          onInput={handleSceneChange}
          maxlength={MAX_SCENE_LENGTH}
          autoHeight
        />
        <Text className="index-page__count">
          {scene.length}/{MAX_SCENE_LENGTH}
        </Text>
      </View>

      <View className="index-page__presets">
        {PRESET_SCENES.map((preset) => (
          <View
            key={preset}
            className={`index-page__tag ${scene === preset ? 'index-page__tag--active' : ''}`}
            onClick={() => handlePresetClick(preset)}
          >
            <Text className="index-page__tag-text">{preset}</Text>
          </View>
        ))}
      </View>

      <View className="index-page__section-title">
        <Text>选择风格：</Text>
      </View>

      <StyleSelector styles={STYLES} value={style} onChange={setStyle} />

      <View className="index-page__btn-area">
        <View
          className={`index-page__btn ${loading ? 'index-page__btn--loading' : ''}`}
          onClick={handleGenerate}
        >
          <Text className="index-page__btn-text">
            {loading ? '正在酝酿怼人话术...' : '😈 生成怼人话术'}
          </Text>
        </View>
      </View>

      <View className="index-page__safe-bottom" />
    </View>
  );
}
