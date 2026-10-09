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
                if (data.success && data.data) {
                    documentSelect.innerHTML = '<option value="">-- Direct Prompt / Manual Context --</option>';
                    data.data.forEach(doc => {
                        const opt = document.createElement('option');
                        opt.value = doc.id;
                        opt.textContent = `${doc.title || doc.originalFileName} (${doc.status})`;
                        documentSelect.appendChild(opt);
                    });
                }
            }
        } catch (e) {
            console.log('Document service offline or unreachable:', e);
        }
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

        try {
            const resp = await fetch(`/api/documents/${currentDocumentId}/content`);
            if (resp.ok) {
                const data = await resp.json();
                if (data.success && data.data) {
                    currentDocumentText = data.data.extractedText || '';
                    contextPreview.textContent = currentDocumentText.substring(0, 200) + '...';
                }
            }
        } catch (e) {
            console.error('Error loading document content:', e);
        }

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
            historyList.innerHTML = '<div class="empty-history">No prior Q&A recorded.</div>';
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
                renderAiBubble(question, '⚠️ Server error. Invoking safe fallback handler for legal question.');
            }
        } catch (err) {
            removeTypingBubble(loadingId);
            renderAiBubble(question, '⚠️ Communication network error. Please try again.');
        }
    });

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
