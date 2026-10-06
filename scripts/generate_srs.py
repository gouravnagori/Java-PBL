import os
import sys
from reportlab.lib import colors
from reportlab.lib.pagesizes import letter
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, KeepTogether, HRFlowable
)
from reportlab.pdfgen import canvas

class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super(NumberedCanvas, self).__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super(NumberedCanvas, self).showPage()
        super(NumberedCanvas, self).save()

    def draw_page_decorations(self, page_count):
        self.saveState()
        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#64748B"))
        
        # Header (pages > 1)
        if self._pageNumber > 1:
            self.drawString(54, 750, "Software Requirements Specification | Legal Document Analyser & Adviser")
            self.setStrokeColor(colors.HexColor("#CBD5E1"))
            self.setLineWidth(0.5)
            self.line(54, 744, 558, 744)

        # Footer (all pages)
        self.setStrokeColor(colors.HexColor("#E2E8F0"))
        self.setLineWidth(0.5)
        self.line(54, 45, 558, 45)
        
        page_text = f"Page {self._pageNumber} of {page_count}"
        self.drawRightString(558, 32, page_text)
        self.drawString(54, 32, "CONFIDENTIAL — Academic Project (Advanced Java PBL)")
        self.restoreState()

def generate_srs_pdf(output_path):
    os.makedirs(os.path.dirname(os.path.abspath(output_path)), exist_ok=True)
    doc = SimpleDocTemplate(
        output_path,
        pagesize=letter,
        leftMargin=54,
        rightMargin=54,
        topMargin=54,
        bottomMargin=54
    )

    styles = getSampleStyleSheet()
    
    # Custom styles
    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=20,
        leading=24,
        textColor=colors.HexColor('#0F172A'),
        alignment=1, # Centered
        spaceAfter=6
    )
    
    subtitle_style = ParagraphStyle(
        'DocSubTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=13,
        leading=16,
        textColor=colors.HexColor('#1E40AF'),
        alignment=1,
        spaceAfter=15
    )

    h1_style = ParagraphStyle(
        'Heading1_Custom',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=12,
        leading=16,
        textColor=colors.HexColor('#0F172A'),
        spaceBefore=12,
        spaceAfter=6,
        keepWithNext=True
    )

    h2_style = ParagraphStyle(
        'Heading2_Custom',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=10,
        leading=14,
        textColor=colors.HexColor('#1E3A8A'),
        spaceBefore=8,
        spaceAfter=4,
        keepWithNext=True
    )

    body_style = ParagraphStyle(
        'Body_Custom',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9,
        leading=13,
        textColor=colors.HexColor('#334155'),
        spaceAfter=4
    )

    bullet_style = ParagraphStyle(
        'Bullet_Custom',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9,
        leading=13,
        textColor=colors.HexColor('#334155'),
        leftIndent=12,
        firstLineIndent=-8,
        spaceAfter=3
    )

    tbl_header_style = ParagraphStyle(
        'TableHeader',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8.5,
        leading=11,
        textColor=colors.HexColor('#FFFFFF'),
        alignment=0
    )

    tbl_cell_style = ParagraphStyle(
        'TableCell',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8,
        leading=11,
        textColor=colors.HexColor('#1E293B')
    )

    tbl_cell_bold = ParagraphStyle(
        'TableCellBold',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8,
        leading=11,
        textColor=colors.HexColor('#0F172A')
    )

    story = []

    # Title Banner
    story.append(Spacer(1, 10))
    story.append(Paragraph("Software Requirements Specification", title_style))
    story.append(Paragraph("Legal Document Analyser and Adviser", subtitle_style))
    story.append(HRFlowable(width="100%", thickness=1.5, color=colors.HexColor('#1E40AF'), spaceBefore=0, spaceAfter=14))

    # 1. Introduction
    story.append(Paragraph("1. Introduction", h1_style))
    
    story.append(Paragraph("1.1 Purpose", h2_style))
    story.append(Paragraph(
        "This document specifies the requirements for the <b>Legal Document Analyser and Adviser</b>, a web-based system built entirely on the Java platform. Users upload legal documents (contracts, agreements, notices, policies), receive plain-language summaries, identify key clauses and risks, and get general guidance on next steps. It is intended for developers, testers, project guides and evaluators.",
        body_style
    ))

    story.append(Paragraph("1.2 Scope", h2_style))
    story.append(Paragraph(
        "The system accepts legal documents in common formats, extracts their text, analyses it using Natural Language Processing (NLP) and a Large Language Model (LLM), and presents structured results: summary, clause breakdown, risk highlights, glossary of legal terms and suggested actions. It also provides a question-and-answer adviser so users can ask about their document. The system provides informational assistance only and does not replace a qualified lawyer.",
        body_style
    ))

    story.append(Paragraph("1.3 Definitions and Abbreviations", h2_style))
    abbreviations = [
        "<b>SRS:</b> Software Requirements Specification",
        "<b>NLP:</b> Natural Language Processing",
        "<b>LLM:</b> Large Language Model",
        "<b>OCR:</b> Optical Character Recognition",
        "<b>RAG:</b> Retrieval-Augmented Generation",
        "<b>API:</b> Application Programming Interface",
        "<b>JWT:</b> JSON Web Token",
        "<b>JDK:</b> Java Development Kit",
        "<b>JPA:</b> Jakarta Persistence API (ORM specification, implemented by Hibernate)",
        "<b>MVC:</b> Model-View-Controller",
        "<b>DTO:</b> Data Transfer Object"
    ]
    for item in abbreviations:
        story.append(Paragraph(f"• &nbsp; {item}", bullet_style))

    story.append(Paragraph("1.4 Intended Audience", h2_style))
    story.append(Paragraph(
        "Students, small business owners, tenants, freelancers and general citizens who must understand legal documents without legal training; legal interns and paralegals seeking a quick first-pass review.",
        body_style
    ))

    # 2. Overall Description
    story.append(Paragraph("2. Overall Description", h1_style))
    
    story.append(Paragraph("2.1 Product Perspective", h2_style))
    story.append(Paragraph(
        "A standalone Java web application with a browser-based front end, a Spring Boot backend, an AI analysis engine and a relational database. It integrates with an external LLM service (called over HTTP from Java) and an OCR engine (Tesseract through the Tess4J library).",
        body_style
    ))

    story.append(Paragraph("2.2 Product Functions (Summary)", h2_style))
    story.append(Paragraph(
        "Document upload, text extraction, summarisation, clause identification, risk detection, legal-term explanation, question-and-answer advice, report download and history management.",
        body_style
    ))

    story.append(Paragraph("2.3 User Classes", h2_style))
    user_classes = [
        "<b>General User:</b> uploads documents, reads analysis, asks questions.",
        "<b>Registered User:</b> all of the above plus saved history and report downloads.",
        "<b>Administrator:</b> manages users, monitors usage, updates prompts, rules and knowledge base."
    ]
    for uc in user_classes:
        story.append(Paragraph(f"• &nbsp; {uc}", bullet_style))

    story.append(Paragraph("2.4 Operating Environment", h2_style))
    story.append(Paragraph(
        "Modern web browsers (Chrome, Edge, Firefox, Safari) on desktop and mobile. The server runs on JDK 17 or later (JDK 21 LTS recommended) on a Linux-based cloud or local server; the application is platform independent because it runs on the JVM.",
        body_style
    ))

    story.append(Paragraph("2.5 Design and Implementation Constraints", h2_style))
    story.append(Paragraph("See Section 13 for detailed security, regulatory, and technical constraints.", body_style))

    # 3. System Features
    story.append(Paragraph("3. System Features", h1_style))
    
    features_data = [
        [Paragraph("<b>ID</b>", tbl_header_style), Paragraph("<b>Feature</b>", tbl_header_style), Paragraph("<b>Description</b>", tbl_header_style)],
        [Paragraph("F1", tbl_cell_bold), Paragraph("User Authentication", tbl_cell_bold), Paragraph("Registration, login, logout, password reset (Spring Security)", tbl_cell_style)],
        [Paragraph("F2", tbl_cell_bold), Paragraph("Document Upload", tbl_cell_bold), Paragraph("Upload PDF, DOCX, TXT and scanned images (Spring MVC MultipartFile)", tbl_cell_style)],
        [Paragraph("F3", tbl_cell_bold), Paragraph("Text Extraction", tbl_cell_bold), Paragraph("Parse text with PDFBox and Apache POI; OCR for scanned files with Tess4J", tbl_cell_style)],
        [Paragraph("F4", tbl_cell_bold), Paragraph("Document Classification", tbl_cell_bold), Paragraph("Detect type: rental, employment, NDA, loan, sale, etc.", tbl_cell_style)],
        [Paragraph("F5", tbl_cell_bold), Paragraph("Summarisation", tbl_cell_bold), Paragraph("Short and detailed plain-language summaries", tbl_cell_style)],
        [Paragraph("F6", tbl_cell_bold), Paragraph("Clause Extraction", tbl_cell_bold), Paragraph("Identify parties, dates, obligations, payment, termination, jurisdiction", tbl_cell_style)],
        [Paragraph("F7", tbl_cell_bold), Paragraph("Risk Detection", tbl_cell_bold), Paragraph("Flag unfair, ambiguous, missing or high-risk clauses with severity", tbl_cell_style)],
        [Paragraph("F8", tbl_cell_bold), Paragraph("Legal Term Explainer", tbl_cell_bold), Paragraph("Simple definitions of jargon in context", tbl_cell_style)],
        [Paragraph("F9", tbl_cell_bold), Paragraph("Legal Adviser Chat", tbl_cell_bold), Paragraph("Ask questions about the document and get grounded answers", tbl_cell_style)],
        [Paragraph("F10", tbl_cell_bold), Paragraph("Suggestions", tbl_cell_bold), Paragraph("Recommended actions, questions to ask, clauses to negotiate", tbl_cell_style)],
        [Paragraph("F11", tbl_cell_bold), Paragraph("Report Generation", tbl_cell_bold), Paragraph("Download analysis as PDF (OpenPDF or iText)", tbl_cell_style)],
        [Paragraph("F12", tbl_cell_bold), Paragraph("History Management", tbl_cell_bold), Paragraph("View, search and delete past analyses", tbl_cell_style)],
        [Paragraph("F13", tbl_cell_bold), Paragraph("Multilingual Support", tbl_cell_bold), Paragraph("English and Hindi input and output (optional extension)", tbl_cell_style)],
        [Paragraph("F14", tbl_cell_bold), Paragraph("Admin Dashboard", tbl_cell_bold), Paragraph("User management, logs, analytics", tbl_cell_style)],
    ]
    
    t_features = Table(features_data, colWidths=[35, 125, 344])
    t_features.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor('#1E3A8A')),
        ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('VALIGN', (0, 0), (-1, -1), 'TOP'),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#CBD5E1')),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.HexColor('#FFFFFF'), colors.HexColor('#F8FAFC')]),
        ('TOPPADDING', (0, 0), (-1, -1), 4),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
    ]))
    story.append(t_features)
    story.append(Spacer(1, 8))

    # 4. Functional Requirements
    story.append(Paragraph("4. Functional Requirements", h1_style))
    fr_data = [
        [Paragraph("<b>ID</b>", tbl_header_style), Paragraph("<b>Requirement</b>", tbl_header_style)],
        [Paragraph("FR1", tbl_cell_bold), Paragraph("The system shall allow users to register with name, email and password.", tbl_cell_style)],
        [Paragraph("FR2", tbl_cell_bold), Paragraph("The system shall authenticate users and maintain secure sessions.", tbl_cell_style)],
        [Paragraph("FR3", tbl_cell_bold), Paragraph("The system shall accept PDF, DOCX, TXT, JPG and PNG uploads up to a configurable size limit (default 10 MB, set through spring.servlet.multipart.max-file-size).", tbl_cell_style)],
        [Paragraph("FR4", tbl_cell_bold), Paragraph("The system shall validate file type and size and reject unsupported or corrupt files with a clear message.", tbl_cell_style)],
        [Paragraph("FR5", tbl_cell_bold), Paragraph("The system shall extract text from digital documents and apply OCR to scanned ones.", tbl_cell_style)],
        [Paragraph("FR6", tbl_cell_bold), Paragraph("The system shall classify the document type and display it to the user.", tbl_cell_style)],
        [Paragraph("FR7", tbl_cell_bold), Paragraph("The system shall generate a summary in simple language.", tbl_cell_style)],
        [Paragraph("FR8", tbl_cell_bold), Paragraph("The system shall extract key clauses and key entities (parties, dates, amounts, duration, governing law).", tbl_cell_style)],
        [Paragraph("FR9", tbl_cell_bold), Paragraph("The system shall detect risky, one-sided or missing clauses and assign Low, Medium or High severity with an explanation.", tbl_cell_style)],
        [Paragraph("FR10", tbl_cell_bold), Paragraph("The system shall explain legal terms found in the document.", tbl_cell_style)],
        [Paragraph("FR11", tbl_cell_bold), Paragraph("The system shall answer user questions using the uploaded document as the primary context.", tbl_cell_style)],
        [Paragraph("FR12", tbl_cell_bold), Paragraph("The system shall provide suggested next steps and a disclaimer that output is not legal advice.", tbl_cell_style)],
        [Paragraph("FR13", tbl_cell_bold), Paragraph("The system shall allow users to export results as a PDF report.", tbl_cell_style)],
        [Paragraph("FR14", tbl_cell_bold), Paragraph("The system shall store analysis history for registered users and allow deletion at any time.", tbl_cell_style)],
        [Paragraph("FR15", tbl_cell_bold), Paragraph("The system shall show processing progress and handle failures with retry options.", tbl_cell_style)],
        [Paragraph("FR16", tbl_cell_bold), Paragraph("The system shall let administrators view users, activity logs and system statistics.", tbl_cell_style)],
        [Paragraph("FR17", tbl_cell_bold), Paragraph("The system shall let users give feedback (helpful or not helpful) on each analysis.", tbl_cell_style)],
    ]
    t_fr = Table(fr_data, colWidths=[38, 466])
    t_fr.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor('#1E3A8A')),
        ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('VALIGN', (0, 0), (-1, -1), 'TOP'),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#CBD5E1')),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.HexColor('#FFFFFF'), colors.HexColor('#F8FAFC')]),
        ('TOPPADDING', (0, 0), (-1, -1), 3.5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3.5),
    ]))
    story.append(t_fr)
    story.append(Spacer(1, 8))

    # 5. Non-Functional Requirements
    story.append(Paragraph("5. Non-Functional Requirements", h1_style))
    nfr_items = [
        "<b>Performance:</b> A document of up to 20 pages shall be analysed within 60 seconds under normal load; page loads under 3 seconds. Long-running analysis runs asynchronously (Spring @Async or an ExecutorService thread pool) so the UI is never blocked.",
        "<b>Scalability:</b> Support at least 100 concurrent users through horizontal scaling of stateless Spring Boot instances behind a load balancer.",
        "<b>Reliability and Availability:</b> Target 99% uptime; graceful handling of LLM or OCR service failure using timeouts, retries and fallbacks (for example Resilience4j).",
        "<b>Usability:</b> Clean interface, plain language, responsive design, minimal steps from upload to result.",
        "<b>Accuracy:</b> Extracted clauses and summaries shall be traceable to source text; the system shall not invent clauses.",
        "<b>Maintainability:</b> Layered, modular code with documented APIs (OpenAPI/Swagger), configurable prompts and rules stored outside the code.",
        "<b>Portability:</b> Runs on Windows, Linux and macOS because the JVM is platform independent; also packaged as a Docker image.",
        "<b>Accessibility:</b> Follow WCAG 2.1 AA guidelines where practical.",
        "<b>Compatibility:</b> Works on current versions of major browsers (Chrome, Edge, Firefox, Safari)."
    ]
    for nfr in nfr_items:
        story.append(Paragraph(f"• &nbsp; {nfr}", bullet_style))

    # 6. User Requirements
    story.append(Paragraph("6. User Requirements", h1_style))
    user_reqs = [
        "Users shall be able to upload a document and understand it without legal knowledge.",
        "Users shall be able to see which parts are risky and why.",
        "Users shall be able to ask follow-up questions in natural language.",
        "Users shall be able to save or download results.",
        "Users shall be assured that their documents remain private.",
        "Administrators shall be able to monitor and maintain the system without code changes."
    ]
    for ur in user_reqs:
        story.append(Paragraph(f"• &nbsp; {ur}", bullet_style))

    # 7. System Requirements
    story.append(Paragraph("7. System Requirements", h1_style))
    
    story.append(Paragraph("7.1 Hardware (Server)", h2_style))
    hw_server = [
        "<b>Processor:</b> 4 cores or higher",
        "<b>RAM:</b> 8 GB minimum (16 GB recommended); JVM heap configured through -Xms and -Xmx",
        "<b>Storage:</b> 50 GB SSD or more",
        "<b>Network:</b> stable broadband connection for LLM API access",
        "<b>GPU:</b> optional, needed only for hosting local models"
    ]
    for s in hw_server:
        story.append(Paragraph(f"• &nbsp; {s}", bullet_style))

    story.append(Paragraph("7.2 Hardware (Client)", h2_style))
    story.append(Paragraph("Any device with a modern browser, 2 GB RAM, internet connection.", body_style))

    story.append(Paragraph("7.3 Software", h2_style))
    sw_reqs = [
        "<b>Server OS:</b> Ubuntu 22.04 or equivalent (Windows or macOS for development)",
        "<b>Runtime:</b> JDK 17 or later (JDK 21 LTS recommended)",
        "<b>Build tool:</b> Apache Maven (or Gradle)",
        "<b>Application server:</b> embedded Tomcat provided by Spring Boot",
        "<b>Client:</b> Chrome, Edge, Firefox or Safari (latest two versions)"
    ]
    for sw in sw_reqs:
        story.append(Paragraph(f"• &nbsp; {sw}", bullet_style))

    # 8. External Interface Requirements
    story.append(Paragraph("8. External Interface Requirements", h1_style))
    
    story.append(Paragraph("8.1 User Interface", h2_style))
    story.append(Paragraph(
        "<b>Pages:</b> Home, Login/Register, Upload, Analysis Result (tabs for Summary, Clauses, Risks, Terms, Chat), History, Profile and Admin Dashboard. Risk levels use colour and text labels. The layout is responsive. Pages are rendered with Thymeleaf templates and Bootstrap (a React front end calling the REST API is an acceptable alternative).",
        body_style
    ))

    story.append(Paragraph("8.2 Hardware Interface", h2_style))
    story.append(Paragraph("No special hardware; standard input devices, optional camera or scanner for document images.", body_style))

    story.append(Paragraph("8.3 Software Interfaces", h2_style))
    sw_interfaces = [
        "<b>LLM provider API:</b> For summarisation, extraction and chat, called through Spring WebClient or the Java 11+ HttpClient (or the LangChain4j library).",
        "<b>OCR engine:</b> Tesseract accessed through Tess4J.",
        "<b>Database connector:</b> JDBC with Spring Data JPA and Hibernate.",
        "<b>PDF generation library for reports:</b> OpenPDF or iText.",
        "<b>Optional email service:</b> Spring Mail (JavaMail) for verification and password reset."
    ]
    for swi in sw_interfaces:
        story.append(Paragraph(f"• &nbsp; {swi}", bullet_style))

    story.append(Paragraph("8.4 Communication Interfaces", h2_style))
    story.append(Paragraph("HTTPS with REST and JSON between client and server; TLS 1.2 or higher; SMTP for email notifications.", body_style))

    # 9. Database Requirements
    story.append(Paragraph("9. Database Requirements", h1_style))
    
    story.append(Paragraph("9.1 Main Entities (JPA @Entity classes)", h2_style))
    entities = [
        "<b>User:</b> user_id, name, email, password_hash, role, created_at",
        "<b>Document:</b> doc_id, user_id, file_name, file_type, storage_path, uploaded_at, doc_type",
        "<b>Analysis:</b> analysis_id, doc_id, summary, status, created_at",
        "<b>Clause:</b> clause_id, analysis_id, clause_type, text, page_ref",
        "<b>Risk:</b> risk_id, analysis_id, clause_id, severity, explanation, suggestion",
        "<b>ChatMessage:</b> msg_id, analysis_id, role, content, timestamp",
        "<b>Feedback:</b> feedback_id, analysis_id, rating, comment",
        "<b>Log:</b> log_id, user_id, action, timestamp"
    ]
    for ent in entities:
        story.append(Paragraph(f"• &nbsp; {ent}", bullet_style))

    story.append(Paragraph("9.2 Relationships", h2_style))
    story.append(Paragraph(
        "One user has many documents (@OneToMany); one document has many analyses; one analysis has many clauses, risks and chat messages. Each relationship is mapped with JPA annotations and accessed through Spring Data repositories.",
        body_style
    ))

    story.append(Paragraph("9.3 Data Requirements", h2_style))
    story.append(Paragraph(
        "Passwords stored only as salted hashes; uploaded files encrypted at rest; retention period configurable; user-initiated deletion removes the document, analysis and chat data (cascade delete). Regular automated backups. Schema changes are versioned with Flyway or Liquibase.",
        body_style
    ))

    # 10. Software and Technology Requirements
    story.append(Paragraph("10. Software and Technology Requirements", h1_style))
    tech_data = [
        [Paragraph("<b>Layer</b>", tbl_header_style), Paragraph("<b>Technology</b>", tbl_header_style)],
        [Paragraph("Language and runtime", tbl_cell_bold), Paragraph("Java 17 or later (JDK 21 LTS recommended)", tbl_cell_style)],
        [Paragraph("Backend framework", tbl_cell_bold), Paragraph("Spring Boot (Spring MVC for REST controllers, Spring Security for authentication and authorisation)", tbl_cell_style)],
        [Paragraph("Frontend", tbl_cell_bold), Paragraph("Thymeleaf with HTML, CSS, JavaScript and Bootstrap; React optional", tbl_cell_style)],
        [Paragraph("AI / NLP", tbl_cell_bold), Paragraph("LLM API via LangChain4j or HttpClient; Apache OpenNLP or Stanford CoreNLP for sentence splitting, tokenising and entity recognition; embeddings for semantic search", tbl_cell_style)],
        [Paragraph("Vector store for RAG", tbl_cell_bold), Paragraph("PostgreSQL with pgvector, or Apache Lucene, or an in-memory store provided by LangChain4j", tbl_cell_style)],
        [Paragraph("Document processing", tbl_cell_bold), Paragraph("Apache PDFBox (PDF), Apache POI (DOCX), Tess4J (Tesseract OCR)", tbl_cell_style)],
        [Paragraph("Persistence", tbl_cell_bold), Paragraph("Spring Data JPA with Hibernate", tbl_cell_style)],
        [Paragraph("Database", tbl_cell_bold), Paragraph("PostgreSQL or MySQL", tbl_cell_style)],
        [Paragraph("Authentication", tbl_cell_bold), Paragraph("Spring Security with JWT (jjwt library), BCrypt password encoder", tbl_cell_style)],
        [Paragraph("Report generation", tbl_cell_bold), Paragraph("OpenPDF or iText", tbl_cell_style)],
        [Paragraph("Build tool", tbl_cell_bold), Paragraph("Maven or Gradle", tbl_cell_style)],
        [Paragraph("Version control", tbl_cell_bold), Paragraph("Git and GitHub", tbl_cell_style)],
        [Paragraph("Deployment", tbl_cell_bold), Paragraph("Executable JAR or Docker, cloud hosting or university server", tbl_cell_style)],
        [Paragraph("Testing", tbl_cell_bold), Paragraph("JUnit 5, Mockito, Spring Boot Test, Postman, Selenium", tbl_cell_style)],
        [Paragraph("API documentation", tbl_cell_bold), Paragraph("springdoc-openapi (Swagger UI)", tbl_cell_style)],
    ]
    t_tech = Table(tech_data, colWidths=[130, 374])
    t_tech.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor('#1E3A8A')),
        ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('VALIGN', (0, 0), (-1, -1), 'TOP'),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#CBD5E1')),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.HexColor('#FFFFFF'), colors.HexColor('#F8FAFC')]),
        ('TOPPADDING', (0, 0), (-1, -1), 3.5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3.5),
    ]))
    story.append(t_tech)
    story.append(Spacer(1, 8))

    # 11. System Architecture
    story.append(Paragraph("11. System Architecture", h1_style))
    story.append(Paragraph(
        "The application follows a layered architecture:<br/>"
        "1. <b>Presentation layer:</b> Thymeleaf views and REST controllers (@Controller, @RestController).<br/>"
        "2. <b>Service layer:</b> Business logic (@Service) for authentication, document handling, analysis, chat and reporting.<br/>"
        "3. <b>Repository layer:</b> Database access (@Repository, Spring Data JPA interfaces).<br/>"
        "4. <b>AI engine module:</b> Classes that call the LLM, run NLP steps and perform retrieval.<br/>"
        "5. <b>Database:</b> Relational store for users, documents, analyses and history.",
        body_style
    ))

    story.append(Paragraph("11.1 Suggested Package Structure", h2_style))
    pkg_data = [
        [Paragraph("<b>Package</b>", tbl_header_style), Paragraph("<b>Contents</b>", tbl_header_style)],
        [Paragraph("config", tbl_cell_bold), Paragraph("SecurityConfig, WebClientConfig, AsyncConfig, OpenApiConfig", tbl_cell_style)],
        [Paragraph("controller", tbl_cell_bold), Paragraph("AuthController, DocumentController, AnalysisController, ChatController, AdminController", tbl_cell_style)],
        [Paragraph("service", tbl_cell_bold), Paragraph("UserService, DocumentService, TextExtractionService, ClassificationService, AnalysisService, RiskService, ChatService, ReportService", tbl_cell_style)],
        [Paragraph("ai", tbl_cell_bold), Paragraph("LlmClient, PromptBuilder, ChunkingService, EmbeddingService, VectorStoreService, PromptInjectionGuard", tbl_cell_style)],
        [Paragraph("repository", tbl_cell_bold), Paragraph("UserRepository, DocumentRepository, AnalysisRepository, ClauseRepository, RiskRepository, ChatMessageRepository", tbl_cell_style)],
        [Paragraph("model", tbl_cell_bold), Paragraph("JPA entities and enums (Role, Severity, DocType, AnalysisStatus)", tbl_cell_style)],
        [Paragraph("dto", tbl_cell_bold), Paragraph("Request and response objects", tbl_cell_style)],
        [Paragraph("exception", tbl_cell_bold), Paragraph("Custom exceptions and a @ControllerAdvice global handler", tbl_cell_style)],
        [Paragraph("security", tbl_cell_bold), Paragraph("JwtUtil, JwtAuthFilter, UserDetailsServiceImpl", tbl_cell_style)],
    ]
    t_pkg = Table(pkg_data, colWidths=[90, 414])
    t_pkg.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor('#1E3A8A')),
        ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('VALIGN', (0, 0), (-1, -1), 'TOP'),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#CBD5E1')),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.HexColor('#FFFFFF'), colors.HexColor('#F8FAFC')]),
        ('TOPPADDING', (0, 0), (-1, -1), 3.5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3.5),
    ]))
    story.append(t_pkg)
    story.append(Spacer(1, 6))

    story.append(Paragraph("11.2 Java Concepts Demonstrated", h2_style))
    story.append(Paragraph(
        "The project is designed to exercise core and advanced Java topics: object-oriented design (inheritance, interfaces, polymorphism, for example a DocumentParser interface with PdfParser, DocxParser, TxtParser and ImageParser implementations), collections and generics, streams and lambdas, exception handling with custom exceptions, multithreading with ExecutorService and CompletableFuture, file I/O, JDBC and JPA, and annotations.",
        body_style
    ))

    # 12. System Workflow
    story.append(Paragraph("12. System Workflow", h1_style))
    workflows = [
        "1. The user registers or logs in.",
        "2. The user uploads a legal document.",
        "3. The system validates the file and stores it securely.",
        "4. Text is extracted (PDFBox, POI, or Tess4J OCR if the file is scanned).",
        "5. Text is cleaned and split into sections or chunks.",
        "6. The document type is classified.",
        "7. The AI engine generates the summary, extracts clauses and entities, and detects risks (executed asynchronously).",
        "8. Results are saved to the database and shown on the result page.",
        "9. The user asks questions; the system retrieves relevant chunks and generates grounded answers.",
        "10. The user downloads a report, gives feedback or deletes the document."
    ]
    for wf in workflows:
        story.append(Paragraph(wf, bullet_style))
    
    story.append(Spacer(1, 4))
    story.append(Paragraph(
        "<b>Data flow:</b> User → Web UI → Spring Controller → Service Layer → Document Processor → AI Engine (with vector store) → Repository → Database → Web UI → User.",
        body_style
    ))

    # 13. Security Requirements
    story.append(Paragraph("13. Security Requirements", h1_style))
    sec_items = [
        "All traffic shall be encrypted with HTTPS.",
        "Passwords shall be hashed with Spring Security's BCryptPasswordEncoder (or Argon2); sessions use JWT with expiry.",
        "Role-based access control (ROLE_USER, ROLE_ADMIN) separates users and administrators using Spring Security method and URL security.",
        "Users shall access only their own documents and analyses.",
        "Uploaded files shall be scanned and validated (content type, extension and size) to prevent malicious uploads.",
        "Input validation (Jakarta Bean Validation), output escaping and JPA parameterised queries shall prevent injection and XSS attacks; CSRF protection is enabled for browser forms.",
        "Prompt-injection safeguards shall prevent document content from overriding system instructions.",
        "API keys shall be held in environment variables or external configuration, never in source code.",
        "Rate limiting shall protect against abuse (for example Bucket4j).",
        "Documents sent to third-party AI services shall be minimised, and users informed of this.",
        "Audit logs shall record sensitive actions.",
        "The system shall comply with applicable data protection law, including India's Digital Personal Data Protection Act, 2023."
    ]
    for sec in sec_items:
        story.append(Paragraph(f"• &nbsp; {sec}", bullet_style))

    # 14. Constraints
    story.append(Paragraph("14. Constraints", h1_style))
    constraints = [
        "The system gives informational output only and is not a substitute for a licensed lawyer.",
        "Accuracy depends on text quality, especially for poor scans and handwriting.",
        "Dependence on third-party LLM availability, cost and rate limits.",
        "Context-length limits restrict very long documents; chunking is required.",
        "Legal interpretation varies by jurisdiction; initial coverage targets Indian law.",
        "Java NLP libraries are less extensive than Python's, so most language understanding is delegated to the LLM, with OpenNLP used for lightweight pre-processing.",
        "Project timeline, budget and team size limit scope."
    ]
    for c in constraints:
        story.append(Paragraph(f"• &nbsp; {c}", bullet_style))

    # 15. Assumptions
    story.append(Paragraph("15. Assumptions", h1_style))
    assumptions = [
        "Users have internet access and a modern browser.",
        "Uploaded documents are mostly in English; Hindi is optional.",
        "Documents are legible and not password-protected.",
        "The LLM and OCR services remain available and affordable.",
        "Users consent to automated analysis of their documents.",
        "Legal reference data used by the system is reasonably current.",
        "JDK 17 or later and Maven are installed in the development and deployment environments."
    ]
    for a in assumptions:
        story.append(Paragraph(f"• &nbsp; {a}", bullet_style))

    # 16. Future Scope
    story.append(Paragraph("16. Future Scope", h1_style))
    future_scope = [
        "Support for more regional languages and voice input",
        "Contract comparison and version difference detection",
        "Auto-drafting of legal documents and notices from templates",
        "Integration with Indian legal databases (acts, sections, case law)",
        "Citation of relevant statutes and judgments",
        "Lawyer marketplace for expert consultation",
        "E-signature and document tracking",
        "Mobile applications for Android (Java/Kotlin) and iOS",
        "Fine-tuned domain-specific legal models",
        "Deadline and renewal reminders extracted from contracts",
        "Offline or on-premise deployment for privacy-sensitive users"
    ]
    for f in future_scope:
        story.append(Paragraph(f"• &nbsp; {f}", bullet_style))

    # 17. Conclusion
    story.append(Paragraph("17. Conclusion", h1_style))
    story.append(Paragraph(
        "The Legal Document Analyser and Adviser aims to make legal documents understandable to ordinary people by combining document processing, NLP and LLM technology on a robust Java and Spring Boot foundation. By summarising content, identifying clauses, highlighting risks and answering questions in plain language, the system reduces dependence on costly manual review and helps users make informed decisions. This SRS defines the functional, non-functional, security and technical requirements for building a reliable, secure and user-friendly solution, and provides a base for future enhancements.",
        body_style
    ))

    # Build PDF
    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"Successfully generated SRS PDF at: {output_path}")

if __name__ == "__main__":
    out_file = sys.argv[1] if len(sys.argv) > 1 else os.path.join("docs", "Legal Document Analyser & Advisor - SRS.pdf")
    generate_srs_pdf(out_file)
