package com.key.jorigin.fastdfs;

/**
 * 系统通用异常 
 *
 */  
public class AppException extends Exception {  
    private static final long serialVersionUID = -1848618491499044704L;  
  
    private Module module;  
    private String code;  
    private String description;  
  
  
    public AppException(Module module, String code, String message) {  
        super(message);  
        this.module = module;  
        this.code = code;  
    }  
  
    public AppException(Module module, String code, String message, String description) {  
        super(message);  
        this.module = module;  
        this.code = code;  
        this.description = description;  
    }  
  
    /** 
     * 产生异常的模块 
     *  
     * @return 
     */  
    public Module getModule() {  
        return module;  
    }  
  
    /** 
     * 错误码 
     *  
     * @return 
     */  
    public String getCode() {  
        return code;  
    }  
  
    /** 
     * 用户可读描述信息 
     *  
     * @return 
     */  
    public String getDescription() {  
        return description;  
    }  
  
    @Override  
    public String toString() {  
        StringBuilder sb = new StringBuilder();  
        sb.append(getClass().getName());  
        sb.append(": [");  
        sb.append(module);  
        sb.append("] - ");  
        sb.append(code);  
        sb.append(" - ");  
        sb.append(getMessage());  
        if (getDescription() != null) {  
            sb.append(" - ");  
            sb.append(getDescription());  
        }  
        return sb.toString();  
    }  
}  