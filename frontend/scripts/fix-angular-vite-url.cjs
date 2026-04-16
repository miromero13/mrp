const fs = require('node:fs');
const path = require('node:path');

const target = path.join(
  process.cwd(),
  'node_modules',
  '@angular',
  'build',
  'src',
  'tools',
  'vite',
  'utils.js',
);

if (!fs.existsSync(target)) {
  console.log('[fix-angular-vite-url] target file not found, skipping.');
  process.exit(0);
}

let source = fs.readFileSync(target, 'utf8');

if (source.includes('/* copilot url guard */')) {
  console.log('[fix-angular-vite-url] patch already applied.');
  process.exit(0);
}

const before = "function pathnameWithoutBasePath(url, basePath) {\n    const parsedUrl = new URL(url, 'http://localhost');\n    const pathname = decodeURIComponent(parsedUrl.pathname);\n";
const after = "function pathnameWithoutBasePath(url, basePath) {\n    /* copilot url guard */\n    let parsedUrl;\n    try {\n        parsedUrl = new URL(url, 'http://localhost');\n    }\n    catch {\n        return '/';\n    }\n    let pathname;\n    try {\n        pathname = decodeURIComponent(parsedUrl.pathname);\n    }\n    catch {\n        pathname = parsedUrl.pathname || '/';\n    }\n";

if (!source.includes(before)) {
  console.log('[fix-angular-vite-url] expected source pattern not found, skipping.');
  process.exit(0);
}

source = source.replace(before, after);
fs.writeFileSync(target, source);
console.log('[fix-angular-vite-url] patch applied.');
