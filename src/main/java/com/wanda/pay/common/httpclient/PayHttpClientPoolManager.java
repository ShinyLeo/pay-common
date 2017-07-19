package com.wanda.pay.common.httpclient;

import com.wanda.pay.common.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PayHttpClientPoolManager {


    private static volatile PayHttpClientPoolManager instance;

    private PoolingHttpClientConnectionManager httpClientManager;
    private IdleConnectionMonitorThread idleEvictThread;

    private PayHttpClientPoolManager() {
        try {
            init();
        } catch (Exception e) {
            log.warn("Init http client error", e);
        }
    }

    private void init() throws Exception {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", plainsf).register("https", sslsf).build();
        httpClientManager = new PoolingHttpClientConnectionManager(registry);
        httpClientManager.setMaxTotal(Config.DEFAULT_MAX_TOTAL);
        httpClientManager.setDefaultMaxPerRoute(Config.DEFAULT_MAX_PER_ROUTE);

        idleEvictThread = new IdleConnectionMonitorThread(httpClientManager);
        idleEvictThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                instance.shutdown();
                log.info("Shutdown httpclient pool finished");
            }
        });

        log.info("Init httpclient pool successful, pool size: {}, max size per route: {}, idle timeout: {}ms, clear interval: {}ms.", Config.DEFAULT_MAX_TOTAL, Config.DEFAULT_MAX_PER_ROUTE, Config.DEFAULT_IDLE_TIMEOUT, Config.DEFAULT_CLEAR_IDLE_INTERVAL);
    }

    public static PayHttpClientPoolManager getInstance() {
        if (instance == null) {
            synchronized (PayHttpClientPoolManager.class) {
                if (instance == null) {
                    instance = new PayHttpClientPoolManager();
                }
            }
        }
        return instance;
    }

    /**
     * 关闭连接池
     */
    public void shutdown() {
        this.idleEvictThread.shutdown();
        this.httpClientManager.shutdown();
    }

    /**
     * 从连接池中获取HttpClient实例,不需要手动关闭
     * <pre>
     *     CloseableHttpResponse response = null;
     *     try {
     *         CloseableHttpClient httpClient = PayHttpClientPoolManager.getInstance().getConnection();
     *         response = httpClient.execute(...);
     *     } finally {
     *         if (response != null) {
     *             try {
     *                 response.close();
     *             } catch (IOException e) {
     *
     *             }
     *         }
     *     }
     * </pre>
     *
     * @return HttpClient实例
     */
    public CloseableHttpClient getConnection() {
        CloseableHttpClient httpClient = HttpClients.custom().setRetryHandler(new PayHttpClientRetryHandler()).setConnectionManager(httpClientManager).build();
        return httpClient;
    }

    /**
     * GET请求
     *
     * @param url
     * @param otherParams
     * @return
     * @throws PayHttpClientException
     */
    public String doGet(String url, String... otherParams) throws PayHttpClientException {
        return doGet(url, Config.DEFAULT_CONNECTIONT_IMEOUT, Config.DEFAULT_SO_TIMEOUT, otherParams);
    }

    public String doGet(String url, Map<String, Object> params, String... otherParams) throws PayHttpClientException {
        return doGet(PayHttpClient.appendParams(url, params), otherParams);
    }

    public String doGet(String url, int timeout, String... otherParams) throws PayHttpClientException {
        return doGet(url, timeout, timeout, otherParams);
    }

    public String doGet(String url, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        return PayHttpClient.doNoNeedCloseGet(getConnection(), url, connTimeout, soTimeout, otherParams);
    }

    /**
     * POST请求
     * @param url
     * @param params
     * @param otherParams
     * @return
     * @throws PayHttpClientException
     */
    public String doPost(String url, Map<String, Object> params, String... otherParams) throws PayHttpClientException {
        return doPost(url, params, Config.DEFAULT_CONNECTIONT_IMEOUT, Config.DEFAULT_SO_TIMEOUT, otherParams);
    }

    public String doPost(String url, Map<String, Object> params, int timeout, String... otherParams) throws PayHttpClientException {
        return doPost(url, params, timeout, timeout, otherParams);
    }

    public String doPost(String url, Map<String, Object> params, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        return PayHttpClient.doNoNeedClosePost(getConnection(), url, params, connTimeout, soTimeout, otherParams);
    }

    public String doPost(String url, String json, String... otherParams) throws PayHttpClientException {
        return doPost(url, json, Config.DEFAULT_CONNECTIONT_IMEOUT, Config.DEFAULT_SO_TIMEOUT, otherParams);
    }

    public String doPost(String url, String json, int timeout, String... otherParams) throws PayHttpClientException {
        return doPost(url, json, timeout, timeout, otherParams);
    }

    public String doPost(String url, String json, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        return PayHttpClient.doNoNeedClosePost(getConnection(), url, json, connTimeout, soTimeout, otherParams);
    }

    public String doDelete(String url, String... otherParams) throws PayHttpClientException {
        return doDelete(url, Config.DEFAULT_CONNECTIONT_IMEOUT, Config.DEFAULT_SO_TIMEOUT, otherParams);
    }

    public String doDelete(String url, int timeout, String... otherParams) throws PayHttpClientException {
        return doDelete(url, timeout, timeout, otherParams);
    }

    public String doDelete(String url, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        return PayHttpClient.doNoNeedCloseDelete(getConnection(), url, connTimeout, soTimeout, otherParams);
    }

    public String doPut(String url, Map<String, Object> params, String... otherParams) throws PayHttpClientException {
        return doPut(url, params, Config.DEFAULT_CONNECTIONT_IMEOUT, Config.DEFAULT_SO_TIMEOUT, otherParams);
    }

    public String doPut(String url, Map<String, Object> params, int timeout, String... otherParams) throws PayHttpClientException {
        return doPut(url, params, timeout, timeout, otherParams);
    }

    public String doPut(String url, Map<String, Object> params, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        return PayHttpClient.doNoNeedClosePut(getConnection(), url, params, connTimeout, soTimeout, otherParams);
    }

    public String doPut(String url, String json, String... otherParams) throws PayHttpClientException {
        return doPut(url, json, Config.DEFAULT_CONNECTIONT_IMEOUT, Config.DEFAULT_SO_TIMEOUT, otherParams);
    }

    public String doPut(String url, String json, int timeout, String... otherParams) throws PayHttpClientException {
        return doPut(url, json, timeout, timeout, otherParams);
    }

    public String doPut(String url, String json, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        return PayHttpClient.doNoNeedClosePut(getConnection(), url, json, connTimeout, soTimeout, otherParams);
    }

    /**
     * 闲置连接清理线程
     */
    private static class IdleConnectionMonitorThread extends Thread {

        private final PoolingHttpClientConnectionManager connMgr;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread(PoolingHttpClientConnectionManager connMgr) {
            this.connMgr = connMgr;
            this.setDaemon(true);
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(Config.DEFAULT_CLEAR_IDLE_INTERVAL);
                        connMgr.closeExpiredConnections();
                        connMgr.closeIdleConnections(Config.DEFAULT_IDLE_TIMEOUT, TimeUnit.MILLISECONDS);
                        log.info("Clear idle httpclient pool connections finished");
                    }
                }
            } catch (InterruptedException ex) {
                log.warn("IdleConnectionMonitorThread terminated");
            }
        }

        public void shutdown() {
            if (!shutdown) {
                shutdown = true;
                synchronized (this) {
                    notifyAll();
                }
            }
        }
    }
}