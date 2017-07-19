package com.wanda.pay.common.httpclient;

import com.wanda.pay.common.config.Config;
import com.wanda.pay.common.constants.PayConstants;
import com.wanda.pay.common.helper.CollectionHelper;
import com.wanda.pay.common.helper.MapHelper;
import com.wanda.pay.common.helper.Pair;
import com.wanda.pay.common.helper.URLEncodeHelper;
import com.wanda.pay.common.trace.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by robin on 16/12/22.
 */
@Slf4j
public class PayHttpClient {
    /**
     * GET 请求
     * @param url
     * @param otherParams 附加参数，按TraceID，SpanID的顺序
     * @return
     * @throws PayHttpClientException
     */
    public static String doGet(String url, String... otherParams) throws PayHttpClientException {
        return doGet(url, Config.DEFAULT_CONNECTIONT_IMEOUT, Config.DEFAULT_SO_TIMEOUT, otherParams);
    }

    public static String doGet(String url, Map<String, Object> params,int timeoutMillis, String... otherParams) throws PayHttpClientException {
        return doGet(PayHttpClient.appendParams(url, params),timeoutMillis, otherParams);
    }

    public String doGet(String url, Map<String, Object> params, String... otherParams) throws PayHttpClientException {
        return doGet(PayHttpClient.appendParams(url, params), otherParams);
    }

    public static String doGet(String url, int timeout, String... otherParams) throws PayHttpClientException {
        return doGet(url, timeout, timeout, otherParams);
    }

    public static String doGet(String url, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        CloseableHttpClient httpClient = createHttpClient();
        try {
            return doNoNeedCloseGet(httpClient, url, connTimeout, soTimeout, otherParams);
        } finally {
            close(httpClient);
        }
    }

    /**
     * GET 请求基础方法
     *
     * @param httpClient 需要自行管理其生命周期
     * @param url
     * @param connTimeout
     * @param soTimeout
     * @param otherParams
     * @return
     * @throws PayHttpClientException
     */
    public static String doNoNeedCloseGet(CloseableHttpClient httpClient, String url, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        Pair<String, String> tracePair = new Pair<>();
        url = appendCommonParam(url, tracePair, otherParams);

        HttpGet httpget = new HttpGet(url);
        return doRequest(httpClient, httpget, url, connTimeout, soTimeout, tracePair);
    }

    /**
     * POST请求，请求体以Map格式
     *
     * @param url
     * @param params
     * @param otherParams 附加参数，按TraceID，SpanID的顺序
     * @return
     * @throws PayHttpClientException
     */
    public static String doPost(String url, Map<String, Object> params, String... otherParams) throws PayHttpClientException {
        return doPost(url, params, Config.DEFAULT_CONNECTIONT_IMEOUT, Config.DEFAULT_SO_TIMEOUT, otherParams);
    }

    public static String doPost(String url, Map<String, Object> params, int timeout, String... otherParams) throws PayHttpClientException {
        return doPost(url, params, timeout, timeout, otherParams);
    }

    public static String doPost(String url, Map<String, Object> params, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        CloseableHttpClient httpClient = createHttpClient();
        try {
            return doNoNeedClosePost(httpClient, url, params, connTimeout, soTimeout, otherParams);
        } finally {
            close(httpClient);
        }
    }

    /**
     * POST请求，请求体以JSON格式
     *
     * @param url
     * @param json
     * @param otherParams 附加参数，按TraceID，SpanID的顺序
     * @return
     * @throws PayHttpClientException
     */
    public static String doPost(String url, String json, String... otherParams) throws PayHttpClientException {
        return doPost(url, json, Config.DEFAULT_CONNECTIONT_IMEOUT, Config.DEFAULT_SO_TIMEOUT, otherParams);
    }

    public static String doPost(String url, String json, int timeout, String... otherParams) throws PayHttpClientException {
        return doPost(url, json, timeout, timeout, otherParams);
    }

    public static String doPost(String url, String json, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        CloseableHttpClient httpClient = createHttpClient();
        try {
            return doNoNeedClosePost(httpClient, url, new StringEntity(json, URLEncodeHelper.DEFAULT_CHARSET), connTimeout, soTimeout, otherParams);
        } finally {
            close(httpClient);
        }
    }

    public static String doNoNeedClosePost(CloseableHttpClient httpClient, String url, String json, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        return doNoNeedClosePost(httpClient, url, new StringEntity(json, URLEncodeHelper.DEFAULT_CHARSET), connTimeout, soTimeout, otherParams);
    }

    public static String doNoNeedClosePost(CloseableHttpClient httpClient, String url, Map<String, Object> params, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        return doNoNeedClosePost(httpClient, url, buildFormEntity(params), connTimeout, soTimeout, otherParams);
    }

    /**
     * POST 请求基础方法
     *
     * @param httpClient 需要自行管理其生命周期
     * @param url
     * @param entity
     * @param connTimeout
     * @param soTimeout
     * @param otherParams
     * @return
     * @throws PayHttpClientException
     */
    public static String doNoNeedClosePost(CloseableHttpClient httpClient, String url, HttpEntity entity, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        Pair<String, String> tracePair = new Pair<>();
        url = appendCommonParam(url, tracePair, otherParams);

        HttpPost httppost = new HttpPost(url);
        putEntity(httppost, entity);
        return doRequest(httpClient, httppost, url, connTimeout, soTimeout, tracePair);
    }

    /**
     * DELETE 请求
     *
     * @param url
     * @param otherParams 附加参数，按TraceID，SpanID的顺序
     * @return
     * @throws PayHttpClientException
     */
    public static String doDelete(String url, String... otherParams) throws PayHttpClientException {
        return doDelete(url, Config.DEFAULT_CONNECTIONT_IMEOUT, Config.DEFAULT_SO_TIMEOUT, otherParams);
    }

    public static String doDelete(String url, int timeout, String... otherParams) throws PayHttpClientException {
        return doDelete(url, timeout, timeout, otherParams);
    }

    public static String doDelete(String url, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        CloseableHttpClient httpClient = createHttpClient();
        try {
            return doNoNeedCloseDelete(httpClient, url, connTimeout, soTimeout, otherParams);
        } finally {
            close(httpClient);
        }
    }

    /**
     * DELETE 请求基础方法
     *
     * @param httpClient 需要自行管理其生命周期
     * @param url
     * @param connTimeout
     * @param soTimeout
     * @param otherParams
     * @return
     * @throws PayHttpClientException
     */
    public static String doNoNeedCloseDelete(CloseableHttpClient httpClient, String url, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        Pair<String, String> tracePair = new Pair<>();
        url = appendCommonParam(url, tracePair, otherParams);

        HttpDelete httpdelete = new HttpDelete(url);
        return doRequest(httpClient, httpdelete, url, connTimeout, soTimeout, tracePair);
    }

    /**
     * PUT请求，请求体以Map格式，设置为默认超时时间
     *
     * @param url
     * @param params
     * @param otherParams 附加参数，按TraceID，SpanID的顺序
     * @return
     * @throws PayHttpClientException
     */
    public static String doPut(String url, Map<String, Object> params, String... otherParams) throws PayHttpClientException {
        return doPut(url, params, Config.DEFAULT_CONNECTIONT_IMEOUT, Config.DEFAULT_SO_TIMEOUT, otherParams);
    }

    /**
     * PUT请求，请求体以Map格式，设置连接超时与Socket超时一致
     *
     * @param url
     * @param params
     * @param timeout
     * @param otherParams
     * @return
     * @throws PayHttpClientException
     */
    public static String doPut(String url, Map<String, Object> params, int timeout, String... otherParams) throws PayHttpClientException {
        return doPut(url, params, timeout, timeout, otherParams);
    }

    public static String doPut(String url, Map<String, Object> params, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        CloseableHttpClient httpClient = createHttpClient();
        try {
            return doNoNeedClosePut(httpClient, url, params, connTimeout, soTimeout, otherParams);
        } finally {
            close(httpClient);
        }
    }

    /**
     * PUT请求，请求体以JSON格式
     *
     * @param url
     * @param json
     * @param otherParams 附加参数，按TraceID，SpanID的顺序
     * @return
     * @throws PayHttpClientException
     */
    public static String doPut(String url, String json, String... otherParams) throws PayHttpClientException {
        return doPut(url, json, Config.DEFAULT_CONNECTIONT_IMEOUT, Config.DEFAULT_SO_TIMEOUT, otherParams);
    }

    /**
     * PUT请求，请求体以JSON格式，设置连接超时与Socket超时一致
     *
     * @param url
     * @param json
     * @param timeout
     * @param otherParams
     * @return
     * @throws PayHttpClientException
     */
    public static String doPut(String url, String json, int timeout, String... otherParams) throws PayHttpClientException {
        return doPut(url, json, timeout, timeout, otherParams);
    }

    public static String doPut(String url, String json, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        CloseableHttpClient httpClient = createHttpClient();
        try {
            return doNoNeedClosePut(httpClient, url, new StringEntity(json, URLEncodeHelper.DEFAULT_CHARSET), connTimeout, soTimeout, otherParams);
        } finally {
            close(httpClient);
        }
    }

    public static String doNoNeedClosePut(CloseableHttpClient httpClient, String url, String json, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        return doNoNeedClosePut(httpClient, url, new StringEntity(json, URLEncodeHelper.DEFAULT_CHARSET), connTimeout, soTimeout, otherParams);
    }

    public static String doNoNeedClosePut(CloseableHttpClient httpClient, String url, Map<String, Object> params, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        return doNoNeedClosePut(httpClient, url, buildFormEntity(params), connTimeout, soTimeout, otherParams);
    }

    /**
     * PUT 请求基础方法
     *
     * @param httpClient 需要自行管理其生命周期
     * @param url
     * @param connTimeout
     * @param soTimeout
     * @param otherParams 附加参数，按TraceID，SpanID的顺序
     * @return
     * @throws PayHttpClientException
     */
    public static String doNoNeedClosePut(CloseableHttpClient httpClient, String url, HttpEntity entity, int connTimeout, int soTimeout, String... otherParams) throws PayHttpClientException {
        Pair<String, String> tracePair = new Pair<>();
        url = appendCommonParam(url, tracePair, otherParams);

        HttpPut httpput = new HttpPut(url);
        putEntity(httpput, entity);

        return doRequest(httpClient, httpput, url, connTimeout, soTimeout, tracePair);
    }

    /**
     * HTTP请求基础方法
     *
     * @param httpClient
     * @param method
     * @param url
     * @param connTimeout
     * @param soTimeout
     * @param tracePair Trace参数，可不传
     * @return
     * @throws PayHttpClientException
     */
    public static String doRequest(CloseableHttpClient httpClient, HttpRequestBase method, String url, int connTimeout, int soTimeout, Pair<String, String> tracePair) throws PayHttpClientException {
        long start = System.currentTimeMillis();
        log.info("PayHttpClient request url: {}, connTimeout: {}ms, soTimeout: {}ms, traceId: {}, spanId: {}", url, connTimeout, soTimeout, tracePair.getFirst(), tracePair.getSecond());

        String response = "";
        try {
            method.setConfig(buildRequestConfig(connTimeout, soTimeout));
            response = httpClient.execute(method, buildResponseHandler());
            return response;
        } catch (ConnectTimeoutException e) {
            throw new PayHttpClientException(PayHttpClientExceptionEnum.CONN_TIMEOUT);
        } catch (SocketTimeoutException e) {
            throw new PayHttpClientException(PayHttpClientExceptionEnum.SO_TIMEOUT);
        } catch (ClientProtocolException e) {
            throw new PayHttpClientException(PayHttpClientExceptionEnum.INVALID_STATUS, e.getMessage());
        } catch (Exception e) {
            log.error("PayHttpClient request error", e);
            throw new PayHttpClientException(PayHttpClientExceptionEnum.SYSTEM_ERROR);
        } finally {
            log.info("PayHttpClient request response: {}, cost: {}ms, traceId: {}, spanId: {}", response, System.currentTimeMillis() - start, tracePair.getFirst(), tracePair.getSecond());
        }
    }

    private static CloseableHttpClient createHttpClient() {

        HttpRequestRetryHandler retryHandler = new PayHttpClientRetryHandler();
        CloseableHttpClient httpClient = HttpClients.custom().setRetryHandler(retryHandler).build();
        return httpClient;
    }

    /**
     * 关闭HttpClient
     *
     * @param httpClient
     */
    private static void close(CloseableHttpClient httpClient) {
        try {
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (Exception e) {
        }
    }

    /**
     * 为某URL附加参数（会将值转换为UTF-8格式）
     *
     * @param url 目标URL
     * @param key 参数名称
     * @param value 参数值
     * @return 附加参数后的URL
     */
    public static String appendParam(String url, String key, String value) {
        StringBuilder sb = new StringBuilder(url);
        if (url.indexOf("?") > 0) {
            sb.append("&");
        } else {
            sb.append("?");
        }
        sb.append(key).append("=").append(URLEncodeHelper.encode(value));
        return sb.toString();
    }

    /**
     * 创建请求配置
     *
     * @param connTimeout
     * @param soTimeout
     * @return
     */
    private static RequestConfig buildRequestConfig(int connTimeout, int soTimeout) {
        return RequestConfig.custom().setConnectTimeout(connTimeout).setSocketTimeout(soTimeout).build();
    }

    /**
     * 创建请求回调函数
     *
     * @return
     */
    private static ResponseHandler<String> buildResponseHandler() {
        return new ResponseHandler<String>() {
            public String handleResponse(final HttpResponse response) throws IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    String result = entity != null ? EntityUtils.toString(entity) : null;
                    return result;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };
    }

    /**
     * 创建表单参数请求体
     *
     * @param params
     * @return
     */
    private static UrlEncodedFormEntity buildFormEntity (Map<String, Object> params) {
        UrlEncodedFormEntity entity = null;
        if (MapHelper.isNotEmpty(params)) {
            List<NameValuePair> formParams = new ArrayList<>();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() != null) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                }
            }
            if (CollectionHelper.isNotEmpty(formParams)) {
                entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
            }
        }
        return entity;
    }

    /**
     * 给URL附加额外参数(TraceID, SpanID)
     *
     * @param url
     * @param tracePair
     * @param otherParams
     * @return
     */
    private static String appendCommonParam(String url, Pair<String, String> tracePair, String... otherParams) {
        url = appendParam(url, PayConstants.KEY_UNIN_SOURCE, Config.DEFAULT_UNIN_SOURCE);
        String traceId;
        String childSpanId;
        if (ArrayUtils.isNotEmpty(otherParams)) {
            if (otherParams.length > 0) {
                traceId = otherParams[0];
            } else {
                traceId = TraceContext.current().getTraceId();
            }
            if (otherParams.length > 1) {
                childSpanId = otherParams[1];
            } else {
                childSpanId = TraceContext.current().getChildSpanId();
            }
        } else {
            traceId = TraceContext.current().getTraceId();
            childSpanId = TraceContext.current().getChildSpanId();
        }
        if (StringUtils.isNotBlank(traceId)) {
            url = appendParam(url, PayConstants.KEY_TRACE_ID, traceId);
        }
        if (StringUtils.isNotBlank(childSpanId)) {
            url = appendParam(url, PayConstants.KEY_SPAN_ID, childSpanId);
        }
        if (tracePair != null) {
            tracePair.setFirst(traceId);
            tracePair.setSecond(childSpanId);
        }
        return url;
    }

    /**
     * 设置请求体，如果请求体位字符串，则添加JSON处理Header，默认为JSON请求体
     * @param method
     * @param entity
     */
    private static void putEntity(HttpEntityEnclosingRequestBase method, HttpEntity entity) {
        if (entity != null) {
            method.setEntity(entity);
            if (!(entity instanceof UrlEncodedFormEntity)) {
                method.addHeader("Content-type", "application/json; charset=utf-8");
                method.setHeader("Accept", "application/json");
            }
        }
    }

    public static String appendParams(String url, Map<String, Object> params) {
        if (params != null && params.size() > 0) {
            StringBuilder fullUrl = new StringBuilder(url);
            if (url.indexOf("?") > -1) {

            } else {
                fullUrl.append("?1=1");
            }
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() != null && StringUtils.isNotBlank(entry.getValue().toString())) {
                    fullUrl.append("&").append(entry.getKey()).append("=").append(URLEncodeHelper.encode(entry.getValue().toString()));
                }
            }
            return fullUrl.toString();
        } else {
            return url;
        }
    }
}
