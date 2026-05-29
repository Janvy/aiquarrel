import { View, Text } from '@tarojs/components';
import { HistoryItem as HistoryItemType } from '../../utils/api';
import './index.scss';

interface HistoryItemProps {
  item: HistoryItemType;
  onDelete: (id: string) => void;
  onClick: (id: string) => void;
}

export default function HistoryItem({ item, onDelete, onClick }: HistoryItemProps) {
  return (
    <View className="history-item-wrapper">
      <View className="history-item" onClick={() => onClick(item.id)}>
        <Text className="history-item__scene">"{item.scene}"</Text>
        <Text className="history-item__style">
          {item.style === 'diplomatic' && '🤝'}
          {item.style === 'passive_aggressive' && '🎭'}
          {item.style === 'crazy' && '🤪'}
          {item.style === 'literary' && '📝'}
          {item.style === 'bossy' && '🕶️'}
          {' '}{item.styleName}
        </Text>
        <Text className="history-item__preview">{item.contentPreview}</Text>
        <Text className="history-item__time">{item.createdAt}</Text>
      </View>
      <View className="history-item__delete" onClick={() => onDelete(item.id)}>
        <Text className="history-item__delete-text">删除</Text>
      </View>
    </View>
  );
}
