package top.kongk.wenda.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.kongk.wenda.model.Category;
import top.kongk.wenda.model.SensitiveWord;

import java.util.List;
import java.util.Map;

/**
 * @author kkk
 */
@Mapper
public interface SensitiveWordDao {

    int countBySelective(SensitiveWord sensitiveWord);

    List<SensitiveWord> selectBySelectiveWithPage(Map map);

    List<SensitiveWord> selectAllSelectiveWord();

    int addSensitiveWord(SensitiveWord sensitiveWord);

    int deleteSensitiveWordById(Integer id);

    SensitiveWord selectByName(String name);

    SensitiveWord getSensitiveWordById(Integer id);
}
