package ethanp.examples;

import org.eclipse.jetty.alpn.ALPN;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.NegotiatingServerConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.PushSessionCacheFilter;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.servlet.DispatcherType;
import javax.servlet.Servlet;
import java.io.File;
import java.util.EnumSet;

/**
 * Ethan Petuchowski 10/23/15
 * <p>
 * I got this from https://github.com/eclipse/jetty.project/blob/bd27e7d2d480949b32ad6abfbda0c24e6042c1e3/examples/embedded/src/main/java/org/eclipse/jetty/embedded/Http2Server.java
 * <p>
 * Then I trimmed it down to do what I need.
 */
public class ProjectH2S {
    public static void main(String... args) throws Exception {
        Server server = new Server();

        // for all routes on SESSIONs, use this handler
        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);

        // serve static content from this path (works!)
        context.setResourceBase("src/main/resources/docroot");

        // create an EnumSet only containing REQUEST
        // but not containing FORWARD, INCLUDE, ASYNC, or ERROR
        EnumSet<DispatcherType> requestEnumSet = EnumSet.of(DispatcherType.REQUEST);

        // add the "optimized server-push from cache" filter to all REQUESTs
        context.addFilter(PushSessionCacheFilter.class, "/*", requestEnumSet);

        // Servlet filters can intercept HTTP requests targeted at your web application
        context.addFilter(PushedTilesFilter.class, "/*", requestEnumSet);

        // This object will organize the loading of the servlet when needed or requested.
        ServletHolder servlet = new ServletHolder(ProjectH2S.servlet);

        context.addServlet(servlet, "/test/*");

        // This servlet, normally mapped to /,
        // provides the handling for static content,
        // as well as the OPTION and TRACE methods,
        // for the context.
        Class<DefaultServlet> defaultServlet = DefaultServlet.class;

        context.addServlet(defaultServlet, "/").setInitParameter("maxCacheSize", "81920");

        server.setHandler(context);
        server.addConnector(baseHttp1Connector(server));
        server.addConnector(baseHttp2Connector(server));
        server.start();
        //server.dumpStdErr();
        server.join();
    }

    static Servlet servlet = new ProtocolDebugServlet();

    public static final String PASSWORD = "password";
    public static final File keystoreFile = new File("keystore");

    public static HttpConfiguration baseHttpConfig() {
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecurePort(8443);

        // send the X-Powered-By header in responses
        http_config.setSendXPoweredBy(true);

        // send the Server header in responses
        http_config.setSendServerVersion(true);

        return http_config;
    }

    public static HttpConfiguration baseHttpsConfig() {
        HttpConfiguration httpsConfig = new HttpConfiguration(baseHttpConfig());
        httpsConfig.addCustomizer(new SecureRequestCustomizer());
        return httpsConfig;
    }

    /**
     * A Connection factory is responsible for instantiating and configuring a Connection instance
     * to handle an EndPoint accepted by a Connector.
     */
    public static SslContextFactory basicSslContextFactory() {
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keystoreFile.getAbsolutePath());
        sslContextFactory.setKeyStorePassword(PASSWORD);
        sslContextFactory.setKeyManagerPassword(PASSWORD);
        sslContextFactory.setCipherComparator(new HTTP2Cipher.CipherComparator());
        return sslContextFactory;
    }

    public static ALPNServerConnectionFactory baseAlpn() {
        NegotiatingServerConnectionFactory.checkProtocolNegotiationAvailable();
        ALPN.debug = false;
        ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
        alpn.setDefaultProtocol(HttpVersion.HTTP_1_1.asString());
        return alpn;
    }

    public static ServerConnector baseHttp1Connector(Server server) {
        ServerConnector http = new ServerConnector(
            server,
            new HttpConnectionFactory(baseHttpConfig()),
            new HTTP2CServerConnectionFactory(baseHttpConfig())
        );
        http.setPort(8080);
        return http;
    }

    public static ServerConnector baseHttp2Connector(Server server) {
        ServerConnector connector = new ServerConnector(
            server,
            new SslConnectionFactory(
                basicSslContextFactory(),
                HttpVersion.HTTP_1_1.asString()
            ),
            baseAlpn(),
            new HTTP2ServerConnectionFactory(baseHttpsConfig()),
            new HttpConnectionFactory(baseHttpsConfig())
        );
        connector.setPort(8443);
        return connector;
    }


}
