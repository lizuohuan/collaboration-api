package com.magicbeans.collaboration.controller;


import com.magicbeans.collaboration.entity.ClothingMatch;
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

import com.magicbeans.collaboration.service.IClothingMatchService;

import org.springframework.stereotype.Controller;

/**
 * <p>
 * 搭配 前端控制器
 * </p>
 *
 * @author null123
 * @since 2018-02-12
 */
@RestController
@RequestMapping("/clothingMatch")
public class ClothingMatchController {


    @Autowired
    private  IClothingMatchService clothingMatchService;


    /**
     * 分页查询
     * @param pages
     * @return
     */
    @GetMapping(value = "list")
    @ApiOperation(value = "测试接口")
    public ResponseData adminList(Pages<ClothingMatch> pages) {
        return ResponseData.success(clothingMatchService.findPage(pages, null, null));
    }


    /**
     * 根据Id删除
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID删除")
    @GetMapping(value = "del/{id}")
    public ResponseData deleteById(@PathVariable String id) {
        clothingMatchService.delete(id);
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
        return ResponseData.success( clothingMatchService.find(id));
    }


    /**
     * 增加修改
     * @param clothingmatch
     * @return
     */
    @ApiOperation(value = "增加修改")
    @PostMapping(value = "save")
    public ResponseData save(@RequestBody  ClothingMatch clothingmatch){
        if(StringUtils.isEmpty(clothingmatch.getId())){
             clothingMatchService.save(clothingmatch);
        }else{
             clothingMatchService.update(clothingmatch);
        }
        return ResponseData.success();
    }

}

