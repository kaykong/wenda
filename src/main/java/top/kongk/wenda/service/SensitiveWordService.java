package top.kongk.wenda.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kongk.wenda.dao.SensitiveWordDao;
import top.kongk.wenda.model.Page;
import top.kongk.wenda.model.SensitiveWord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kkk
 */
@Service

public class SensitiveWordService {

    private static final Logger log = LoggerFactory.getLogger(SensitiveWordService.class);

    @Autowired
    private SensitiveWordDao sensitiveWordDao;

    public Page<SensitiveWord> getSensitiveWordsBySelectiveWithPage(SensitiveWord sensitiveWord, int currPage, int pageSize) {

        int totalCount = sensitiveWordDao.countBySelective(sensitiveWord);

        if (currPage < 0) {
            currPage = 1;
        }

        //初始化Page 生成Page对象,传入当前页数,每页的记录数,以及记录总数
        Page<SensitiveWord> page = new Page<>(currPage, pageSize, totalCount);

        if (totalCount == 0) {
            return page;
        }

        //设置map, 因为 sensitiveWordDao.selectBySelectiveWithPage(map) 需要
        Map map = new HashMap(3);
        map.put("sensitiveWord", sensitiveWord);
        map.put("start", page.getStart());
        map.put("pageSize", page.getPageSize());

        List<SensitiveWord> sensitiveWordList = sensitiveWordDao.selectBySelectiveWithPage(map);

        page.setLists(sensitiveWordList);

        return page;
    }


    public boolean addSensitiveWord(SensitiveWord sensitiveWord) {
        try {
            sensitiveWordDao.addSensitiveWord(sensitiveWord);
        } catch (Exception e) {
            log.error("新增敏感词失败");
        }
        return sensitiveWord.getId() != null && sensitiveWord.getId() > 0;
    }


    public boolean deleteSensitiveWordById(Integer id) {
        return sensitiveWordDao.deleteSensitiveWordById(id) > 0;
    }

    /**
     * 如果存在返回true
     *
     * @param name
     * @return boolean
     */
    public boolean checkSensitiveWord(String name) {
        return sensitiveWordDao.selectByName(name) != null;
    }

    public SensitiveWord getSensitiveWordById(Integer id) {
        return sensitiveWordDao.getSensitiveWordById(id);
    }
}
