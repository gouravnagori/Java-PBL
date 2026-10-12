const API_BASE = '/api/auth';

function saveToken(token) {
    localStorage.setItem('auth_jwt_token', token);
}

function getToken() {
    return localStorage.getItem('auth_jwt_token');
}

function removeToken() {
    localStorage.removeItem('auth_jwt_token');
    localStorage.removeItem('auth_user_name');
    localStorage.removeItem('auth_user_email');
}

function isAuthenticated() {
    return !!getToken();
}

async function fetchWithAuth(url, options = {}) {
    const token = getToken();
    const headers = options.headers || {};
    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }
    headers['Content-Type'] = 'application/json';

    const res = await fetch(url, { ...options, headers });
    if (res.status === 401) {
        removeToken();
        window.location.href = '/auth.html';
        throw new Error('Unauthorized');
    }
    return res;
}

async function checkAuthStatus() {
    if (!isAuthenticated()) {
        updateNavbarAuth(null);
        return null;
    }

    try {
        const res = await fetchWithAuth('/api/auth/me');
        if (res.ok) {
            const data = await res.json();
            updateNavbarAuth(data.data);
            return data.data;
        } else {
            removeToken();
            updateNavbarAuth(null);
            return null;
        }
    } catch (e) {
        updateNavbarAuth(null);
        return null;
    }
}

function updateNavbarAuth(user) {
    const authSlot = document.getElementById('nav-auth-slot');
    if (!authSlot) return;

    if (user) {
        authSlot.innerHTML = `
            <span style="color: #94a3b8; font-size: 0.9rem;">👤 ${user.name}</span>
            <a href="/cases.html" class="nav-link">📂 Cases</a>
            <button onclick="logout()" class="btn-primary" style="padding: 0.4rem 0.9rem; font-size: 0.85rem; background: #334155;">Logout</button>
        `;
    } else {
        authSlot.innerHTML = `
            <a href="/auth.html" class="btn-primary">Sign In / Register</a>
        `;
    }
}

async function logout() {
    try {
        await fetchWithAuth('/api/auth/logout', { method: 'POST' });
    } catch (e) {}
    removeToken();
    window.location.href = '/auth.html';
}

document.addEventListener('DOMContentLoaded', checkAuthStatus);
