package cn.core.master;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class MasterPageTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	public int doAfterBody() throws JspException {
		return SKIP_BODY;
	}

	@Override
	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		if (bodyContent != null) {
			if (out instanceof BodyContent) {
				out = ((BodyContent) out).getEnclosingWriter();
			}
		}
		String content = this.bodyContent.getString();
		try {
			this.bodyContent.clear();
			out.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return SKIP_PAGE;
	}
}