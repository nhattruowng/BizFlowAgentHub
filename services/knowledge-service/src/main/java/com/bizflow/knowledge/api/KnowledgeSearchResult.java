package com.bizflow.knowledge.api;

public class KnowledgeSearchResult {
    private String docId;
    private String chunkId;
    private String title;
    private String source;
    private String content;

    public KnowledgeSearchResult() {}

    public KnowledgeSearchResult(String docId, String chunkId, String title, String source, String content) {
        this.docId = docId;
        this.chunkId = chunkId;
        this.title = title;
        this.source = source;
        this.content = content;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getChunkId() {
        return chunkId;
    }

    public void setChunkId(String chunkId) {
        this.chunkId = chunkId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
