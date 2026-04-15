/* ============================================================
   Modal helpers
   ============================================================ */
function openModal(id) {
  const el = document.getElementById(id);
  if (!el) return;
  el.classList.add('modal--open');
  // Focus first focusable element
  const focusable = el.querySelector('input, button, select, textarea, a[href]');
  if (focusable) setTimeout(() => focusable.focus(), 50);
}

function closeModal(id) {
  const el = document.getElementById(id);
  if (el) el.classList.remove('modal--open');
}

// Close modal on Escape key
document.addEventListener('keydown', function (e) {
  if (e.key === 'Escape') {
    document.querySelectorAll('.modal.modal--open').forEach(m => {
      m.classList.remove('modal--open');
    });
    // Also close preview panel if open
    const panel = document.getElementById('preview-panel');
    if (panel && !panel.classList.contains('preview-panel--closed')) {
      panel.classList.add('preview-panel--closed');
    }
  }
});

/* ============================================================
   Auto-dismiss flash messages
   ============================================================ */
document.addEventListener('DOMContentLoaded', function () {
  document.querySelectorAll('.flash').forEach(function (el) {
    setTimeout(function () {
      el.style.transition = 'opacity 0.4s ease';
      el.style.opacity = '0';
      setTimeout(() => el.remove(), 400);
    }, 4000);
  });
});
