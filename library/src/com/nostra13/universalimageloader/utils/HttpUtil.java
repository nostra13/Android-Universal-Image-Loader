package com.nostra13.universalimageloader.utils;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;


public class HttpUtil {
	public static boolean IS_CMWAP = false;
	private static final String CMWAP_HOST = "10.0.0.172";
	private static final int CMWAP_PORT = 80;

	
	/**
	 * 将传入的键/值对参数转换为NameValuePair参数集
	 * 
	 * @param paramsMap
	 *            参数集, 键/值对
	 * @return NameValuePair参数集
	 */
	public static List<NameValuePair> getParamsList(
			Map<String, String> paramsMap) {
		if (paramsMap == null || paramsMap.size() == 0) {
			return null;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> map : paramsMap.entrySet()) {
			params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
		}
		return params;
	}

	/**
	 * 将传入的键/值对参数转换为String
	 * 
	 * @param paramsMap
	 *            参数集, 键/值对
	 * @return NameValuePair参数集
	 */
	public static String getParamsString(Map<String, String> paramsMap) {
		if (paramsMap == null || paramsMap.size() == 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> map : paramsMap.entrySet()) {
			try {
				sb.append(URLEncoder.encode(map.getKey(), HTTP.UTF_8) + "="
						+ URLEncoder.encode(map.getValue(), HTTP.UTF_8) + "&");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String a = sb.toString();
		a = a.substring(0, a.length() - 1);
		return a;
	}

	/**
	 * urlByGet2InputStreamSSL
	 * **/
	private static javax.net.ssl.SSLSocketFactory socketFactory;
	private static void initSSLSocketFactory() {
		if (socketFactory != null) {
			if (HttpsURLConnection.getDefaultSSLSocketFactory().equals(
					socketFactory)) {
				return;
			} else {
				HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);
				return;
			}
		}
		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			TrustManager trustManager = new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}
			};
			sslContext.init(null, new TrustManager[] { trustManager }, null);
			socketFactory = sslContext.getSocketFactory();
			HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	/**
	 * HttpsURLConnection Get方式提交
	 * 
	 * @param url
	 *            提交地址
	 */
	public static InputStream urlByGet2InputStreamSSL(String urlstr) {
		
		initSSLSocketFactory();
		URL mUrl;
		try {
			mUrl = new URL(urlstr);
			HttpsURLConnection urlConn = (HttpsURLConnection) mUrl.openConnection();
			urlConn.setConnectTimeout(10 * 1000);
			urlConn.setReadTimeout(10 * 1000);

			urlConn.connect();
			if (urlConn.getResponseCode() == 200) {
				return urlConn.getInputStream();
				//return inputstream;
			} else {
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * javax.net.ssl.SSLPeerUnverifiedException: No peer certificate
	 * 
	 * @author Administrator
	 * 
	 */
	public static class SSLSocketFactoryEx extends SSLSocketFactory {

		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}
			};
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	public static HttpClient getSSLHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			
			//设置超时
			int timeoutConnection = 20000;
			HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
			int timeoutSocket = 20000;
			HttpConnectionParams.setSoTimeout(params, timeoutSocket);
			
			// 设置 user agent
			String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
			HttpProtocolParams.setUserAgent(params, userAgent);
			
			
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);
			DefaultHttpClient httpClient = new DefaultHttpClient(ccm, params);
			// 设置cmwap代理
			if (IS_CMWAP) {
				HttpHost proxy = new HttpHost(CMWAP_HOST, CMWAP_PORT);
				httpClient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}
	/**
	 * HttpsURLConnection 
	 * 
	 * @param url
	 *            
	 */
	public static InputStream urlByGet2InputStream(String urlstr) {
		URL mUrl;
		try {
			mUrl = new URL(urlstr);
			HttpURLConnection urlConn = (HttpURLConnection) mUrl.openConnection();			
			urlConn.setConnectTimeout(10 * 1000);
			urlConn.setReadTimeout(10 * 1000);

			return urlConn.getInputStream();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}