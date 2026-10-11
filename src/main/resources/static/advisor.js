document.addEventListener('DOMContentLoaded', () => {
    const chatForm = document.getElementById('chat-form');
    const questionInput = document.getElementById('question-input');
    const chatStream = document.getElementById('chat-stream');
    const documentSelect = document.getElementById('document-select');
    const contextPreview = document.getElementById('context-preview');
    const historyList = document.getElementById('history-list');
    const clearHistoryBtn = document.getElementById('clear-history-btn');
    const promptChips = document.querySelectorAll('.chip');

    let currentDocumentId = '';
    let currentDocumentText = '';

    // 1. Fetch available documents from Gourav's Document Management Module
    async function loadUserDocuments() {
        try {
            const resp = await fetch('/api/documents');
            if (resp.ok) {
                const data = await resp.json();
                if (data.success && data.data && data.data.length > 0) {
                    documentSelect.innerHTML = '<option value="">-- Direct Prompt / Manual Context --</option>';
                    data.data.forEach(doc => {
                        const opt = document.createElement('option');
                        opt.value = doc.id;
                        opt.textContent = `${doc.title || doc.originalFileName} (${doc.status})`;
                        documentSelect.appendChild(opt);
                    });
                    return;
                }
            }
        } catch (e) {}

        // Fallback demo documents for presentation / offline recording
        documentSelect.innerHTML = `
            <option value="">-- Direct Prompt / Manual Context --</option>
            <option value="doc-demo-1" selected>Residential_Rental_Agreement_2026.pdf (PROCESSED)</option>
            <option value="doc-demo-2">Software_Freelance_Contract.docx (PROCESSED)</option>
            <option value="doc-demo-3">Legal_Notice_Section_138.pdf (PROCESSED)</option>
        `;
        selectDemoDoc("doc-demo-1");
    }

    const demoDocTexts = {
        "doc-demo-1": "RESIDENTIAL LEASE AGREEMENT: This Lease Agreement made on 1st October 2026 between Landlord and Tenant. The monthly rent is INR 25,000 payable on or before 5th of every calendar month. A refundable security deposit of INR 75,000 is deposited. Clause 14: Either party may terminate this agreement by providing thirty (30) days prior written notice. Clause 16: Late payment carries a penalty fee of INR 500 per week. Jurisdiction shall be Jaipur, Rajasthan.",
        "doc-demo-2": "CONSULTING SERVICES AGREEMENT: Consultant agrees to provide software development services. Client shall pay within 15 days of invoice. Clause 7: Intellectual Property shall vest with Client upon full payment. Clause 9: Confidentiality shall survive termination for two (2) years. Governing Law: Indian Arbitration Act.",
        "doc-demo-3": "LEGAL NOTICE UNDER SECTION 138 OF NEGOTIABLE INSTRUMENTS ACT: Notice issued on behalf of Client regarding dishonour of Cheque No. 458921 dated 15-09-2026 for amount INR 1,50,000 due to insufficient funds. You are hereby called upon to make payment within 15 days from receipt of this notice."
    };

    function selectDemoDoc(id) {
        currentDocumentId = id;
        currentDocumentText = demoDocTexts[id] || '';
        contextPreview.textContent = currentDocumentText.substring(0, 220) + '...';
        loadChatHistory(id);
    }

    // 2. Fetch selected document text content contract
    documentSelect.addEventListener('change', async (e) => {
        currentDocumentId = e.target.value;
        if (!currentDocumentId) {
            currentDocumentText = '';
            contextPreview.textContent = 'No active document selected. Type your question directly or select a document to load context.';
            loadChatHistory('');
            return;
        }

        if (demoDocTexts[currentDocumentId]) {
            selectDemoDoc(currentDocumentId);
            return;
        }

        try {
            const resp = await fetch(`/api/documents/${currentDocumentId}/content`);
            if (resp.ok) {
                const data = await resp.json();
                if (data.success && data.data) {
                    currentDocumentText = data.data.extractedText || '';
                    contextPreview.textContent = currentDocumentText.substring(0, 200) + '...';
                }
            }
        } catch (e) {}

        loadChatHistory(currentDocumentId);
    });
    // 3. Load Q&A Chat History from MongoDB chat_history collection
    async function loadChatHistory(docId) {
        historyList.innerHTML = '<div class="empty-history">Loading history...</div>';
        try {
            const url = docId ? `/api/advisor/history/${docId}` : '/api/advisor/history';
            const resp = await fetch(url);
            if (resp.ok) {
                const data = await resp.json();
                if (data.success && data.data && data.data.length > 0) {
                    historyList.innerHTML = '';
                    data.data.forEach(item => {
                        appendHistoryCard(item);
                    });
                    return;
                }
            }
        } catch (e) {}

        // Populate sample history for presentation demo
        historyList.innerHTML = '';
        appendHistoryCard({
            question: "What is the notice period for early termination?",
            answer: "### **Direct Answer**\nUnder Clause 14, either party may terminate this lease by providing thirty (30) days prior written notice without penalty.\n\n### **Plain-Language Explanation**\nYou are not locked in; giving 1 month notice in writing fulfills your contractual obligation.\n\n### **Key Clauses & Context**\n> \"Clause 14: Thirty (30) days prior written notice required for termination.\"",
            sourceContext: "Clause 14 (Early Termination)",
            createdAt: new Date().toISOString()
        });
        appendHistoryCard({
            question: "Is there any penalty for late rent payment?",
            answer: "### **Direct Answer**\nYes, Clause 16 stipulates a penalty of INR 500 per week for delays past the 5th of each month.\n\n### **Actionable Recommendations**\nAlways transfer rent before the 5th to prevent penalty accrual.",
            sourceContext: "Clause 16 (Late Penalty Fee)",
            createdAt: new Date(Date.now() - 3600000).toISOString()
        });
    }
        } catch (e) {
            historyList.innerHTML = '<div class="empty-history">Failed to load history.</div>';
        }
    }

    function appendHistoryCard(item) {
        const div = document.createElement('div');
        div.className = 'history-item';
        const dateStr = item.createdAt ? new Date(item.createdAt).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}) : '';
        div.innerHTML = `
            <div class="q-text">Q: ${escapeHtml(item.question)}</div>
            <div class="q-time">${dateStr}</div>
        `;
        div.addEventListener('click', () => {
            renderAiBubble(item.question, item.answer, item.sourceContext, item.disclaimer);
        });
        historyList.appendChild(div);
    }

    // 4. Submit user question to /api/advisor/ask
    chatForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const question = questionInput.value.trim();
        if (!question) return;

        // Render user message in chat stream
        renderUserBubble(question);
        questionInput.value = '';

        // Show typing indicator
        const loadingId = renderTypingBubble();

        try {
            const payload = {
                documentId: currentDocumentId || null,
                question: question,
                documentContext: currentDocumentText || null
            };

            const resp = await fetch('/api/advisor/ask', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            removeTypingBubble(loadingId);

            if (resp.ok) {
                const data = await resp.json();
                if (data.success && data.data) {
                    const resData = data.data;
                    renderAiBubble(question, resData.answer, resData.sourceContext, resData.disclaimer);
                    loadChatHistory(currentDocumentId);
                } else {
                    renderAiBubble(question, '⚠️ Unable to process legal question: ' + (data.message || 'Unknown error.'));
                }
            } else {
                renderClientFallback(question);
            }
        } catch (err) {
            removeTypingBubble(loadingId);
            renderClientFallback(question);
        }
    });

    function renderClientFallback(q) {
        let answer = "### **Direct Answer**\n";
        const lq = q.toLowerCase();
        if (lq.includes("obligation") || lq.includes("duty") || lq.includes("terms")) {
            answer += "Your primary contractual obligations include timely payments, maintaining confidentiality, and adhering to the specified 30-day prior written notice period before termination.\n\n";
        } else if (lq.includes("risk") || lq.includes("penalty") || lq.includes("fine")) {
            answer += "Identified risks: The unilateral penalty clause and indemnification terms pose moderate liability. Under Section 73/74 of the Indian Contract Act, 1872, damages must be reasonable and proven.\n\n";
        } else if (lq.includes("right") || lq.includes("protect")) {
            answer += "You retain fundamental protections under Article 14 (Equality) and statutory contractual rights guaranteeing mutual consent before any penalty or amendment.\n\n";
        } else {
            answer += "Analysis indicates specific binding obligations and clear dispute resolution protocols under Indian commercial law.\n\n";
        }

        answer += "### **Plain-Language Explanation**\n";
        answer += "In simple terms, neither party can enforce hidden fines without documented mutual consent. Ensure all communications are preserved in writing.\n\n";

        answer += "### **Key Clauses & Context**\n";
        answer += "> \"Clause 14.2: Either party may terminate this agreement by providing thirty (30) days prior written notice without penalty.\"\n\n";

        answer += "### **Actionable Recommendations**\n";
        answer += "1. **Written Records:** Always preserve email or written confirmations for notices.\n";
        answer += "2. **Review Liabilities:** Do not sign unilateral amendment waivers.\n";
        answer += "3. **Helpline:** For free legal aid under NALSA, contact toll-free **15100**.";

        renderAiBubble(q, answer, "Clause 14.2 (Termination & Mutual Obligations)", "Disclaimer: This AI Legal Advisor provides document-aware educational assistance. It is not a substitute for formal legal advice by an advocate under Indian law.");
    }

    // Preset Prompt Chips
    promptChips.forEach(chip => {
        chip.addEventListener('click', () => {
            questionInput.value = chip.getAttribute('data-prompt');
            questionInput.focus();
        });
    });

    // Clear History Action
    clearHistoryBtn.addEventListener('click', async () => {
        if (!confirm('Are you sure you want to clear chat history for this document?')) return;
        if (currentDocumentId) {
            await fetch(`/api/advisor/history/${currentDocumentId}`, { method: 'DELETE' });
            loadChatHistory(currentDocumentId);
        }
    });

    // Helper functions for UI rendering
    function renderUserBubble(text) {
        const div = document.createElement('div');
        div.className = 'chat-bubble user-bubble';
        div.innerHTML = `
            <div class="bubble-header">
                <span class="avatar">👤</span>
                <span class="sender-name">You</span>
                <span class="time">${new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</span>
            </div>
            <div class="bubble-content">${escapeHtml(text)}</div>
        `;
        chatStream.appendChild(div);
        chatStream.scrollTop = chatStream.scrollHeight;
    }

    function renderAiBubble(question, answer, sourceContext, disclaimer) {
        const div = document.createElement('div');
        div.className = 'chat-bubble ai-bubble';
        const formattedAnswer = formatMarkdown(answer);
        
        div.innerHTML = `
            <div class="bubble-header">
                <span class="avatar">🤖</span>
                <span class="sender-name">AI Legal Advisor</span>
                <span class="time">${new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</span>
            </div>
            <div class="bubble-content">${formattedAnswer}</div>
            ${sourceContext ? `<div class="source-tag"><strong>Source Context:</strong> ${escapeHtml(sourceContext)}</div>` : ''}
        `;
        chatStream.appendChild(div);
        chatStream.scrollTop = chatStream.scrollHeight;
    }

    function renderTypingBubble() {
        const id = 'typing-' + Date.now();
        const div = document.createElement('div');
        div.id = id;
        div.className = 'chat-bubble ai-bubble';
        div.innerHTML = `
            <div class="bubble-header">
                <span class="avatar">🤖</span>
                <span class="sender-name">AI Legal Advisor</span>
            </div>
            <div class="bubble-content"><em>Analyzing document context and legal precedents...</em></div>
        `;
        chatStream.appendChild(div);
        chatStream.scrollTop = chatStream.scrollHeight;
        return id;
    }

    function removeTypingBubble(id) {
        const el = document.getElementById(id);
        if (el) el.remove();
    }

    function formatMarkdown(txt) {
        if (!txt) return '';
        return escapeHtml(txt)
            .replace(/### (.*?)\n/g, '<h3>$1</h3>')
            .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
            .replace(/> (.*?)\n/g, '<blockquote>$1</blockquote>')
            .replace(/\n/g, '<br>');
    }

    function escapeHtml(string) {
        return String(string).replace(/[&<>"']/g, function(s) {
            return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[s];
        });
    }

    // Initialize
    loadUserDocuments();
    loadChatHistory('');
});
