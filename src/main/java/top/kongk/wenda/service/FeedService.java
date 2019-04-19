package top.kongk.wenda.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kongk.wenda.dao.FeedDao;
import top.kongk.wenda.model.Feed;

import java.util.List;


/**
 * @author kk
 */
@Service
public class FeedService {

    @Autowired
    FeedDao feedDao;

    /**
     * feed id 在maxId内 获取 关注的人的 count个feed事件
     *
     * @param maxId
     * @param userIds
     * @param count
     * @return java.util.List<top.kongk.wenda.model.Feed>
     */
    public List<Feed> getUserFeeds(int maxId, List<Integer> userIds, int count) {
        return feedDao.selectUserFeeds(maxId, userIds, count);
    }

    /**
     * 添加feed
     *
     * @param feed
     * @return boolean
     */
    public boolean addFeed(Feed feed) {
        feedDao.addFeed(feed);
        return feed.getId() > 0;
    }

    /**
     * 根据id获取feed
     *
     * @param id
     * @return top.kongk.wenda.model.Feed
     */
    public Feed getById(int id) {
        return feedDao.getFeedById(id);
    }

    public void deleteFeedByUserIdDataType(int userId, String data, int type) {
        feedDao.deleteFeedByUserIdDataType(userId, data, type);
    }
}
