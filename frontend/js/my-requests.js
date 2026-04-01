// js/my-requests.js
requireAuth();

async function loadAll() {
  const [recv, sent] = await Promise.all([
    fetch(`${API_BASE}/requests/received`, { headers: authHeaders() }).then(r => r.json()),
    fetch(`${API_BASE}/requests/sent`,     { headers: authHeaders() }).then(r => r.json())
  ]);
  renderReceived(recv);
  renderSent(sent);
}

function renderReceived(requests) {
  const el = document.getElementById('receivedList');
  if (!requests.length) {
    el.innerHTML = '<p class="empty-state">No requests received yet.</p>'; return;
  }
  el.innerHTML = requests.map(r => `
    <div class="request-card">
      <div class="request-info">
        <div class="book-title">${r.book.title}</div>
        <div class="req-meta">
          Requested by <strong>${r.requester.name}</strong>
          (${r.requester.department || 'N/A'}) · ${r.requestedAt?.split('T')[0] || ''}
        </div>
        ${r.message ? `<div class="req-message">"${r.message}"</div>` : ''}
        ${r.status === 'PENDING' ? `
          <div class="req-actions">
            <button class="btn-accept" onclick="respond(${r.id}, 'accept')">✓ Accept</button>
            <button class="btn-reject" onclick="respond(${r.id}, 'reject')">✗ Reject</button>
          </div>` : ''}
        ${r.status === 'ACCEPTED' ? `
          <div class="req-actions">
            <button class="btn-return" onclick="respond(${r.id}, 'returned')">↩ Mark Returned</button>
          </div>` : ''}
      </div>
      <span class="status-badge ${r.status}">${r.status}</span>
    </div>
  `).join('');
}

function renderSent(requests) {
  const el = document.getElementById('sentList');
  if (!requests.length) {
    el.innerHTML = '<p class="empty-state">You haven\'t requested any books yet.</p>'; return;
  }
  el.innerHTML = requests.map(r => `
    <div class="request-card">
      <div class="request-info">
        <div class="book-title">${r.book.title}</div>
        <div class="req-meta">
          by ${r.book.author} · Owner: <strong>${r.book.owner.name}</strong>
          · ${r.requestedAt?.split('T')[0] || ''}
        </div>
        ${r.message ? `<div class="req-message">"${r.message}"</div>` : ''}
      </div>
      <span class="status-badge ${r.status}">${r.status}</span>
    </div>
  `).join('');
}

async function respond(id, action) {
  await fetch(`${API_BASE}/requests/${id}/${action}`, {
    method: 'PATCH', headers: authHeaders()
  });
  loadAll();
}

// Tab switching
document.querySelectorAll('.tab-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(c => c.classList.add('hidden'));
    btn.classList.add('active');
    document.getElementById(`tab-${btn.dataset.tab}`).classList.remove('hidden');
  });
});

loadAll();
