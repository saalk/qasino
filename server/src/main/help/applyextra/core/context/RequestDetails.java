package applyextra.core.context;

import io.opentracing.Span;

import java.util.Optional;

/**
 * Contains information from the HTTPRequest that should be accessible in all
 * layers of the application.
 */
public class RequestDetails {

    /**
     * Enum with VIP on the load-balancer.
     */
    public enum VIP {

        /**
         * Production
         */
        VIP_PROD(),
        /**
         * Green cell
         */
        VIP_GREEN(),
        /**
         * Orange cell
         */
        VIP_ORANGE(),
        /**
         * Preview
         */
        VIP_PREVIEW()
    }

    /**
     * IP-address of the client (the end-user).
     */
    private final String clientIpAddress;

    /**
     * The server-port of the URL (i.e. 80 or 443).
     */
    private final int serverPort;

    /**
     * The application-server-name.
     */
    private final String serverName;

    /**
     * The remote host.
     */
    private final String remoteHost;

    /**
     * The IP-address of the application-server.
     */
    private final String localAddress;

    /**
     * The user-Agent.
     */
    private final String userAgent;


    private final String xLbt1CcHeader;

    /**
     * The VIP on the loadbalancer. The header-name host is used to determine the VIP. This
     * feature is used to distinguish between CDS-environments:
     * <ul>
     * <li>prod_ CDS4R VIP Prod</li>
     * <li>green_ CDS4R VIP Groen</li>
     * <li>orange_ CDS4R VIP Oranje</li>
     * <li>preview_ CDS4R VIP Preview</li>
     * </ul>
     */
    private final VIP vip;

    private final Optional<Span> tracingSpan;

    /**
     * Constructor
     *  @param clientIpAddress the client IP-address.
     * @param remoteHost      name of the host.
     * @param serverPort      port for this server.
     * @param serverName      name of the server.
     * @param localAddress    the local address.
     * @param userAgent       the user-Agent.
     * @param xLbt1CcHeader   the xLbt1CcHeader
     * @param vip             the vip.
     * @param tracingSpan
     */
    public RequestDetails(final String clientIpAddress,
                          final String remoteHost,
                          final int serverPort,
                          final String serverName,
                          final String localAddress,
                          final String userAgent,
                          final String xLbt1CcHeader,
                          final VIP vip,
                          final Optional<Span> tracingSpan) {

        this.clientIpAddress = clientIpAddress;
        this.serverPort = serverPort;
        this.serverName = serverName;
        this.remoteHost = remoteHost;
        this.localAddress = localAddress;
        this.userAgent = userAgent;
        this.xLbt1CcHeader = xLbt1CcHeader;
        this.vip = vip;
        this.tracingSpan = tracingSpan;
    }

    /**
     * Returns the client IP-address.
     *
     * @return the client IP-address.
     */
    public String getClientIpAddress() {
        return clientIpAddress;
    }

    /**
     * Returns the serverPort.
     *
     * @return the serverPort.
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Return the serverName.
     *
     * @return the serverName.
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Return the remote host.
     *
     * @return the remote host.
     */
    public String getRemoteHost() {
        return remoteHost;
    }

    /**
     * Returns the local address.
     *
     * @return the local address.
     */
    public String getLocalAddress() {
        return localAddress;
    }

    /**
     * Returns the user-Agent.
     *
     * @return the user-Agent.
     */
    public String getUserAgent() {
        return userAgent;
    }


    /**
     * Getter for the value of the x-lbt1-cc header
     *
     * @return the value of the x-lbt1-cc header
     */
    public String getxLbt1CcHeader() {
        return xLbt1CcHeader;
    }

    /**
     * Returns the VIP on the load-balancer. The header-name host is used to determine the VIP.
     * This feature is used to distinguish between CDS-environments:
     * <ul>
     * <li>prod_ CDS4R VIP Prod</li>
     * <li>green_ CDS4R VIP Groen</li>
     * <li>orange_ CDS4R VIP Oranje</li>
     * <li>preview_ CDS4R VIP Preview</li>
     * </ul>
     *
     * @return the vip.
     */
    public VIP getVip() {
        return vip;
    }

    public Optional<Span> getTracingSpan() {
        return tracingSpan;
    }
}
