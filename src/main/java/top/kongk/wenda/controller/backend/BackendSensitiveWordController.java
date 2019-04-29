package top.kongk.wenda.controller.backend;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.kongk.wenda.model.*;
import top.kongk.wenda.service.CourseCategoryService;
import top.kongk.wenda.service.SensitiveService;
import top.kongk.wenda.service.SensitiveWordService;
import top.kongk.wenda.util.WendaUtil;

import java.util.Date;
import java.util.List;


/**
 * @author kk
 */
@Controller
@RequestMapping("/backend")
public class BackendSensitiveWordController {

    @Autowired
    SensitiveWordService sensitiveWordService;

    @Autowired
    SensitiveService service;

    private String checkSensitiveWordName(String name) {
        if (StringUtils.isBlank(name)) {
            return WendaUtil.getJSONString(1, "敏感词不能为空");
        }

        boolean check = sensitiveWordService.checkSensitiveWord(name);

        if (!check) {
            return null;
        } else {
            return WendaUtil.getJSONString(1, "敏感词已存在");
        }
    }


    @RequestMapping(value = "/addSensitiveWord", method = RequestMethod.POST)
    @ResponseBody
    public String addSensitiveWord(SensitiveWord sensitiveWord) {

        String checkName = checkSensitiveWordName(sensitiveWord.getName());
        if (checkName != null) {
            return checkName;
        }

        boolean check = sensitiveWordService.addSensitiveWord(sensitiveWord);

        if (check) {
            service.addWord(sensitiveWord.getName());
            return WendaUtil.getJSONString(0);
        } else {
            return WendaUtil.getJSONString(1, "新增失败");
        }
    }

    @RequestMapping(value = "/deleteSensitiveWord/{id}", method = RequestMethod.POST)
    @ResponseBody
    public String deleteSensitiveWord(@PathVariable("id") Integer id) {

        SensitiveWord sensitiveWord = sensitiveWordService.getSensitiveWordById(id);
        if (sensitiveWord == null) {
            return WendaUtil.getJSONString(1, "不存在的敏感词");
        }

        boolean check = sensitiveWordService.deleteSensitiveWordById(id);

        if (check) {
            service.deleteWord(sensitiveWord.getName());
            return WendaUtil.getJSONString(0);
        } else {
            return WendaUtil.getJSONString(1, "删除失败");
        }
    }



    @RequestMapping(value = "/sensitiveWorlds", method = RequestMethod.GET)
    @ResponseBody
    public Page<SensitiveWord> sensitiveWorlds(SensitiveWord sensitiveWord,
                                   @RequestParam(name = "currPage", defaultValue = "1") int currPage,
                                   @RequestParam(name = "pageSize", defaultValue = "8") int pageSize) {

        if (currPage < 1 || pageSize < 1) {
            return null;
        }

        return sensitiveWordService.getSensitiveWordsBySelectiveWithPage(sensitiveWord, currPage, pageSize);
    }

    @RequestMapping(value = "/checkSensitiveWord", method = RequestMethod.GET)
    @ResponseBody
    public String checkSensitiveWord(@RequestParam("name") String name) {

        if (StringUtils.isBlank(name)) {
            return WendaUtil.getJSONString(1, "敏感词不能为空");
        }

        boolean check = sensitiveWordService.checkSensitiveWord(name);

        if (!check) {
            return WendaUtil.getJSONString(0);
        } else {
            return WendaUtil.getJSONString(1, "敏感词已存在");
        }
    }


}
