// js/my-books.js
requireAuth();

let allMyBooks = [];

async function loadAndStore() {
  const res = await fetch(`${API_BASE}/books/mine`, { headers: authHeaders() });
  if (res.status === 401) { logout(); return; }
  allMyBooks = await res.json();
  renderMyBooks(allMyBooks);
}

function renderMyBooks(books) {
  const list = document.getElementById('myBookList');
  if (!books.length) {
    list.innerHTML = '<p class="empty-state">📚 You haven\'t listed any books yet. Add one above!</p>';
    return;
  }
  list.innerHTML = books.map(b => `
    <div class="book-row">
      <div class="book-row-icon ${spineColor(b.id)}">📚</div>
      <div class="book-row-info">
        <h4>${b.title}</h4>
        <p>${b.author}${b.edition ? ` · ${b.edition} ed.` : ''}${b.subject ? ` · ${b.subject}` : ''}</p>
        <div class="book-meta" style="margin-top:.4rem;">
          <span class="tag condition">${b.condition}</span>
          <span class="tag status-${b.status.toLowerCase()}">${b.status}</span>
        </div>
      </div>
      <div class="book-row-actions">
        <button class="btn-icon edit"   onclick="editBook(${b.id})"   title="Edit">✏️</button>
        <button class="btn-icon delete" onclick="deleteBook(${b.id})" title="Remove">🗑️</button>
      </div>
    </div>
  `).join('');
}

document.getElementById('bookForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const id = document.getElementById('bookId').value;
  const body = {
    title:       document.getElementById('bTitle').value,
    author:      document.getElementById('bAuthor').value,
    subject:     document.getElementById('bSubject').value,
    edition:     document.getElementById('bEdition').value,
    condition:   document.getElementById('bCondition').value,
    description: document.getElementById('bDesc').value
  };
  const url    = id ? `${API_BASE}/books/${id}` : `${API_BASE}/books`;
  const method = id ? 'PUT' : 'POST';
  try {
    const res = await fetch(url, { method, headers: authHeaders(), body: JSON.stringify(body) });
    if (!res.ok) { const d = await res.json(); throw new Error(d.message || 'Save failed'); }
    resetForm();
    loadAndStore();
  } catch (err) {
    showAlert(err.message);
  }
});

function editBook(id) {
  const b = allMyBooks.find(x => x.id === id);
  if (!b) return;
  document.getElementById('bookId').value     = b.id;
  document.getElementById('bTitle').value     = b.title;
  document.getElementById('bAuthor').value    = b.author;
  document.getElementById('bSubject').value   = b.subject   || '';
  document.getElementById('bEdition').value   = b.edition   || '';
  document.getElementById('bCondition').value = b.condition;
  document.getElementById('bDesc').value      = b.description || '';
  document.getElementById('formTitle').textContent  = 'Edit Book';
  document.getElementById('bookSubmit').textContent = 'Update Book';
  document.getElementById('cancelEdit').classList.remove('hidden');
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

async function deleteBook(id) {
  if (!confirm('Remove this book from listings?')) return;
  await fetch(`${API_BASE}/books/${id}`, { method: 'DELETE', headers: authHeaders() });
  loadAndStore();
}

function resetForm() {
  document.getElementById('bookForm').reset();
  document.getElementById('bookId').value = '';
  document.getElementById('formTitle').textContent  = 'List a New Book';
  document.getElementById('bookSubmit').textContent = 'List Book';
  document.getElementById('cancelEdit').classList.add('hidden');
}

loadAndStore();
