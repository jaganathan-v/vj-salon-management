// ============================
// VJ SALON — UTILITIES
// ============================

// --- Toast Notifications ---
function showToast(message, type = 'info', title = '') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    container.style.cssText = 'position:fixed;bottom:20px;right:20px;z-index:9999;display:flex;flex-direction:column;gap:10px;';
    document.body.appendChild(container);
  }
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.style.cssText = 'min-width:250px;padding:15px;border-radius:8px;background:#333;color:#fff;box-shadow:0 4px 12px rgba(0,0,0,0.15);display:flex;align-items:center;gap:10px;opacity:0;transform:translateY(20px);transition:all 0.3s ease;';
  
  const icons = { success: '✓', error: '✕', info: '✦', warning: '⚠' };
  const colors = { success: '#4caf50', error: '#f44336', info: '#2196f3', warning: '#ff9800' };
  
  toast.style.borderLeft = `4px solid ${colors[type]}`;
  
  let html = `<div style="font-size:20px;color:${colors[type]}">${icons[type]}</div><div>`;
  if (title) html += `<div style="font-weight:bold;margin-bottom:4px;">${title}</div>`;
  html += `<div style="font-size:14px;">${message}</div></div>`;
  toast.innerHTML = html;
  
  container.appendChild(toast);
  
  // Animate in
  requestAnimationFrame(() => {
    toast.style.opacity = '1';
    toast.style.transform = 'translateY(0)';
  });
  
  // Auto remove
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(-20px)';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

// --- Modal ---
function openModal(modalId) { 
  const m = document.getElementById(modalId);
  if(m) m.classList.add('active'); 
}
function closeModal(modalId) { 
  const m = document.getElementById(modalId);
  if(m) m.classList.remove('active'); 
}
// Generic modal with custom title+body:
function showModal(title, bodyHTML, onConfirm = null) {
  let modal = document.getElementById('generic-modal');
  if (!modal) {
    modal = document.createElement('div');
    modal.id = 'generic-modal';
    modal.className = 'modal';
    modal.style.cssText = 'position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,0.5);display:none;align-items:center;justify-content:center;z-index:1000;';
    modal.innerHTML = `
      <div class="modal-content" style="background:var(--bg-card,#fff);color:var(--text-main,#333);padding:20px;border-radius:12px;min-width:300px;max-width:500px;box-shadow:0 10px 30px rgba(0,0,0,0.2);">
        <h3 id="generic-modal-title" style="margin-top:0;margin-bottom:15px;"></h3>
        <div id="generic-modal-body" style="margin-bottom:20px;font-size:15px;"></div>
        <div style="display:flex;justify-content:flex-end;gap:10px;">
          <button id="generic-modal-cancel" style="padding:8px 16px;border:none;border-radius:6px;background:#ddd;cursor:pointer;color:#333;">Cancel</button>
          <button id="generic-modal-confirm" style="padding:8px 16px;border:none;border-radius:6px;background:var(--primary-color,#007bff);color:#fff;cursor:pointer;display:none;">Confirm</button>
        </div>
      </div>
    `;
    document.body.appendChild(modal);
    
    // Add CSS for active
    const style = document.createElement('style');
    style.innerHTML = '.modal.active { display:flex !important; }';
    document.head.appendChild(style);
  }
  
  document.getElementById('generic-modal-title').textContent = title;
  document.getElementById('generic-modal-body').innerHTML = bodyHTML;
  
  const cancelBtn = document.getElementById('generic-modal-cancel');
  const confirmBtn = document.getElementById('generic-modal-confirm');
  
  cancelBtn.onclick = () => closeModal('generic-modal');
  
  if (onConfirm) {
    confirmBtn.style.display = 'block';
    confirmBtn.onclick = () => {
      onConfirm();
      closeModal('generic-modal');
    };
  } else {
    confirmBtn.style.display = 'none';
    cancelBtn.textContent = 'Close';
  }
  
  openModal('generic-modal');
}

// --- Flip Clock ---
let clockInterval = null;
function initClock(elementId = 'clock-display') {
  const el = document.getElementById(elementId);
  if (!el) return;
  
  function update() {
    const now = new Date();
    const h = String(now.getHours()).padStart(2, '0');
    const m = String(now.getMinutes()).padStart(2, '0');
    const s = String(now.getSeconds()).padStart(2, '0');
    
    el.innerHTML = `
      <span class="flip-digit" style="background:#222;color:#fff;padding:4px 8px;border-radius:4px;font-family:monospace;font-size:1.2em;">${h}</span>
      <span class="flip-separator" style="font-weight:bold;margin:0 2px;">:</span>
      <span class="flip-digit" style="background:#222;color:#fff;padding:4px 8px;border-radius:4px;font-family:monospace;font-size:1.2em;">${m}</span>
      <span class="flip-separator" style="font-weight:bold;margin:0 2px;">:</span>
      <span class="flip-digit" style="background:#222;color:#fff;padding:4px 8px;border-radius:4px;font-family:monospace;font-size:1.2em;">${s}</span>
    `;
  }
  
  update();
  if (clockInterval) clearInterval(clockInterval);
  clockInterval = setInterval(update, 1000);
}

// --- Theme Management ---
function applyTheme() {
  const theme = localStorage.getItem('vj_theme') || 'dark';
  document.documentElement.setAttribute('data-theme', theme);
}

// --- Font Size ---
function applyFontSize() {
  const size = localStorage.getItem('vj_fontsize') || 'medium';
  const sizes = { small: '14px', medium: '16px', large: '18px' };
  document.documentElement.style.fontSize = sizes[size];
}

// --- Format helpers ---
function formatCurrency(amount) { return '₹' + Number(amount).toLocaleString('en-IN'); }
function formatDate(dateStr) { return new Date(dateStr).toLocaleDateString('en-IN', {day:'2-digit',month:'short',year:'numeric'}); }
function formatTime(timeStr) {
  if (!timeStr) return '';
  const [h, m] = timeStr.split(':');
  const hour = parseInt(h);
  const ampm = hour >= 12 ? 'PM' : 'AM';
  return (hour % 12 || 12) + ':' + m + ' ' + ampm;
}
function generateBookingRef() { return 'VJ-' + Date.now().toString(36).toUpperCase().slice(-6); }
function getStatusBadgeClass(status) {
  const map = { FREE:'badge-free', BUSY:'badge-busy', FOOD_BREAK:'badge-break', OPEN:'badge-open', CLOSED:'badge-closed', PENDING:'badge-pending', CONFIRMED:'badge-open', COMPLETED:'badge-teal', CANCELLED:'badge-closed' };
  return map[status] || 'badge-amber';
}
function getStatusLabel(status) {
  const map = { FREE:'Free', BUSY:'With Client', FOOD_BREAK:'Food Break', PENDING:'Pending', CONFIRMED:'Confirmed', COMPLETED:'Completed', CANCELLED:'Cancelled' };
  return map[status] || status;
}

// --- Sidebar ---
function initSidebar() {
  const page = window.location.pathname.split('/').pop() || 'index.html';
  document.querySelectorAll('.sidebar-item').forEach(item => {
    if (item.getAttribute('href') === page || item.dataset.page === page) {
      item.classList.add('active');
    }
  });
}

// --- Loading Screen ---
function hideLoading() {
  setTimeout(() => {
    const ls = document.getElementById('loading-screen');
    if (ls) ls.classList.add('hidden');
  }, 1200);
}

// --- CSV Export ---
function exportCSV(data, filename) {
  if (!data || !data.length) { showToast('No data to export', 'error'); return; }
  const headers = Object.keys(data[0]);
  const csv = [headers.join(','), ...data.map(row => headers.map(h => JSON.stringify(row[h]??'')).join(','))].join('\n');
  const blob = new Blob([csv], {type:'text/csv'});
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a'); a.href=url; a.download=filename; a.click();
  URL.revokeObjectURL(url);
}

// --- Star rating render (read-only) ---
function renderStars(rating, max=5) {
  return Array.from({length:max}, (_,i) => `<span class='rating-star ${i<rating?'filled':''}' style='color:${i<rating?'#ffc107':'#ccc'};'>★</span>`).join('');
}

// --- Apply all settings on load ---
function initPage() {
  applyTheme();
  applyFontSize();
  hideLoading();
  initSidebar();
  initClock();
}

// Run on every page
window.addEventListener('DOMContentLoaded', initPage);
