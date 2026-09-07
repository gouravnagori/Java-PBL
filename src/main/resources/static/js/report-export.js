/**
 * PDF Export and Audit Report Modal Component
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module Lead)
 */

class ReportExportManager {
    constructor() {
        this.activeModal = null;
    }

    async openReportModal(documentId) {
        try {
            const res = await fetch(`/api/reports/${documentId}`);
            if (!res.ok) throw new Error('Failed to fetch report data');
            const data = (await res.json()).data;
            this.renderModal(data);
        } catch (err) {
            console.error('Report modal error:', err);
            alert('Unable to load report details: ' + err.message);
        }
    }

    renderModal(report) {
        if (this.activeModal) {
            this.activeModal.remove();
        }

        const modalOverlay = document.createElement('div');
        modalOverlay.className = 'report-modal-overlay';
        modalOverlay.style.cssText = `
            position: fixed; top: 0; left: 0; right: 0; bottom: 0;
            background: rgba(0, 0, 0, 0.75); backdrop-filter: blur(8px);
            display: flex; align-items: center; justify-content: center;
            z-index: 1000; padding: 20px;
        `;

        const modalBody = document.createElement('div');
        modalBody.className = 'report-modal-content';
        modalBody.style.cssText = `
            background: #121826; border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 16px; width: 100%; max-width: 700px; max-height: 85vh;
            overflow-y: auto; padding: 32px; color: #f8fafc; display: flex;
            flex-direction: column; gap: 20px; box-shadow: 0 20px 40px rgba(0,0,0,0.6);
        `;

        modalBody.innerHTML = `
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <h2 style="font-size: 1.4rem; font-weight: 700;">Legal Audit Report Preview</h2>
                <button id="close-modal-btn" style="background: transparent; border: none; color: #94a3b8; font-size: 1.5rem; cursor: pointer;">&times;</button>
            </div>
            
            <div style="background: rgba(255, 255, 255, 0.04); padding: 16px; border-radius: 8px;">
                <p><strong>Document:</strong> ${this.escape(report.documentTitle)}</p>
                <p><strong>Risk Category:</strong> <span class="badge badge-${report.riskCategory ? report.riskCategory.toLowerCase() : 'low'}">${this.escape(report.riskCategory)}</span></p>
                <p><strong>Risk Score:</strong> ${report.overallRiskScore} / 100</p>
            </div>

            <div>
                <h3 style="font-size: 1.1rem; font-weight: 600; margin-bottom: 8px;">Executive Summary</h3>
                <p style="color: #cbd5e1; font-size: 0.95rem; line-height: 1.5;">${this.escape(report.executiveSummary)}</p>
            </div>

            <div>
                <h3 style="font-size: 1.1rem; font-weight: 600; margin-bottom: 8px;">Key Risk Findings</h3>
                <ul style="padding-left: 20px; color: #f87171; font-size: 0.9rem;">
                    ${(report.keyRiskFindings || []).map(f => `<li>${this.escape(f)}</li>`).join('')}
                </ul>
            </div>

            <div style="display: flex; gap: 12px; justify-content: flex-end; margin-top: 12px;">
                <button id="download-pdf-modal-btn" class="btn-primary">Download PDF Executive Audit</button>
            </div>
        `;

        modalOverlay.appendChild(modalBody);
        document.body.appendChild(modalOverlay);
        this.activeModal = modalOverlay;

        document.getElementById('close-modal-btn').onclick = () => modalOverlay.remove();
        document.getElementById('download-pdf-modal-btn').onclick = () => {
            window.open(`/api/reports/${report.documentId}/pdf`, '_blank');
        };
    }

    escape(str) {
        if (!str) return '';
        return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
    }
}

window.reportExportManager = new ReportExportManager();
