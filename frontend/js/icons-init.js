(function () {
  const SOURCES = [
    'https://unpkg.com/lucide@0.469.0/dist/umd/lucide.min.js',
    'https://cdn.jsdelivr.net/npm/lucide@0.469.0/dist/umd/lucide.min.js'
  ];

  function render() {
    if (window.lucide) {
      window.lucide.createIcons();
      return true;
    }
    return false;
  }

  function load(src) {
    return new Promise((resolve, reject) => {
      const s = document.createElement('script');
      s.src = src;
      s.async = true;
      s.onload = resolve;
      s.onerror = reject;
      document.head.appendChild(s);
    });
  }

  async function boot() {
    if (render()) return;
    for (const src of SOURCES) {
      try {
        await load(src);
        if (render()) return;
      } catch (_) {}
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', boot);
  } else {
    boot();
  }
})();
