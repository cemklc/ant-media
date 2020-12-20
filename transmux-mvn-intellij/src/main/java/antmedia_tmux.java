import org.bytedeco.ffmpeg.global.avcodec.*;
import org.bytedeco.ffmpeg.global.avformat.*;
import org.bytedeco.ffmpeg.global.avutil.*;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.ffmpeg.*;
import org.bytedeco.ffmpeg.avcodec.*;
import org.bytedeco.ffmpeg.avformat.*;
import org.bytedeco.ffmpeg.avutil.*;
import org.bytedeco.ffmpeg.avfilter.*;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.PointerPointer;

import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;

/**
 import org.bytedeco.javacpp.*;
 import org.bytedeco.ffmpeg.swscale.*;
 import org.bytedeco.javacpp.DoublePointer;
 import org.bytedeco.ffmpeg.presets.*;
 import java.io.*;
 */


public class antmedia_tmux {


    public static void main(String[] args) throws Exception {

        AVFormatContext ifmt_ctx = new AVFormatContext(null);
        AVFormatContext ofmt_ctx = new AVFormatContext(null);
        int ret;
        String vf_path;
        vf_path = "C:\\Users\\cmklc\\OneDrive\\Masaüstü\\antmedia\\transmux-mvn-intellij\\bun33s.flv";


        String in_filename = vf_path;

        AVInputFormat avInputFormat = new AVInputFormat(null);
        AVDictionary avDictionary = new AVDictionary(null);

        ret = avformat_open_input(ifmt_ctx, in_filename, avInputFormat, avDictionary);

        // Read packets of a media file to get stream information
        ret = avformat_find_stream_info(ifmt_ctx, (AVDictionary) null);


        //av_dump_format(ifmt_ctx, 0, in_filename, 0);
        String out_filename = "new_out_from_flv_4.mp4";

        ret = avformat_alloc_output_context2(ofmt_ctx, null, null, out_filename);

        // initializes the stream context for the streams inside the input file
        // generaly consist of audio, video and subtitle streams
        int input_stream_size = 0;

        input_stream_size = ifmt_ctx.nb_streams();

        for (int stream_idx = 0; stream_idx < input_stream_size; stream_idx++) {
            AVStream out_stream;                                // initalize the output stream
            AVStream in_stream = ifmt_ctx.streams(stream_idx);   // initializes and reads the input stream type

            // initializes then checks the codec type of stream, if it is audio video or subtitle
            AVCodecParameters in_codedpar = in_stream.codecpar();
            if (in_codedpar.codec_type() != AVMEDIA_TYPE_AUDIO &&
                    in_codedpar.codec_type() != AVMEDIA_TYPE_VIDEO &&
                    in_codedpar.codec_type() != AVMEDIA_TYPE_SUBTITLE) {
                continue;
            }
            // initializes and writes the output stream and copies the values with input stream at that index
            out_stream = avformat_new_stream(ofmt_ctx, null);
            ret = avcodec_parameters_copy(out_stream.codecpar(), in_codedpar);
            out_stream.codecpar().codec_tag(0);
        }


        // Shows the output file information
        av_dump_format(ofmt_ctx, 0, out_filename, 1);

        // Allocates new memory and initializes for output file with AV Input Output
        AVIOContext pb = new AVIOContext(null);
        ret = avio_open(pb, out_filename, AVIO_FLAG_WRITE);
        ofmt_ctx.pb(pb);

        // Prepare outputfile for writing information
        AVDictionary avOutDict = new AVDictionary(null);
        ret = avformat_write_header(ofmt_ctx, avOutDict);

        // Initialize packet for stream information
        AVPacket pkt = new AVPacket();

        // start by reading the input frame after there is no more
        while (av_read_frame(ifmt_ctx, pkt) >= 0) {

            int stream_ind = pkt.stream_index();
            AVRational input_time_base = ifmt_ctx.streams(stream_ind).time_base();
            AVRational output_time_base = ofmt_ctx.streams(stream_ind).time_base();

            av_packet_rescale_ts(pkt,input_time_base , output_time_base);
            av_interleaved_write_frame(ofmt_ctx, pkt);

        }
        av_write_trailer(ofmt_ctx);
    }
}
