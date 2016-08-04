package tests.rtp.jitsi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.List;
import java.util.Map;
import org.jitsi.service.libjitsi.LibJitsi;
import org.jitsi.service.neomedia.DefaultStreamConnector;
import org.jitsi.service.neomedia.MediaDirection;
import org.jitsi.service.neomedia.MediaService;
import org.jitsi.service.neomedia.MediaStream;
import org.jitsi.service.neomedia.MediaStreamTarget;
import org.jitsi.service.neomedia.MediaType;
import org.jitsi.service.neomedia.MediaUseCase;
import org.jitsi.service.neomedia.StreamConnector;
import org.jitsi.service.neomedia.device.MediaDevice;
import org.jitsi.service.neomedia.format.MediaFormat;

/**
 * This class streams screen recorded video. It can either send an H264 encoded
 * RTP stream or receive one depending on the value of the variable
 * isReceivingVideo_.
 */
public class VideoStreamer {

    // Set to false if sending video, set to true if receiving video.
    private static final boolean isReceivingVideo_ = true;

    public final MediaService mediaService_;
    private final Map<MediaFormat, Byte> RTP_payload_number_map_;

    public static final int LOCAL_BASE_PORT_NUMBER = 15000;
    public static final String REMOTE_HOST_IP_ADDRESS = "127.0.0.1";
    public static final int REMOTE_BASE_PORT_NUMBER = 10000;

    private MediaStream videoMediaStream_;
    private final int localBasePort_;
    private final InetAddress remoteAddress_;
    private final int remoteBasePort_;

    /**
     * Initializes a new VideoStreamer instance which is to send or receive
     * video from a specific host and a specific port.
     *
     * @param isReceiver - true if this instance of VideoStreamer is receiving a
     * video stream, false if it is sending a video stream.
     */
    public VideoStreamer(boolean isReceiver) throws IOException {
        this.remoteAddress_ = InetAddress.getByName(REMOTE_HOST_IP_ADDRESS);
        mediaService_ = LibJitsi.getMediaService();
        RTP_payload_number_map_ = mediaService_.getDynamicPayloadTypePreferences();
        if (isReceiver) {
            this.localBasePort_ = LOCAL_BASE_PORT_NUMBER;
            this.remoteBasePort_ = REMOTE_BASE_PORT_NUMBER;
            startVideoStream(MediaDirection.RECVONLY);
        } else {
            // switch the local and remote ports for the transmitter so they hook up with the receiver.
            this.localBasePort_ = REMOTE_BASE_PORT_NUMBER;
            this.remoteBasePort_ = LOCAL_BASE_PORT_NUMBER;
            startVideoStream(MediaDirection.SENDONLY);
        }
    }

    /**
     * Initializes the receipt of video, starts it, and tries to record any
     * incoming packets.
     *
     * @param intended_direction either sending or receiving an RTP video
     * stream.
     */
    public final void startVideoStream(final MediaDirection intended_direction) throws SocketException {
        final MediaType video_media_type = MediaType.VIDEO;
        final int local_video_port = localBasePort_;
        final int remote_video_port = remoteBasePort_;
        MediaDevice video_media_device = mediaService_.getDefaultDevice(video_media_type, MediaUseCase.DESKTOP);
        final MediaStream video_media_stream = mediaService_.createMediaStream(video_media_device);
        video_media_stream.setDirection(intended_direction);
        // Obtain the list of formats that are available for a specific video_media_device and pick H264 if availible.
        MediaFormat video_format = null;
        final List<MediaFormat> supported_video_formats = video_media_device.getSupportedFormats();
        for (final MediaFormat availible_video_format : supported_video_formats) {
            final String encoding = availible_video_format.getEncoding();
            final double clock_rate = availible_video_format.getClockRate();
            if (encoding.equals("H264") && clock_rate == 90000) {
                video_format = availible_video_format;
            }
        }
        if (video_format == null) {
            System.out.println("You do not have the H264 video codec");
            System.exit(-1);
        }
        final byte dynamic_RTP_payload_type_for_H264 = getRTPDynamicPayloadType(video_format);
        if (dynamic_RTP_payload_type_for_H264 < 96 || dynamic_RTP_payload_type_for_H264 > 127) {
            System.out.println("Invalid RTP payload type number");
            System.exit(-1);
        }
        video_media_stream.addDynamicRTPPayloadType(dynamic_RTP_payload_type_for_H264, video_format);
        video_media_stream.setFormat(video_format);
        final int local_RTP_video_port = local_video_port + 0;
        final int local_RTCP_video_port = local_video_port + 1;
        final StreamConnector video_connector = new DefaultStreamConnector(
                new DatagramSocket(local_RTP_video_port),
                new DatagramSocket(local_RTCP_video_port)
        );
        video_media_stream.setConnector(video_connector);
        final int remote_RTP_video_port = remote_video_port + 0;
        final int remote_RTCP_video_port = remote_video_port + 1;
        video_media_stream.setTarget(new MediaStreamTarget(
                new InetSocketAddress(remoteAddress_, remote_RTP_video_port),
                new InetSocketAddress(remoteAddress_, remote_RTCP_video_port))
        );
        video_media_stream.setName(video_media_type.toString());
        this.videoMediaStream_ = video_media_stream;
        videoMediaStream_.start();
        listenForVideoPackets(video_connector.getDataSocket());
    }

    public void listenForVideoPackets(final DatagramSocket videoDataSocket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean socket_is_closed = false;
                while (!socket_is_closed) {
                    final byte[] buffer = new byte[5000];
                    final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    try {
                        videoDataSocket.receive(packet);
                        final byte[] packet_data = new byte[packet.getLength()];
                        System.arraycopy(packet.getData(), packet.getOffset(), packet_data, 0, packet.getLength());
                        final StringBuilder string_builder = new StringBuilder();
                        for (int i = 0; i < ((packet_data.length > 30) ? 30 : packet_data.length); ++i) {
                            byte b = packet_data[i];
                            string_builder.append(String.format("%02X ", b));
                        }
                        System.out.println("First thirty (or fewer) bytes of packet in hex: " + string_builder.toString());
                    } catch (SocketException socket_closed) {
                        System.out.println("Socket is closed");
                        socket_is_closed = true;
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * Checks if the given format exists in the list of formats with listed
     * dynamic RTP payload numbers and returns that number.
     *
     * @param format - format to look up an RTP payload number for
     * @return - RTP payload on success or -1 either if payload number cannot be
     * found or if payload number is static.
     */
    public byte getRTPDynamicPayloadType(final MediaFormat format) {
        for (Map.Entry<MediaFormat, Byte> entry : RTP_payload_number_map_.entrySet()) {
            final MediaFormat map_format = entry.getKey();
            final Byte rtp_payload_type = entry.getValue();
            if (map_format.getClockRate() == format.getClockRate() && map_format.getEncoding().equals(format.getEncoding())) {
                return rtp_payload_type;
            }
        }
        return -1;
    }

    /**
     * Close the MediaStream.
     */
    public void close() {
        try {
            this.videoMediaStream_.stop();
        } finally {
            this.videoMediaStream_.close();
            this.videoMediaStream_ = null;
        }
    }

    public static void main(String[] args) throws Exception {
        LibJitsi.start();
        try {
            VideoStreamer rtp_streamer
                    = new VideoStreamer(isReceivingVideo_);
            try {
                /*
                 * Wait for the media to be received and (hopefully) played back.
                 * Transmits for 1 minute and receives for 30 seconds to allow the
                 * tranmission to have a delay (if necessary).
                 */
                final long then = System.currentTimeMillis();
                final long waiting_period;
                if (isReceivingVideo_) {
                    waiting_period = 30000;
                } else {
                    waiting_period = 60000;
                }
                try {
                    while (System.currentTimeMillis() - then < waiting_period) {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ie) {
                }
            } finally {
                rtp_streamer.close();
            }
            System.err.println("Exiting VideoStreamer");
        } finally {
            LibJitsi.stop();
        }
    }
}