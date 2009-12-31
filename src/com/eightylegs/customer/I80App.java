package com.eightylegs.customer;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

public interface I80App {

	public String getVersion();

	public void initialize(Properties properties, byte[] data);

	public Collection<String> parseLinks(byte[] documentContent, String url,
			Map<String, String> headers,
			Map<Default80AppPropertyKeys, Object> default80AppProperties,
			String statusCodeLine);

	public byte[] processDocument(byte[] documentContent, String url,
			Map<String, String> headers,
			Map<Default80AppPropertyKeys, Object> default80AppProperties,
			String statusCodeLine);

}
