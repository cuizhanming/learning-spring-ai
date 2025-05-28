package net.springio.chatpdf;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
class DocumentService {

	private final ChatClient chatClient;
	
    private final VectorStore vectorStore;

    private final TokenTextSplitter tokenSplitter = new TokenTextSplitter();

    DocumentService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder
        		.defaultAdvisors(
        				new SimpleLoggerAdvisor(), 
        				new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults())) // RAG advisor
        		.build();
        this.vectorStore = vectorStore;
    }
    
    void loadDocument(Resource resource) {
    	var pdfReader = new PagePdfDocumentReader(resource, PdfDocumentReaderConfig.defaultConfig());

		vectorStore.write(tokenSplitter.split(pdfReader.read()));
    }
    
    String askQuestion(String question) {
        return chatClient
                .prompt()
                .user(question)
                .call()
                .content();
    }
}
