package com.key.jorigin.fastdfs;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by zwh on 18/1/4.
 */
public class SimpleFastdfsComponent {

    Map<String, Integer> trackerIpPorts = null;
    //可以不要，如果用http拼上storeIp下载,则需要
    String storeIp = null;
    boolean supportHttp = false;

    String charset = "UTF-8";
    int connect_timeout = 2000;
    int network_timeout = 2000;

    TrackerServer trackerServer;
    StorageServer storageServer;
    TrackerClient trackerClient;

    public SimpleFastdfsComponent() {
        this(null);
    }

    public SimpleFastdfsComponent(Map<String, Integer> trackerIpPorts) {
        this(trackerIpPorts, null);
    }

    public SimpleFastdfsComponent(Map<String, Integer> trackerIpPorts, String storeIp) {
        this(trackerIpPorts, storeIp, null, null, null);
    }

    public SimpleFastdfsComponent(Map<String, Integer> trackerIpPorts, String storeIp, String charset, Integer connectTimeout, Integer networkTimeout) {
        if (trackerIpPorts != null) {
            this.trackerIpPorts = trackerIpPorts;
        } else {
            this.trackerIpPorts = new HashMap<>(2, 1.0f);
            this.trackerIpPorts.put("192.168.1.177", 22122);
        }

        if (StringUtils.isBlank(storeIp)) {
            this.storeIp = "192.168.1.179";
        } else {
            this.storeIp = storeIp;
            supportHttp = true;
        }

        if (connectTimeout != null) {
            this.connect_timeout = connectTimeout;
        }
        if (networkTimeout != null) {
            this.network_timeout = networkTimeout;
        }
        if (StringUtils.isNotBlank(charset)) {
            this.charset = charset;
        }
        init();
    }


    public void init() {
        ClientGlobal.setG_charset(this.charset);
        ClientGlobal.setG_network_timeout(this.network_timeout);
        ClientGlobal.setG_connect_timeout(this.connect_timeout);

        InetSocketAddress[] trackerAddrServers = new InetSocketAddress[this.trackerIpPorts.size()];
        Iterator<Map.Entry<String, Integer>> iterator = this.trackerIpPorts.entrySet().iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            trackerAddrServers[index++] = new InetSocketAddress(entry.getKey(), entry.getValue());
        }

        TrackerGroup trackerGroup = new TrackerGroup(trackerAddrServers);
        ClientGlobal.setG_tracker_group(trackerGroup);

        this.trackerClient = new TrackerClient();

        int tryCount = 0;
        while (tryCount < 3) {
            try {
                trackerServer = trackerClient.getConnection();
//                StorageClient1 storageClient1 = new StorageClient1(trackerServer, null);
                break;
            } catch (Exception e) {
                //log e
                tryCount++;
            }
        }
    }

    public void group() throws Exception {

        StructGroupStat[] stats = this.trackerClient.listGroups(trackerServer);

        for (StructGroupStat stat : stats) {
            System.out.println(stat.getGroupName() + "__" + stat.getFreeMB() + "__" + stat.getStorageCount() + "__"
                    + stat.getStorageHttpPort() + "__" + stat.getStoragePort() + "__" + stat.getTotalMB());

            System.out.println("==========================");
            StructStorageStat[] storageStats = this.trackerClient.listStorages(trackerServer, stat.getGroupName());

            for (StructStorageStat sstat : storageStats) {
                System.out.println(sstat.getDomainName() + "__" + sstat.getFreeMB() + "__" + sstat.getIpAddr() + "__"
                        + sstat.getStorageHttpPort() + "__" + sstat.getStoragePort() + "__" + sstat.getTotalMB());
            }
            System.out.println("==========================");
        }

        /**
         *
         * group2__3748__2__8888__23000__35851
         ==========================
         __4844__192.168.1.177__8888__23000__35851
         __3748__192.168.1.179__8888__23000__35851
         ==========================
         */

    }


    public String upload(byte[] fileBytes, String extName, NameValuePair[] metaList) throws Exception {

        StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);
        String fileId = storageClient1.upload_file1(fileBytes, extName, metaList);

        return fileId;
    }

    public byte[] download(String fileId, boolean isTest) throws Exception {
        StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);
        byte[] bytes = storageClient1.download_file1(fileId);
        if (isTest) {
            saveFile(bytes);
        }
        return bytes;
    }

    public byte[] downloadHttp(String fileId, boolean isTest) throws Exception {

        byte[] bytes = downloadForHttp("http://" + storeIp + "/" + fileId);

        if (isTest) {
            saveFile(bytes);
        }

        return bytes;
    }

    private byte[] downloadForHttp(String downLoadUrl) {

        byte[] fileBytes = null;
        int retryTime = 0;
        int statusCode = 0;
        try {
            do {
                HttpClient httpClient = new HttpClient();
                HttpMethod method = new GetMethod(downLoadUrl);
                Header header = new Header("User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
                method.addRequestHeader(header);
                statusCode = httpClient.executeMethod(method);
                if (statusCode == 200) {
                    fileBytes = method.getResponseBody();
                }
                retryTime++;
            } while (statusCode != 200 && retryTime < 3);
        } catch (Exception e) {
        }
        return fileBytes;
    }

    private void saveFile(byte[] bytes) throws FileNotFoundException {
        String fileName = "/tmp/" + UUID.randomUUID().toString() + ".txt";
        System.out.println("fileName-->" + fileName);
        FileOutputStream fos = new FileOutputStream(new File(fileName));
        try {
            fos.write(bytes);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] s) throws Exception {

        SimpleFastdfsComponent simpleFastdfsComponent = new SimpleFastdfsComponent();
//
//        String fileId = simpleFastdfsComponent.upload(getFileByte(), "txt", null);
//
//        System.out.println(fileId);
//
//        simpleFastdfsComponent.download(fileId, true);
//
//        simpleFastdfsComponent.downloadHttp(fileId, true);

        simpleFastdfsComponent.group();
    }

    private static byte[] getFileByte() throws FileNotFoundException {

        File file = new File("/tmp/jjjjjjjjjjj.txt");

        byte[] bytes = new byte[(int) file.length()];

        FileInputStream fls = new FileInputStream(file);
        try {
            fls.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fls.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

}
