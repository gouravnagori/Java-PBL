document.addEventListener('DOMContentLoaded', async () => {
    if (!isAuthenticated()) {
        window.location.href = '/auth.html';
        return;
    }

    await loadCases();

    const caseForm = document.getElementById('new-case-form');
    if (caseForm) {
        caseForm.addEventListener('submit', handleCreateCase);
    }
});

async function loadCases() {
    const listEl = document.getElementById('case-list');
    if (!listEl) return;

    try {
        const res = await fetchWithAuth('/api/cases');
        const json = await res.json();
        const cases = json.data || [];

        if (cases.length === 0) {
            listEl.innerHTML = `
                <div style="grid-column: 1/-1; text-align: center; padding: 3rem; color: #94a3b8; background: rgba(18,24,38,0.5); border-radius: 12px;">
                    <h3>No Legal Cases Created Yet</h3>
                    <p style="margin-top: 0.5rem;">Group your agreements, NDAs, and contracts under a dedicated legal case.</p>
                </div>
            `;
            return;
        }

        listEl.innerHTML = cases.map(c => `
            <div class="case-card">
                <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.75rem;">
                    <span style="font-size: 0.75rem; color: #6366f1; font-weight: 600;">${c.caseNumber}</span>
                    <span class="badge ${c.status === 'ACTIVE' ? 'badge-active' : c.status === 'FLAGGED_RISK' ? 'badge-risk' : 'badge-pending'}">${c.status}</span>
                </div>
                <h3 style="margin-bottom: 0.5rem; font-size: 1.15rem;">${c.title}</h3>
                <p style="color: #94a3b8; font-size: 0.85rem; margin-bottom: 1rem; line-height: 1.4;">${c.description || 'No case description provided.'}</p>
                <div style="font-size: 0.8rem; color: #64748b; border-top: 1px solid rgba(255,255,255,0.05); padding-top: 0.75rem; display: flex; justify-content: space-between;">
                    <span>Client: <strong>${c.clientName || 'N/A'}</strong></span>
                    <span>Category: <strong>${c.category}</strong></span>
                </div>
            </div>
        `).join('');

    } catch (err) {
        console.error('Failed to load cases', err);
    }
}

async function handleCreateCase(e) {
    e.preventDefault();
    const title = document.getElementById('case-title').value;
    const clientName = document.getElementById('case-client').value;
    const category = document.getElementById('case-category').value;
    const description = document.getElementById('case-desc').value;

    try {
        const res = await fetchWithAuth('/api/cases', {
            method: 'POST',
            body: JSON.stringify({ title, clientName, category, description })
        });
        if (res.ok) {
            document.getElementById('new-case-form').reset();
            closeModal();
            await loadCases();
        } else {
            const err = await res.json();
            alert(err.message || 'Error creating case');
        }
    } catch (e) {
        alert('Server connection error.');
    }
}

function openModal() {
    document.getElementById('case-modal').style.display = 'flex';
}

function closeModal() {
    document.getElementById('case-modal').style.display = 'none';
}
