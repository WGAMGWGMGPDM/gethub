package com.zr.repo.utils;


import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: 张忍
 * @Date: 2020-03-22 19:06
 */
public class HttpUtil {
    private static Logger log= LoggerFactory.getLogger(HttpUtil.class);
    /**
     * 远程读取文件
     *
     * @param netUrl
     * @return
     */
    public static File getNetUrl(String netUrl) {
        //判断http和https
        File file = null;
        if (netUrl.startsWith("https://")) {
            log.error("暂不支持https协议文件拉取");
            //file = getNetUrlHttps(netUrl);
        } else {
            file = getNetUrlHttp(netUrl);
        }
        return file;
    }

    /**
     * 根据url获取文件 到临时目录
     * @param netUrl
     * @return
     */
    public static File getNetUrlHttp(String netUrl) {
        //对本地文件命名
        String fileName = StringUtil.reloadFile(netUrl);
        File file = null;

        URL urlfile;
        InputStream inStream = null;
        OutputStream os = null;
        try {
            file = File.createTempFile("net_url", fileName);
            //下载
            urlfile = new URL(netUrl);
            inStream = urlfile.openStream();
            os = new FileOutputStream(file);

            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            log.error("远程文件获取错误：" + netUrl);
            e.printStackTrace();
        } finally {
            try {
                if (null != os) {
                    os.close();
                }
                if (null != inStream) {
                    inStream.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    /**
     * 根据URL 获取文件到指定目录
     * @param netUrl 文件网络地址
     * @param path 文件存储本地路径
     * @return
     */
    public static File getNetUrlHttp(String netUrl,String path) {
        File file = null;
        URL urlfile;
        InputStream inStream = null;
        OutputStream os = null;
        try {
            log.info(path);

            file = new File( path);
            //如果父级文件夹不存在就创建
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            //下载
            urlfile = new URL(netUrl);
            inStream = urlfile.openStream();
            os = new FileOutputStream(file);

            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            log.error("远程文件获取错误：" + netUrl);
            e.printStackTrace();
        } finally {
            try {
                if (null != os) {
                    os.close();
                }
                if (null != inStream) {
                    inStream.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return file;
    }

//    /**
//     * 下载文件到本地(支持https)
//     *
//     * @param fileUrl 远程地址
//     * @throws Exception
//     */
//
//    public static File getNetUrlHttps(String fileUrl) {
//        //对本地文件进行命名
//        String file_name = StringUtil.reloadFile(fileUrl);
//        File file = null;
//
//        DataInputStream in = null;
//        DataOutputStream out = null;
//        try {
//            file = File.createTempFile("net_url", file_name);
//
//            SSLContext sslcontext = SSLContext.getInstance("SSL", "SunJSSE");
//            sslcontext.init(null, new TrustManager[]{new X509TrustUtil()}, new java.security.SecureRandom());
//            URL url = new URL(fileUrl);
//            HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
//                @Override
//                public boolean verify(String s, SSLSession sslsession) {
//                    log.warn("WARNING: Hostname is not matched for cert.");
//                    return true;
//                }
//            };
//            HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
//            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
//            HttpsURLConnection urlCon = (HttpsURLConnection) url.openConnection();
//            urlCon.setConnectTimeout(6000);
//            urlCon.setReadTimeout(6000);
//            int code = urlCon.getResponseCode();
//            if (code != HttpURLConnection.HTTP_OK) {
//                throw new Exception("文件读取失败");
//            }
//            // 读文件流
//            in = new DataInputStream(urlCon.getInputStream());
//            out = new DataOutputStream(new FileOutputStream(file));
//            byte[] buffer = new byte[2048];
//            int count = 0;
//            while ((count = in.read(buffer)) > 0) {
//                out.write(buffer, 0, count);
//            }
//            out.close();
//            in.close();
//        } catch (Exception e) {
//            log.error("远程图片获取错误：" + fileUrl);
//            e.printStackTrace();
//        } finally {
//            try {
//                if (null != out) {
//                    out.close();
//                }
//                if (null != in) {
//                    in.close();
//                }
//            } catch (Exception e2) {
//                e2.printStackTrace();
//            }
//        }
//
//        return file;
//    }

}
