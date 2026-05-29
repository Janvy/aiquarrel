import { PropsWithChildren } from 'react';
import { useLaunch } from '@tarojs/taro';
import './app.scss';

function App({ children }: PropsWithChildren<object>) {
  useLaunch(() => {
    console.log('AI吵架生成器 启动');
  });

  return children;
}

export default App;
