export interface Style {
  value: string;
  label: string;
  emoji: string;
}

export const STYLES: Style[] = [
  { value: 'diplomatic', label: '高情商', emoji: '🤝' },
  { value: 'passive_aggressive', label: '阴阳怪气', emoji: '🎭' },
  { value: 'crazy', label: '发疯文学', emoji: '🤪' },
  { value: 'literary', label: '文艺', emoji: '📝' },
  { value: 'bossy', label: '霸总', emoji: '🕶️' },
];

export const STYLE_MAP: Record<string, string> = {};
STYLES.forEach((s) => {
  STYLE_MAP[s.value] = s.label;
});

export const PRESET_SCENES: string[] = [
  '同事甩锅',
  '对象冷暴力',
  '亲戚催婚',
  '室友很吵',
  '老板画饼',
  '朋友借钱不还',
];

export const DAILY_LIMIT = 50;

export const PAGE_SIZE = 20;

export const MAX_SCENE_LENGTH = 200;

export const DEBOUNCE_GENERATE = 2000;

export const DEBOUNCE_FAVORITE = 500;

export const SWIPE_THRESHOLD = 60;
