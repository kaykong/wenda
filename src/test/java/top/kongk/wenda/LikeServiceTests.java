package top.kongk.wenda;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.kongk.wenda.model.EntityType;
import top.kongk.wenda.service.CommentService;

/**
 * Created by nowcoder on 2016/8/28.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
public class LikeServiceTests {


    @Autowired
    CommentService commentService;

    @Test
    public void testLike() {
        System.out.println("**********");
        Integer answerReplyCount = commentService.getAnswerReplyCount(12, EntityType.ENTITY_ANSWER, 0);
        System.out.println(answerReplyCount);
        System.out.println("--------------");
    }


}
