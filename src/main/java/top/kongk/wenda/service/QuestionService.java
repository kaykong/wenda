package top.kongk.wenda.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kongk.wenda.common.ServerResponse;
import top.kongk.wenda.common.UserValidatorUtil;
import top.kongk.wenda.dao.LoginTicketDao;
import top.kongk.wenda.dao.QuestionDao;
import top.kongk.wenda.dao.UserDao;
import top.kongk.wenda.model.LoginTicket;
import top.kongk.wenda.model.User;
import top.kongk.wenda.util.MD5Util;
import top.kongk.wenda.util.WendaUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author kkk
 */
@Service

public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    private QuestionDao questionDao;


}
