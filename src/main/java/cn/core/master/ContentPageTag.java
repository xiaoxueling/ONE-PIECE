package cn.core.master;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class ContentPageTag extends BodyTagSupport {
	private final String masterFolderPath = "/WEB-INF/master/";
	private final String masterPageSuffix = ".jsp";

	private static final long serialVersionUID = 1L;

	@Override
	public void doInitBody() throws JspException {
		try {
			this.pageContext.getRequest().setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		super.doInitBody();
	}

	@Override
	public int doAfterBody() throws JspException {
		return SKIP_BODY;
	}

	@Override
	public int doStartTag() throws JspException {
		// 执行子标签
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		CustomResponse bufferedResponse = new CustomResponse((HttpServletResponse) this.pageContext.getResponse());
		try {
			// 渲染页面
			this.pageContext.getServletContext().getRequestDispatcher(this.getMasterPageUrl())
					.include(this.pageContext.getRequest(), bufferedResponse);
			// out.clearBuffer();
			out.write(bufferedResponse.getContent());
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return SKIP_PAGE;
	}

	private String getMasterPageUrl() {
		return this.masterFolderPath + this.materPageId + this.masterPageSuffix;
	}

	private String materPageId;

	public String getMaterPageId() {
		return materPageId;
	}

	public void setMaterPageId(String materPageId) {
		this.materPageId = materPageId;
	}

}