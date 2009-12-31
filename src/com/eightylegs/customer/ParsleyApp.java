package com.eightylegs.customer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.envjs.Global;
import org.mozilla.javascript.tools.envjs.Window;

public class ParsleyApp implements I80App {

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Args: parselet html");
			System.exit(1);
		}
		ParsleyApp app = new ParsleyApp();
		app.initialize(new Properties(), getBytes(new File(args[0])));
		System.out.println(new String(app.processDocument(getBytes(new File(
				args[1])), args[1], null, null, null)));
	}

	private static byte[] getBytes(File file) throws IOException {
		return getBytes(new FileInputStream(file));
	}

	private static byte[] getBytes(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));
		StringBuffer buf = new StringBuffer();
		String line;

		while ((line = reader.readLine()) != null) {
			buf.append(line + "\n");
		}
		return buf.toString().getBytes();

	}

	private static byte[] getBytes(String path) {
		try {
			return getBytes(com.eightylegs.customer.ParsleyApp.class
					.getResourceAsStream(path));
		} catch (Exception e) {
			System.err.println("Couldn't access resource: " + path);
			throw new RuntimeException(e);
		}
	}

	private String parselet;
	private Global global;
	private Context cx;

	@Override
	public String getVersion() {
		return "80App_1.2";
	}

	@Override
	public void initialize(Properties properties, byte[] data) {
		parselet = new String(data);
	}

	@Override
	public byte[] processDocument(byte[] documentContent, String url,
			Map<String, String> headers,
			Map<Default80AppPropertyKeys, Object> default80AppProperties,
			String statusCodeLine) {
		Window window = new Window();
		global = new Global(window);
		cx = ContextFactory.getGlobal().enterContext();
		global.init(cx);
		global.initStandardObjects(cx, false);
		cx.setOptimizationLevel(-1);
		cx.setLanguageVersion(Context.VERSION_1_5);
		try {
			load("env-rhino.js");

			Object doc = Context.javaToJS(new String(documentContent), global);
			Object jsurl = Context.javaToJS(url, global);
			Object jsparselet = Context.javaToJS(parselet, global);
			ScriptableObject.putProperty(global, "externalDoc", doc);
			ScriptableObject.putProperty(global, "externalUrl", jsurl);
			ScriptableObject
					.putProperty(global, "externalParselet", jsparselet);
			eval("Envjs.load(externalUrl, externalDoc);");
			load("jquery.js");
			load("json2.js");
			load("jquery.parsley.js");
			return eval(
					"JSON.stringify(pQuery.extractAndGroup(eval('('+externalParselet+')')))")
					.getBytes();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	private String eval(String string) {
		return Context.toString(cx.evaluateString(global, string, "eval", 1,
				null));
	}

	private void load(String string) throws IOException {
		cx
				.evaluateString(global, new String(getBytes(string)), string,
						1, null);
	}

	@Override
	public Collection<String> parseLinks(byte[] documentContent, String url,
			Map<String, String> headers,
			Map<Default80AppPropertyKeys, Object> default80AppProperties,
			String statusCodeLine) {
		return null;// Default link handling
	}
}
