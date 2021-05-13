package hu.szrnkapeter.cssjsminifier.compressor.js;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.javascript.jscomp.AbstractCommandLineRunner;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.CompilerOptions.LanguageMode;

import hu.szrnkapeter.cssjsminifier.util.JSCompileType;

import com.google.javascript.jscomp.SourceFile;

public class GoogleClosureCompilerJSCompressor implements JSCompressor {

	@Override
	public String compress(final String inputFilename, final JSCompileType compileType) throws Exception {
		final Compiler compiler = new Compiler();
		final CompilerOptions options = new CompilerOptions();
		options.setLanguageIn(LanguageMode.STABLE_IN); //todo : takes this from config
		options.setLanguageOut(LanguageMode.STABLE_OUT);

		if (JSCompileType.WHITESPACE.equals(compileType)) {
			CompilationLevel.WHITESPACE_ONLY.setOptionsForCompilationLevel(options);
		} else if (JSCompileType.SIMPLE.equals(compileType)) {
			CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
		} else {
			CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
		}

		List<SourceFile> list = null;

		try {
			list = AbstractCommandLineRunner.getBuiltinExterns(CompilerOptions.Environment.CUSTOM);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		list.add(SourceFile.fromFile(inputFilename));
		compiler.compile(new ArrayList<SourceFile>(), list, options);

		return compiler.toSource();
	}

}
