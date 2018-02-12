package com.magicbeans.collaboration.controller;


import com.magicbeans.collaboration.entity.UserSystemConfig;
import org.springframework.web.bind.annotation.RequestMapping;
import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.base.Pages;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.magicbeans.collaboration.service.IUserSystemConfigService;

import org.springframework.stereotype.Controller;

/**
 * <p>
 * 用户的系统配置 前端控制器
 * </p>
 *
 * @author null123
 * @since 2018-02-12
 */
@RestController
@RequestMapping("/userSystemConfig")
public class UserSystemConfigController {


    @Autowired
    private  IUserSystemConfigService userSystemConfigService;


    /**
     * 分页查询
     * @param pages
     * @return
     */
    @GetMapping(value = "list")
    @ApiOperation(value = "测试接口")
    public ResponseData adminList(Pages<UserSystemConfig> pages) {
        return ResponseData.success(userSystemConfigService.findPage(pages, null, null));
    }


    /**
     * 根据Id删除
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID删除")
    @GetMapping(value = "del/{id}")
    public ResponseData deleteById(@PathVariable String id) {
        userSystemConfigService.delete(id);
        return ResponseData.success();
    }

    /**
     * 根据ID查询实体
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID查询实体")
    @GetMapping(value = "get/{id}")
    public ResponseData findById(@PathVariable String id){
        return ResponseData.success( userSystemConfigService.find(id));
    }


    /**
     * 增加修改
     * @param admin
     * @return
     */
    @ApiOperation(value = "增加修改")
    @PostMapping(value = "save")
    public ResponseData save(@RequestBody  UserSystemConfig usersystemconfig){
        if(StringUtils.isEmpty(usersystemconfig.getId())){
             userSystemConfigService.save(usersystemconfig);
        }else{
             userSystemConfigService.update(usersystemconfig);
        }
        return ResponseData.success();
    }

}

