package com.magicbeans.collaboration.controller;


import com.magicbeans.collaboration.controller.base.BaseController;
import com.magicbeans.collaboration.entity.User;
import com.magicbeans.collaboration.exception.InterfaceCommonException;
import com.magicbeans.collaboration.redis.RedisService;
import com.magicbeans.collaboration.sms.SMSCode;
import com.magicbeans.collaboration.util.CommonUtil;
import com.magicbeans.collaboration.util.LoginHelper;
import com.magicbeans.collaboration.util.StatusConstant;
import com.magicbeans.collaboration.util.TextMessage;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.web.bind.annotation.RequestMapping;
import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.base.Pages;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import com.magicbeans.collaboration.service.IUserService;


import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author null123
 * @since 2018-02-12
 */
@RestController
@RequestMapping("/user")
@Api(description = "用户接口")
public class UserController extends BaseController {


    @Autowired
    private  IUserService userService;
    @Resource
    private RedisService redisService;

    /**
     * 分页查询
     * @param pages
     * @return
     */
    @GetMapping(value = "list")
    @ApiOperation(value = "测试接口")
    public ResponseData adminList(Pages<User> pages) {
        return ResponseData.success(userService.findPage(pages, null, null));
    }


    @RequestMapping(value = "/sendCode",method = RequestMethod.POST)
    @ApiOperation(value = "注册发送验证码",notes = "验证码的正确性由服务端验证，移动端暂不用验证 ")
    @ApiImplicitParam(name = "phone",value = "手机号码" ,required = true)
    public ResponseData sendMsg(String phone){

        if(CommonUtil.isEmpty(phone)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        User user = userService.find("phone",phone);
        if(null != user && !CommonUtil.isEmpty(user.getPwd())){
            return buildFailureJson(StatusConstant.OBJECT_EXIST,"手机号已经存在");
        }
        String code = SMSCode.createRandomCode();
        String msg = MessageFormat.format(TextMessage.MSG_CODE, code);
        boolean isSuccess = SMSCode.sendMessage(msg, phone);
        if(!isSuccess){
            return buildFailureJson(StatusConstant.Fail_CODE,"发送失败");
        }
        redisService.set(TextMessage.REDIS_KEY_PREFIX + phone,code,TextMessage.EXPIRE_TIME, TimeUnit.MINUTES);
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"发送成功",code);
    }


    @RequestMapping(value = "/register",method = RequestMethod.POST)
    @ApiOperation(value = "注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone",value = "手机号",required = true),
            @ApiImplicitParam(name = "code",value = "验证码",required = true),
            @ApiImplicitParam(name = "pwd",value = "加过密的文本",required = true),
            @ApiImplicitParam(name = "deviceType",value = "设备类型，0:android  1:ios 其他不传"),
            @ApiImplicitParam(name = "deviceToken",value = "设备请求的推送token")
    })
    public ResponseData register(String phone,String code,String pwd,
                                 String deviceToken,Integer deviceType){
        if(CommonUtil.isEmpty(phone,code,pwd)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        Object o = redisService.get(TextMessage.REDIS_KEY_PREFIX + phone);
        if(null == o || !code.equals(o.toString())){
            return buildFailureJson(StatusConstant.Fail_CODE,"验证码失效");
        }
        User user = userService.find("phone",phone);
        if(null != user && !CommonUtil.isEmpty(user.getPwd())){
            return buildFailureJson(StatusConstant.OBJECT_EXIST,"手机号已经存在");
        }
        User r = new User();
        if(null == user){
            r.setPhone(phone);
            r.setPwd(pwd);
            r.setDeviceToken(deviceToken);
            r.setDeviceType(deviceType);
            userService.save(r);
        }
        else{
            r = user;
            r.setPwd(pwd);
            r.setDeviceToken(deviceToken);
            r.setDeviceType(deviceType);
            userService.update(r);
        }
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        r.setToken(token);
        redisService.set(token,r,StatusConstant.LOGIN_VALID,TimeUnit.DAYS);
        userService.update(r);
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"注册成功",r);
    }


    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ApiOperation(value = "登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone",value = "手机号",required = true),
            @ApiImplicitParam(name = "pwd",value = "密码",required = true),
            @ApiImplicitParam(name = "deviceToken",value = "设备token"),
            @ApiImplicitParam(name = "deviceType",value = "设备类型 0 android  1 ios")
    })
    public ResponseData login(String phone,String pwd,String deviceToken,Integer deviceType){

        if(CommonUtil.isEmpty(phone,pwd)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        User user = userService.find("phone",phone);
        if(null == user){
            return buildFailureJson(StatusConstant.OBJECT_NOT_EXIST,"手机号错误");
        }
        if(!pwd.equals(user.getPwd())){
            return buildFailureJson(StatusConstant.Fail_CODE,"密码错误");
        }
        user.setDeviceToken(deviceToken);
        user.setDeviceType(deviceType);
        if(!CommonUtil.isEmpty(user.getToken())){
            redisService.remove(user.getToken());
        }
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        user.setToken(token);
        redisService.set(token,user,StatusConstant.LOGIN_VALID, TimeUnit.DAYS);
        userService.update(user);
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"登录成功",user);
    }


    @RequestMapping(value = "/getInfo",method = RequestMethod.POST)
    @ApiOperation(value = "获取个人基本信息")
    public ResponseData getInfo(){
        try {
            User user = LoginHelper.getCurrentUser(redisService);
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",user);
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"设置失败");
        }
    }


    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ApiOperation(value = "更新用户字段操作")
    public ResponseData setBaseInfo(User user){
        try {
            User currentUser = LoginHelper.getCurrentUser(redisService);
            user.setId(currentUser.getId());
            userService.update(user);

            User sql = userService.find("id", user.getId());
            redisService.set(currentUser.getToken(),sql,StatusConstant.LOGIN_VALID,TimeUnit.DAYS);
            return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"操作成功");
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"设置失败");
        }
    }



    /**
     * 根据ID查询实体
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID查询实体")
    @GetMapping(value = "get/{id}")
    public ResponseData findById(@PathVariable String id){
        return ResponseData.success( userService.find(id));
    }



    /**
     * 增加修改
     * @param user
     * @return
     */
    @ApiOperation(value = "添加子用户")
    @PostMapping(value = "save")
    public ResponseData save(@RequestBody  User user) throws Exception {

        User loginUser = LoginHelper.getCurrentUser(redisService);
        user.setParentId(loginUser.getId());
        userService.save(user);
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"添加成功");
    }

}

