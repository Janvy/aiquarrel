const sharp = require('sharp');
const path = require('path');
const fs = require('fs');

const OUT_DIR = path.resolve(__dirname, '../src/assets/icons');
const SIZE = 81;
const STROKE = 2.5;

function svgIcon(body, color) {
  return Buffer.from(
    `<svg xmlns="http://www.w3.org/2000/svg" width="${SIZE}" height="${SIZE}" viewBox="0 0 24 24" fill="none" stroke="${color}" stroke-width="${STROKE}" stroke-linecap="round" stroke-linejoin="round">${body}</svg>`
  );
}

const icons = {
  // Feather: home
  home: '<path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/>',

  // Feather: clock
  history: '<circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>',

  // Feather: user
  mine: '<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>',
};

async function generate() {
  for (const [name, body] of Object.entries(icons)) {
    // normal state (gray #999999)
    await sharp(svgIcon(body, '#999999'))
      .resize(SIZE, SIZE)
      .png()
      .toFile(path.join(OUT_DIR, `${name}.png`));

    // active state (primary #FF6B6B)
    await sharp(svgIcon(body, '#FF6B6B'))
      .resize(SIZE, SIZE)
      .png()
      .toFile(path.join(OUT_DIR, `${name}-active.png`));

    console.log(`Generated ${name}.png and ${name}-active.png`);
  }
  console.log('All 6 icons generated successfully.');
}

generate().catch(console.error);
