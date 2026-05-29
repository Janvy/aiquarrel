import { View, Text } from '@tarojs/components';
import { Style } from '../../utils/constants';
import './index.scss';

interface StyleSelectorProps {
  styles: Style[];
  value: string;
  onChange: (style: string) => void;
}

export default function StyleSelector({ styles, value, onChange }: StyleSelectorProps) {
  return (
    <View className="style-selector">
      {styles.map((s) => (
        <View
          key={s.value}
          className={`style-card ${value === s.value ? 'style-card--active' : ''}`}
          onClick={() => onChange(s.value)}
        >
          <Text className="style-card__emoji">{s.emoji}</Text>
          <Text className="style-card__label">{s.label}</Text>
        </View>
      ))}
    </View>
  );
}
