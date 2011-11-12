package com.varun.yfs.server.schoolscreening.export;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class ExportServlet extends HttpServlet
{
	private static final long serialVersionUID = 7669055084009277659L;
	private static Logger logger = Logger.getLogger(ExportServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		OutputStream out = null;
		try
		{
			String fileName = request.getParameter("ExportedFilename");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

			BufferedInputStream iStream = new BufferedInputStream(new FileInputStream(fileName));
			byte[] arg0 = new byte[2048];
			while (iStream.read(arg0) != -1)
				response.getOutputStream().write(arg0);

			logger.debug("Finished streaming file contents: " + fileName);

		} catch (Exception e)
		{
			logger.error("Exception in Excel Sample Servlet: " + e.getMessage());
			throw new ServletException("Exception in Excel Sample Servlet", e);
		} finally
		{
			if (out != null)
			{
				out.close();
			}
		}
	}
}