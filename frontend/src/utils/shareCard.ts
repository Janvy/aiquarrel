/**
 * 分享卡片 Canvas 绘制模块
 * 设计规范参考 docs/前端工程师资源需求.md REQ-003
 *
 * 画布尺寸: 750×940px (5:4 比例)
 * 背景: 主色渐变 #FF6B6B → #FF8E8E
 * 白色卡片: 710×860px, 圆角 24rpx, 内边距 32rpx
 */
interface ShareCardData {
  scene: string;
  styleLabel: string;
  styleEmoji: string;
  content: string;
}

const W = 750;
const H = 940;
const CARD_X = 20;
const CARD_Y = 20;
const CARD_W = 710;
const CARD_H = 860;
const CARD_R = 24;
const PAD = 32;

function wrapText(
  ctx: { measureText: (t: string) => { width: number } },
  text: string,
  maxWidth: number,
  maxLines: number,
): string[] {
  const lines: string[] = [];
  let current = '';
  for (let i = 0; i < text.length; i++) {
    const ch = text[i];
    const test = current + ch;
    if (ctx.measureText(test).width > maxWidth && current.length > 0) {
      lines.push(current);
      current = ch;
      if (lines.length >= maxLines) break;
    } else {
      current = test;
    }
  }
  if (current && lines.length < maxLines) {
    lines.push(current);
  }
  return lines;
}

function drawRoundRect(
  ctx: { beginPath: () => void; moveTo: (x: number, y: number) => void; arcTo: (x1: number, y1: number, x2: number, y2: number, r: number) => void; closePath: () => void; fill: () => void },
  x: number, y: number, w: number, h: number, r: number,
) {
  ctx.beginPath();
  ctx.moveTo(x + r, y);
  ctx.arcTo(x + w, y, x + w, y + h, r);
  ctx.arcTo(x + w, y + h, x, y + h, r);
  ctx.arcTo(x, y + h, x, y, r);
  ctx.arcTo(x, y, x + w, y, r);
  ctx.closePath();
}

export function drawShareCard(
  ctx: CanvasRenderingContext2D,
  data: ShareCardData,
): void {
  // 1. 背景渐变
  const grad = ctx.createLinearGradient(0, 0, 0, H);
  grad.addColorStop(0, '#FF6B6B');
  grad.addColorStop(1, '#FF8E8E');
  ctx.fillStyle = grad;
  ctx.fillRect(0, 0, W, H);

  // 2. 白色卡片
  drawRoundRect(ctx, CARD_X, CARD_Y, CARD_W, CARD_H, CARD_R);
  ctx.fillStyle = '#FFFFFF';
  ctx.fill();
  // 阴影 (simplified — WeChat Canvas 2D shadow support varies)
  ctx.save();
  drawRoundRect(ctx, CARD_X, CARD_Y, CARD_W, CARD_H, CARD_R);
  ctx.shadowColor = 'rgba(0,0,0,0.06)';
  ctx.shadowBlur = 16;
  ctx.shadowOffsetY = 4;
  ctx.fillStyle = '#FFFFFF';
  ctx.fill();
  ctx.restore();

  let cy = CARD_Y + PAD;

  // 3. 品牌名
  const brandText = '🎯 AI吵架生成器';
  ctx.fillStyle = '#FF6B6B';
  ctx.font = 'bold 28px sans-serif';
  ctx.textAlign = 'center';
  ctx.fillText(brandText, W / 2, cy + 28);
  cy += 28 + 24;

  // 4. 分割线
  ctx.strokeStyle = '#F0F0F0';
  ctx.lineWidth = 2;
  ctx.beginPath();
  ctx.moveTo(CARD_X + PAD, cy);
  ctx.lineTo(CARD_X + CARD_W - PAD, cy);
  ctx.stroke();
  cy += 24;

  // 5. 场景引用
  const sceneText = `"${data.scene}"`;
  ctx.fillStyle = '#999999';
  ctx.font = '24px sans-serif';
  ctx.textAlign = 'center';
  ctx.fillText(sceneText, W / 2, cy + 24);
  cy += 24 + 16;

  // 6. 风格标签
  const tagText = `${data.styleEmoji} ${data.styleLabel}`;
  ctx.font = '20px sans-serif';
  const tagW = ctx.measureText(tagText).width + 24;
  const tagH = 36;
  const tagX = (W - tagW) / 2;
  drawRoundRect(ctx, tagX, cy, tagW, tagH, 18);
  ctx.fillStyle = '#FF6B6B';
  ctx.fill();
  ctx.fillStyle = '#FFFFFF';
  ctx.textAlign = 'center';
  ctx.fillText(tagText, W / 2, cy + tagH - 10);
  cy += tagH + 32;

  // 7. 正文 (最多6行, 36px, 行距1.8, 居中对齐)
  const contentMaxW = CARD_W - PAD * 2 - 20;
  ctx.font = '36px sans-serif';
  const lines = wrapText(ctx, data.content, contentMaxW, 6);
  const lineH = Math.round(36 * 1.8);
  ctx.fillStyle = '#333333';
  ctx.textAlign = 'center';
  for (const line of lines) {
    ctx.fillText(line, W / 2, cy + 36);
    cy += lineH;
  }
  cy += 32;

  // 8. 引导区
  const guideY = Math.max(CARD_Y + CARD_H - 120, cy);
  ctx.fillStyle = '#FFF8F8';
  ctx.fillRect(CARD_X + PAD, guideY, CARD_W - PAD * 2, 100);
  drawRoundRect(ctx, CARD_X + PAD, guideY, CARD_W - PAD * 2, 100, 12);
  ctx.fill();

  // 引导文案
  ctx.fillStyle = '#999999';
  ctx.font = '20px sans-serif';
  ctx.textAlign = 'left';
  ctx.fillText('📋 扫码生成你的怼人话术', CARD_X + PAD + 20, guideY + 38);

  // 小程序码占位 (白色方块 + 文字)
  const qrX = CARD_X + CARD_W - PAD - 100;
  const qrY = guideY + 10;
  ctx.fillStyle = '#FFFFFF';
  ctx.fillRect(qrX, qrY, 80, 80);
  ctx.strokeStyle = '#F0F0F0';
  ctx.lineWidth = 1;
  ctx.strokeRect(qrX, qrY, 80, 80);
  ctx.fillStyle = '#CCCCCC';
  ctx.font = '16px sans-serif';
  ctx.textAlign = 'center';
  ctx.fillText('小程序码', qrX + 40, qrY + 45);

  // 9. 品牌水印 (卡片外底部)
  ctx.fillStyle = 'rgba(255,255,255,0.7)';
  ctx.font = '20px sans-serif';
  ctx.textAlign = 'center';
  ctx.fillText('AI吵架生成器 · 用幽默化解情绪', W / 2, H - 20);
}
