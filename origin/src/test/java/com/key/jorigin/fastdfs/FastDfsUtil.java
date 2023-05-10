package com.key.jorigin.fastdfs;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketTimeoutException;
import java.util.UUID;

/**
 * @ClassName: FastDfsUtil
 * @Description: fastdfs文件操作工具类 1).初始化连接池； 2).实现文件的上传与下载;
 */
public class FastDfsUtil {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FastDfsUtil.class);
    /**
     * 连接池
     */
    private ConnectionPool connectionPool = null;
    /**
     * 连接池默认最小连接数
     */
    private long minPoolSize = 10;
    /**
     * 连接池默认最大连接数
     */
    private long maxPoolSize = 30;
    /**
     * 当前创建的连接数
     */
    private volatile long nowPoolSize = 0;
    /**
     * 默认等待时间（单位：秒）
     */
    private long waitTimes = 200;

    public FastDfsUtil(long minPoolSize, long maxPoolSize, long waitTimes) {
        this.minPoolSize = minPoolSize;
        this.maxPoolSize = maxPoolSize;
        this.waitTimes = waitTimes;
    }

    /**
     * 初始化线程池
     *
     * @Description:
     */
    public void init() {
        String logId = UUID.randomUUID().toString();
        LOGGER.info("[初始化线程池(Init)][" + logId + "][默认参数：minPoolSize="
                + minPoolSize + ",maxPoolSize=" + maxPoolSize + ",waitTimes="
                + waitTimes + "]");
        connectionPool = new ConnectionPool(minPoolSize, maxPoolSize, waitTimes);
    }

    /**
     * @param groupName 组名如group0
     * @param fileBytes 文件字节数组
     * @param extName   文件扩展名：如png
     * @param linkUrl   访问地址：http://image.xxx.com
     * @return 图片上传成功后地址
     * @throws AppException
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public String upload(String groupName, byte[] fileBytes, String extName,
                         String linkUrl) throws AppException {
        String logId = UUID.randomUUID().toString();
        /** 封装文件信息参数 */
        NameValuePair[] metaList = new NameValuePair[]{new NameValuePair(
                "fileName", "")};
        TrackerServer trackerServer = null;
        try {

            /** 获取fastdfs服务器连接 */
            trackerServer = connectionPool.checkout(logId);
            StorageServer storageServer = null;
            StorageClient1 client1 = new StorageClient1(trackerServer,
                    storageServer);

            /** 以文件字节的方式上传 */
            String[] results = client1.upload_file(groupName, fileBytes,
                    extName, metaList);

            /** 上传完毕及时释放连接 */
            connectionPool.checkin(trackerServer, logId);

            LOGGER.info("[上传文件（upload）-fastdfs服务器相应结果][" + logId
                    + "][result：results=" + results + "]");

            /** results[0]:组名，results[1]:远程文件名 */
            if (results != null && results.length == 2) {

                return linkUrl + "/" + results[0] + "/" + results[1];
            } else {
                /** 文件系统上传返回结果错误 */
                throw ERRORS.UPLOAD_RESULT_ERROR.ERROR();
            }
        } catch (AppException e) {

            LOGGER.error("[上传文件（upload)][" + logId + "][异常：" + e + "]");
            throw e;

        } catch (SocketTimeoutException e) {
            LOGGER.error("[上传文件（upload)][" + logId + "][异常：" + e + "]");
            throw ERRORS.WAIT_IDLECONNECTION_TIMEOUT.ERROR();
        } catch (Exception e) {

            LOGGER.error("[上传文件（upload)][" + logId + "][异常：" + e + "]");
            connectionPool.drop(trackerServer, logId);
            throw ERRORS.SYS_ERROR.ERROR();

        }

    }

    /**
     * @param group_name      组名
     * @param remote_filename 远程文件名称
     * @throws AppException
     * @Description: 删除fastdfs服务器中文件
     */
    public void deleteFile(String group_name, String remote_filename)
            throws AppException {

        String logId = UUID.randomUUID().toString();
        LOGGER.info("[ 删除文件（deleteFile）][" + logId + "][parms：group_name="
                + group_name + ",remote_filename=" + remote_filename + "]");
        TrackerServer trackerServer = null;

        try {
            /** 获取可用的tracker,并创建存储server */
            trackerServer = connectionPool.checkout(logId);
            StorageServer storageServer = null;
            StorageClient1 client1 = new StorageClient1(trackerServer,
                    storageServer);
            /** 删除文件,并释放 trackerServer */
            int result = client1.delete_file(group_name, remote_filename);

            /** 上传完毕及时释放连接 */
            connectionPool.checkin(trackerServer, logId);

            LOGGER.info("[ 删除文件（deleteFile）--调用fastdfs客户端返回结果][" + logId
                    + "][results：result=" + result + "]");

            /** 0:文件删除成功，2：文件不存在 ，其它：文件删除出错 */
            if (result == 2) {

                throw ERRORS.NOT_EXIST_FILE.ERROR();

            } else if (result != 0) {

                throw ERRORS.DELETE_RESULT_ERROR.ERROR();

            }

        } catch (AppException e) {

            LOGGER.error("[ 删除文件（deleteFile）][" + logId + "][异常：" + e + "]");
            throw e;

        } catch (SocketTimeoutException e) {
            LOGGER.error("[ 删除文件（deleteFile）][" + logId + "][异常：" + e + "]");
            throw ERRORS.WAIT_IDLECONNECTION_TIMEOUT.ERROR();
        } catch (Exception e) {

            LOGGER.error("[ 删除文件（deleteFile）][" + logId + "][异常：" + e + "]");
            connectionPool.drop(trackerServer, logId);
            throw ERRORS.SYS_ERROR.ERROR();

        }
    }

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public long getMinPoolSize() {
        return minPoolSize;
    }

    public void setMinPoolSize(long minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public long getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(long maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public long getNowPoolSize() {
        return nowPoolSize;
    }

    public void setNowPoolSize(long nowPoolSize) {
        this.nowPoolSize = nowPoolSize;
    }

    public long getWaitTimes() {
        return waitTimes;
    }

    public void setWaitTimes(long waitTimes) {
        this.waitTimes = waitTimes;
    }


    public static void main(String[] a) {
//        #fastdfs连接池最大连接数
//                maxPoolSize=30
//#fastdfs等待时间（单位：秒）
//        waitTimes=400
//#fastdfs连接池最小连接数
//                minPoolSize=20

        FastDfsUtil fastDfsUtil = new FastDfsUtil(30, 40, 400);
        fastDfsUtil.init();


//        fastDfsUtil.upload()
//        fastDfsUtil.deleteFile();
    }
}  