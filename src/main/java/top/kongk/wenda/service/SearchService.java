package top.kongk.wenda.service;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kongk.wenda.dao.QuestionDao;
import top.kongk.wenda.model.Question;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author kk
 */
@Service
public class SearchService {


    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionDao questionDao;

    /**
     * solr 连接url
     */
    private static final String SOLR_URL = "http://127.0.0.1:8983/solr/wenda";

    /**
     * solr 客户端
     */
    private HttpSolrClient client = new HttpSolrClient.Builder(SOLR_URL).build();

    /**
     * 问题标题
     */
    private static final String QUESTION_TITLE_FIELD = "question_title";

    /**
     * 问题内容
     */
    private static final String QUESTION_CONTENT_FIELD = "question_content";


    /**
     * 根据关键词 搜索问题
     *
     * @param keyword
     * @param offset
     * @param count
     * @param hlPre
     * @param hlPos
     * @return java.util.List<top.kongk.wenda.model.Question>
     */
    public List<Question> searchQuestion(String keyword, int offset, int count,
                                         String hlPre, String hlPos) throws Exception {
        List<Question> questionList = new ArrayList<>();

        //设置查询条件
        SolrQuery query = new SolrQuery(keyword);
        query.setRows(count);
        query.setStart(offset);
        query.setHighlight(true);
        query.setHighlightSimplePre(hlPre);
        query.setHighlightSimplePost(hlPos);
        query.set("hl.fl", QUESTION_TITLE_FIELD + "," + QUESTION_CONTENT_FIELD);
        QueryResponse response = client.query(query);

        //将查到的结果放入 question list集合
        for (Map.Entry<String, Map<String, List<String>>> entry : response.getHighlighting().entrySet()) {
            Question q = new Question();
            q.setId(Integer.parseInt(entry.getKey()));
            if (entry.getValue().containsKey(QUESTION_CONTENT_FIELD)) {
                List<String> contentList = entry.getValue().get(QUESTION_CONTENT_FIELD);
                if (contentList.size() > 0) {
                    q.setContent(contentList.get(0));
                }
            }
            if (entry.getValue().containsKey(QUESTION_TITLE_FIELD)) {
                List<String> titleList = entry.getValue().get(QUESTION_TITLE_FIELD);
                if (titleList.size() > 0) {
                    q.setTitle(titleList.get(0));
                }
            }
            questionList.add(q);
        }
        return questionList;
    }


    /**
     * 获取相似问题列表
     *
     * @param keyword
     * @param offset
     * @param count
     * @return java.util.List<top.kongk.wenda.model.Question>
     */
    public List<Question> searchQuestionTitle(String keyword, int offset, int count) throws Exception {

        //设置查询条件
        SolrQuery query = new SolrQuery(keyword);
        query.setRows(count);
        query.setStart(offset);
        query.set("fl", QUESTION_TITLE_FIELD + ",score,id");
        query.set("df", QUESTION_TITLE_FIELD);

        QueryResponse response = client.query(query);

        SolrDocumentList results = response.getResults();
        List<Question> questions = new ArrayList<>();

        for (SolrDocument result : results) {
            String id = (String) result.get("id");
            if (StringUtils.isNumeric(id)) {
                int i = Integer.parseInt(id);
                Question questionById = questionService.getById(i);
                if (questionById == null) {
                    continue;
                }
                Question question = new Question();

                question.setId(questionById.getId());
                //标题
                question.setTitle(questionById.getTitle());
                //回答数
                question.setCommentCount(questionById.getCommentCount());
                //相似度
                question.setContent("相似度:" + result.get("score"));
                questions.add(question);
            } else {
                continue;
            }
        }

        return questions;
    }


    /**
     * 给新加入的问题添加索引
     *
     * @param qid
     * @param title
     * @param content
     * @return boolean
     */
    public boolean indexQuestion(int qid, String title, String content) throws Exception {
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField("id", qid);
        doc.setField(QUESTION_TITLE_FIELD, title);
        doc.setField(QUESTION_CONTENT_FIELD, content);
        //在1000毫秒内返回
        UpdateResponse response = client.add(doc, 1000);
        return response != null && response.getStatus() == 0;
    }

    /**
     * 根据id 删除
     *
     * @param id
     * @return void
     */
    public void deleteById(String id) {
        System.out.println("======================deleteById ===================");
        try {
            UpdateResponse rsp = client.deleteById(id);
            client.commit();
            System.out.println("delete id:" + id + " result:" + rsp.getStatus() + " Qtime:" + rsp.getQTime());
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
    }


    public void deleteAll() {
        try {

            //把删除的条件设置为"*:*"就可以了
            client.deleteByQuery("*:*");
            client.commit();

            /*//传递List<String>
            List<Question> questions = questionDao.selectQuestions(null, null, null, null);
            List<String> stringList = new ArrayList<>(questions.size());
            for (Question question : questions) {
                stringList.add(question.getId().toString());
            }
            UpdateResponse rsp = client.deleteById(stringList);
            client.commit();
            System.out.println("delete id stringList:" + " result:" + rsp.getStatus() + " Qtime:" + rsp.getQTime());
            */
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
    }

    public void indexAll() {

        //传递List<String>
        List<Question> questions = questionDao.selectQuestions(null, null, null, null);
        for (Question question : questions) {
            try {
                indexQuestion(question.getId(), question.getTitle(), question.getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}
