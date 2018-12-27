package cn.core.master;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public final class CustomResponse extends HttpServletResponseWrapper {
	private StringWriter strWriter = new StringWriter();
	private PrintWriter out = new PrintWriter(strWriter);

	public CustomResponse(HttpServletResponse response) {
		super(response);
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return out;
	}

	public String getContent() {
		return strWriter.toString();
	}
}