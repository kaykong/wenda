package top.kongk.wenda.service;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import top.kongk.wenda.util.WendaUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kkk
 */
@Service
public class SensitiveService implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(SensitiveService.class);

    private TrieNode rootNode = new TrieNode();


    @Override
    public void afterPropertiesSet() {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                addWord(line);
            }
        } catch (Exception e) {
            log.error("读取敏感词文件[SensitiveWords.txt]失败 {}", e.getMessage());
        }
    }

    private class TrieNode {

        /**
         * 是否是结尾的字符
         */
        private Boolean endWord = false;
        /**
         * 子节点
         */
        private Map<Character, TrieNode> subNode = new HashMap<>();


        public Boolean getEndWord() {
            return endWord;
        }

        public void setEndWord(Boolean endWord) {
            this.endWord = endWord;
        }

        public Boolean isEndWord() {
            return endWord;
        }

        /**
         * 在当前位置后添加子节点
         *
         * @author kongkk
         * @param character 字符
         * @param trieNode 下一个节点
         */
        public void addSubNode(Character character, TrieNode trieNode) {
            subNode.put(character, trieNode);
        }

        /**
         * 获取值为key的下一个节点
         *
         * @author kongkk
         * @param key
         * @return top.kongk.wenda.service.SensitiveService.TrieNode
         */
        public TrieNode getSubNode(Character key) {
            return subNode.get(key);
        }
    }


    private void addWord(String word) {
        if (StringUtils.isBlank(word)) {
            return;
        }

        TrieNode trieNode = this.rootNode;

        for (int i = 0; i < word.length(); ++i) {
            char c = word.charAt(i);
            TrieNode subNode = trieNode.getSubNode(c);
            if (subNode == null) {
                subNode = new TrieNode();
                trieNode.addSubNode(c, subNode);
            }

            trieNode = subNode;

            if (i == word.length() - 1) {
                trieNode.setEndWord(true);
            }
        }
    }

    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }

        TrieNode trieNode = rootNode;
        //过滤起始位置
        int begin = 0;
        //过滤光标的位置
        int position = 0;
        StringBuilder sb = new StringBuilder();

        while(position < text.length()) {
            char c = text.charAt(position);

            if (isContinueChar(c)) {
                sb.append(c);
                position++;
                continue;
            }

            trieNode = trieNode.getSubNode(c);

            if (trieNode == null) {
                //当前过滤结束, begin指向的字符不是敏感词的开头
                sb.append(text.charAt(begin));
                begin = begin + 1;
                position = begin;
                trieNode = rootNode;
            } else if (trieNode.isEndWord()) {
                //text.substring(begin, position + 1) 是敏感词
                sb.append(WendaUtil.REPLACE_COMMENT);
                begin = position + 1;
                position = begin;
                trieNode = rootNode;
            } else {
                //正在过滤
                ++position;
            }
        }

        return sb.toString();
    }

    private boolean isContinueChar(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }


    public static void main(String[] args) {
        SensitiveService service = new SensitiveService();
        service.addWord("你好");
        service.addWord("关键词");
        service.addWord("敏感词");
        String text = "你好呀, 哈哈哈, 关键的呢";
        String filter = service.filter(text);
        System.out.println(text);
        System.out.println(filter);
    }



}
