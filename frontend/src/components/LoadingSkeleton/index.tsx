import { View } from '@tarojs/components';
import './index.scss';

interface LoadingSkeletonProps {
  type?: 'card' | 'list' | 'text';
  count?: number;
}

export default function LoadingSkeleton({ type = 'text', count = 3 }: LoadingSkeletonProps) {
  if (type === 'card') {
    return (
      <View className="skeleton-card">
        <View className="skeleton-card__block skeleton-animate" />
      </View>
    );
  }

  if (type === 'list') {
    return (
      <View className="skeleton-list">
        {Array.from({ length: count }).map((_, i) => (
          <View key={i} className="skeleton-list__item">
            <View className="skeleton-list__title skeleton-animate" />
            <View className="skeleton-list__desc skeleton-animate" />
            <View className="skeleton-list__time skeleton-animate" />
          </View>
        ))}
      </View>
    );
  }

  return (
    <View className="skeleton-text">
      {Array.from({ length: count }).map((_, i) => (
        <View
          key={i}
          className="skeleton-text__line skeleton-animate"
          style={{ width: `${80 + Math.random() * 20}%` }}
        />
      ))}
    </View>
  );
}
