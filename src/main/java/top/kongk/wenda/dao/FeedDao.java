package top.kongk.wenda.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.kongk.wenda.model.Feed;

import java.util.List;


/**
 * @author kk
 */
@Mapper
public interface FeedDao {

    /**
     * 插入feed
     *
     * @param feed
     * @return int
     */
    int addFeed(Feed feed);

    /**
     * 根据id获取feed
     *
     * @param id
     * @return top.kongk.wenda.model.Feed
     */
    Feed getFeedById(int id);

    /**
     *
     *
     * @param maxId 用来标记id的最大值
     * @param userIds 当前用户关注的人的ids
     * @param count 获取多少个
     * @return java.util.List<top.kongk.wenda.model.Feed>
     */
    List<Feed> selectUserFeeds(@Param("maxId") int maxId,
                               @Param("userIds") List<Integer> userIds,
                               @Param("count") int count);
}
