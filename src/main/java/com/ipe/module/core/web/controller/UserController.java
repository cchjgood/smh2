package com.ipe.module.core.web.controller;

import com.ipe.common.util.MD5;
import com.ipe.module.core.entity.Role;
import com.ipe.module.core.entity.User;
import com.ipe.module.core.service.UserService;
import com.ipe.module.core.web.security.SystemRealm;
import com.ipe.module.core.web.util.BodyWrapper;
import com.ipe.module.core.web.util.RestRequest;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tangdu
 * Date: 13-9-7
 * Time: 下午10:27
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/user")
public class UserController extends AbstractController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = {"/list"})
    public
    @ResponseBody
    BodyWrapper list(User user, RestRequest rest) {
        try {
            int startRow = rest.getStart();
            int endRow = rest.getLimit();
            Page<User> page = userService.findAll(null, new PageRequest(startRow, endRow,rest.getSorts()));
            return success(page);
        } catch (Exception e) {
            return failure(e);
        }
    }

    @RequestMapping(value = {"/edit"}, method = RequestMethod.POST)
    public
    @ResponseBody
    BodyWrapper edit(User user, RestRequest rest) {
        try {
            user.setUpdatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            userService.save(user);
            return success(user);
        } catch (Exception e) {
            return failure(e);
        }
    }

    @RequestMapping(value = {"/add"}, method = RequestMethod.POST)
    public
    @ResponseBody
    BodyWrapper add(User user, RestRequest rest) {
        try {
            user.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            userService.save(user);
            return success(user);
        } catch (Exception e) {
            return failure(e);
        }
    }

    @RequestMapping(value = {"/del"})
    public
    @ResponseBody
    BodyWrapper del(String[] ids, RestRequest rest) {
        try {
            userService.delete(ids);
            return success();
        } catch (Exception e) {
            return failure(e);
        }
    }

    /**
     * 修改密码
     * @param userPwd
     * @param ouserPwd
     * @param request
     * @return
     */
    @RequestMapping(value = {"/uppwd"})
    public
    @ResponseBody
    BodyWrapper upPwd(@RequestParam String userPwd,
                      @RequestParam String ouserPwd,
                      HttpServletRequest request) {
        try {
            String userId=((SystemRealm.UserInfo) SecurityUtils.getSubject().getPrincipal()).getUserId();
            userPwd= MD5.digest(userPwd);
            ouserPwd=MD5.digest(ouserPwd);
            boolean b=userService.updatePwd(userId,userPwd,ouserPwd);
            if(b){
                return success();
            }else{
                return failure("原密码不正确");
            }
        } catch (Exception e) {
            return failure(e);
        }
    }

    /**
     * 用户拥有角色
     * @param request
     * @return
     */
    @RequestMapping(value = {"/userRole"})
    public
    @ResponseBody
    BodyWrapper userRole(@RequestParam String userId,HttpServletRequest request) {
        try {
            List<Role> roles=userService.getUserRole(userId);
            return success(roles);
        } catch (Exception e) {
            return failure(e);
        }
    }

    /**
     * 用户不具有角色
     * @param request
     * @return
     */
    @RequestMapping(value = {"/userNotRole"})
    public
    @ResponseBody
    BodyWrapper userNotRole(@RequestParam String userId,HttpServletRequest request) {
        try {
            List<Role> roles=userService.getUserNotRole(userId);
            return success(roles);
        } catch (Exception e) {
            return failure(e);
        }
    }
}
