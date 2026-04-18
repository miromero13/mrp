import type { Config } from 'tailwindcss';

export default {
  content: ['./src/**/*.{html,ts}', './libs/**/*.{html,ts}'],
  theme: {
    extend: {
      colors: {
        sidebar: 'var(--sidebar)',
        'sidebar-foreground': 'var(--sidebar-foreground)',
        'sidebar-primary': 'var(--sidebar-primary)',
        'sidebar-primary-foreground': 'var(--sidebar-primary-foreground)',
        'sidebar-accent': 'var(--sidebar-accent)',
        'sidebar-accent-foreground': 'var(--sidebar-accent-foreground)',
        'sidebar-border': 'var(--sidebar-border)',
      },
      boxShadow: {
        'sidebar-border': '0 0 0 1px var(--sidebar-border)',
        'sidebar-accent': '0 0 0 1px var(--sidebar-accent)',
      },
      ringColor: {
        'sidebar-ring': 'var(--sidebar-ring)',
      },
    },
  },
} satisfies Config;
