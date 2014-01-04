package org.jphototagger.lib.help;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
final class HelpSearch {

    private static final String FIELD_NAME_HELP_PAGE_CONTENTS = "contents";
    private static final String FIELD_NAME_HELP_PAGE_URL = "url";
    private static final String FIELD_NAME_HELP_PAGE_TITLE = "title";
    private final HelpNode rootNode;
    private final StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_34);
    private final Directory directory = new RAMDirectory();
    private IndexWriter indexWriter;

    HelpSearch(HelpNode rootNode) {
        if (rootNode == null) {
            throw new NullPointerException("rootNode == null");
        }

        this.rootNode = rootNode;
    }

    void startIndexing() {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_34, analyzer);
        try {
            indexWriter = new IndexWriter(directory, indexWriterConfig);
            Collection<HelpPage> helpPages = HelpUtil.findHelpPagesRecursive(rootNode);
            for (HelpPage helpPage : helpPages) {
                addHelpPageToIndex(helpPage);
            }
            indexWriter.optimize();
            indexWriter.close();
        } catch (Throwable t) {
            Logger.getLogger(HelpSearch.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    private void addHelpPageToIndex(HelpPage helpPage) throws IOException {
        String helpPageTitle = StringUtil.emptyStringIfNull(helpPage.getTitle());
        String helpPageUrl = helpPage.getUrl();
        String helpPageContentAsString = getHelpPageContentAsString(helpPage);
        Document document = new Document();
        document.add(new Field(FIELD_NAME_HELP_PAGE_TITLE, helpPageTitle, Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field(FIELD_NAME_HELP_PAGE_URL, helpPageUrl, Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field(FIELD_NAME_HELP_PAGE_CONTENTS, helpPageContentAsString, Field.Store.YES, Field.Index.ANALYZED));
        indexWriter.addDocument(document);
    }

    private String getHelpPageContentAsString(HelpPage helpPage) throws IOException {
        InputStream helpPageContent = HelpUtil.class.getResourceAsStream(helpPage.getUrl());
        String helpPageContentAsString = StringUtil.convertStreamToString(helpPageContent, "UTF-8");
        return removeHtmlTags(helpPageContentAsString);
    }

    private String removeHtmlTags(String stringWithHtmlTags) {
        String stringWithoutHtmlTags = stringWithHtmlTags;
        stringWithoutHtmlTags = stringWithoutHtmlTags.replaceAll("\\<.*?>", "");
        stringWithoutHtmlTags = stringWithoutHtmlTags.replaceAll("&nbsp;", "");
        stringWithoutHtmlTags = stringWithoutHtmlTags.replaceAll("&amp;", "");
        stringWithoutHtmlTags = stringWithoutHtmlTags.replaceAll("&quot;", "");
        return stringWithoutHtmlTags;
    }

    List<HelpPage> findHelpPagesMatching(String queryString) {
        if (queryString == null) {
            throw new NullPointerException("queryString == null");
        }

        if (indexWriter == null) {
            throw new IllegalStateException("startIndexing was not called");
        }

        List<HelpPage> matchingHelpPageUrls = Collections.emptyList();
        try {
            String andQuery = generateAndQuery(queryString);
            QueryParser queryParser = new QueryParser(Version.LUCENE_34, FIELD_NAME_HELP_PAGE_CONTENTS, analyzer);
            Query query = queryParser.parse(andQuery);
            IndexSearcher indexSearcher = new IndexSearcher(directory, true);
            TopScoreDocCollector collector = TopScoreDocCollector.create(100000, true);
            indexSearcher.search(query, collector);
            ScoreDoc[] scoreDocs = collector.topDocs().scoreDocs;
            matchingHelpPageUrls = new ArrayList<>(scoreDocs.length);
            for (ScoreDoc scoreDoc : scoreDocs) {
                int docId = scoreDoc.doc;
                Document document = indexSearcher.doc(docId);
                addHelpPage(document, matchingHelpPageUrls);
            }
        } catch (Throwable t) {
            Logger.getLogger(HelpSearch.class.getName()).log(Level.SEVERE, null, t);
        }

        return matchingHelpPageUrls;
    }

    private String generateAndQuery(String string) {
        List<String> tokens = StringUtil.getStringsWithContent(string.split(" "));
        int tokenCount = tokens.size();
        boolean wordCompleted = string.endsWith(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokenCount; i++) {
            String token = tokens.get(i);
            sb.append(i == 0 ? "" : " AND ");
            sb.append(token);
            sb.append(i == tokenCount - 1 && !wordCompleted ? "*" : "");
        }
        return sb.toString();
    }

    private void addHelpPage(Document document, List<HelpPage> matchingHelpPageUrls) {
        String helpPageTitle = document.get(FIELD_NAME_HELP_PAGE_TITLE);
        String helpPageUrl = document.get(FIELD_NAME_HELP_PAGE_URL);
        HelpPage helpPage = new HelpPage();
        helpPage.setTitle(helpPageTitle);
        helpPage.setUrl(helpPageUrl);
        matchingHelpPageUrls.add(helpPage);
    }
}
