import { View, Text, Button, Image } from '@tarojs/components';
import './index.scss';

interface EmptyStateProps {
  description: string;
  actionText?: string;
  onAction?: () => void;
  image?: string;
}

export default function EmptyState({ description, actionText, onAction, image }: EmptyStateProps) {
  return (
    <View className="empty-state">
      {image ? (
        <Image className="empty-state__image" src={image} mode="aspectFit" />
      ) : (
        <Text className="empty-state__icon">💬</Text>
      )}
      <Text className="empty-state__desc">{description}</Text>
      {actionText && onAction && (
        <Button className="empty-state__btn" onClick={onAction}>
          {actionText}
        </Button>
      )}
    </View>
  );
}
