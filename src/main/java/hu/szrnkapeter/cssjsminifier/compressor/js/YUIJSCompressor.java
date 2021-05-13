package hu.szrnkapeter.cssjsminifier.compressor.js;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

import hu.szrnkapeter.cssjsminifier.util.JSCompileType;

public class YUIJSCompressor implements JSCompressor {

	private static class YuiCompressorErrorReporter implements ErrorReporter {
		@Override
		public void error(final String message, final String sourceName, final int line, final String lineSource, final int lineOffset) {
			if (line < 0) {
				System.err.println(message);
			} else {
				System.err.println(line + ":" + lineOffset + ":" + message);
			}
		}

		@Override
		public EvaluatorException runtimeError(final String message, final String sourceName, final int line, final String lineSource, final int lineOffset) {
			error(message, sourceName, line, lineSource, lineOffset);
			return new EvaluatorException(message);
		}

		@Override
		public void warning(final String message, final String sourceName, final int line, final String lineSource, final int lineOffset) {
			if (line < 0) {
				System.err.println(message);
			} else {
				System.err.println(line + ":" + lineOffset + ":" + message);
			}
		}
	}

	@Override
	public String compress(final String inputFilename, final JSCompileType compileType) throws Exception {
		try(Reader in = new InputStreamReader(new FileInputStream(inputFilename), "UTF-8")) {
			final JavaScriptCompressor compressor = new JavaScriptCompressor(in, new YuiCompressorErrorReporter());
			final StringWriter out = new StringWriter();
			compressor.compress(out, -1, true, true, false, false);
			return out.toString();
		}
	}
}
