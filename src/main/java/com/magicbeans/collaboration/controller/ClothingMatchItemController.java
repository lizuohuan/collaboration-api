package com.magicbeans.collaboration.controller;


import com.magicbeans.collaboration.entity.ClothingMatchItem;
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

import com.magicbeans.collaboration.service.IClothingMatchItemService;

import org.springframework.stereotype.Controller;

/**
 * <p>
 * 搭配的衣服 前端控制器
 * </p>
 *
 * @author null123
 * @since 2018-02-12
 */
@RestController
@RequestMapping("/clothingMatchItem")
public class ClothingMatchItemController {


    @Autowired
    private  IClothingMatchItemService clothingMatchItemService;


    /**
     * 分页查询
     * @param pages
     * @return
     */
    @GetMapping(value = "list")
    @ApiOperation(value = "测试接口")
    public ResponseData adminList(Pages<ClothingMatchItem> pages) {
        return ResponseData.success(clothingMatchItemService.findPage(pages, null, null));
    }


    /**
     * 根据Id删除
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID删除")
    @GetMapping(value = "del/{id}")
    public ResponseData deleteById(@PathVariable String id) {
        clothingMatchItemService.delete(id);
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
        return ResponseData.success( clothingMatchItemService.find(id));
    }


    /**
     * 增加修改
     * @param clothingmatchitem
     * @return
     */
    @ApiOperation(value = "增加修改")
    @PostMapping(value = "save")
    public ResponseData save(@RequestBody  ClothingMatchItem clothingmatchitem){
        if(StringUtils.isEmpty(clothingmatchitem.getId())){
             clothingMatchItemService.save(clothingmatchitem);
        }else{
             clothingMatchItemService.update(clothingmatchitem);
        }
        return ResponseData.success();
    }

}

