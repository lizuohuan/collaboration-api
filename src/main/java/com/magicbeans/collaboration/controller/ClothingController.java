package com.magicbeans.collaboration.controller;


import com.magicbeans.collaboration.entity.Clothing;
import com.magicbeans.collaboration.service.IClothingService;
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


import org.springframework.stereotype.Controller;

/**
 * <p>
 * 服装 前端控制器
 * </p>
 *
 * @author null123
 * @since 2018-02-12
 */
@RestController
@RequestMapping("/clothing")
public class ClothingController {


    @Autowired
    private IClothingService clothingService;


    /**
     * 分页查询
     * @param pages
     * @return
     */
    @GetMapping(value = "list")
    @ApiOperation(value = "测试接口")
    public ResponseData adminList(Pages<Clothing> pages) {
        return ResponseData.success(clothingService.findPage(pages, null, null));
    }


    /**
     * 根据Id删除
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID删除")
    @GetMapping(value = "del/{id}")
    public ResponseData deleteById(@PathVariable String id) {
        clothingService.delete(id);
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
        return ResponseData.success( clothingService.find(id));
    }


    /**
     * 增加修改
     * @param clothing
     * @return
     */
    @ApiOperation(value = "增加修改")
    @PostMapping(value = "save")
    public ResponseData save(@RequestBody  Clothing clothing){
        if(StringUtils.isEmpty(clothing.getId())){
             clothingService.save(clothing);
        }else{
             clothingService.update(clothing);
        }
        return ResponseData.success();
    }

}

