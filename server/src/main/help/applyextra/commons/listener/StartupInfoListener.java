package applyextra.commons.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Slf4j
public class StartupInfoListener implements ServletContextListener {

	@Override
	public void contextInitialized(final ServletContextEvent servletContextEvent) {
		ServletContext context = servletContextEvent.getServletContext();

		final String appName = getAppName(context);
		log.warn("MSG01001I: starting application: " + appName);
		log.warn("MSG01002I: application: {} build level: {}", appName, getAppVersion(context));
	}

	@Override
	public void contextDestroyed(final ServletContextEvent servletContextEvent) {
		ServletContext context = servletContextEvent.getServletContext();

		log.warn("MSG01003I: stopping application: " + getAppName(context));
	}

	private String getAppName(final ServletContext context) {
		return context.getServletContextName();
	}

	private String getAppVersion(final ServletContext context) {
		String appVersion = "unknown";

		InputStream is = null;
		try {
			is = context.getResourceAsStream("META-INF/MANIFEST.MF");
			if (is != null) {
				Manifest mf = new Manifest(is);
				Attributes mainAttributes = mf.getMainAttributes();
				String manifestAppVersion = mainAttributes.getValue("Implementation-Version"); // Returns null if not found
				if (manifestAppVersion != null) {
					appVersion = manifestAppVersion;
				}
			}
		} catch (IOException e) {
			log.warn("Could not determine App Version.", e);
		} finally {
			IOUtils.closeQuietly(is);
		}

		return appVersion;
	}
}
