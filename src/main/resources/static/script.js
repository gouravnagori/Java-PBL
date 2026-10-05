/**
 * Legal Document Analyser & Advisor - Client UI Script
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module Lead)
 * Updated Week 14: Polish toasts, fallback mock handlers, responsive rendering
 */

document.addEventListener('DOMContentLoaded', () => {
    initDashboard();

    const fileInput = document.getElementById('file-upload-input');
    const fileNameDisplay = document.getElementById('selected-file-name');
    const uploadBtn = document.getElementById('btn-upload-submit');
    const refreshBtn = document.getElementById('btn-refresh-analytics');

    if (fileInput) {
        fileInput.addEventListener('change', (e) => {
            if (e.target.files.length > 0) {
                fileNameDisplay.textContent = e.target.files[0].name;
            } else {
                fileNameDisplay.textContent = 'No file chosen';
            }
        });
    }

    if (uploadBtn) {
        uploadBtn.addEventListener('click', handleFileUpload);
    }

    if (refreshBtn) {
        refreshBtn.addEventListener('click', () => {
            showToast('Refreshing dashboard metrics...');
            loadDashboardAnalytics();
            loadReportSummaries();
        });
    }
});

function initDashboard() {
    loadDashboardAnalytics();
    loadReportSummaries();
}

async function loadDashboardAnalytics() {
    try {
        const response = await fetch('/api/analytics/dashboard');
        if (!response.ok) return;

        const result = await response.json();
        if (result.success && result.data) {
            const data = result.data;
            document.getElementById('kpi-total-docs').textContent = data.totalDocumentsProcessed || 0;
            document.getElementById('kpi-total-clauses').textContent = data.totalClausesAnalyzed || 0;
            document.getElementById('kpi-high-risks').textContent = data.highRiskFlagsDetected || 0;
            document.getElementById('kpi-avg-score').textContent = (data.averageRiskScore || 0).toFixed(1);
        }
    } catch (err) {
        console.warn('Analytics API endpoint offline or returning fallback data:', err);
    }
}

async function loadReportSummaries() {
    const tableBody = document.getElementById('documents-table-body');
    if (!tableBody) return;

    try {
        const response = await fetch('/api/reports/user/default');
        if (!response.ok) return;

        const result = await response.json();
        if (result.success && Array.isArray(result.data) && result.data.length > 0) {
            tableBody.innerHTML = '';
            result.data.forEach(item => {
                const tr = document.createElement('tr');

                const badgeClass = getBadgeClass(item.riskLevel);
                tr.innerHTML = `
                    <td style="font-weight: 600;">${escapeHtml(item.fileName || 'Untitled')}</td>
                    <td>${escapeHtml(item.documentType || 'CONTRACT')}</td>
                    <td>${(item.riskScore || 0).toFixed(1)} / 100</td>
                    <td><span class="badge ${badgeClass}">${escapeHtml(item.riskLevel || 'LOW')}</span></td>
                    <td>${item.totalClauses || 0}</td>
                    <td>
                        <button class="btn-secondary" style="padding: 4px 12px; font-size: 0.8rem;" onclick="downloadPdfReport('${item.documentId}')">
                            📄 Download PDF
                        </button>
                    </td>
                `;
                tableBody.appendChild(tr);
            });
        }
    } catch (err) {
        console.warn('Report summary fetch error:', err);
    }
}

async function handleFileUpload() {
    const fileInput = document.getElementById('file-upload-input');
    if (!fileInput || fileInput.files.length === 0) {
        showToast('Please select a legal document file (.pdf, .docx, .txt, image) to upload.');
        return;
    }

    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    showToast('Uploading contract & initiating legal analysis...');

    try {
        const response = await fetch('/api/documents/upload', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            showToast('Document uploaded successfully! Analysis initiated.');
            loadDashboardAnalytics();
            loadReportSummaries();
        } else {
            showToast('Failed to upload document. Please ensure valid format.');
        }
    } catch (err) {
        console.error('Upload error:', err);
        showToast('Upload request dispatched.');
    }
}

function downloadPdfReport(documentId) {
    showToast('Generating PDF audit report download...');
    window.open(`/api/reports/${documentId}/pdf`, '_blank');
}

function showToast(message) {
    const toast = document.createElement('div');
    toast.className = 'toast-banner';
    toast.textContent = message;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3500);
}

function getBadgeClass(riskLevel) {
    switch ((riskLevel || '').toUpperCase()) {
        case 'CRITICAL': return 'badge-critical';
        case 'HIGH': return 'badge-high';
        case 'MEDIUM': return 'badge-medium';
        default: return 'badge-low';
    }
}

function escapeHtml(str) {
    return str.replace(/&/g, "&amp;")
              .replace(/</g, "&lt;")
              .replace(/>/g, "&gt;")
              .replace(/"/g, "&quot;")
              .replace(/'/g, "&#039;");
}
