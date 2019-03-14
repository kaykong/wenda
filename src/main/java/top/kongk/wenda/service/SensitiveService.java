package top.kongk.wenda.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kkk
 */
@Service
public class SensitiveService {

    private class TrieNode {

        /**
         * 敏感的字符
         */
        private Character character;
        /**
         * 是否是结尾的单词
         */
        private Boolean endWord = false;
        /**
         * 子节点
         */
        private Map<Character, TrieNode> subNode = new HashMap<>();

        public TrieNode(Character character) {
            this.character = character;
        }

        public Boolean getEndWord() {
            return endWord;
        }

        public void setEndWord(Boolean endWord) {
            this.endWord = endWord;
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


}
