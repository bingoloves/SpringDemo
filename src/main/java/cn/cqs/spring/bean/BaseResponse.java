package cn.cqs.spring.bean;

public class BaseResponse<T> {
    private int code;
    private T data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 设置公共返回值
     * @param t
     */
    public static <T> BaseResponse<T> doSuccess(T t){
        BaseResponse<T> baseResponse = new BaseResponse<T>();
        baseResponse.setCode(1);
        baseResponse.setData(t);
        baseResponse.setMsg("请求成功");
        return baseResponse;
    }
    /**
     * 设置公共返回值
     */
    public static <T> BaseResponse<T> doError(String msg){
        BaseResponse<T> baseResponse = new BaseResponse<T>();
        baseResponse.setCode(0);
        baseResponse.setData(null);
        baseResponse.setMsg(msg);
        return baseResponse;
    }
}
