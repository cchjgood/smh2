package com.ipe.module.core.web.controller;

import com.ipe.common.util.ZipUtil;
import com.ipe.module.core.entity.Log;
import com.ipe.module.core.service.LogService;
import com.ipe.module.core.web.util.BodyWrapper;
import com.ipe.module.core.web.util.RestRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Role: tangdu
 * Date: 13-9-7
 * Time: 下午10:27
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/log")
public class LogController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(LogController.class);
    @Autowired
    private LogService logService;

    /**
     * 登录日志
     * @param log
     * @param rest
     * @return
     */
    @RequestMapping(value = {"/loginlist"})
    public
    @ResponseBody
    BodyWrapper loginList(Log log, RestRequest rest) {
        try {
            int startRow = rest.getStart();
            int endRow = rest.getLimit();
            Page<Log> page = logService.findAll(null, new PageRequest(startRow, endRow,rest.getSorts()));
            return success(page);
        } catch (Exception e) {
            LOG.error("query error", e);
            return failure(e);
        }
    }

    /**
     * 操作日志
     * @param log
     * @param rest
     * @return
     */
    @RequestMapping(value = {"/buslist"})
    public
    @ResponseBody
    BodyWrapper busList(Log log, RestRequest rest) {
        try {
            int startRow = rest.getStart();
            int endRow = rest.getLimit();
            Page<Log> page = logService.findAll(null, new PageRequest(startRow, endRow,rest.getSorts()));
            return success(page);
        } catch (Exception e) {
            LOG.error("query error", e);
            return failure(e);
        }
    }

    @RequestMapping(value = {"/del"})
    public
    @ResponseBody
    BodyWrapper del(String[] ids, RestRequest rest) {
        try {
            logService.delete(ids);
            return success();
        } catch (Exception e) {
            LOG.error("del error", e);
            return failure(e);
        }
    }


    @Value("#{app.logs_filepath}")
    private String logsPath;

    /**
     * 下载日志文件
     * @param response
     */
    @RequestMapping(value = {"/downlogs"})
    public void downlogs(HttpServletResponse response) {
        try {
            File file = new File(logsPath);
            if(file.canRead()){
                response.setContentType("application/x-download");
                response.setHeader("Pragma", "public");
                response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
                String  fileName = new String(file.getName().trim().getBytes("GBK"), "ISO-8859-1");
                response.addHeader("Content-disposition", "attachment;filename=" + fileName+SIMPLEDATEFORMAT.format(new Date())+".zip");
                ZipUtil.zipFiles(logsPath, response.getOutputStream());
            }
        } catch (Exception e) {
            LOG.error("del error", e);
            super.downFile(response);
        }
    }
}
