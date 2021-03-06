package com.magicbeans.collaboration.controller.base;

import com.magicbeans.base.ajax.ResponseData;
import org.apache.log4j.Logger;


/**
 * 共同的Controller基类.
 */
public class BaseController {


    protected Logger logger = Logger.getLogger(BaseController.class);
    /**
     * 构造rest返回参数
     * @param status
     * @param code
     * @param message
     * @param data
     * @return
     */
    protected ResponseData buildResultData(boolean status, int code, String message, Object data) {
        ResponseData result = new ResponseData();
        result.setStatus(status);
        result.setCode(code);
        result.setMsg(message);
        result.setBody(data);
        return result;
    }

    /**
     * 成功返回
     * @param code
     * @param msg
     * @param data
     * @return
     */
    protected ResponseData buildSuccessJson(int code, String msg, Object data) {
        return buildResultData(true, code, msg, data);
    }

    /**
     * 成功不带数据返回
     * @param code
     * @param msg
     * @return
     */
    public ResponseData buildSuccessCodeJson(int code, String msg) {
        return buildSuccessJson(code, msg, null);
    }

    /**
     * 失败返回
     * @param code
     * @param msg
     * @return
     */
    protected ResponseData buildFailureJson(int code, String msg) {
        return buildResultData(false, code, msg, null);
    }

    /**
     * 失败返回
     * @param msg
     * @return
     */
    protected ResponseData buildFailureMessage(String msg) {
        return buildFailureJson(-1, msg);
    }

}
