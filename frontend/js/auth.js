// js/auth.js

const API_BASE = 'http://localhost:8080/api';
// Production: const API_BASE = 'https://bookbridge-backend.onrender.com/api';

function getToken()  { return localStorage.getItem('token'); }
function getUserId() { return localStorage.getItem('userId'); }

function requireAuth() {
  if (!getToken()) window.location.href = 'index.html';
}

function showAlert(msg, type = 'error') {
  const el = document.getElementById('alert');
  if (!el) return;
  el.textContent = msg;
  el.className = `alert ${type}`;
}

function logout() {
  localStorage.clear();
  window.location.href = 'index.html';
}

function authHeaders() {
  return { 'Authorization': `Bearer ${getToken()}`, 'Content-Type': 'application/json' };
}

const SPINE_COLORS = ['spine-1','spine-2','spine-3','spine-4','spine-5','spine-6','spine-7','spine-8'];
function spineColor(id) { return SPINE_COLORS[id % SPINE_COLORS.length]; }

document.addEventListener('DOMContentLoaded', () => {
  const logoutBtn = document.getElementById('logoutBtn');
  if (logoutBtn) logoutBtn.addEventListener('click', logout);

  const navUser = document.getElementById('navUser');
  if (navUser) {
    const name = localStorage.getItem('userName') || '';
    const dept = localStorage.getItem('userDept') || '';
    navUser.textContent = name + (dept ? ` · ${dept}` : '');
  }
});
