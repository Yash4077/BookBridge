// js/browse.js
requireAuth();

let pendingBookId = null;

async function loadBooks() {
  document.getElementById('searchInput').value = '';
  const res = await fetch(`${API_BASE}/books/browse`, { headers: authHeaders() });
  if (res.status === 401) { logout(); return; }
  const books = await res.json();
  renderGrid(books);
}

async function doSearch() {
  const q = document.getElementById('searchInput').value.trim();
  if (!q) { loadBooks(); return; }
  const res = await fetch(`${API_BASE}/books/search?q=${encodeURIComponent(q)}`, { headers: authHeaders() });
  const books = await res.json();
  renderGrid(books);
}

function renderGrid(books) {
  const grid = document.getElementById('bookGrid');
  if (!books.length) {
    grid.innerHTML = '<p class="empty-state">📭 No books found. Try a different search!</p>';
    return;
  }
  grid.innerHTML = books.map(b => `
    <div class="book-card">
      <div class="book-spine ${spineColor(b.id)}">📖</div>
      <h4>${b.title}</h4>
      <p class="book-author">by ${b.author}</p>
      <p class="book-owner">👤 ${b.owner.name} · ${b.owner.department || 'College'}</p>
      <div class="book-meta">
        ${b.subject  ? `<span class="tag subject">${b.subject}</span>`   : ''}
        ${b.edition  ? `<span class="tag edition">${b.edition} ed.</span>` : ''}
        <span class="tag condition">${b.condition}</span>
        <span class="tag status-${b.status.toLowerCase()}">${b.status}</span>
      </div>
      ${b.description ? `<p style="font-size:.82rem;color:var(--muted);margin-bottom:.75rem;">${b.description}</p>` : ''}
      <button class="btn-request"
        onclick="openModal(${b.id}, '${b.title.replace(/'/g,"\\'")}', this)"
        ${b.status !== 'AVAILABLE' ? 'disabled' : ''}>
        ${b.status === 'AVAILABLE' ? '🤝 Request Book' : '⏳ Unavailable'}
      </button>
    </div>
  `).join('');
}

function openModal(bookId, title) {
  pendingBookId = bookId;
  document.getElementById('modalBookTitle').textContent = `"${title}"`;
  document.getElementById('reqMessage').value = '';
  document.getElementById('modal').classList.remove('hidden');
}

function closeModal() {
  document.getElementById('modal').classList.add('hidden');
  pendingBookId = null;
}

async function submitRequest() {
  const message = document.getElementById('reqMessage').value.trim();
  try {
    const res = await fetch(`${API_BASE}/requests`, {
      method: 'POST',
      headers: authHeaders(),
      body: JSON.stringify({ bookId: pendingBookId, message })
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'Request failed');
    closeModal();
    loadBooks();
    alert('✅ Request sent! The owner will respond soon.');
  } catch (err) {
    alert('Error: ' + err.message);
  }
}

// Allow Enter key in search
document.getElementById('searchInput')?.addEventListener('keydown', e => {
  if (e.key === 'Enter') doSearch();
});

loadBooks();
