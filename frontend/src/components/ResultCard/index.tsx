import { View, Text } from '@tarojs/components';
import LoadingSkeleton from '../LoadingSkeleton';
import './index.scss';

interface ResultCardProps {
  content: string;
  styleLabel: string;
  styleEmoji: string;
  loading?: boolean;
}

export default function ResultCard({ content, styleLabel, styleEmoji, loading }: ResultCardProps) {
  if (loading) {
    return (
      <View className="result-card result-card--loading">
        <LoadingSkeleton type="text" count={4} />
      </View>
    );
  }

  return (
    <View className="result-card">
      <Text className="result-card__content">{content}</Text>
      <View className="result-card__tag">
        <Text className="result-card__tag-emoji">{styleEmoji}</Text>
        <Text className="result-card__tag-label">{styleLabel}</Text>
      </View>
    </View>
  );
}
