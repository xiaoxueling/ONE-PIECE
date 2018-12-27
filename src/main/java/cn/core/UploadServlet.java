package cn.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	}

	private void commonFileProcess(String filename, InputStream is) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(filename));
			int b = 0;
			while ((b = is.read()) != -1) {
				fos.write(b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!request.getContentType().split(";")[0].equals("multipart/form-data"))
			return;

		Collection<Part> parts = request.getParts();
		Part part = null;
		for (Part p : parts) {
			if (p.getName().equals("uploadify")) {
				part = p;
				break;
			}
		}

		// 设置接收的编码格式
		request.setCharacterEncoding("UTF-8");
		Date date = new Date();// 获取当前时间
		SimpleDateFormat sdfFolderName = new SimpleDateFormat("yyyyMMdd");

		String newFolderName = sdfFolderName.format(date); // 存放文件夹名
		// 获得容器中上传文件夹所在的物理路径
		String userDir = System.getProperty("user.dir");
		String savePath = userDir + "\\upload\\" + newFolderName + "\\";
		File file = new File(savePath);
		if (!file.isDirectory()) {
			file.mkdirs();
		}

		String cd = part.getHeader("Content-Disposition");
		String[] cds = cd.split(";");
		String origionalFileName = cds[2].substring(cds[2].indexOf("=") + 1).substring(cds[2].lastIndexOf("//") + 1)
				.replace("\"", "");
		String formatName = origionalFileName.substring(origionalFileName.lastIndexOf("."));
		String newfileName = UUID.randomUUID().toString() + formatName;// 存放文件名称

		InputStream is = part.getInputStream();
		commonFileProcess(savePath + "\\" + newfileName, is);

		Object needOriginFileName = request.getParameter("origin");
		if (needOriginFileName != null && needOriginFileName.toString() == "true") {
			response.getWriter().write(origionalFileName + "|/upload/" + newFolderName + "/" + newfileName);
		} else {
			response.getWriter().write("/upload/" + newFolderName + "/" + newfileName);
		}

	}

}
