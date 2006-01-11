/*
* $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//httpclient/src/test/org/apache/commons/httpclient/server/SimpleHttpServerConnection.java,v 1.21 2004/12/11 22:35:26 olegk Exp $
* $Revision: 224451 $
* $Date: 2005-07-23 06:23:59 -0400 (Sat, 23 Jul 2005) $
*
* ====================================================================
*
*  Copyright 1999-2004 The Apache Software Foundation
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
* ====================================================================
*
* This software consists of voluntary contributions made by many
* individuals on behalf of the Apache Software Foundation.  For more
* information on the Apache Software Foundation, please see
* <http://www.apache.org/>.
*
*/


package org.apache.axis2.transport.http.server;

import org.apache.commons.httpclient.ChunkedOutputStream;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpParser;
import org.apache.commons.httpclient.StatusLine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * A connection to the SimpleHttpServer.
 */
public class SimpleHttpServerConnection {
    private static final String HTTP_ELEMENT_CHARSET = "US-ASCII";
    private Socket socket = null;
    private OutputStream out = null;
    private boolean keepAlive = false;
    private InputStream in = null;
    public static final int DEFAULT_TIMEOUT = 60000;  // 1 Minute

    public SimpleHttpServerConnection(final Socket socket) throws IOException {
        super();

        if (socket == null) {
            throw new IllegalArgumentException("Socket may not be null");
        }

        this.socket = socket;
        this.socket.setTcpNoDelay(true);
        this.socket.setSoTimeout(DEFAULT_TIMEOUT);
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
    }

    public synchronized void close() {
        try {
            if (socket != null) {
                socket.shutdownInput();
                socket.shutdownOutput();
                in.close();
                out.close();
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
        }
    }

    public SimpleRequest readRequest() throws IOException {
        try {
            String line = null;

            do {
                line = HttpParser.readLine(in, HTTP_ELEMENT_CHARSET);
            } while ((line != null) && (line.length() == 0));

            if (line == null) {
                setKeepAlive(false);

                return null;
            }

            SimpleRequest request = new SimpleRequest(RequestLine.parseLine(line),
                    HttpParser.parseHeaders(this.in, HTTP_ELEMENT_CHARSET),
                    this.in);

            return request;
        } catch (IOException e) {
            close();

            throw e;
        }
    }

    public SimpleResponse readResponse() throws IOException {
        try {
            String line = null;

            do {
                line = HttpParser.readLine(in, HTTP_ELEMENT_CHARSET);
            } while ((line != null) && (line.length() == 0));

            if (line == null) {
                setKeepAlive(false);

                return null;
            }

            SimpleResponse response = new SimpleResponse(new StatusLine(line),
                    HttpParser.parseHeaders(this.in, HTTP_ELEMENT_CHARSET),
                    this.in);

            return response;
        } catch (IOException e) {
            close();

            throw e;
        }
    }

    public void writeRequest(final SimpleRequest request) throws IOException {
        if (request == null) {
            return;
        }

        ResponseWriter writer = new ResponseWriter(this.out, HTTP_ELEMENT_CHARSET);

        writer.println(request.getRequestLine().toString());

        Iterator item = request.getHeaderIterator();

        while (item.hasNext()) {
            Header header = (Header) item.next();

            writer.print(header.toExternalForm());
        }

        writer.println();
        writer.flush();

        OutputStream outsream = this.out;
        InputStream content = request.getBody();

        if (content != null) {
            Header transferenc = request.getFirstHeader("Transfer-Encoding");

            if (transferenc != null) {
                request.removeHeaders("Content-Length");

                if (transferenc.getValue().indexOf("chunked") != -1) {
                    outsream = new ChunkedOutputStream(outsream);
                }
            }

            byte[] tmp = new byte[4096];
            int i = 0;

            while ((i = content.read(tmp)) >= 0) {
                outsream.write(tmp, 0, i);
            }

            if (outsream instanceof ChunkedOutputStream) {
                ((ChunkedOutputStream) outsream).finish();
            }
        }

        outsream.flush();
    }

    public void writeResponse(final SimpleResponse response) throws IOException {
        if (response == null) {
            return;
        }

        ResponseWriter writer = new ResponseWriter(this.out, HTTP_ELEMENT_CHARSET);

        writer.println(response.getStatusLine());

        Iterator item = response.getHeaderIterator();

        while (item.hasNext()) {
            Header header = (Header) item.next();

            writer.print(header.toExternalForm());
        }

        writer.println();
        writer.flush();

        OutputStream outsream = this.out;
        InputStream content = response.getBody();

        if (content != null) {
            Header transferenc = response.getFirstHeader("Transfer-Encoding");

            if (transferenc != null) {
                response.removeHeaders("Content-Length");

                if (transferenc.getValue().indexOf("chunked") != -1) {
                    outsream = new ChunkedOutputStream(outsream);

                    byte[] tmp = new byte[1024];
                    int i = 0;

                    while ((i = content.read(tmp)) >= 0) {
                        outsream.write(tmp, 0, i);
                    }

                    if (outsream instanceof ChunkedOutputStream) {
                        ((ChunkedOutputStream) outsream).finish();
                    }
                }
            } else {

                /**
                 * read the content when needed to embed content-length
                 */
                byte[] tmp = new byte[1024];
                int i = 0;

                while ((i = content.read(tmp)) >= 0) {
                    outsream.write(tmp, 0, i);
                }
            }
        }

        outsream.flush();
    }

    public InputStream getInputStream() {
        return this.in;
    }

    public OutputStream getOutputStream() {
        return this.out;
    }

    public int getSocketTimeout() throws SocketException {
        return this.socket.getSoTimeout();
    }

    /**
     * Returns the ResponseWriter used to write the output to the socket.
     *
     * @return Returns this connection's ResponseWriter.
     */
    public ResponseWriter getWriter() throws UnsupportedEncodingException {
        return new ResponseWriter(out);
    }

    public boolean isKeepAlive() {
        return this.keepAlive;
    }

    public synchronized boolean isOpen() {
        return this.socket != null;
    }

    public void setKeepAlive(boolean b) {
        this.keepAlive = b;
    }

    public void setSocketTimeout(int timeout) throws SocketException {
        this.socket.setSoTimeout(timeout);
    }

    public String getURL(String suffix) throws Exception {
        String hostAddress = getIpAddress();
        return "http://" + hostAddress + ":" + socket.getLocalPort() + "/" + suffix;
    }

    /**
     * Returns the ip address to be used for the replyto epr
     * CAUTION:
     * This will simply go though the list of available network
     * interfaces and will return the final address of the final interface
     * available in the list. This workes fine for the simple cases where
     * 1.) there's only the loopback interface, where the ip is 127.0.0.1
     * 2.) there's an additional interface availbale which is used to
     * access an external network and has only one ip assigned to it.
     * <p/>
     * TODO:
     * - Improve this logic to genaralize it a bit more
     * - Obtain the ip to be used here from the Call API
     *
     * @return Returns String.
     * @throws SocketException
     */
    public static String getIpAddress() throws SocketException {
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        String address = null;

        while (e.hasMoreElements()) {
            NetworkInterface netface = (NetworkInterface) e.nextElement();
            Enumeration addresses = netface.getInetAddresses();

            while (addresses.hasMoreElements()) {
                InetAddress ip = (InetAddress) addresses.nextElement();

                // the last available ip address will be returned
                address = ip.getHostAddress();
            }
        }

        return address;
    }
}
