import Taro from '@tarojs/taro';

const DEVICE_ID_KEY = 'device_id';

function generateUUID(): string {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0;
    const v = c === 'x' ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}

export function getDeviceId(): string {
  try {
    const stored = Taro.getStorageSync(DEVICE_ID_KEY);
    if (stored) return stored;
  } catch {
    // ignore
  }
  const uuid = generateUUID();
  try {
    Taro.setStorageSync(DEVICE_ID_KEY, uuid);
  } catch {
    // ignore
  }
  return uuid;
}
