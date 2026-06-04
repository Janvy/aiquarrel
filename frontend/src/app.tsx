import { PropsWithChildren } from 'react';
import { useLaunch } from '@tarojs/taro';
import './app.scss';

function App({ children }: PropsWithChildren<object>) {
  useLaunch(() => {
    console.log('AI怼人神器 启动');
  });

  return children;
}

export default App;
