/**
 * LEXADVISOR — Professional Legal Review & Advisory Client Application
 * Features:
 * - High-impact Landing Page & Hero Section interactions
 * - Dual Language Explanations (Simple English & सरल हिंदी व्याख्या)
 * - Concrete Actionable Next Steps Checklist
 * - Authentication & Guest Reviewer mode
 * - Multi-format Document OCR & 13-Rule Risk Engine
 */

// Application State
const state = {
    token: localStorage.getItem('lex_token') || null,
    userName: localStorage.getItem('lex_user_name') || null,
    userEmail: localStorage.getItem('lex_user_email') || null,
    selectedFile: null,
    currentReport: null
};

// ==============================================================================
// Initialization & Navigation
// ==============================================================================
document.addEventListener('DOMContentLoaded', () => {
    initAuthUI();
    setupDropzone();
});

function initAuthUI() {
    const navSlot = document.getElementById('nav-user-slot');
    const authView = document.getElementById('auth-view');
    const workspaceView = document.getElementById('workspace-view');

    if (state.token && state.userName) {
        // Logged In State
        navSlot.innerHTML = `
            <span class="user-status-text">Signed in as <strong>${escapeHtml(state.userName)}</strong></span>
            <button class="btn-header-outline" onclick="handleLogout()">Sign Out</button>
        `;
        authView.style.display = 'none';
        workspaceView.style.display = 'block';
    } else {
        // Logged Out State
        navSlot.innerHTML = `
            <button class="btn-header-outline" onclick="showAuthView()">Sign In / Register</button>
        `;
        // Allow workspace to remain visible so landing page visitors can upload immediately
        authView.style.display = 'none';
        workspaceView.style.display = 'block';
    }
}

function showAuthView() {
    const authView = document.getElementById('auth-view');
    const workspaceView = document.getElementById('workspace-view');
    authView.style.display = 'block';
    authView.scrollIntoView({ behavior: 'smooth' });
}

function scrollToUpload() {
    const uploadCard = document.getElementById('upload-card');
    if (uploadCard) {
        uploadCard.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
}

function scrollToFeatures() {
    const features = document.getElementById('features-section');
    if (features) {
        features.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
}

function bypassAuthForReview() {
    state.userName = "Guest Reviewer";
    state.userEmail = "guest@lexadvisor.local";
    state.token = null;
    document.getElementById('auth-view').style.display = 'none';
    scrollToUpload();
    showToast("Continuing as Guest. You can upload and analyze any document freely.");
}

function switchAuthTab(mode) {
    const tabLogin = document.getElementById('tab-login');
    const tabRegister = document.getElementById('tab-register');
    const formLogin = document.getElementById('form-login');
    const formRegister = document.getElementById('form-register');
    const heading = document.getElementById('auth-heading');
    const subheading = document.getElementById('auth-subheading');
    const alertBox = document.getElementById('auth-alert');

    alertBox.style.display = 'none';

    if (mode === 'login') {
        tabLogin.classList.add('active');
        tabRegister.classList.remove('active');
        formLogin.style.display = 'block';
        formRegister.style.display = 'none';
        heading.textContent = 'Sign In to LexAdvisor';
        subheading.textContent = 'Access contract clause auditing and actionable legal advice.';
    } else {
        tabRegister.classList.add('active');
        tabLogin.classList.remove('active');
        formRegister.style.display = 'block';
        formLogin.style.display = 'none';
        heading.textContent = 'Create an Account';
        subheading.textContent = 'Register to audit contracts and retain private analysis reports.';
    }
}

// ==============================================================================
// Authentication Requests
// ==============================================================================
async function handleLoginSubmit(event) {
    event.preventDefault();
    const email = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value;
    const submitBtn = document.getElementById('btn-login-submit');

    submitBtn.disabled = true;
    submitBtn.textContent = 'Signing in...';

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const resData = await response.json();

        if (response.ok && resData.data && resData.data.token) {
            saveSession(resData.data.token, resData.data.name || email.split('@')[0], resData.data.email || email);
            showToast('Signed in successfully.');
            initAuthUI();
            scrollToUpload();
        } else {
            showAuthAlert(resData.message || 'Invalid email or password. Please try again.', 'error');
        }
    } catch (err) {
        console.error('Login error:', err);
        showAuthAlert('Unable to connect to authentication service.', 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Sign In';
    }
}

async function handleRegisterSubmit(event) {
    event.preventDefault();
    const name = document.getElementById('reg-name').value.trim();
    const email = document.getElementById('reg-email').value.trim();
    const password = document.getElementById('reg-password').value;
    const submitBtn = document.getElementById('btn-register-submit');

    submitBtn.disabled = true;
    submitBtn.textContent = 'Creating account...';

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password, role: 'ROLE_USER' })
        });

        const resData = await response.json();

        if (response.ok && resData.data && resData.data.token) {
            saveSession(resData.data.token, resData.data.name || name, resData.data.email || email);
            showToast('Account registered successfully.');
            initAuthUI();
            scrollToUpload();
        } else {
            showAuthAlert(resData.message || 'Registration failed. Email may already be in use.', 'error');
        }
    } catch (err) {
        console.error('Registration error:', err);
        showAuthAlert('Unable to complete registration. Please try again.', 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Create Account & Continue';
    }
}

function saveSession(token, name, email) {
    state.token = token;
    state.userName = name;
    state.userEmail = email;
    localStorage.setItem('lex_token', token);
    localStorage.setItem('lex_user_name', name);
    localStorage.setItem('lex_user_email', email);
}

function handleLogout() {
    state.token = null;
    state.userName = null;
    state.userEmail = null;
    localStorage.removeItem('lex_token');
    localStorage.removeItem('lex_user_name');
    localStorage.removeItem('lex_user_email');
    initAuthUI();
    showToast('You have been signed out.');
}

function showAuthAlert(message, type) {
    const alertBox = document.getElementById('auth-alert');
    alertBox.textContent = message;
    alertBox.className = `alert-message alert-${type}`;
    alertBox.style.display = 'block';
}

// ==============================================================================
// Document Dropzone & Selection
// ==============================================================================
function setupDropzone() {
    const dropzone = document.getElementById('dropzone');
    const fileInput = document.getElementById('file-input');

    if (!dropzone || !fileInput) return;

    ['dragenter', 'dragover'].forEach(name => {
        dropzone.addEventListener(name, (e) => {
            e.preventDefault();
            e.stopPropagation();
            dropzone.classList.add('drag-active');
        });
    });

    ['dragleave', 'drop'].forEach(name => {
        dropzone.addEventListener(name, (e) => {
            e.preventDefault();
            e.stopPropagation();
            dropzone.classList.remove('drag-active');
        });
    });

    dropzone.addEventListener('drop', (e) => {
        if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
            handleFileSelection(e.dataTransfer.files[0]);
        }
    });

    fileInput.addEventListener('change', (e) => {
        if (e.target.files && e.target.files.length > 0) {
            handleFileSelection(e.target.files[0]);
        }
    });
}

function triggerFileInput() {
    const fileInput = document.getElementById('file-input');
    if (fileInput) fileInput.click();
}

function handleFileSelection(file) {
    state.selectedFile = file;

    const fileBadge = document.getElementById('file-badge');
    const fileNameText = document.getElementById('file-name-text');
    const fileSizeText = document.getElementById('file-size-text');
    const startBtn = document.getElementById('btn-start-analysis');

    fileNameText.textContent = file.name;
    fileSizeText.textContent = formatBytes(file.size);
    fileBadge.style.display = 'flex';
    startBtn.disabled = false;
}

function clearSelectedFile(event) {
    if (event) event.stopPropagation();
    state.selectedFile = null;

    const fileInput = document.getElementById('file-input');
    if (fileInput) fileInput.value = '';

    const fileBadge = document.getElementById('file-badge');
    const startBtn = document.getElementById('btn-start-analysis');

    if (fileBadge) fileBadge.style.display = 'none';
    if (startBtn) startBtn.disabled = true;
}

// ==============================================================================
// Document Ingestion & Legal Analysis Pipeline Execution
// ==============================================================================
async function executeUploadAndAnalysis() {
    if (!state.selectedFile) {
        showToast('Please select a legal document to analyze.');
        return;
    }

    const uploadCard = document.getElementById('upload-card');
    const progressCard = document.getElementById('progress-card');
    const progressTitle = document.getElementById('progress-step-title');
    const progressDetail = document.getElementById('progress-step-detail');
    const reportView = document.getElementById('report-view');

    // Switch to in-progress state
    uploadCard.style.display = 'none';
    reportView.style.display = 'none';
    progressCard.style.display = 'block';

    const headers = {};
    if (state.token) {
        headers['Authorization'] = `Bearer ${state.token}`;
    }

    try {
        // Step 1: Ingest document and extract text
        progressTitle.textContent = '1/2 Extracting Document Clauses...';
        progressDetail.textContent = 'Extracting document text using PDFBox/POI and Groq Multimodal Vision OCR.';

        const formData = new FormData();
        formData.append('file', state.selectedFile);

        const uploadResponse = await fetch('/api/documents/upload', {
            method: 'POST',
            headers: headers,
            body: formData
        });

        if (!uploadResponse.ok) {
            throw new Error(`Upload failed with status ${uploadResponse.status}`);
        }

        const uploadResult = await uploadResponse.json();
        const documentId = uploadResult.data?.documentId;

        if (!documentId) {
            throw new Error('Server did not return a valid document ID.');
        }

        // Step 2: Trigger Legal Analysis & Risk Scoring
        progressTitle.textContent = '2/2 Formulating Hindi & English Explanations & Next Steps...';
        progressDetail.textContent = 'Auditing 13 legal risk rules, synthesizing bilingual plain-language summaries, and building your next steps plan.';

        const analysisResponse = await fetch(`/api/analysis/${documentId}`, {
            method: 'POST',
            headers: headers
        });

        if (!analysisResponse.ok) {
            throw new Error(`Legal analysis failed with status ${analysisResponse.status}`);
        }

        const analysisResult = await analysisResponse.json();
        const report = analysisResult.data;

        if (!report) {
            throw new Error('Analysis completed but report data was empty.');
        }

        state.currentReport = report;

        // Step 3: Render Report & Advisory Memo
        progressCard.style.display = 'none';
        renderLegalAdvisoryReport(state.selectedFile.name, report);
        reportView.style.display = 'block';
        reportView.scrollIntoView({ behavior: 'smooth', block: 'start' });
        showToast('Legal analysis, bilingual explanation & next steps generated successfully.');

    } catch (err) {
        console.error('Analysis execution failed:', err);
        progressCard.style.display = 'none';
        uploadCard.style.display = 'block';
        showToast('Analysis encountered an issue: ' + err.message);
    }
}

// ==============================================================================
// Render Legal Analysis & Advisory Memo
// ==============================================================================
function renderLegalAdvisoryReport(fileName, report) {
    // 1. Meta Details
    document.getElementById('rep-doc-title').textContent = fileName;
    document.getElementById('rep-doc-type').textContent = formatDocType(report.docType);
    document.getElementById('rep-doc-meta').textContent = `Audited on ${new Date().toLocaleDateString('en-US', {
        year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit'
    })}`;

    // 2. Risk Score & Badge
    const score = Math.round(report.compositeRiskScore || 0);
    document.getElementById('rep-score-num').textContent = `${score} / 100`;

    const badgeElem = document.getElementById('rep-score-badge');
    badgeElem.className = 'score-badge';

    if (score >= 70) {
        badgeElem.textContent = 'CRITICAL RISK';
        badgeElem.classList.add('critical');
    } else if (score >= 45) {
        badgeElem.textContent = 'HIGH RISK';
        badgeElem.classList.add('high');
    } else if (score >= 20) {
        badgeElem.textContent = 'MODERATE RISK';
        badgeElem.classList.add('medium');
    } else {
        badgeElem.textContent = 'LOW RISK / SAFE';
        badgeElem.classList.add('low');
    }

    // 3. Action Verdict Banner
    const verdictBanner = document.getElementById('rep-verdict-banner');
    const verdictTitle = document.getElementById('rep-verdict-title');
    const verdictDesc = document.getElementById('rep-verdict-desc');
    const verdictBadge = document.getElementById('rep-verdict-badge');

    verdictBanner.className = 'verdict-banner';
    verdictBadge.className = 'score-badge';

    const verdict = report.actionVerdict || (score >= 45 ? "DO NOT SIGN YET — RENEGOTIATE FLAGGED CLAUSES" : "SAFE TO SIGN — STANDARD TERMS VERIFIED");

    if (verdict.includes("DO NOT SIGN") || score >= 70) {
        verdictBanner.classList.add('critical');
        verdictBadge.classList.add('critical');
        verdictTitle.textContent = "🛑 ACTION REQUIRED: DO NOT SIGN IN CURRENT FORM";
        verdictDesc.textContent = "Critical liability risks or one-sided conditions were identified. Request the recommended counter-clauses below before signing.";
        verdictBadge.textContent = "RENEGOTIATE FIRST";
    } else if (verdict.includes("CAUTION") || score >= 35) {
        verdictBanner.classList.add('caution');
        verdictBadge.classList.add('high');
        verdictTitle.textContent = "⚠️ PROCEED WITH CAUTION: REVIEW FLAGGED CLAUSES";
        verdictDesc.textContent = "Several terms favor the other party. Consider clarifying notice and liability terms prior to execution.";
        verdictBadge.textContent = "AMEND KEY TERMS";
    } else {
        verdictBanner.classList.add('safe');
        verdictBadge.classList.add('low');
        verdictTitle.textContent = "✅ READY TO PROCEED: STANDARD TERMS VERIFIED";
        verdictDesc.textContent = "This agreement appears balanced with normal commercial terms. Follow standard pre-signing verification.";
        verdictBadge.textContent = "SAFE TO SIGN";
    }

    // 4. Executive Summary
    document.getElementById('rep-executive-summary').textContent =
        report.riskSummary || 'Document analyzed successfully. No adverse liability or unusual risk conditions detected.';

    // 5. Metric Boxes
    const totalClauses = report.totalClausesDetected || (report.clauses ? report.clauses.length : 0);
    const criticalRisks = report.criticalCount || 0;
    const highRisks = report.highCount || 0;
    const totalRisks = report.totalRisksFound || 0;
    const cleanClauses = Math.max(0, totalClauses - totalRisks);

    document.getElementById('stat-total-clauses').textContent = totalClauses;
    document.getElementById('stat-critical-risks').textContent = criticalRisks;
    document.getElementById('stat-high-risks').textContent = highRisks;
    document.getElementById('stat-clean-clauses').textContent = cleanClauses;

    // 6. Bilingual Explanations (English & Hindi)
    const engText = report.englishExplanation ||
        `This document is an agreement setting rules, responsibilities, and timelines between both parties. Review the flagged clauses carefully before signing.`;
    const hinText = report.hindiExplanation ||
        `यह दस्तावेज़ दोनों पक्षों के बीच काम की जिम्मेदारियों, नियमों और भुगतान की शर्तों को तय करता है। हस्ताक्षर करने से पहले इसमें दिए गए मुख्य जोखिमों और नियमों को ध्यान से समझें।`;

    document.getElementById('text-explanation-en').innerHTML = formatParagraphs(engText);
    document.getElementById('text-explanation-hi').innerHTML = formatParagraphs(hinText);

    // 7. Actionable Next Steps Checklist
    const nextStepsContainer = document.getElementById('next-steps-container');
    nextStepsContainer.innerHTML = '';

    const steps = (report.nextSteps && report.nextSteps.length > 0)
        ? report.nextSteps
        : [
            "Do Not Sign Immediately: Request the other party to modify flagged risk clauses in writing.",
            "Cap Your Liability: Ensure liability is capped at reasonable contract fees rather than remaining unlimited.",
            "Confirm Mutual Notice: Insist on a 30 to 60 days advance written notice period for contract termination.",
            "Final Pre-Signing Check: Verify names, payment figures, and milestones before executing."
        ];

    steps.forEach((stepText, index) => {
        const item = document.createElement('div');
        item.className = 'next-step-item';
        item.innerHTML = `
            <div class="next-step-badge">${index + 1}</div>
            <div class="next-step-text">${escapeHtml(stepText)}</div>
        `;
        nextStepsContainer.appendChild(item);
    });

    // 8. Build Advisory Cards List
    const advisoryList = document.getElementById('advisory-cards-list');
    advisoryList.innerHTML = '';

    const flaggedItems = [];
    if (report.clauses && Array.isArray(report.clauses)) {
        report.clauses.forEach(clause => {
            if (clause.risks && Array.isArray(clause.risks) && clause.risks.length > 0) {
                clause.risks.forEach(risk => {
                    flaggedItems.push({
                        risk: risk,
                        clauseText: risk.citedText || clause.rawText,
                        clauseType: clause.clauseType
                    });
                });
            }
        });
    }

    document.getElementById('count-risks-tab').textContent = flaggedItems.length;
    document.getElementById('count-all-tab').textContent = totalClauses;

    if (flaggedItems.length === 0) {
        advisoryList.innerHTML = `
            <div style="background-color: var(--color-bg-quote); border: 1px solid var(--color-border); padding: 24px; border-radius: 6px; text-align: center;">
                <p style="font-family: var(--font-serif); font-size: 16px; color: var(--color-risk-low-text); margin-bottom: 6px;">
                    ✓ No Adverse Risk Clauses Detected
                </p>
                <p style="font-size: 14px; color: var(--color-text-muted);">
                    All evaluated clauses conform to standard contractual provisions. No unlimited liabilities, unreasonable indemnifications, or unilateral termination terms were flagged.
                </p>
            </div>
        `;
    } else {
        flaggedItems.forEach(item => {
            const risk = item.risk;
            const severityClass = (risk.severity || 'medium').toLowerCase();

            const card = document.createElement('div');
            card.className = `advisory-card ${severityClass}`;

            card.innerHTML = `
                <div class="advisory-card-header">
                    <div class="advisory-title-wrap">
                        <span class="severity-pill ${severityClass}">${escapeHtml(risk.severity || 'RISK')}</span>
                        <div class="advisory-title">${escapeHtml(risk.riskTitle || 'Flagged Risk Provision')}</div>
                    </div>
                </div>
                <div class="advisory-card-body">
                    <!-- Quoted Clause Excerpt -->
                    <div class="clause-quote-box">
                        <div class="clause-quote-label">Contract Clause Excerpt</div>
                        "${escapeHtml(item.clauseText || 'Clause excerpt')}"
                    </div>

                    <!-- Risk Explanation -->
                    <div class="advisory-detail-group">
                        <div class="advisory-detail-label">Legal Risk Analysis</div>
                        <p class="advisory-explanation-text">
                            ${escapeHtml(risk.explanation || 'This provision may create legal or financial exposure for your party.')}
                        </p>
                    </div>

                    <!-- Actionable Legal Advice -->
                    <div class="actionable-advice-box">
                        <div class="actionable-advice-header">
                            ⚖ Recommended Action & Negotiation Counter-Clause
                        </div>
                        <div class="actionable-advice-text">
                            ${escapeHtml(risk.suggestion || 'Review this clause with legal counsel and request a bilateral limitation of liability or mutual notice period.')}
                        </div>
                    </div>
                </div>
            `;
            advisoryList.appendChild(card);
        });
    }

    // 9. Build All Clauses List
    const allClausesList = document.getElementById('all-clauses-list');
    allClausesList.innerHTML = '';

    if (report.clauses && Array.isArray(report.clauses)) {
        report.clauses.forEach((clause, index) => {
            const item = document.createElement('div');
            item.className = 'clause-item-card';

            const hasRisk = clause.hasRisk || (clause.risks && clause.risks.length > 0);
            const statusTag = hasRisk
                ? `<span style="color: var(--color-risk-critical-text); font-weight: 600;">[Risk Flagged]</span>`
                : `<span style="color: var(--color-risk-low-text); font-weight: 600;">[Standard]</span>`;

            item.innerHTML = `
                <div class="clause-item-header">
                    <span class="clause-item-num">Clause ${clause.clauseIndex || (index + 1)} ${statusTag}</span>
                    <span class="clause-item-type">${escapeHtml(clause.clauseType || 'GENERAL_PROVISION')}</span>
                </div>
                <p class="clause-item-text">${escapeHtml(clause.rawText || '')}</p>
            `;
            allClausesList.appendChild(item);
        });
    }
}

// ==============================================================================
// Language Switcher (English vs Hindi)
// ==============================================================================
function switchLanguage(lang) {
    const btnEn = document.getElementById('btn-lang-en');
    const btnHi = document.getElementById('btn-lang-hi');
    const panelEn = document.getElementById('panel-explanation-en');
    const panelHi = document.getElementById('panel-explanation-hi');

    if (lang === 'hi') {
        btnHi.classList.add('active');
        btnEn.classList.remove('active');
        panelHi.classList.add('active');
        panelEn.classList.remove('active');
    } else {
        btnEn.classList.add('active');
        btnHi.classList.remove('active');
        panelEn.classList.add('active');
        panelHi.classList.remove('active');
    }
}

function switchReportTab(tab) {
    const tabRisks = document.getElementById('tab-show-risks');
    const tabAll = document.getElementById('tab-show-all');
    const listRisks = document.getElementById('advisory-cards-list');
    const listAll = document.getElementById('all-clauses-list');

    if (tab === 'risks') {
        tabRisks.classList.add('active');
        tabAll.classList.remove('active');
        listRisks.style.display = 'block';
        listAll.style.display = 'none';
    } else {
        tabAll.classList.add('active');
        tabRisks.classList.remove('active');
        listAll.style.display = 'block';
        listRisks.style.display = 'none';
    }
}

function resetToUploadView() {
    clearSelectedFile();
    document.getElementById('report-view').style.display = 'none';
    document.getElementById('progress-card').style.display = 'none';
    document.getElementById('upload-card').style.display = 'block';
    scrollToUpload();
}

// ==============================================================================
// Utility Functions
// ==============================================================================
function formatDocType(type) {
    if (!type) return 'LEGAL CONTRACT';
    return type.toString()
        .replace(/_/g, ' ')
        .toLowerCase()
        .replace(/\b\w/g, c => c.toUpperCase());
}

function formatBytes(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
}

function formatParagraphs(text) {
    if (!text) return '';
    return text.split('\n\n').map(p => `<p style="margin-bottom: 10px;">${escapeHtml(p)}</p>`).join('');
}

function escapeHtml(str) {
    if (!str) return '';
    return str.toString()
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function showToast(message) {
    const existing = document.querySelector('.toast-notice');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.className = 'toast-notice';
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transition = 'opacity 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}
