package com.magicbeans.collaboration.controller;


import com.magicbeans.base.db.Filter;
import com.magicbeans.collaboration.controller.base.BaseController;
import com.magicbeans.collaboration.entity.Closet;
import com.magicbeans.collaboration.entity.User;
import com.magicbeans.collaboration.redis.RedisService;
import com.magicbeans.collaboration.service.IClothingService;
import com.magicbeans.collaboration.service.IUserService;
import com.magicbeans.collaboration.service.IUserSystemConfigService;
import com.magicbeans.collaboration.util.CommonUtil;
import com.magicbeans.collaboration.util.LoginHelper;
import com.magicbeans.collaboration.util.StatusConstant;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.web.bind.annotation.RequestMapping;
import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.base.Pages;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.magicbeans.collaboration.service.IClosetService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 衣柜 前端控制器
 * </p>
 *
 * @author null123
 * @since 2018-02-12
 */
@RestController
@RequestMapping("/closet")
@Api(description = "衣柜接口")
public class ClosetController extends BaseController {


    @Autowired
    private  IClosetService closetService;
    @Resource
    private IUserSystemConfigService userSystemConfigService;
    @Resource
    private RedisService redisService;
    @Resource
    private IUserService userService;
    @Resource
    private IClothingService clothingService;
    /**
     * 分页查询
     * @param pages
     * @return
     */
    @GetMapping(value = "list")
    @ApiOperation(value = "获取衣柜列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",required = true),
            @ApiImplicitParam(name = "current",value = "分页参数 从1开始",required = true),
            @ApiImplicitParam(name = "size",value = "分页参数",required = true)
    })
    public ResponseData adminList(Pages<Closet> pages ,String userId) throws Exception {
        if (CommonUtil.isEmpty(userId)) {
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        User user = LoginHelper.getCurrentUser(redisService);
        if (!user.getId().equals(userId)) {
            User user1 = userService.find(userId); {
                if (null == user1) {
                    return buildFailureJson(StatusConstant.NO_DATA,"未知用户");
                }
                if ((null != user1.getParentId() && !user.getId().equals(user1.getParentId())) &&
                        (null != user1.getAgencyUserId() && !user.getId().equals(user1.getAgencyUserId()))) {
                    return buildFailureJson(StatusConstant.NOT_AGREE,"对不起，您没有权限查看此用户的衣柜");
                }
            }
        }
        //获取此用户的配置
//        UserSystemConfig config = userSystemConfigService.find("userId",userId);

        List<Filter> filters = new ArrayList<>();
        filters.add(Filter.eq("userId",userId));
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",closetService.findPage(pages, filters, null));
    }


    /**
     * 根据Id删除
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID删除")
    @GetMapping(value = "del/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "衣柜id",required = true)
    })
    public ResponseData deleteById(@PathVariable String id) {
        closetService.delete(id);
        return ResponseData.success();
    }

    /**
     * 根据ID查询实体
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID查询实体")
    @GetMapping(value = "info")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "衣柜id",required = true)
    })
    public ResponseData info(String id){
        if (CommonUtil.isEmpty(id)) {
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        Closet closet = closetService.find(id);
        if (null == closet) {
            return buildFailureJson(StatusConstant.NO_DATA,"未知衣柜");
        }
        closet.setClothingList(clothingService.findList("closetId",id));
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",closet);
    }


    /**
     * 增加修改
     * @param closet
     * @return
     */
    @ApiOperation(value = "增加修改")
    @PostMapping(value = "save")
    public ResponseData save(@RequestBody  Closet closet){
        if(StringUtils.isEmpty(closet.getId())){
             closetService.save(closet);
        }else{
             closetService.update(closet);
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"操作成功");
    }

}

